package com.github.raphcal.localserver;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * Local server.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class LocalServer {

    private final HttpRequestHandler servlet;
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
