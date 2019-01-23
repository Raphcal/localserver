package com.github.raphcal.localserver;

/**
 * Liste des constantes du protocole HTTP.
 *
 * @author Raphaël Calabro <ddaeke-github at yahoo.fr>
 */
public final class HttpConstants {

    private HttpConstants() {
    }

    public static final String VERSION_1_1 = "HTTP/1.1";
    public static final String VERSION_1_0 = "HTTP/1.0";

    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PUT = "PUT";
    public static final String METHOD_HEAD = "HEAD";
    public static final String METHOD_DELETE = "DELETE";
    public static final String METHOD_OPTIONS = "OPTIONS";
    public static final String METHOD_TRACE = "TRACE";
    public static final String METHOD_CONNECT = "CONNECT";

    public static final String HEADER_ACCEPT = "Accept";
    /**
     * The Accept-Charset request-header field can be used to indicate what
     * character sets are acceptable for the response. This field allows clients
     * capable of understanding more comprehensive or special-purpose character
     * sets to signal that capability to a server which is capable of
     * representing documents in those character sets.<br>
     *
     * <pre>    Accept-Charset = "Accept-Charset" ":"
     *         1#( ( charset | "*" )[ ";" "q" "=" qvalue ] )</pre>
     *
     * Character set values are described in section
     * <a href="http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html#sec3.4">3.4</a>.<br>
     * Each charset MAY be given an associated quality value which represents
     * the user's preference for that charset. The default value is q=1.<br>
     * An example is
     *
     * <pre>    Accept-Charset: iso-8859-5, unicode-1-1;q=0.8</pre>
     *
     * The special value "*", if present in the Accept-Charset field, matches
     * every character set (including ISO-8859-1) which is not mentioned
     * elsewhere in the Accept-Charset field. If no "*" is present in an
     * Accept-Charset field, then all character sets not explicitly mentioned
     * get a quality value of 0, except for ISO-8859-1, which gets a quality
     * value of 1 if not explicitly mentioned.<br>
     * <br>
     * If no Accept-Charset header is present, the default is that any character
     * set is acceptable. If an Accept-Charset header is present, and if the
     * server cannot send a response which is acceptable according to the
     * Accept-Charset header, then the server SHOULD send an error response with
     * the 406 (not acceptable) status code, though the sending of an
     * unacceptable response is also allowed.<br>
     *
     * @see #HEADER_CONTENT_TYPE
     */
    public static final String HEADER_ACCEPT_CHARSET = "Accept-Charset";
    public static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    public static final String HEADER_ACCEPT_LANGUAGE = "Accept-Language";
    /**
     * @see #HEADER_RANGE
     * @see #HEADER_CONTENT_RANGE
     * @see #HEADER_IF_RANGE
     */
    public static final String HEADER_ACCEPT_RANGES = "Accept-Ranges";
    public static final String HEADER_AGE = "Age";
    /**
     * The Allow entity-header field lists the set of methods supported by the
     * resource identified by the Request-URI. The purpose of this field is
     * strictly to inform the recipient of valid methods associated with the
     * resource. An Allow header field MUST be present in a 405 (Method Not
     * Allowed) response.<br>
     * <pre>    Allow   = "Allow" ":" #Method</pre> Example of use:<br>
     * <pre>    Allow: GET, HEAD, PUT</pre> This field cannot prevent a client from
     * trying other methods. However, the indications given by the Allow header
     * field value SHOULD be followed. The actual set of allowed methods is
     * defined by the origin server at the time of each request.<br>
     * <br>
     * The Allow header field MAY be provided with a PUT request to recommend
     * the methods to be supported by the new or modified resource. The server
     * is not required to support these methods and SHOULD include an Allow
     * header in the response giving the actual supported methods.<br>
     * <br>
     * A proxy MUST NOT modify the Allow header field even if it does not
     * understand all the methods specified, since the user agent might have
     * other means of communicating with the origin server.<br>
     */
    public static final String HEADER_ALLOW = "Allow";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_CONNECTION = "Connection";
    /**
     * The Content-Disposition response-header field has been proposed as a
     * means for the origin server to suggest a default filename if the user
     * requests that the content is saved to a file. This usage is derived from
     * the definition of Content-Disposition in RFC 1806.
     * <pre>
     *  content-disposition = "Content-Disposition" ":"
     *                        disposition-type *( ";" disposition-parm )
     *  disposition-type = "attachment" | disp-extension-token
     *  disposition-parm = filename-parm | disp-extension-parm
     *  filename-parm = "filename" "=" quoted-string
     *  disp-extension-token = token
     *  disp-extension-parm = token "=" ( token | quoted-string )
     * </pre> An example is
     * <pre>
     *  Content-Disposition: attachment; filename="fname.ext"
     * </pre> The receiving user agent SHOULD NOT respect any directory path
     * information present in the filename-parm parameter, which is the only
     * parameter believed to apply to HTTP implementations at this time. The
     * filename SHOULD be treated as a terminal component only.<br>
     * <br>
     * If this header is used in a response with the application/octet- stream
     * content-type, the implied suggestion is that the user agent should not
     * display the response, but directly enter a `save response as...' dialog.
     */
    public static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";
    public static final String HEADER_CONTENT_ENCODING = "Content-Encoding";
    public static final String HEADER_CONTENT_LANGUAGE = "Content-Language";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_LOCATION = "Content-Location";
    public static final String HEADER_CONTENT_MD5 = "Content-MD5";
    public static final String HEADER_CONTENT_RANGE = "Content-Range";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_COOKIE = "Cookie";
    public static final String HEADER_DATE = "Date";
    public static final String HEADER_ETAG = "ETag";
    public static final String HEADER_EXPECT = "Expect";
    public static final String HEADER_EXPIRES = "Expires";
    public static final String HEADER_FROM = "From";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_IF_MATCH = "If-Match";
    public static final String HEADER_IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String HEADER_IF_NONE_MATCH = "If-None-Match";
    public static final String HEADER_IF_RANGE = "If-Range";
    public static final String HEADER_IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    public static final String HEADER_LAST_MODIFIED = "Last-Modified";
    public static final String HEADER_LOCATION = "Location";
    public static final String HEADER_FORWARDS = "Max-Forwards";
    public static final String HEADER_PRAGMA = "Pragma";
    public static final String HEADER_PROXY_AUTHENTICATE = "Proxy-Authenticate";
    public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String HEADER_RANGE = "Range";
    public static final String HEADER_REFERER = "Referer";
    public static final String HEADER_RETRY_AFTER = "Retry-After";
    public static final String HEADER_SERVER = "Server";
    public static final String HEADER_SET_COOKIE = "Set-Cookie";
    public static final String HEADER_TE = "TE";
    public static final String HEADER_TRAILER = "Trailer";
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String HEADER_UPGRADE = "Upgrade";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_VARY = "Vary";
    public static final String HEADER_VIA = "Via";
    public static final String HEADER_WARNING = "Warning";
    public static final String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";

