package no.statnett.larm.core.jnlp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JarServlet extends HttpServlet {
    private static final long serialVersionUID = -7561828542215370107L;
    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jarFile = req.getRequestURI();
        if (!jarFile.startsWith(req.getContextPath())) {
            log.error("Huh? Expected " + jarFile + " to start with " + req.getContextPath());
        } else {
            jarFile = jarFile.substring(req.getContextPath().length()+1);
        }

        if (!getAllowedJars().contains(jarFile)) {
            log.warn("Tried to serve illegal jar file " + jarFile);
            resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        InputStream result = null;

        File file = new File("../larm-poc-client/target/" + jarFile);
        if (file.exists()) {
            log.debug("Found " + file);
            result = new FileInputStream(file);
        }
        if (result == null) {
            result = getServletContext().getResourceAsStream("/WEB-INF/lib/" + jarFile);
        }
        if (result == null) {
            LoggerFactory.getLogger(getClass()).warn("Tried to serve unknown jar file " + jarFile);
        } else {
            copyAndClose(result, resp.getOutputStream());
        }
    }

    private void copyAndClose(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024 * 4];
        int n;
        while (-1 != (n = inputStream.read(buffer))) {
            outputStream.write(buffer, 0, n);
        }
        outputStream.flush();
        inputStream.close();
    }

    private List<String> getAllowedJars() {
        ArrayList<String> allowedJars = new ArrayList<String>();
        allowedJars.add("larm-poc-client-1.0-SNAPSHOT-jar-with-dependencies.jar");
        return allowedJars;
    }
}
