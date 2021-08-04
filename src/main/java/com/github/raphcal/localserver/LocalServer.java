package com.github.raphcal.localserver;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Local server.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class LocalServer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(LocalServer.class);

    /**
     * Servlet to use for handling http requests.
     */
    private final HttpRequestHandler servlet;

    /**
     * Thread running the server.
     */
    private final ServerThread serverThread;

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
        this(port, servlet, ServerImplementationType.LOCALSERVER);
    }

    /**
     * Creates a new server.
     * <p>
     * The server will try to bind to given port. If the port is occupied, the
     * server will try on the next port and so on until the connection succeed.
     *
     * @param port Port to bind to.
     * @param servlet Servlet to use for handling http requests.
     * @param type Implementation type to use.
     * @see HttpServlet
     */
    public LocalServer(int port, HttpRequestHandler servlet, ServerImplementationType type) {
        this.servlet = servlet;
        this.serverThread = type.create(port, servlet);
    }

    /**
     * Try 5 times to start a new server on a random port. A different server
     * implementation will be used at each attempt.
     *
     * @param servlet Servlet to use for handling http requests.
     * @return An instance of <code>LocalServer</code> if startup succeeds or
     * <code>null</code> if every attempt failed.
     */
    public static LocalServer startServerOnRandomPort(HttpRequestHandler servlet) {
        return startServerOnRandomPort(servlet, null, null);
    }

    /**
     * Try 5 times to start a new server on a random port.
     *
     * @param servlet Servlet to use for handling http requests.
     * @param implementationType Implementation to use or <code>null</code> to
     * try a different implementation at each try.
     * @return An instance of <code>LocalServer</code> if startup succeeds or
     * <code>null</code> if every attempt failed.
     */
    public static LocalServer startServerOnRandomPort(HttpRequestHandler servlet, ServerImplementationType implementationType) {
        return startServerOnRandomPort(servlet, implementationType, null);
    }

    /**
     * Try <code>retries</code> times to start a new server on a random port.
     *
     * @param servlet Servlet to use for handling http requests.
     * @param retries Maximum retry count.
     * @return An instance of <code>LocalServer</code> if startup succeeds or
     * <code>null</code> if every attempt failed.
     */
    public static LocalServer startServerOnRandomPort(HttpRequestHandler servlet, int retries) {
        return startServerOnRandomPort(servlet, null, retries);
    }

    /**
     * Try to start a new server on a random port.
     *
     * @param servlet Servlet to use for handling http requests.
     * @param implementationType Implementation to use or <code>null</code> to
     * try a different implementation at each try.
     * @param retries Maximum retry count or <code>null</code> to use default
     * retry count (= 5).
     * @return An instance of <code>LocalServer</code> if startup succeeds or
     * <code>null</code> if every attempt failed.
     */
    public static LocalServer startServerOnRandomPort(HttpRequestHandler servlet, ServerImplementationType implementationType, Integer retries) {
        LocalServer localServer = null;
        final ServerImplementationType[] implementations = ServerImplementationType.values();
        final int retryCount = retries != null
                ? retries
                : 5;
        for (int retry = 0; retry < retryCount; retry++) {
            final int port = 10000 + (int) (Math.random() * 8000.0);
            localServer = new LocalServer(port, servlet, implementationType != null
                    ? implementationType
                    : implementations[retry % implementations.length]);
            localServer.start();

            boolean running = false;
            try {
                final URL serverURL = new URL("http", "localhost", localServer.getEndpoint().getPort(), "/");
                final HttpURLConnection connection = (HttpURLConnection)serverURL.openConnection();
                running = connection.getResponseCode() == 200;
            } catch (IOException e) {
                LOGGER.debug("Unable to connect to local server", e);
            }

            if (!running) {
                localServer.stop();
                localServer = null;
            }
        }
        return localServer;
    }

    /**
     * Start the server in a new thread.
     * This method blocks until the server is started.
     */
    public void start() {
        serverThread.start();
    }

    /**
     * Stop the server.
     */
    public void stop() {
        serverThread.stop();
    }

    /**
     * Stop the server after the specified delay.
     *
     * @param delay Delay.
     * @param unit Duration unit.
     */
    public void stop(long delay, TimeUnit unit) {
        serverThread.stop(delay, unit);
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
        return serverThread.getEndpoint();
    }
}
