package no.statnett.larm.ediel;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.core.repository.Repository;

public class EdielServlet extends HttpServlet {

    private static final long serialVersionUID = -8962987923221263389L;
    private Repository repository;

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("Application/EDIFACT");
        new EdielService(repository).process(req.getReader(), resp.getWriter());
    }

    @Override
    public void init() throws ServletException {
        repository = LarmHibernateRepository.withJndiUrl("jdbc/primaryDs");
    }
}
