package no.statnett.larm.ediel;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.core.repository.Repository;

public class EdielServlet extends HttpServlet {

    private static final long serialVersionUID = -8962987923221263389L;
    private Repository repository;
    private Timer timer = new Timer("EDIEL-file-scanner", true);

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("Application/EDIFACT");
        process(req.getPathInfo(), req.getReader(), resp.getWriter());
    }

    @Override
    public void init() throws ServletException {
        repository = LarmHibernateRepository.withJndiUrl("jdbc/primaryDs");

        FileListener edielProcessor = new FileListener() {
            @Override
            public void processFile(String fileName, Reader inputFile, Writer outputFile) throws IOException {
                process(fileName, inputFile, outputFile);
            }
        };

        final FileScanner scanner = new FileScanner(new File("data/ediel/input"), new File("data/ediel/output"), edielProcessor);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                scanner.scan();
            }
        }, 0, 1000);
    }

    protected void process(String fileName, Reader edifactRequest, Writer edifactResponse) throws IOException {
        new EdielService(repository).process(fileName, edifactRequest, edifactResponse);
    }

    @Override
    public void destroy() {
        timer.cancel();
    }
}
