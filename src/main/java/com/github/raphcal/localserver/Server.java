package com.github.raphcal.localserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread gérant l'envoi et la réception de requêtes HTTP.
 *
 * @author Raphaël Calabro <ddaeke-github at yahoo.fr>
 */
class Server implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final HttpRequestHandler servlet;
    private final InetSocketAddress endpoint;
    private final Object runningLock;
    private final Semaphore startSemaphore;

    /**
     * Créé une nouveau serveur HTTP.
     *
     * @param servlet Objet s'occupant de configurer les réponses aux requêtes
     * reçues.
     * @param endpoint Adresse où écouter les requêtes.
     * @param runningLock Objet servant de verrou d'exécution.
     * @param startLock Lock de démarrage.
     */
    public Server(HttpRequestHandler servlet, InetSocketAddress endpoint, Object runningLock, Semaphore startSemaphore) {
        this.servlet = servlet;
        this.endpoint = endpoint;
        this.runningLock = runningLock;
        this.startSemaphore = startSemaphore;

        try {
            startSemaphore.acquire();
        } catch (InterruptedException ex) {
            LOGGER.error("Sémaphore non disponible au démarrage du serveur", ex);
        }
    }

    @Override
    public void run() {

        synchronized (runningLock) {
            ServerSocketChannel serverChannel = null;
            Selector selector = null;
            try {
                serverChannel = ServerSocketChannel.open();
                selector = Selector.open();

                serverChannel.configureBlocking(false);
                serverChannel.socket().bind(endpoint);
                serverChannel.register(selector, SelectionKey.OP_ACCEPT);

                final Set<SelectionKey> keys = selector.selectedKeys();

                startSemaphore.release();

                while (!Thread.currentThread().isInterrupted()) {
                    selector.select();

                    final Iterator<SelectionKey> keyIterator = keys.iterator();
                    while (keyIterator.hasNext()) {
                        final SelectionKey key = keyIterator.next();
                        keyIterator.remove();

                        if (key.isAcceptable()) {
                            // Nouvelle connexion
                            final ServerSocketChannel server = (ServerSocketChannel) key.channel();

                            final SocketChannel channel = server.accept();
                            channel.configureBlocking(false);

                            channel.register(selector, SelectionKey.OP_READ, new Attachment());

                        } else if (key.isReadable()) {
                            // Lecture
                            final SocketChannel channel = (SocketChannel) key.channel();

                            // TODO: Mettre en cache la requête jusqu'à lecture 
                            // complète. Quand OK, délégation au servlet puis 
                            // modification de l'intérêt en écriture.
                            final Attachment attachment = (Attachment) key.attachment();
                            final ByteBuffer buffer = attachment.getBuffer();
                            int bytes = channel.read(buffer);

                            if (bytes == -1) {
                                // Fermeture du flux
                                channel.close();
                                key.cancel();

                            } else if (bytes > 0) {
                                buffer.flip();
                                attachment.getRequestBuilder().feedBytes(buffer);
                                buffer.compact();
                            }

                            if (attachment.getRequestBuilder().isReady()) {
                                final HttpRequest request = attachment.getRequestBuilder().getRequest();
                                final HttpResponse response = attachment.getResponse();
                                response.configureDefaults();

                                servlet.handleRequest(request, response);

                                key.interestOps(SelectionKey.OP_WRITE);
                            }

                        } else if (key.isWritable()) {
                            // Ecriture
                            final SocketChannel channel = (SocketChannel) key.channel();
                            final Attachment attachment = (Attachment) key.attachment();

                            // TODO: Ecrire le retour contenu dans l'attachement et 
                            // fermer la connexion ensuite.
                            channel.write(ByteBuffer.wrap(attachment.getResponse().toByteArray()));

                            channel.close();
                            key.cancel();
                        }
                    }
                }

            } catch (IOException e) {
                LOGGER.error("Erreur du serveur local (" + endpoint + ").", e);

            } finally {
                try {
                    if (selector != null) {
                        selector.close();
                    }

                    if (serverChannel != null) {
                        serverChannel.close();
                    }

                } catch (IOException e) {
                    LOGGER.error("Erreur lors de l'arrêt du serveur local (" + endpoint + ").", e);
                }
            }
        }
    }
}
