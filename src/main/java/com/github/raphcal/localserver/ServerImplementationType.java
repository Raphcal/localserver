package com.github.raphcal.localserver;

/**
 * Implementation type to use.
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public enum ServerImplementationType {

    /**
     * Custom Http server.
     */
    LOCALSERVER {

        /**
         * {@inheritDoc}
         */
        @Override
        ServerThread create(int port, HttpRequestHandler servlet) {
            return new LocalServerThread(port, servlet);
        }

    },

    /**
     * Sun implementation of HttpServer, provided with the JDK since 1.6.
     */
    SUN_HTTP_SERVER {

        /**
         * {@inheritDoc}
         */
        @Override
        ServerThread create(int port, HttpRequestHandler servlet) {
            return new LocalServerThread(port, servlet);
        }

    };

    /**
     * Creates a new server binded on the given port. Requests will be processed
     * by the given servlet.
     *
     * @param port Port to listen.
     * @param servlet Servet that will handle requests.
     * @return A new server.
     */
    abstract ServerThread create(int port, HttpRequestHandler servlet);

}
