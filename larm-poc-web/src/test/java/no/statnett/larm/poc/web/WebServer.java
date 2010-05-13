package no.statnett.larm.poc.web;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.core.container.RedirectionContext;
import no.statnett.larm.core.container.ShutdownHandler;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;

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

        Repository repository = LarmHibernateRepository.withJndiUrl("jdbc/primaryDs");
        if (repository.findAll(Elspotområde.class).isEmpty()) {
            repository.insertAll(new Elspotområde("NO1"), new Elspotområde("NO2"), new Elspotområde("NO3"), new Elspotområde("NO4"));
        }
        if (repository.findAll(Stasjonsgruppe.class).isEmpty()) {
            Elspotområde elspotområde = repository.findAll(Elspotområde.class).get(0);
            repository.insertAll(new Stasjonsgruppe("NOKG00116", elspotområde),
                    new Stasjonsgruppe("NOKG00056", elspotområde),
                    new Stasjonsgruppe("NOKG00049", elspotområde));
        }

        Server server = new Server(8088);
        server.addHandler(new WebAppContext("src/main/webapp", "/larm-poc"));
        server.addHandler(new ShutdownHandler(server, "abc"));
        server.addHandler(new RedirectionContext("/", "/larm-poc"));
        server.start();
    }

}
