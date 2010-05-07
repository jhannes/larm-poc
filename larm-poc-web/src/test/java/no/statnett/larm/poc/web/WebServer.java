package no.statnett.larm.poc.web;

import no.statnett.larm.core.container.ShutdownHandler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebServer {

    public static void main(String[] args) throws Exception {
        ShutdownHandler.attemptShutdown(8088, "abc");
        Server server = new Server(8088);
        server.addHandler(new WebAppContext("src/main/webapp", "/larm-poc"));
        server.addHandler(new ShutdownHandler(server, "abc"));
        server.start();
    }
    
}
