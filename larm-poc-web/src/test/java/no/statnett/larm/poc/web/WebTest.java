package no.statnett.larm.poc.web;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.core.repository.Repository;

import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.BeforeClass;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.plus.naming.EnvEntry;
import org.mortbay.jetty.webapp.WebAppContext;

public class WebTest {

    private static String applicationUrl;
	private static Repository repository;

    @BeforeClass
    public static void startWebServer() throws Exception {
    	JdbcConnectionPool dataSource = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;MODE=Oracle;MVCC=true", "", "");
    	new EnvEntry("jdbc/primaryDs", dataSource);
    	dataSource.getConnection();

        Server server = new Server(0);
        server.addHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();
        int serverPort = server.getConnectors()[0].getLocalPort();
        applicationUrl = "http://localhost:" + serverPort + "/";

        repository = LarmHibernateRepository.withJndiUrl("jdbc/primaryDs");
    }

    public String getApplicationUrl() {
        return applicationUrl;
    }


    protected Repository getRepository() {
        return repository;
    }


}
