package com.github.raphcal.localserver;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Représente une réponse HTTP.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class HttpResponse extends AbstractHttpMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpResponse.class);

    private static final int HEX_TEN = 0x10;
    private static final HashMap<Byte, Integer> HEX_MAP;

    private static final int HEADER_PART_COUNT = 3;

    static {
        final HashMap<Byte, Integer> map = new HashMap<Byte, Integer>();
        for (int value = 0; value < HEX_TEN; value++) {
            final String hexString = Integer.toHexString(value);

            if (hexString.length() == 1) {
                final char c = hexString.charAt(0);
                final char upper = Character.toUpperCase(c);

                map.put((byte) c, value);

                if (upper != c) {
                    map.put((byte) upper, value);
                }
            }
        }

        HEX_MAP = map;
    }

    private int statusCode = HttpConstants.STATUS_CODE_200_OK;
    private String statusMessage = HttpConstants.STATUS_MESSAGE_200_OK;

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);

    /**
     * La réponse est initialisée avec les propriétés suivantes :
     * <ul>
     * <li>Version HTTP 1.1</li>
     * <li>Etat 200 OK</li>
     * <li>En-tête "Date" à l'heure actuelle</li>
     * <li>En-tête "Connection" à la valeur "close"</li>
     * </ul>
     */
    public void configureDefaults() {
        setVersion(HttpConstants.VERSION_1_1);

        clearHeaders();
        setContentType("text/html");
        setHeader(HttpConstants.HEADER_CONNECTION, "close");
        setHeader(HttpConstants.HEADER_DATE, dateFormat.format(new Date()));
    }

    /**
     * Créé une nouvelle réponse HTTP avec un code d'erreur à "200 OK".
     */
    public HttpResponse() {
    }

    /**
     * Initialise un objet HttpResponse à partir d'une réponse d'un serveur.<br>
     * <br>
     * L'en-tête "Transfert-Encoding: chunked" est géré de façon basique.
     *
     * @param response Réponse HTTP.
     */
    public HttpResponse(byte[] response) {
        final StringBuilder tokenBuilder = new StringBuilder();

        int offset = nextLine(response, 0, tokenBuilder);
        String[] state = tokenBuilder.toString().split(" ", HEADER_PART_COUNT);

        // Version du protocole utilisé par la réponse
        setVersion(state[0]);

        // Code d'erreur
        statusCode = Integer.parseInt(state[1]);
        // Message d'erreur
        statusMessage = state[2];

        boolean contentReached = false;
        while (offset < response.length && !contentReached) {
            offset = nextLine(response, offset + 2, tokenBuilder); // +2 = saut du CRLF
            String[] header = tokenBuilder.toString().split("\\:", 2);

            if (header.length == 2) {
                setHeader(header[0].trim(), header[1].trim());
            } else {
                contentReached = true;
            }
        }
        offset += 2; // Positionnement au début du contenu

        if (HttpConstants.TRANSFERT_ENCODING_CHUNKED.equals(getHeader(HttpConstants.HEADER_TRANSFER_ENCODING))) {
            // Lecture de la réponse sous forme de morceaux ("chunked")

            // Lecture du contenu
            final ByteArrayOutputStream output = new ByteArrayOutputStream();

            while (offset < response.length) {
                // Taille du prochain morceau
                int chunkSize = 0;
                while (response[offset] != (byte) '\r') {

                    final Integer byteValue = HEX_MAP.get(response[offset]);
                    if (byteValue != null) {
                        chunkSize *= HEX_TEN;
                        chunkSize += byteValue;

                    } else if (response[offset] != ' ') {
                        LOGGER.info("ChunkSize, valeur incorrecte : " + (int) response[offset] + " '" + (char) response[offset] + "', current size : " + chunkSize);
                    }

                    offset++;
                }

                offset += 2;
                output.write(response, offset, chunkSize);

                offset += chunkSize + 2; // 2 = CRLF à la fin du chunk
            }

            final byte[] bytes = output.toByteArray();
            getContentBuilder().write(bytes, 0, bytes.length);
        } else // Lecture du reste en tant que contenu
        {
            getContentBuilder().write(response, offset, response.length - offset);
        }
    }

    /**
     * Lit les données de data en tant que texte ASCII jusqu'au prochain saut de
     * ligne.
     *
     * @param data Tableau d'octets contenant du texte ASCII.
     * @param off L'index du premier octet à lire.
     * @param line Le résultat est stocké dans cette variable. Son contenu
     * précédent sera effacé.
     * @return L'index de fin de la ligne lue.
     */
    private int nextLine(byte[] data, int off, StringBuilder line) {
        line.setLength(0);

        while (off < data.length && data[off] != (byte) '\r' && data[off] != '\n') {
            line.append((char) data[off]);
            off++;
        }

        return off;
    }

    /**
     * Récupère le code d'erreur HTTP à renvoyer.
     *
     * @return Le code d'erreur qui sera renvoyé.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * Défini le code d'erreur HTTP à renvoyer (200 par défaut).
     *
     * @param statusCode Le code d'erreur à renvoyer.
     */
    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * Récupère le message d'erreur à renvoyer.
     *
     * @return Le message d'erreur qui sera renvoyé.
     */
    public String getStatusMessage() {
        return statusMessage;
    }

    /**
     * Défini le message d'erreur HTTP à renvoyer ("OK" par défaut).
     *
     * @param statusMessage Le message d'erreur à renvoyer.
     */
    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    /**
     * Récupère la ligne d'en-tête de la réponse.
     *
     * @return La ligne d'en-tête.
     */
    @Override
    protected String getFirstLine() {
        return getVersion() + ' ' + Integer.toString(statusCode) + ' ' + statusMessage;
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
     * Récupère le flux de sortie de la réponse. Un appel à flush() ou à close()
     * valide les données écrites.
     *
     * @return un flux permettant d'écrire la réponse.
     */
    public OutputStream getOutputStream() {
        return new ByteArrayOutputStream() {

            @Override
            public void close() throws IOException {
                appendContent(toByteArray(), true);
            }

            @Override
            public void flush() throws IOException {
                appendContent(toByteArray(), true);
                reset();
            }

        };
    }

    /**
     * Récupère le flux de sortie de la réponse. Un appel à flush() ou à close()
     * valide les données écrites.
     *
     * Les données écrites sont encodées avec l'encodage courant.
     *
     * @return un flux permettant d'écrire la réponse.
     * @see #getOutputStream()
     * @see #getCharset()
     */
    public Writer getWriter() {
        return new OutputStreamWriter(getOutputStream(), getCharset());
    }
}