    public static final String CONTENT_TYPE_FORM = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
    public static final String TRANSFERT_ENCODING_CHUNKED = "chunked";

    public static final String AUTH_BASIC = "Basic";
    public static final String AUTH_NTLM = "NTLM";

    public static final int STATUS_CODE_100_CONTINUE = 100;
    public static final String STATUS_MESSAGE_100_CONTINUE = "CONTINUE";
    public static final int STATUS_CODE_101_SWITCHING_PROTOCOLS = 101;
    public static final String STATUS_MESSAGE_101_SWITCHING_PROTOCOLS = "SWITCHING PROTOCOLS";

    public static final int STATUS_CODE_200_OK = 200;
    public static final String STATUS_MESSAGE_200_OK = "OK";
    public static final int STATUS_CODE_201_CREATED = 201;
    public static final String STATUS_MESSAGE_201_CREATED = "CREATED";
    public static final int STATUS_CODE_202_ACCEPTED = 202;
    public static final String STATUS_MESSAGE_202_ACCEPTED = "ACCEPTED";

    public static final int STATUS_CODE_300_MULTIPLE_CHOICES = 300;
    public static final String STATUS_MESSAGE_300_MULTIPLE_CHOICES = "MULTIPLE CHOICES";
    public static final int STATUS_CODE_301_MOVED_PERMANENTLY = 301;
    public static final String STATUS_MESSAGE_301_MOVED_PERMANENTLY = "MOVED PERMANENTLY";
    public static final int STATUS_CODE_304_NOT_MODIFIED = 304;
    public static final String STATUS_MESSAGE_304_NOT_MODIFIED = "NOT MODIFIED";

    public static final int STATUS_CODE_400_BAD_REQUEST = 400;
    public static final String STATUS_MESSAGE_400_BAD_REQUEST = "BAD REQUEST";
    public static final int STATUS_CODE_401_UNAUTHORIZED = 401;
    public static final String STATUS_MESSAGE_401_UNAUTHORIZED = "UNAUTHORIZED";
    public static final int STATUS_CODE_403_FORBIDDEN = 403;
    public static final String STATUS_MESSAGE_403_FORBIDDEN = "FORBIDDEN";
    public static final int STATUS_CODE_404_NOT_FOUND = 404;
    public static final String STATUS_MESSAGE_404_NOT_FOUND = "NOT FOUND";
    public static final int STATUS_CODE_407_PROXY_AUTHENTICATION_REQUIRED = 407;
    public static final String STATUS_MESSAGE_407_PROXY_AUTHENTICATION_REQUIRED = "PROXY AUTHENTICATION REQUIRED";
    public static final int STATUS_CODE_410_GONE = 410;
    public static final String STATUS_MESSAGE_410_GONE = "GONE";

    public static final int STATUS_CODE_500_INTERNAL_SERVER_ERROR = 500;
    public static final String STATUS_MESSAGE_500_INTERNAL_SERVER_ERROR = "INTERNAL SERVER ERROR";
}
