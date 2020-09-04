package com.github.raphcal.localserver;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
class SunHttpServerThread implements ServerThread {

    private static final Logger LOGGER = LoggerFactory.getLogger(SunHttpServerThread.class);

    private final HttpServer server;
    private long startTime;

    public SunHttpServerThread(int port, HttpRequestHandler servlet) {
        try {
            final HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
            httpServer.createContext("/", new HttpHandlerAdapter(servlet));
            this.server = httpServer;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to start server thread", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void start() {
        LOGGER.info("Starting server thread...");
        startTime = new Date().getTime();
        server.start();
        LOGGER.info("Server listening on " + server.getAddress());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        LOGGER.info("Stopping server " + server.getAddress() + "...");
        server.stop(0);
        LOGGER.info("Server stopped (total execution time : "
                        + TimeUnit.SECONDS.convert(new Date().getTime() - startTime, TimeUnit.MILLISECONDS)
                        + "s).");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop(long delay, TimeUnit unit) {
        LOGGER.info("Server will stop in " + delay + ' ' + unit.name().toLowerCase() + '.');
        server.stop((int) TimeUnit.SECONDS.convert(delay, unit));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InetSocketAddress getEndpoint() {
        return server.getAddress();
    }

}
