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
 * Local server.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class LocalServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(LocalServer.class);

    private static final double MILLISECONDS = 1000.0;

    private final Server server;
    private final HttpRequestHandler servlet;
    private final Thread serverThread;

    private long startTime;

    private final Object runningLock = new Object();
    private final AtomicBoolean stopping = new AtomicBoolean(false);
    private final Semaphore startSemaphore = new Semaphore(1);

    /**
     * Creates a new server.
     * <p>
     * The server will try to bind to given port. If the port is occupied, the
     * server will try on the next port and so on until the connection succeed.
     *
     * @param port Port to bind to.
     * @param servlet Servlet to use for handling http requests.
     * @see HttpServlet
     */
    public LocalServer(int port, HttpRequestHandler servlet) {
        this.servlet = servlet;
        this.server = new Server(servlet, port, runningLock, startSemaphore);
        this.serverThread = new Thread(server);
    }

    /**
     * Start the server in a new thread.
     * This method blocks until the server is started.
     */
    public void start() {
        if (!serverThread.isAlive()) {
            LOGGER.info("Starting server thread...");
            startTime = new Date().getTime();
            serverThread.start();
            try {
                startSemaphore.acquire();
            } catch (InterruptedException ex) {
                LOGGER.error("Server start has been interrupted.", ex);
            }
            LOGGER.info("Server listening on " + server.getEndpoint());
        } else {
            LOGGER.warn("Server is already started and listening on " + server.getEndpoint());
        }
    }

    /**
     * Stop the server.
     */
    public void stop() {
        if (serverThread != null) {
            LOGGER.info("Stopping server " + server.getEndpoint() + "...");
            serverThread.interrupt();

            synchronized (runningLock) {
                final double totalTime = (new Date().getTime() - startTime) / MILLISECONDS;
                LOGGER.info("Server stopped (total execution time : " + totalTime + "s).");
            }
        } else {
            LOGGER.warn("Server is not started.");
        }
    }

    /**
     * Stop the server after the specified delay.
     *
     * @param delay Delay.
     * @param unit Duration unit.
     */
    public void stop(long delay, TimeUnit unit) {
        if (stopping.compareAndSet(false, true)) {
            LOGGER.info("Server will stop in " + delay + ' ' + unit.name().toLowerCase() + '.');

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
     * Returns the servlet handling http requests.
     *
     * @return Servlet instance handling http requests.
     */
    public HttpRequestHandler getServlet() {
        return servlet;
    }
    
    /**
     * Address bound to the server.
     * Will be null if the server is not running.
     *
     * @return Address bound to the server.
     */
    public InetSocketAddress getEndpoint() {
        return server.getEndpoint();
    }
}
