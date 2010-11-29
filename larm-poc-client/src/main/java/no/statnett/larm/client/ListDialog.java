package no.statnett.larm.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import no.statnett.larm.core.async.AsyncCallback;
import no.statnett.larm.core.repository.RepositoryAsync;

public abstract class ListDialog<T> extends JPanel {

    private static final long serialVersionUID = -5859496760985176762L;

    private SpecificationPanel<T> specificationPanel;

    private final RepositoryAsync repositoryAsync;

    private JTable itemTable = new JTable();

    public ListDialog(RepositoryAsync repositoryAsync, SpecificationPanel<T> specificationPanel) {
        this.repositoryAsync = repositoryAsync;
        this.specificationPanel = specificationPanel;

        layoutDialog();

        specificationPanel.addActionListener(createActionListener());
        updateSearchResults(new ArrayList<T>());
    }

    protected void layoutDialog() {
        setLayout(new BorderLayout());
        add(BorderLayout.NORTH, getSearchPanel());
        add(BorderLayout.CENTER, new JScrollPane(getSearchResult()));
    }

    private ActionListener createActionListener() {
        return new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                repositoryAsync.find(specificationPanel.getSpecification(), new AsyncCallback<List<T>>() {
                    public void onSuccess(List<T> result) {
                        updateSearchResults(result);
                    }

                    public void onFailure(Throwable e) {
                        reportError("Feil under søk", e);
                    }
                });
            }
        };
    }

    public void reportError(String whatWasHappening, Throwable e) {
        System.err.println(whatWasHappening);
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, e.toString(), "Feil: " + whatWasHappening,
                JOptionPane.ERROR_MESSAGE);
    }

    public JTable getSearchResult() {
        return itemTable;
    }

    private void updateSearchResults(List<T> searchResults) {
        itemTable.setModel(createTableModel(searchResults));
        itemTable.setColumnModel(createColumnModel(searchResults));
    }

    protected abstract TableColumnModel createColumnModel(List<T> searchResults);

    protected abstract TableModel createTableModel(List<T> searchResults);

    public SpecificationPanel<T> getSearchPanel() {
        return specificationPanel;
    }

}