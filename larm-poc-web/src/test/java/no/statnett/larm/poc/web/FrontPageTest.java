package no.statnett.larm.poc.web;

import static org.fest.assertions.Assertions.assertThat;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public class FrontPageTest extends WebTest {

    @Test
    public void indexPageShouldSayHelloWorld() throws Exception {
        WebDriver browser = new HtmlUnitDriver();

        browser.get(getApplicationUrl());
        assertThat(browser.findElement(By.tagName("h1")).getText()).isEqualTo("Velkommen til LARM");
    }

}
