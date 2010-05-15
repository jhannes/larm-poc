package no.statnett.larm.poc.client.stasjon;

import java.util.List;

import javax.naming.NamingException;
import javax.swing.table.TableModel;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.client.ListDialog;
import no.statnett.larm.client.PropertyTableModel;
import no.statnett.larm.core.async.SyncAsyncProxy;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.RepositoryAsync;
import no.statnett.larm.poc.client.ApplicationFrame;
import ch.lambdaj.Lambda;

public class StasjonListDialog extends ListDialog<Stasjon> {
    private static final long serialVersionUID = 3377211805587015468L;

    public StasjonListDialog(RepositoryAsync repositoryAsync) {
        super(repositoryAsync, new StasjonSpecificationPanel());
    }

    @Override
    protected TableModel createTableModel(List<Stasjon> searchResults) {
        PropertyTableModel<Stasjon> tableModel = new PropertyTableModel<Stasjon>(searchResults);
        tableModel.addLambdaColumn("Stasjonsnavn", Lambda.on(Stasjon.class).getNavn());
        tableModel.addLambdaColumn("Fastområde", Lambda.on(Stasjon.class).getFastområde());
        return tableModel;
    }

    public static void main(String[] args) throws NamingException {
        Repository repository = LarmHibernateRepository.withFileDb();
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 1", "F01"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 2", "F01"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 3", "F02"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 4", "F03"));
        ApplicationFrame.display("Stasjoner",
                new StasjonListDialog(SyncAsyncProxy.createAsyncProxy(RepositoryAsync.class, repository)));
    }
}
