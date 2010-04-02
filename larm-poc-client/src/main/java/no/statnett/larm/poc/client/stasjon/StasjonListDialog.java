package no.statnett.larm.poc.client.stasjon;

import no.statnett.larm.core.repository.Repository;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

public class StasjonListDialog extends JPanel {
    private StasjonSpecificationPanel searchPanel = new StasjonSpecificationPanel();
    private Repository repository;
    private JTable searchResult = new JTable();
    private DefaultTableModel tableModel = new DefaultTableModel();

    public StasjonListDialog() {
        tableModel.addColumn("Stasjonsnavn");
        tableModel.addColumn("Fastområde");
        searchResult.setModel(tableModel);

        searchPanel.getSearchButton().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });
    }

    private void performSearch() {
        List<Stasjon> stasjoner = repository.find(searchPanel.getSpecification());
        for (Stasjon stasjon : stasjoner) {
            tableModel.addRow(getRowData(stasjon));
        }
    }

    private Vector getRowData(Stasjon stasjon) {
        Vector<Object> vector = new Vector<Object>();
        vector.add(stasjon.getNavn());
        vector.add(stasjon.getFastområde());
        return vector;
    }

    public StasjonSpecificationPanel getSearchPanel() {
        return searchPanel;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public JTable getSearchResult() {
        return searchResult;
    }
}
