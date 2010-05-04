package no.statnett.larm.core.container;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.ServletHolder;

public class RedirectionContext extends Context {
    public RedirectionContext(String contextPath, String destination) {
        super(null, contextPath);
        addServlet(new ServletHolder(createRedirectServlet(destination)), "/*");
    }

    private HttpServlet createRedirectServlet(final String destination) {
        return new HttpServlet() {
            private static final long serialVersionUID = -8720231336440681859L;

            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
                resp.sendRedirect(destination);
            }
        };
    }

}
