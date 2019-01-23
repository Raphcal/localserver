package com.github.raphcal.localserver;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Représente une requête HTTP.
 *
 * @author Raphaël Calabro <ddaeke-github at yahoo.fr>
 */
public class HttpRequest extends AbstractHttpMessage {

    private String method;
    private String target;
    private HashMap<String, String> parameterMap;

    /**
     * Réinitialise la requête avec des valeurs par défaut :<ul>
     * <li>Méthode GET</li>
     * <li>Chemin '/'</li>
     * <li>HTTP version 1.1</li>
     * <li>Requête unique (Connection: close)</li>
     * <li>Encodage ASCII</li>
     * <li>Contenu en texte (MIME : text/plain)</li>
     * </ul>
     */
    public void configureDefaults() {
        method = HttpConstants.METHOD_GET;
        target = "/";
        setVersion(HttpConstants.VERSION_1_1);
        setContentType("text/plain");
        setCharset(Charset.forName("ASCII"));

        clearHeaders();
        setHeader(HttpConstants.HEADER_CONNECTION, "close");
    }

    /**
     * Défini l'adresse à interroger.
     *
     * @param target L'adresse à interroger.
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * Récupère l'adresse interrogée.
     *
     * @return L'adresse interrogée.
     */
    public String getTarget() {
        return target;
    }

    /**
     * Défini la méthode utilisée par la requête (GET, POST, etc.).
     *
     * @param method La méthode à utiliser.
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * Récupère la méthode utilisée par la requête (GET, POST, etc.).
     *
     * @return La méthode utilisée.
     */
    public String getMethod() {
        return method;
    }

    /**
     * Récupère la ligne d'en-tête de la requête.
     *
     * @return la ligne d'en-tête.
     */
    @Override
    protected String getFirstLine() {
        return method + ' ' + target + ' ' + getVersion();
    }

    /**
     * Récupère la valeur d'un paramètre.
     *
     * @param name Nom du paramètre à récupérer.
     * @return Valeur associée au paramètre ou <code>null</code> s'il n'est pas
     * déclaré.
     */
    public String getParameter(String name) {
        String value = null;

        if (HttpConstants.METHOD_POST.equalsIgnoreCase(method)) {

            if (parameterMap == null) {
                buildParameterMap();
            }

            value = parameterMap.get(name);
        }

        return value;
    }

    /**
     * Récupère tous les paramètres presents dans la requête.
     *
     * @return L'ensemble des paramètres.
     */
    public Set<Map.Entry<String, String>> getParameters() {

        if (parameterMap == null) {
            buildParameterMap();
        }

        if (parameterMap != null) {
            return parameterMap.entrySet();
        } else {
            return null;
        }
    }

    /**
     * Demande la construction de la table des paramètres.
     */
    private void buildParameterMap() {

        final HashMap<String, String> map = new HashMap<String, String>();

        if (HttpConstants.CONTENT_TYPE_FORM.equals(getContentType())) {
            parseURLEncodedForm(map);
        } else if (HttpConstants.CONTENT_TYPE_MULTIPART.equals(getContentType())) {
            parseMultipartForm(map);
        }

        parameterMap = map;
    }

