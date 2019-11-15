package com.github.raphcal.localserver;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;

/**
 * Classe utilitaire pour simplifier l'écriture d'un servlet HTTP utilisable
 * avec LocalServer.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public abstract class HttpServlet implements HttpRequestHandler {

    /**
     * {@inheritDoc}
     */
    @Override
    public final void handleRequest(final HttpRequest request, final HttpResponse response) {
        final Method method = Method.valueOf(request.getMethod());

        try {
            switch (method) {
                case GET:
                    doGet(request, response);
                    break;
                case POST:
                    doPost(request, response);
                    break;
                case HEAD:
                    doHead(request, response);
                    break;
                case OPTIONS:
                    doOptions(request, response);
                    break;
                case PUT:
                    doPut(request, response);
                    break;
                case TRACE:
                    doTrace(request, response);
                    break;
                case DELETE:
                    doDelete(request, response);
                    break;
                default:
                    response.setStatusCode(400);
                    response.setStatusMessage("BAD REQUEST");
                    break;
            }
        } catch (final Exception e) {
            response.setStatusCode(500);
            response.setStatusMessage("INTERNAL SERVER ERROR");

            final Charset charset = Charset.forName("UTF-8");
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            final PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(outputStream, charset));
            e.printStackTrace(printWriter);
            response.setContent(new String(outputStream.toByteArray(), charset));
        }
    }

    /**
     * Gère les appels à la méthode GET.
     *
     * @param request Requête GET HTTP.
     * @param response Réponse HTTP.
     * @throws java.lang.Exception En cas d'erreur pendant le traitement de la requête.
     */
    public void doGet(HttpRequest request, HttpResponse response) throws Exception {
        // Pas d'action.
    }

    /**
     * Gère les appels à la méthode POST.
     *
     * @param request Requête POST HTTP.
     * @param response Réponse HTTP.
     * @throws java.lang.Exception En cas d'erreur pendant le traitement de la requête.
     */
    public void doPost(HttpRequest request, HttpResponse response) throws Exception {
        // Pas d'action.
    }

    /**
     * Gère les appels à la méthode HEAD.
     *
     * @param request Requête HEAD HTTP.
     * @param response Réponse HTTP.
     * @throws java.lang.Exception En cas d'erreur pendant le traitement de la requête.
     */
    public void doHead(HttpRequest request, HttpResponse response) throws Exception {
        // Pas d'action.
    }

    /**
     * Gère les appels à la méthode OPTIONS.
     *
     * @param request Requête OPTIONS HTTP.
     * @param response Réponse HTTP.
     * @throws java.lang.Exception En cas d'erreur pendant le traitement de la requête.
     */
    public void doOptions(HttpRequest request, HttpResponse response) throws Exception {
        // Pas d'action.
    }

    /**
     * Gère les appels à la méthode PUT.
     *
     * @param request Requête PUT HTTP.
     * @param response Réponse HTTP.
     * @throws java.lang.Exception En cas d'erreur pendant le traitement de la requête.
     */
    public void doPut(HttpRequest request, HttpResponse response) throws Exception {
        // Pas d'action.
    }

    /**
     * Gère les appels à la méthode TRACE.
     *
     * @param request Requête TRACE HTTP.
     * @param response Réponse HTTP.
     * @throws java.lang.Exception En cas d'erreur pendant le traitement de la requête.
     */
    public void doTrace(HttpRequest request, HttpResponse response) throws Exception {
        // Pas d'action.
    }

    /**
     * Gère les appels à la méthode DELETE.
     *
     * @param request Requête DELETE HTTP.
     * @param response Réponse HTTP.
     * @throws java.lang.Exception En cas d'erreur pendant le traitement de la requête.
     */
    public void doDelete(HttpRequest request, HttpResponse response) throws Exception {
        // Pas d'action.
    }

    private static enum Method {
        GET, POST, HEAD, OPTIONS, PUT, TRACE, DELETE;
    }
}
