package no.statnett.larm.poc.web;

import no.statnett.larm.core.repository.Repository;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebTest {

    private static String applicationUrl;

    @BeforeClass
    public static void startWebServer() throws Exception {
        Server server = new Server(0);
        server.addHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();
        int serverPort = server.getConnectors()[0].getLocalPort();
        applicationUrl = "http://localhost:" + serverPort + "/";
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }


    protected Repository getRepository() {
        // TODO Auto-generated method stub
        return null;
    }

    @Test
    public void shouldHaveATestHereOrJUnitThrowsaFit() throws Exception {
    }

}
