package no.statnett.larm.reservekraft;

import static ch.lambdaj.Lambda.on;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import javax.swing.table.TableModel;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.budservice.EdielService;
import no.statnett.larm.client.ListDialog;
import no.statnett.larm.client.PropertyTableModel;
import no.statnett.larm.core.async.AsyncCallback;
import no.statnett.larm.core.async.SyncAsyncProxy;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.RepositoryAsync;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;
import no.statnett.larm.poc.client.ApplicationFrame;

public class ReservekraftBudListDialog extends ListDialog<ReservekraftBud> {

    private static final long serialVersionUID = -7467356788876660356L;

    public ReservekraftBudListDialog(RepositoryAsync repositoryAsync) {
        super(repositoryAsync, new ReservekraftBudSpecificationPanel());
        repositoryAsync.findAll(Elspotområde.class, new AsyncCallback<List<Elspotområde>>() {
            @Override
            public void onFailure(Throwable e) {
                // TODO Auto-generated method stub on May 15, 2010
                throw new UnsupportedOperationException("Not implemented yet");
            }

            @Override
            public void onSuccess(List<Elspotområde> result) {
                setElspotområder(result);
            }
        });
    }

    @Override
    public ReservekraftBudSpecificationPanel getSearchPanel() {
        return (ReservekraftBudSpecificationPanel) super.getSearchPanel();
    }

    public void setElspotområder(List<Elspotområde> elspotområder) {
        getSearchPanel().setElspotområder(elspotområder);
    }

    @Override
    protected TableModel createTableModel(List<ReservekraftBud> searchResults) {
        PropertyTableModel<ReservekraftBud> tableModel = new PropertyTableModel<ReservekraftBud>(searchResults);
        tableModel.addLambdaColumn("Stasjonsgruppe", on(ReservekraftBud.class).getStasjonsgruppe().getBeskrivelse());
        tableModel.addLambdaColumn("Elspotområde", on(ReservekraftBud.class).getElspotområde().getNavn());
        tableModel.addLambdaColumn("Pris", on(ReservekraftBud.class).getPris());
        tableModel.addLambdaColumn("Varighet", on(ReservekraftBud.class).getVarighet());
        tableModel.addLambdaColumn("Hviletid", on(ReservekraftBud.class).getHviletid());
        tableModel.addLambdaColumn("Opp/Ned", on(ReservekraftBud.class).getRetning());

        for (int i=0; i<24; i++) {
            tableModel.addLambdaColumn("t" + (i+1), on(ReservekraftBud.class).getVolumForTime(i));
        }

        return tableModel;
    }


    public static void main(String[] args) throws IOException {
        Repository repository = LarmHibernateRepository.withFileDb();
        repository.deleteAll(ReservekraftBud.class);
        repository.deleteAll(Stasjonsgruppe.class);
        repository.deleteAll(Elspotområde.class);
        Elspotområde no1 = new Elspotområde("NO1");
        Elspotområde no2 = new Elspotområde("NO2");
        Elspotområde no4 = new Elspotområde("NO4");
        Elspotområde no3 = new Elspotområde("NO3");
        repository.insertAll(no1, no2, no3, no4);
        repository.insertAll(new Stasjonsgruppe("NOKG00116", "Sørfjord", no1),
                new Stasjonsgruppe("NOKG00056", "Borgund", no2),
                new Stasjonsgruppe("NOKG00049", "Sima", no4));

        new EdielService(repository).process("QuoteNordkraft.edi", new FileReader("src/test/ediel/quotes/QuoteNordkraft.edi"), new StringWriter());
        new EdielService(repository).process("QuoteOstfoldEnergi.edi", new FileReader("src/test/ediel/quotes/QuoteOstfoldEnergi.edi"), new StringWriter());
        new EdielService(repository).process("QuoteStatkraft.edi", new FileReader("src/test/ediel/quotes/QuoteStatkraft.edi"), new StringWriter());

        ReservekraftBudListDialog panel = new ReservekraftBudListDialog(
                SyncAsyncProxy.createAsyncProxy(RepositoryAsync.class, repository));
        ApplicationFrame.display("Reservekraftbud", panel);

    }
}
