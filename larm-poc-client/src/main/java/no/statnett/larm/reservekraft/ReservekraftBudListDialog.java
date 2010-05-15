package no.statnett.larm.reservekraft;

import static ch.lambdaj.Lambda.on;

import java.util.List;

import javax.swing.table.TableModel;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.client.ListDialog;
import no.statnett.larm.client.PropertyTableModel;
import no.statnett.larm.core.async.SyncAsyncProxy;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.RepositoryAsync;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.poc.client.ApplicationFrame;

public class ReservekraftBudListDialog extends ListDialog<ReservekraftBud> {

    private static final long serialVersionUID = -7467356788876660356L;

    public ReservekraftBudListDialog(RepositoryAsync repositoryAsync) {
        super(repositoryAsync, new ReservekraftBudSpecificationPanel());
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
        tableModel.addColumn("Pris");
        tableModel.addColumn("Varighet");
        tableModel.addColumn("Hviletid");
        tableModel.addColumn("Opp/Ned");

        return tableModel;
    }


    public static void main(String[] args) {
        Repository repository = LarmHibernateRepository.withFileDb();
        ApplicationFrame.display("Reservekraftbud",
                new ReservekraftBudListDialog(SyncAsyncProxy.createAsyncProxy(RepositoryAsync.class, repository)));

    }
}
