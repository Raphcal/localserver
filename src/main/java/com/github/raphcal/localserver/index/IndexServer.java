package com.github.raphcal.localserver.index;

import com.github.raphcal.localserver.LocalServer;
import java.io.File;
import java.io.IOException;

/**
 * Directory index server.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class IndexServer {

    public static void main(String[] args) throws IOException {
        final File root;
        if (args.length == 1) {
            root = new File(args[0]);
            if (!root.exists()) {
                System.err.println("Invalid root path: " + root);
                return;
            }
        } else {
            root = new File(System.getenv("HOME"));
        }

        final LocalServer localServer = new LocalServer(8787, new DirectoryIndexHttpServlet(root));
        localServer.start();
        System.out.println("Index server started on " + localServer.getEndpoint() + " listing data from " + root.getCanonicalPath());
    }

}
