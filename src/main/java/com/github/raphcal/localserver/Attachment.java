package com.github.raphcal.localserver;

import java.nio.ByteBuffer;

/**
 * Objets attachés à un client du serveur.
 *
 * @author Raphaël Calabro <ddaeke-github at yahoo.fr>
 */
class Attachment {

    private static final int BUFFER_SIZE = 1024;

    private final ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
    private final HttpRequestBuilder httpRequestBuilder = new HttpRequestBuilder();
    private final HttpResponse response = new HttpResponse();

    public ByteBuffer getBuffer() {
        return buffer;
    }

    public HttpRequestBuilder getRequestBuilder() {
        return httpRequestBuilder;
    }

    public HttpResponse getResponse() {
        return response;
    }
}
