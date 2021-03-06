package no.statnett.larm.ediel.servlet;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Timer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.budservice.EdielService;
import no.statnett.larm.budservice.FileListener;
import no.statnett.larm.budservice.FileScanner;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;

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

        if (repository.findAll(Elspotområde.class).isEmpty()) {
            repository.insertAll(new Elspotområde("NO1"), new Elspotområde("NO2"), new Elspotområde("NO3"), new Elspotområde("NO4"));
        }
        if (repository.findAll(Stasjonsgruppe.class).isEmpty()) {
            Elspotområde elspotområde = repository.findAll(Elspotområde.class).get(0);
            repository.insertAll(new Stasjonsgruppe("NOKG00116", "Sørfjord", elspotområde),
                    new Stasjonsgruppe("NOKG00056", "Borgund", elspotområde),
                    new Stasjonsgruppe("NOKG00049", "Sima", elspotområde));
        }

        FileScanner.scheduleEverySecond(new File("data/ediel/input"), new File("data/ediel/output"), new FileListener() {
            @Override
            public void processFile(String fileName, Reader inputFile, Writer outputFile) throws IOException {
                process(fileName, inputFile, outputFile);
            }
        });
    }

    protected void process(String fileName, Reader edifactRequest, Writer edifactResponse) throws IOException {
        new EdielService(repository).process(fileName, edifactRequest, edifactResponse);
    }

    @Override
    public void destroy() {
        timer.cancel();
    }
}
