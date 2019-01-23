package com.github.raphcal.localserver.index;

import com.github.raphcal.localserver.LocalServer;
import java.io.File;

/**
 * Serveur d'index.
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class IndexServer {
    
    public static void main(String[] args) {
		final File root;
		if (args.length == 1) {
			root = new File(args[0]);
			if (!root.exists()) {
				System.err.println("Chemin invalide : " + root);
				return;
			}
		} else {
			root = new File("/home/codio/workspace");
		}
		
		final DirectoryIndexHttpServlet servlet = new DirectoryIndexHttpServlet();
		servlet.setServerRoot(root);
		
		final LocalServer localServer = new LocalServer(8787, servlet);
		localServer.start();
	}
    
}
