package com.github.raphcal.localserver;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
interface ServerThread {

    /**
     * Start the server in a new thread.
     * This method blocks until the server is started.
     */
    void start();

    /**
     * Stop the server immediatly.
     */
    void stop();

    /**
     * Stop the server after the specified delay.
     *
     * @param delay Delay.
     * @param unit Duration unit.
     */    
    void stop(long delay, TimeUnit unit);

    /**
     * Address bound to the server.
     * Will be null if the server is not running.
     *
     * @return Address bound to the server.
     */
    InetSocketAddress getEndpoint();

}