    /**
     * Parse le contenu d'une requête au format Multipart.
     *
     * @param map Table contenant le résultat.
     */
    private void parseMultipartForm(HashMap<String, String> map) {

        // Recherche du délimiteur
        final String boundary = getFormBoundary();
        if (boundary == null) {
            throw new IllegalStateException("Pas de délimiteur.");
        }

        // Récupération du contenu
        final String content = getContent();
        final char[] characters = content.toCharArray();
        boolean done = false;

        int index = 0;

        while (!done) {

            if (characters[index] == '-' && characters[index + 1] == '-') {
                // Début d'un délimiteur
                index += 2;

                if (content.startsWith(boundary, index)) {
                    // Il s'agit d'un délimiteur
                    index += boundary.length();

                    // Vérifie s'il s'agit du délimiteur final
                    if (!(characters[index] == '-' && characters[index + 1] == '-')) {

                        // Lecture des données d'en-tête
                        final HashMap<String, String> fieldHeaders = new HashMap<String, String>();
                        boolean headers = true;

                        // Buffer
                        final StringBuilder stringBuilder = new StringBuilder();
                        String currentHeader = null;

                        // Etat
                        boolean parsingHeaderName = true;
                        boolean parsing = false;

                        // Nombre de retours à la ligne (permet de déterminer la
                        // fin des en-têtes).
                        int newLineCount = 0;

                        while (headers) {
                            // Lecture des en-têtes

                            if (index < characters.length) {
                                final char c = characters[index];

                                if (parsingHeaderName) {
                                    // Lecture du nom de l'en-tête
                                    if (Character.isWhitespace(c) || c == ':') {
                                        if (parsing && c == ':') {
                                            currentHeader = stringBuilder.toString();
                                            parsingHeaderName = false;

                                            stringBuilder.setLength(0);
                                            parsing = false;
                                        }

                                    } else {
                                        // Fin de l'en-tête
                                        stringBuilder.append(c);
                                        parsing = true;
                                    }

                                } else {
                                    // Lecture de la valeur
                                    if (c == '\r' || c == '\n') {
                                        if (parsing) {
                                            fieldHeaders.put(currentHeader, stringBuilder.toString());
                                            parsingHeaderName = true;

                                            currentHeader = null;
                                            stringBuilder.setLength(0);
                                            parsing = false;
                                        }

                                    } else if (parsing || c != ' ') {
                                        // Valeur de l'en-tête
                                        stringBuilder.append(c);
                                        parsing = true;
                                    }
                                }

                                // Comptage des sauts de lignes
                                if (c == '\n') {
                                    newLineCount++;
                                } else if (c != '\r') {
                                    newLineCount = 0;
                                }

                                if (newLineCount >= 2) {
                                    headers = false;
                                }

                                index++;

                            } else {
                                headers = false;
                            }
                        }

                        // Récupération du contenu
                        final int contentEnd = content.indexOf("--" + boundary, index);
                        final String value = content.substring(index, contentEnd).trim();

                        final String contentDisposition = fieldHeaders.get(HttpConstants.HEADER_CONTENT_DISPOSITION);
                        if (contentDisposition != null) {
                            // Ajout de la valeur
                            final Map<String, String> headerValueMap = parseHeaderValue(contentDisposition);
                            map.put(headerValueMap.get("name"), value);
                        }

                        index = contentEnd;

                    } else {
                        done = true;
                    }

                } else {
                    done = true;
                }

            } else {
                done = true;
            }
        }
    }

    /**
     * Parse le contenu d'une requête au format URLEncoded.
     *
     * @param map Table contenant le résultat.
     */
    private void parseURLEncodedForm(HashMap<String, String> map) {

        final String content = getContent();
        final String charset = "UTF-8";

        final String[] items = content.split("&");
        for (final String item : items) {
            final String[] parts = item.split("=");
            try {
                map.put(URLDecoder.decode(parts[0], charset), URLDecoder.decode(parts[1], charset));

            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException(ex);
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder request = new StringBuilder();

        request.append(getFirstLine()).append("\r\n");
        for (Map.Entry<String, String> header : getHeaders()) {
            request.append(header.getKey()).append(": ").append(header.getValue()).append("\r\n");
        }

        request.append("\r\n").append(getContent());

        return request.toString();
    }

    /**
     * Récupère un flux permettant de lire le contenu de la requête.
     *
     * @return Un flux permettant de lire le contenu de la requête.
     */
    public InputStream getInputStream() {

        final byte[] bytes = getContentBuilder().toByteArray();
        return new ByteArrayInputStream(bytes);
    }

    /**
     * Récupère un flux permettant de lire le contenu de la requête. Les données
     * écrites sont décodées avec l'encodage courant.
     *
     * @return Un flux permettant de lire le contenu de la requête.
     * @see #getInputStream()
     * @see #getCharset()
     */
    public Reader getReader() {

        return new InputStreamReader(getInputStream(), getCharset());
    }
}
