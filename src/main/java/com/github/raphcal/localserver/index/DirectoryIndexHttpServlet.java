package com.github.raphcal.localserver.index;

import com.github.raphcal.localserver.HttpResponse;
import com.github.raphcal.localserver.HttpRequest;
import com.github.raphcal.localserver.HttpServlet;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * List the content of a given directory and its children.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class DirectoryIndexHttpServlet extends HttpServlet {

    /**
     * Root directory.
     */
    private final File serverRoot;

    /**
     * MIME types.
     */
    private final Map<String, String> mimeTypes;

    public DirectoryIndexHttpServlet(File serverRoot) throws IOException {
        this.mimeTypes = createMimeTypeMap();
        this.serverRoot = serverRoot.getCanonicalFile();
    }

    private Map<String, String> createMimeTypeMap() {
        final HashMap<String, String> mimeTypes = new HashMap<>();
        // MIME type list from https://msdn.microsoft.com/en-us/library/bb742440.aspx
        mimeTypes.put("323", "text/h323");
        mimeTypes.put("acx", "application/internet-property-stream");
        mimeTypes.put("ai", "application/postscript");
        mimeTypes.put("aif", "audio/x-aiff");
        mimeTypes.put("aifc", "audio/x-aiff");
        mimeTypes.put("aiff", "audio/x-aiff");
        mimeTypes.put("asf", "video/x-ms-asf");
        mimeTypes.put("asr", "video/x-ms-asf");
        mimeTypes.put("asx", "video/x-ms-asf");
        mimeTypes.put("au", "audio/basic");
        mimeTypes.put("avi", "video/x-msvideo");
        mimeTypes.put("axs", "application/olescript");
        mimeTypes.put("bas", "text/plain");
        mimeTypes.put("bcpio", "application/x-bcpio");
        mimeTypes.put("bin", "application/octet-stream");
        mimeTypes.put("bmp", "image/bmp");
        mimeTypes.put("c", "text/plain");
        mimeTypes.put("cat", "application/vnd.ms-pkiseccat");
        mimeTypes.put("cdf", "application/x-cdf");
        mimeTypes.put("cer", "application/x-x509-ca-cert");
        mimeTypes.put("class", "application/octet-stream");
        mimeTypes.put("clp", "application/x-msclip");
        mimeTypes.put("cmx", "image/x-cmx");
        mimeTypes.put("cod", "image/cis-cod");
        mimeTypes.put("cpio", "application/x-cpio");
        mimeTypes.put("crd", "application/x-mscardfile");
        mimeTypes.put("crl", "application/pkix-crl");
        mimeTypes.put("crt", "application/x-x509-ca-cert");
        mimeTypes.put("csh", "application/x-csh");
        mimeTypes.put("css", "text/css");
        mimeTypes.put("dcr", "application/x-director");
        mimeTypes.put("der", "application/x-x509-ca-cert");
        mimeTypes.put("dir", "application/x-director");
        mimeTypes.put("dll", "application/x-msdownload");
        mimeTypes.put("dms", "application/octet-stream");
        mimeTypes.put("doc", "application/msword");
        mimeTypes.put("dot", "application/msword");
        mimeTypes.put("dvi", "application/x-dvi");
        mimeTypes.put("dxr", "application/x-director");
        mimeTypes.put("eps", "application/postscript");
        mimeTypes.put("etx", "text/x-setext");
        mimeTypes.put("evy", "application/envoy");
        mimeTypes.put("exe", "application/octet-stream");
        mimeTypes.put("fif", "application/fractals");
        mimeTypes.put("flr", "x-world/x-vrml");
        mimeTypes.put("gif", "image/gif");
        mimeTypes.put("gtar", "application/x-gtar");
        mimeTypes.put("gz", "application/x-gzip");
        mimeTypes.put("h", "text/plain");
        mimeTypes.put("hdf", "application/x-hdf");
        mimeTypes.put("hlp", "application/winhlp");
        mimeTypes.put("hqx", "application/mac-binhex40");
        mimeTypes.put("hta", "application/hta");
        mimeTypes.put("htc", "text/x-component");
        mimeTypes.put("htm", "text/html");
        mimeTypes.put("html", "text/html");
        mimeTypes.put("htt", "text/webviewhtml");
        mimeTypes.put("ico", "image/x-icon");
        mimeTypes.put("ief", "image/ief");
        mimeTypes.put("iii", "application/x-iphone");
        mimeTypes.put("ins", "application/x-internet-signup");
        mimeTypes.put("isp", "application/x-internet-signup");
        mimeTypes.put("jfif", "image/pipeg");
        mimeTypes.put("jpe", "image/jpeg");
        mimeTypes.put("jpeg", "image/jpeg");
        mimeTypes.put("jpg", "image/jpeg");
        mimeTypes.put("js", "application/x-javascript");
        mimeTypes.put("latex", "application/x-latex");
        mimeTypes.put("lha", "application/octet-stream");
        mimeTypes.put("lsf", "video/x-la-asf");
        mimeTypes.put("lsx", "video/x-la-asf");
        mimeTypes.put("lzh", "application/octet-stream");
        mimeTypes.put("m13", "application/x-msmediaview");
        mimeTypes.put("m14", "application/x-msmediaview");
        mimeTypes.put("m3u", "audio/x-mpegurl");
        mimeTypes.put("man", "application/x-troff-man");
        mimeTypes.put("mdb", "application/x-msaccess");
        mimeTypes.put("me", "application/x-troff-me");
        mimeTypes.put("mht", "message/rfc822");
        mimeTypes.put("mhtml", "message/rfc822");
        mimeTypes.put("mid", "audio/mid");
        mimeTypes.put("mny", "application/x-msmoney");
        mimeTypes.put("mov", "video/quicktime");
        mimeTypes.put("movie", "video/x-sgi-movie");
        mimeTypes.put("mp2", "video/mpeg");
        mimeTypes.put("mp3", "audio/mpeg");
        mimeTypes.put("mpa", "audio/mpeg");
        mimeTypes.put("mpe", "video/mpeg");
        mimeTypes.put("mpeg", "video/mpeg");
        mimeTypes.put("mpg", "video/mpeg");
        mimeTypes.put("mpp", "application/vnd.ms-project");
        mimeTypes.put("mpv2", "video/mpeg");
        mimeTypes.put("ms", "application/x-troff-ms");
        mimeTypes.put("mvb", "application/x-msmediaview");
        mimeTypes.put("nws", "message/rfc822");
        mimeTypes.put("oda", "application/oda");
        mimeTypes.put("p10", "application/pkcs10");
        mimeTypes.put("p12", "application/pkcs12");
        mimeTypes.put("p7b", "application/x-pkcs7-certificates");
        mimeTypes.put("p7c", "application/x-pkcs7-mime");
        mimeTypes.put("p7m", "application/x-pkcs7-mime");
        mimeTypes.put("p7r", "application/x-pkcs7-certreqresp");
        mimeTypes.put("p7s", "application/x-pkcs7-signature");
        mimeTypes.put("pbm", "image/x-portable-bitmap");
        mimeTypes.put("pdf", "application/pdf");
        mimeTypes.put("pfx", "application/x-pkcs12");
        mimeTypes.put("pgm", "image/x-portable-graymap");
        mimeTypes.put("pko", "application/ynd.ms-pkipko");
        mimeTypes.put("pma", "application/x-perfmon");
        mimeTypes.put("pmc", "application/x-perfmon");
        mimeTypes.put("pml", "application/x-perfmon");
        mimeTypes.put("pmr", "application/x-perfmon");
        mimeTypes.put("pmw", "application/x-perfmon");
        mimeTypes.put("pnm", "image/x-portable-anymap");
        mimeTypes.put("pot", "application/vnd.ms-powerpoint");
        mimeTypes.put("ppm", "image/x-portable-pixmap");
        mimeTypes.put("pps", "application/vnd.ms-powerpoint");
        mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
        mimeTypes.put("prf", "application/pics-rules");
        mimeTypes.put("ps", "application/postscript");
        mimeTypes.put("pub", "application/x-mspublisher");
        mimeTypes.put("qt", "video/quicktime");
        mimeTypes.put("ra", "audio/x-pn-realaudio");
        mimeTypes.put("ram", "audio/x-pn-realaudio");
        mimeTypes.put("ras", "image/x-cmu-raster");
        mimeTypes.put("rgb", "image/x-rgb");
        mimeTypes.put("rmi", "audio/mid");
        mimeTypes.put("roff", "application/x-troff");
        mimeTypes.put("rtf", "application/rtf");
        mimeTypes.put("rtx", "text/richtext");
        mimeTypes.put("scd", "application/x-msschedule");
        mimeTypes.put("sct", "text/scriptlet");
        mimeTypes.put("setpay", "application/set-payment-initiation");
        mimeTypes.put("setreg", "application/set-registration-initiation");
        mimeTypes.put("sh", "application/x-sh");
        mimeTypes.put("shar", "application/x-shar");
        mimeTypes.put("sit", "application/x-stuffit");
        mimeTypes.put("snd", "audio/basic");
        mimeTypes.put("spc", "application/x-pkcs7-certificates");
        mimeTypes.put("spl", "application/futuresplash");
        mimeTypes.put("src", "application/x-wais-source");
        mimeTypes.put("sst", "application/vnd.ms-pkicertstore");
        mimeTypes.put("stl", "application/vnd.ms-pkistl");
        mimeTypes.put("stm", "text/html");
        mimeTypes.put("sv4cpio", "application/x-sv4cpio");
        mimeTypes.put("sv4crc", "application/x-sv4crc");
        mimeTypes.put("t", "application/x-troff");
        mimeTypes.put("tar", "application/x-tar");
        mimeTypes.put("tcl", "application/x-tcl");
        mimeTypes.put("tex", "application/x-tex");
        mimeTypes.put("texi", "application/x-texinfo");
        mimeTypes.put("texinfo", "application/x-texinfo");
        mimeTypes.put("tgz", "application/x-compressed");
        mimeTypes.put("tif", "image/tiff");
        mimeTypes.put("tiff", "image/tiff");
        mimeTypes.put("tr", "application/x-troff");
        mimeTypes.put("trm", "application/x-msterminal");
        mimeTypes.put("tsv", "text/tab-separated-values");
        mimeTypes.put("txt", "text/plain");
        mimeTypes.put("uls", "text/iuls");
        mimeTypes.put("ustar", "application/x-ustar");
        mimeTypes.put("vcf", "text/x-vcard");
        mimeTypes.put("vrml", "x-world/x-vrml");
        mimeTypes.put("wav", "audio/x-wav");
        mimeTypes.put("wcm", "application/vnd.ms-works");
        mimeTypes.put("wdb", "application/vnd.ms-works");
        mimeTypes.put("wks", "application/vnd.ms-works");
        mimeTypes.put("wmf", "application/x-msmetafile");
        mimeTypes.put("wps", "application/vnd.ms-works");
        mimeTypes.put("wri", "application/x-mswrite");
        mimeTypes.put("wrl", "x-world/x-vrml");
        mimeTypes.put("wrz", "x-world/x-vrml");
        mimeTypes.put("xaf", "x-world/x-vrml");
        mimeTypes.put("xbm", "image/x-xbitmap");
        mimeTypes.put("xla", "application/vnd.ms-excel");
        mimeTypes.put("xlc", "application/vnd.ms-excel");
        mimeTypes.put("xlm", "application/vnd.ms-excel");
        mimeTypes.put("xls", "application/vnd.ms-excel");
        mimeTypes.put("xlt", "application/vnd.ms-excel");
        mimeTypes.put("xlw", "application/vnd.ms-excel");
        mimeTypes.put("xof", "x-world/x-vrml");
        mimeTypes.put("xpm", "image/x-xpixmap");
        mimeTypes.put("xwd", "image/x-xwindowdump");
        mimeTypes.put("z", "application/x-compress");
        mimeTypes.put("zip", "application/zip");
        return mimeTypes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doGet(final HttpRequest request, final HttpResponse response) throws IOException {
        String target = request.getTarget();
        if (target.contains("://")) {
            try {
                final URL url = new URL(target);
                target = url.getPath();
            } catch (MalformedURLException e) {
                response.setStatusCode(400);
                response.setStatusMessage("Bad Request");
                response.setContent("Given path is unsupported: " + target);
                return;
            }
        }
        final File file = new File(serverRoot, target).getCanonicalFile();
        if (!file.exists() || !file.getPath().startsWith(serverRoot.getPath())) {
            response.setStatusCode(404);
            response.setStatusMessage("NOT FOUND");
            return;
        }

        response.setStatusCode(200);

        if (file.isDirectory()) {
            final StringBuilder htmlBuilder = new StringBuilder("<html><head><title>")
                    .append(file.getName())
                    .append("</title></head><body><h1>Index of ")
                    .append(target)
                    .append("</h1><hr/><pre>");

            final ArrayList<String> children = new ArrayList<>();

            if (target.length() > 1) {
                children.add("../");
            }

            for (final File child : file.listFiles()) {
                if (child.isDirectory()) {
                    children.add(child.getName() + '/');
                } else {
                    children.add(child.getName());
                }
            }

            for (final String child : children) {
                htmlBuilder.append("<a href=\"")
                        .append(child)
                        .append("\">")
                        .append(child)
                        .append("</a>\n");
            }

            htmlBuilder.append("</pre></body></html>");
            response.setContent(htmlBuilder.toString());
        } else {
            response.setContentType(getContentType(file));

            final FileInputStream inputStream = new FileInputStream(file);
            final OutputStream outputStream = response.getOutputStream();

            try {
                final byte[] bytes = new byte[1024];
                int count = inputStream.read(bytes);
                while (count > 0) {
                    outputStream.write(bytes, 0, count);
                    count = inputStream.read(bytes);
                }
            } finally {
                inputStream.close();
                outputStream.close();
            }
        }
    }

    private String getContentType(final File file) {
        final String name = file.getName();
        final int dot = name.lastIndexOf('.');
        if (dot >= 0) {
            final String extension = name.substring(dot + 1);
            final String contentType = mimeTypes.get(extension);
            if (contentType != null) {
                return contentType;
            }
        }
        return "application/octet-stream";
    }

}
