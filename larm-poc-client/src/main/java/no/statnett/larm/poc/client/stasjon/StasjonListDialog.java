package no.statnett.larm.poc.client.stasjon;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.naming.NamingException;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.core.async.AsyncCallback;
import no.statnett.larm.core.async.SyncAsyncProxy;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.RepositoryAsync;
import no.statnett.larm.poc.client.ApplicationFrame;

public class StasjonListDialog extends JPanel {
    private static final long serialVersionUID = 3377211805587015468L;
    private StasjonSpecificationPanel searchPanel = new StasjonSpecificationPanel();
    private RepositoryAsync repositoryAsync;
    private JTable searchResult = new JTable();
    private DefaultTableModel tableModel = new DefaultTableModel();

    public StasjonListDialog(RepositoryAsync repositoryAsync) {
        this.repositoryAsync = repositoryAsync;
        tableModel.addColumn("Stasjonsnavn");
        tableModel.addColumn("Fastområde");
        searchResult.setModel(tableModel);

        searchPanel.getSearchButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StasjonListDialog.this.repositoryAsync.find(searchPanel.getSpecification(), new AsyncCallback<List<Stasjon>>() {
                    public void onSuccess(List<Stasjon> result) {
                        updateSearchResults(result);
                    }

                    public void onFailure(Throwable e) {
                        reportError("while searching", e);
                    }
                });
            }
        });

        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, searchPanel);
        add(BorderLayout.CENTER, new JScrollPane(searchResult));
    }

    private void reportError(String whatWasHappening, Throwable e) {
        System.err.println(whatWasHappening);
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.toString(), "Feil: " + whatWasHappening,
                JOptionPane.ERROR_MESSAGE);
    }

    private void updateSearchResults(List<Stasjon> stasjoner) {
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Stasjonsnavn");
        tableModel.addColumn("Fastomr�de");
        searchResult.setModel(tableModel);

        for (Stasjon stasjon : stasjoner) {
            tableModel.addRow(getRowData(stasjon));
        }
    }

    private Vector<Object> getRowData(Stasjon stasjon) {
        Vector<Object> vector = new Vector<Object>();
        vector.add(stasjon.getNavn());
        vector.add(stasjon.getFastområde());
        return vector;
    }

    public StasjonSpecificationPanel getSearchPanel() {
        return searchPanel;
    }

    public JTable getSearchResult() {
        return searchResult;
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
