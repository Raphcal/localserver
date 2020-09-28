package com.github.raphcal.localserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Champs communs aux requêtes et aux réponses HTTP.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
abstract class AbstractHttpMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractHttpMessage.class);

    protected static final String HEADER_MAIN_VALUE = "%%value";

    private String version;
    private final HashMap<String, String> headers = new HashMap<String, String>();
    private Charset charset;
    private String contentType;
    private String formBoundary;
    private final ByteArrayOutputStream contentBuilder = new ByteArrayOutputStream();

    /**
     * Défini la version du protocole HTTP à utiliser.
     *
     * @param version La version du protocole HTTP à utiliser.
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Récupère la version du protocole HTTP à utiliser.
     *
     * @return La version du protocole HTTP à utiliser.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Défini ou modifie l'en-tête donné.
     *
     * @param header En-tête HTTP.
     * @param value Valeur a attribuer.
     */
    public void setHeader(String header, String value) {
        headers.put(header, value);

        if (value != null && HttpConstants.HEADER_CONTENT_TYPE.equalsIgnoreCase(header)) {

            final Map<String, String> contentTypeMap = parseHeaderValue(value);
            contentType = contentTypeMap.get(HEADER_MAIN_VALUE);

            final String charsetName = contentTypeMap.get("charset");
            if (charsetName != null) {
                charset = Charset.forName(charsetName);
            }

            formBoundary = contentTypeMap.get("boundary");
        }
    }

    protected Map<String, String> parseHeaderValue(String value) {
        if (value == null) {
            return null;
        }

        final HashMap<String, String> values = new HashMap<String, String>();

        int semicolonIndex = value.indexOf(';');

        if (semicolonIndex > -1) {
            values.put(HEADER_MAIN_VALUE, value.substring(0, semicolonIndex).trim());

            String args = value;
            while (semicolonIndex > -1) {
                args = args.substring(semicolonIndex + 1).trim();
                semicolonIndex = args.indexOf(';');

                final String argumentName;
                String argumentValue;

                final int start = args.indexOf('=');
                final int end = semicolonIndex == -1 ? args.length() : semicolonIndex;

                if (start > -1) {
                    argumentName = args.substring(0, start).trim();
                    argumentValue = args.substring(start + 1, end).trim();

                    if (argumentValue.charAt(0) == '"' || argumentValue.charAt(0) == '\'') {
                        argumentValue = argumentValue.substring(1, argumentValue.length() - 1);
                    }

                } else {
                    argumentName = args.substring(0, end).trim();
                    argumentValue = null;
                }

                values.put(argumentName, argumentValue);
            }

        } else {
            values.put(HEADER_MAIN_VALUE, value);
        }

        return values;
    }

    /**
     * Récupère la valeur de l'en-tête donné.
     *
     * @param header Nom de l'en-tête à récupérer.
     * @return Valeur de l'en-tête ou <code>null</code> s'il n'est pas défini.
     */
    public String getHeader(String header) {
        return headers.get(header);
    }

    /**
     * Récupère l'ensemble des en-têtes définis.
     *
     * @return L'ensemble des en-têtes définis.
     */
    public Set<Map.Entry<String, String>> getHeaders() {
        return headers.entrySet();
    }

    /**
     * Supprime l'en-tête donné.
     *
     * @param header En-tête à supprimer.
     */
    public void removeHeader(String header) {
        headers.remove(header);
    }

    /**
     * Supprime tous les en-têtes.
     */
    public void clearHeaders() {
        headers.clear();
    }

    /**
     * Défini le contenu en tant que String. La chaîne sera convertie en octets
     * à l'aide du charset défini.
     *
     * @param content Contenu.
     * @see #setCharset(java.nio.charset.Charset)
     */
    public void setContent(String content) {
        setContent(content, true);
    }

    /**
     * Défini le contenu en tant que String. La chaîne sera convertie en octets
     * à l'aide du charset défini.
     *
     * @param content Contenu.
     * @param refresh <code>true</code> pour mettre à jour les en-têtes
     * Content-Type et Content-Length.
     * @see #setCharset(java.nio.charset.Charset)
     */
    public void setContent(String content, boolean refresh) {
        contentBuilder.reset();
        appendContent(content, refresh);
    }

    /**
     * Ajoute la chaîne donnée au contenu. La chaîne sera convertie en octets à
     * l'aide du charset défini.
     *
     * @param content Contenu.
     * @param refresh <code>true</code> pour mettre à jour les en-têtes
     * Content-Type et Content-Length.
     */
    public void appendContent(String content, boolean refresh) {
        appendContent(content.getBytes(getCharset()), refresh);
    }

    /**
     * Ajoute les octets donnés au contenu.
     *
     * @param bytes Contenu.
     * @param refresh <code>true</code> pour mettre à jour les en-têtes
     * Content-Type et Content-Length.
     */
    public void appendContent(byte[] bytes, boolean refresh) {
        contentBuilder.write(bytes, 0, bytes.length);

        if (refresh) {
            headers.put(HttpConstants.HEADER_CONTENT_LENGTH, Integer.toString(contentBuilder.size()));
            refreshContentType();
        }
    }

    /**
     * Récupère la valeur de l'en-tête "Content-Length".
     *
     * @return La valeur en <code>int</code> de l'en-tête "Content-Length".
     */
    public int getContentLength() {
        int length = 0;

        final String value = headers.get(HttpConstants.HEADER_CONTENT_LENGTH);
        if (value != null) {
            length = Integer.parseInt(value);
        }

        return length;
    }

    /**
     * Récupère le contenu sous forme de String. Les octets sont convertis en
     * chaîne grâce au charset courant.
     *
     * @return Le contenu sous forme de String.
     * @see #getCharset()
     */
    public String getContent() {
        final Charset charset = getCharset();
        String content = null;
        try {
            content = contentBuilder.toString(charset.displayName());

        } catch (UnsupportedEncodingException ex) {
            LOGGER.error("Encodage non supporté : " + charset.displayName(), ex);
        }

        return content;
    }

    /**
     * Récupère l'objet permettant de construire le contenu sous forme d'octets.
     * Préférez les objets spécifiques définis par HttpRequest et HttpResponse.
     *
     * @return L'objet permettant de construire le contenu sous forme d'octets.
     */
    protected ByteArrayOutputStream getContentBuilder() {
        return contentBuilder;
    }

    /**
     * Défini le type MIME du contenu.
     *
     * @param contentType Type MIME du contenu.
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
        refreshContentType();
    }

    /**
     * Récupère le type MIME du contenu.
     *
     * @return Le type MIME du contenu.
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Défini l'encodage à utiliser.
     *
     * @param charset L'encodage du contenu à utiliser.
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
        refreshContentType();
    }

    /**
     * Récupère l'encodage du contenu.
     *
     * @return L'encodage du contenu.
     */
    public Charset getCharset() {
        return charset != null ? charset : Charset.defaultCharset();
    }

    /**
     * Récupère la chaîne utilisée pour séparer les valeurs du formulaire.
     *
     * @return La chaîne séparant les valeurs du formulaire ou <code>null</code>
     * si non défini.
     */
    public String getFormBoundary() {
        return formBoundary;
    }

    protected void refreshContentType() {
        String type = this.contentType;
        if (charset != null) {
            type += "; charset=" + charset.displayName();
        }

        headers.put(HttpConstants.HEADER_CONTENT_TYPE, type);
    }

    protected abstract String getFirstLine();

    /**
     * Écrit l'en-tête HTTP sur le flux donné.
     *
     * @param outputStream Flux où écrire les données.
     * @throws IOException En cas d'erreur pendant l'écriture.
     */
    public void writeHeader(final OutputStream outputStream) throws IOException {
        writeStringInAscii(outputStream, getFirstLine());
        outputStream.write((int) '\r');
        outputStream.write((int) '\n');

        for (final Map.Entry<String, String> header : getHeaders()) {
            writeStringInAscii(outputStream, header.getKey());
            outputStream.write((int) ':');
            outputStream.write((int) ' ');
            writeStringInAscii(outputStream, header.getValue());
            outputStream.write((int) '\r');
            outputStream.write((int) '\n');
        }

        outputStream.write((int) '\r');
        outputStream.write((int) '\n');
    }

    /**
     * Récupère la requête complète sous forme d'un tableau d'octets.
     *
     * @return La requête complète sous forme d'un tableau d'octets.
     */
    public byte[] toByteArray() {
        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        writeStringInAscii(outputStream, getFirstLine());
        outputStream.write((int) '\r');
        outputStream.write((int) '\n');

        for (final Map.Entry<String, String> header : getHeaders()) {
            writeStringInAscii(outputStream, header.getKey());
            outputStream.write((int) ':');
            outputStream.write((int) ' ');
            writeStringInAscii(outputStream, header.getValue());
            outputStream.write((int) '\r');
            outputStream.write((int) '\n');
        }

        outputStream.write((int) '\r');
        outputStream.write((int) '\n');

        final byte[] content = contentBuilder.toByteArray();
        outputStream.write(content, 0, content.length);

        return outputStream.toByteArray();
    }

    private void writeStringInAscii(OutputStream outputStream, String string) {
        try {
            final byte[] bytes = string.getBytes("ASCII");

            outputStream.write(bytes, 0, bytes.length);

        } catch (UnsupportedEncodingException ex) {
            // Ignoré.
        } catch (IOException ex) {
            // Ignoré.
        }
    }
}
