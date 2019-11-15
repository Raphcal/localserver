package com.github.raphcal.localserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
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
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
class Server implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private InetSocketAddress endpoint;
    private int port;

    private final HttpRequestHandler servlet;
    private final Object runningLock;
    private final Semaphore startSemaphore;

    /**
     * Créé une nouveau serveur HTTP.
     *
     * @param servlet Objet s'occupant de configurer les réponses aux requêtes
     * reçues.
     * @param port Port où écouter les requêtes.
     * @param runningLock Objet servant de verrou d'exécution.
     * @param startLock Lock de démarrage.
     */
    public Server(HttpRequestHandler servlet, int port, Object runningLock, Semaphore startSemaphore) {
        this.port = port;
        this.servlet = servlet;
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
            try (final ServerSocketChannel serverChannel = ServerSocketChannel.open();
                    final Selector selector = Selector.open()) {
                startServer(serverChannel, selector);
                
                while (!Thread.currentThread().isInterrupted()) {
                    handleIO(selector);
                }
            } catch (IOException ex) {
                LOGGER.error("An unexpected error happened for server " + endpoint, ex);
            }
        }
    }
    
    public InetSocketAddress getEndpoint() {
        return endpoint;
    }
    
    private void startServer(ServerSocketChannel serverChannel, Selector selector) throws ClosedChannelException, IOException {
        serverChannel.configureBlocking(false);
        while (endpoint == null) {
            final InetSocketAddress address = new InetSocketAddress(port);
            try {
                serverChannel.socket().bind(address);
                this.endpoint = address;
            } catch (IOException e) {
                LOGGER.debug("Unable to bind to address " + address, e);
                port++;
            }
        }
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        startSemaphore.release();
    }

    private void handleIO(final Selector selector) throws IOException {
        selector.select();
        
        final Set<SelectionKey> keys = selector.selectedKeys();
        final Iterator<SelectionKey> keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
            final SelectionKey key = keyIterator.next();
            keyIterator.remove();
            
            if (key.isAcceptable()) {
                acceptClient(key, selector);
            } else if (key.isReadable()) {
                readData(key);
            } else if (key.isWritable()) {
                writeData(key);
            }
        }
    }

    private void acceptClient(final SelectionKey key, final Selector selector) throws IOException, ClosedChannelException {
        final ServerSocketChannel server = (ServerSocketChannel) key.channel();
        
        final SocketChannel channel = server.accept();
        channel.configureBlocking(false);
        
        channel.register(selector, SelectionKey.OP_READ, new Attachment());
    }

    private void readData(final SelectionKey key) throws IOException {
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
            ((Buffer)buffer).flip();
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
    }

    private void writeData(final SelectionKey key) throws IOException {
        final SocketChannel channel = (SocketChannel) key.channel();
        final Attachment attachment = (Attachment) key.attachment();
        
        // TODO: Ecrire le retour contenu dans l'attachement et
        // fermer la connexion ensuite.
        channel.write(ByteBuffer.wrap(attachment.getResponse().toByteArray()));
        
        channel.close();
        key.cancel();
    }

}
