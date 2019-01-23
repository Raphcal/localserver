package com.github.raphcal.localserver;

import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe permettant de simuler un serveur.
 *
 * @author Raphaël Calabro <ddaeke-github at yahoo.fr>
 */
public class LocalServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalServer.class);

    /**
     * Constante utilisée pour convertir des millisecondes en secondes.
     */
    private static final double MILLISECONDS = 1000.0;

    private final InetSocketAddress endpoint;
    private final HttpRequestHandler servlet;
    private final Thread serverThread;

    private long startTime;

    private final Object runningLock = new Object();
    private final AtomicBoolean stopping = new AtomicBoolean(false);
    private final Semaphore startSemaphore = new Semaphore(1);

    /**
     * Créé un nouveau serveur prêt à écouter sur le port donné.
     *
     * @param port Port à écouter.
     * @param servlet Actions a effectuer lors de la réception d'une requête
     * HTTP.
     * @see HttpServlet
     */
    public LocalServer(int port, HttpRequestHandler servlet) {
        this.endpoint = new InetSocketAddress(port);
        this.servlet = servlet;
        this.serverThread = new Thread(new Server(servlet, endpoint, runningLock, startSemaphore));
    }

    /**
     * Démarre le serveur dans un thread séparé.
     */
    public void start() {
        if (!serverThread.isAlive()) {
            LOGGER.info("Démarrage d'un serveur local à l'adresse " + endpoint + "...");
            startTime = new Date().getTime();
            serverThread.start();
            try {
                startSemaphore.acquire();
            } catch (InterruptedException ex) {
                LOGGER.error("Interruption pendant l'attente du démarrage du serveur.", ex);
            }
        } else {
            LOGGER.warn("Le serveur est déjà démarré.");
        }
    }

    /**
     * Arrête le serveur et libère le port.
     */
    public void stop() {
        if (serverThread != null) {
            LOGGER.info("Arrêt du serveur local " + endpoint + "...");
            serverThread.interrupt();

            synchronized (runningLock) {
                final double totalTime = (new Date().getTime() - startTime) / MILLISECONDS;
                LOGGER.info("Serveur local arrêté (temps d'exécution total : " + totalTime + "s).");
            }
        } else {
            LOGGER.warn("Le serveur n'est pas démarré.");
        }
    }

    /**
     * Programme l'arrêt après l'écoulement du temps spécifié.
     *
     * @param delay Durée.
     * @param unit Unité de la durée.
     */
    public void stop(long delay, TimeUnit unit) {
        if (stopping.compareAndSet(false, true)) {
            LOGGER.info("Le serveur s'arrêtera dans " + delay + ' ' + unit.name().toLowerCase() + '.');

            final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
            executorService.schedule(new Runnable() {

                @Override
                public void run() {
                    stop();
                    executorService.shutdown();
                }

            }, delay, unit);
        }
    }

    /**
     * Récupère l'objet gérant les requêtes HTTP.
     *
     * @return L'objet gérant les requêtes HTTP.
     */
    public HttpRequestHandler getServlet() {
        return servlet;
    }
}
