package com.github.raphcal.localserver;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;

/**
 * Gère la construction de requêtes HTTP à partir d'une connexion du serveur
 * local.
 *
 * @author Raphaël Calabro <ddaeke-github at yahoo.fr>
 */
class HttpRequestBuilder {

    private static enum State {
        METHOD,
        TARGET,
        VERSION,
        HEADER_NAME,
        HEADER_VALUE,
        BODY,
        END;
    }

    private State state = State.METHOD;
    private boolean parsing = false;

    private HttpRequest request = new HttpRequest();

    private String currentHeader;
    private StringBuilder stringBuilder = new StringBuilder();
    private ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    private long length;

    private int newLineCount;

    /**
     * Construit la requête à partir des données du buffer. Il est possible
     * d'appeler plusieurs fois cette méthode successivement pour construire la
     * requête.
     *
     * @param buffer Données à lire.
     * @throws UnsupportedEncodingException Si l'encodage du message est
     * inconnu.
     */
    public void feedBytes(ByteBuffer buffer) throws UnsupportedEncodingException {

        while (buffer.hasRemaining() && state != State.END) {
            // Il reste des données dans le buffer.

            switch (state) {
                case METHOD:
                case TARGET:
                case VERSION: {
                    // Lecture de l'en-tête HTTP

                    final char c = (char) buffer.get();

                    if (Character.isWhitespace(c)) {
                        if (parsing) {
                            // Fin du champ actuel
                            if (state == State.METHOD) {
                                request.setMethod(stringBuilder.toString());
                                state = State.TARGET;

                            } else if (state == State.TARGET) {
                                request.setTarget(stringBuilder.toString());
                                state = State.VERSION;

                            } else if (state == State.VERSION) {
                                request.setVersion(stringBuilder.toString());
                                state = State.HEADER_NAME;
                            }

                            // Remise à zéro des caches.
                            stringBuilder.setLength(0);
                            parsing = false;
                        }

                    } else {
                        // Nom
                        stringBuilder.append(c);
                        parsing = true;
                    }

                    // Comptage des sauts de ligne.
                    countNewLines(c);

                }
                break;

                case HEADER_NAME: {
                    // Lecture du nom d'un en-tête

                    final char c = (char) buffer.get();

                    if (Character.isWhitespace(c) || c == ':') {
                        if (parsing && c == ':') {
                            // Fin du nom de l'en-tête
                            currentHeader = stringBuilder.toString();
                            state = State.HEADER_VALUE;

                            stringBuilder.setLength(0);
                            parsing = false;
                        }

                    } else {
                        // Nom de l'en-tête
                        stringBuilder.append(c);
                        parsing = true;
                    }

                    // Comptage des sauts de ligne.
                    countNewLines(c);

                    if (newLineCount >= 2) // Fin des en-têtes
                    {
                        state = State.BODY;
                    }

                }
                break;

                case HEADER_VALUE: {
                    // Lecture de la valeur d'un en-tête

                    final char c = (char) buffer.get();

                    if (c == '\r' || c == '\n') {
                        if (parsing) {
                            // Fin de la valeur
                            request.setHeader(currentHeader, stringBuilder.toString());
                            state = State.HEADER_NAME;

                            currentHeader = null;
                            stringBuilder.setLength(0);
                            parsing = false;
                        }

                    } else if (parsing || c != ' ') {
                        // Valeur de l'en-tête
                        stringBuilder.append(c);
                        parsing = true;
                    }

                    // Comptage des sauts de ligne.
                    countNewLines(c);

                }
                break;

                case BODY:
                    // Lecture du corps du message

                    byteStream.write(buffer.get());
                    length++;
                    break;

                default:
                    break;
            }
        }

        // Vérification du nombre d'octets lu pour déterminer si la requête
        // est terminée.
        if (state == State.BODY && length >= request.getContentLength()) {
            final String charsetName = request.getCharset().displayName();
            request.setContent(byteStream.toString(charsetName), false);

            state = State.END;
        }
    }

    private void countNewLines(final char c) {

        if (c == '\n') {
            newLineCount++;
        } else if (c != '\r') {
            newLineCount = 0;
        }
    }

    /**
     * Récupère la requête HTTP construite.
     *
     * @return La requête HTTP construite.
     */
    public HttpRequest getRequest() {
        return request;
    }

    /**
     * Indique si la requête est prête.
     *
     * @return <code>true</code> si la requête HTTP a été lue en entier,
     * <code>false</code> sinon.
     */
    public boolean isReady() {
        return state == State.END;
    }
}
