package no.statnett.larm.core.jnlp;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class
        JnlpServlet extends HttpServlet {

    private static final long serialVersionUID = 6254020517387286254L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException,
            IOException {
        String codebaseUrl = "http://" + req.getServerName() + ":" + req.getServerPort() + req.getContextPath();
        String repositoryUrl = codebaseUrl + "/service/repositoryService";
        String servletPath = req.getServletPath();
        if (!servletPath.endsWith(".jnlp") || !servletPath.startsWith("/")) {
            throw new RuntimeException("Huh!? " + servletPath);
        }
        String className = servletPath.substring(1, servletPath.lastIndexOf(".jnlp"));

        resp.setContentType("application/x-java-jnlp-file");

        Element rootElement = DocumentHelper.createElement("jnlp")
                .addAttribute("spec", "1.0+")
                .addAttribute("codebase", codebaseUrl)
                .addAttribute("href", className + ".jnlp");
        Element info = rootElement.addElement("information");
        info.addElement("title").addText("LARM");
        info.addElement("vendor").addText("Statnett");

        Element resources = rootElement.addElement("resources");
        resources.addElement("j2se").addAttribute("version", "1.6+");
        for (String clientJarFile : getClientJarFiles()) {
            resources.addElement("jar").addAttribute("href", clientJarFile);
        }

        Element application = rootElement.addElement("application-desc");
        application.addAttribute("main-class", "no.statnett.larm.poc.client." + className);
        application.addElement("argument").addText(repositoryUrl);

        rootElement.addElement("security").addElement("all-permissions");

        DocumentHelper.createDocument(rootElement).write(resp.getWriter());
    }

    private List<String> getClientJarFiles() {
        ArrayList<String> clientJarFiles = new ArrayList<String>();
        clientJarFiles.add("larm-poc-client-1.0-SNAPSHOT-jar-with-dependencies.jar");
        return clientJarFiles;
    }


}
