package no.statnett.larm.core.container;

import org.apache.commons.io.IOUtils;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.HttpException;
import org.mortbay.jetty.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InitialContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

public class StatusHandler extends AbstractHandler {

    private static Logger logger = LoggerFactory.getLogger(StatusHandler.class);

    public void handle(String target, HttpServletRequest request, HttpServletResponse response,
            int dispatch) throws IOException, ServletException {
        if (!target.equals("/status")) {
            return;
        }

        if (!request.getMethod().equals("GET")) {
            throw new HttpException(HttpServletResponse.SC_BAD_REQUEST);
        }
        logger.info("Returning status");

        response.setContentType("text/plain");
        Map<String, String> statusIndicators = getStatusIndicators();
        for (Map.Entry<String, String> status : statusIndicators.entrySet()) {
            response.getWriter().println(status.getKey() + "=" + status.getValue());
        }
        response.getWriter().close();
        response.setStatus(HttpServletResponse.SC_OK);
        HttpConnection.getCurrentConnection().getRequest().setHandled(true);
    }

    private Map<String,String> getStatusIndicators() {
        Map<String, String> result = new TreeMap<String, String>();
        result.put("server", "running");
        result.put("jdbc/foo", getJdbcStatus("jdbc/foo"));
        result.put("jdbc/bar", getJdbcStatus("jdbc/bar"));
        result.put("jdbc/baz", getJdbcStatus("jdbc/baz"));
        return result;
    }

    private String getJdbcStatus(String jdniName) {
        try {
            InitialContext context = new InitialContext();
            DataSource dataSource = (DataSource) context.lookup(jdniName);
            dataSource.getConnection().close();
            return "OK";
        } catch (Exception e) {
            return e.toString();
        }
    }

    public static boolean checkStatus(int port) {
        try {
            URL url = new URL("http://localhost:" + port + "/status");
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.getResponseCode();
            logger.info("Checking status down " + url + ": " + connection.getResponseMessage());
            System.out.println(IOUtils.toString((InputStream)connection.getContent()));
            return true;
        } catch (SocketException e) {
            logger.info("Not running");
            return false;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    
}
