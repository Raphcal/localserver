package com.github.raphcal.localserver;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * Adapter to ease transition from LocalServer to Sun HttpServer.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 * @see com.sun.net.httpserver.HttpServer
 */
public class HttpHandlerAdapter implements HttpHandler {

    /**
     * Servlet to adapt into an HttpHandler.
     */
    private final HttpRequestHandler servlet;

    /**
     * Creates a new adapter for the given servlet.
     *
     * @param servlet Servlet to adapt into an HttpHandler.
     */
    public HttpHandlerAdapter(HttpRequestHandler servlet) {
        this.servlet = servlet;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        final HttpRequest request = new HttpRequest();
        final HttpResponse response = new HttpResponse();

        request.setMethod(exchange.getRequestMethod());
        request.setTarget(exchange.getRequestURI().toString());
        request.setVersion(exchange.getProtocol());
        for (Map.Entry<String, List<String>> header : exchange.getRequestHeaders().entrySet()) {
            request.setHeader(header.getKey(), String.join(";", header.getValue()));
        }
        try (BufferedInputStream bufferedInputStream = new BufferedInputStream(exchange.getRequestBody())) {
            byte[] result = new byte[4096];
            int index = 0;

            final byte[] bytes = new byte[2048];
            int read = bufferedInputStream.read(bytes, 0, bytes.length);
            while (read != -1) {
                if (index + read > result.length) {
                    byte[] copy = new byte[Math.max(index + read * 2, result.length * 2)];
                    System.arraycopy(result, 0, copy, 0, index);
                    result = copy;
                }
                System.arraycopy(bytes, 0, result, index, read);
                index += read;
                read = bufferedInputStream.read(bytes, 0, bytes.length);
            }

            request.setContent(new String(result, request.getCharset()));
        }

        servlet.handleRequest(request, response);

        final Headers responseHeaders = exchange.getResponseHeaders();
        for (Map.Entry<String, String> header : response.getHeaders()) {
            responseHeaders.add(header.getKey(), header.getValue());
        }
        exchange.sendResponseHeaders(response.getStatusCode(), response.getContentLength());
        try (OutputStream outputStream = exchange.getResponseBody()) {
            outputStream.write(response.getContentBuilder().toByteArray());
        }
    }

}
