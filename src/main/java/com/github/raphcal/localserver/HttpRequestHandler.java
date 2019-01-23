package com.github.raphcal.localserver;

/**
 * Gestionnaire de base pour les requêtes HTTP.
 *
 * @author Raphaël Calabro <ddaeke-github at yahoo.fr>
 */
public interface HttpRequestHandler {

    /**
     * Gère la requête donnée, la réponse doit-être spécifiée dans l'objet
     * <code>response</code>.
     *
     * @param request Requête HTTP reçue.
     * @param response Réponse HTTP à renvoyer.
     */
    void handleRequest(HttpRequest request, HttpResponse response);
}
