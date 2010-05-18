package no.statnett.larm.poc.web;

import no.statnett.larm.core.container.RedirectionContext;
import no.statnett.larm.core.container.ShutdownHandler;

import org.h2.jdbcx.JdbcConnectionPool;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.plus.naming.EnvEntry;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebServer {

    public static void main(String[] args) throws Exception {
        ShutdownHandler.attemptShutdown(8088, "abc");

        JdbcConnectionPool dataSource = JdbcConnectionPool.create("jdbc:h2:file:target/db/manual-test", "", "");
        new EnvEntry("jdbc/primaryDs", dataSource);
        dataSource.getConnection();

        Server server = new Server(8088);
        server.addHandler(new WebAppContext("src/main/webapp", "/larm-poc"));
        server.addHandler(new ShutdownHandler(server, "abc"));
        server.addHandler(new RedirectionContext("/", "/larm-poc"));
        server.start();
    }

}
