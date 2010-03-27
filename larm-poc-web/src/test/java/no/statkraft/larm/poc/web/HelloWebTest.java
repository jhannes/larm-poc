package no.statkraft.larm.poc.web;

import org.junit.Test;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import static org.fest.assertions.Assertions.assertThat;

public class HelloWebTest {

    @Test
    public void indexPageShouldSayHelloWorld() throws Exception {
        Server server = new Server(0);
        server.addHandler(new WebAppContext("src/main/webapp", "/"));
        server.start();
        int serverPort = server.getConnectors()[0].getLocalPort();
        String applicationUrl = "http://localhost:" + serverPort + "/";


        WebDriver browser = new HtmlUnitDriver();

        browser.get(applicationUrl);
        assertThat(browser.findElement(By.tagName("h1")).getText()).isEqualTo("Hello world");
    }

}
