package no.statnett.larm.poc.client.stasjon;

import java.util.List;

import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
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

    @Override
    protected TableColumnModel createColumnModel(List<Stasjon> searchResults) {
        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();
        columnModel.addColumn(newTableColumn("Stasjonsnavn", 0));
        columnModel.addColumn(newTableColumn("Fastområde", 1));
        return columnModel;
    }

    private TableColumn newTableColumn(String string, int i) {
        TableColumn tableColumn = new TableColumn(i);
        tableColumn.setHeaderValue(string);
        return tableColumn;
    }

    public static void main(String[] args) {
        Repository repository = LarmHibernateRepository.withFileDb();
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 1", "F01"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 2", "F01"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 3", "F02"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 4", "F03"));
        ApplicationFrame.display("Stasjoner",
                new StasjonListDialog(SyncAsyncProxy.createAsyncProxy(RepositoryAsync.class, repository)));
    }
}
