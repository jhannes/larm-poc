package no.statnett.larm.reservekraft;

import static ch.lambdaj.Lambda.on;

import java.awt.Color;
import java.awt.Component;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import no.statnett.larm.client.ListDialog;
import no.statnett.larm.client.PropertyTableModel;
import no.statnett.larm.core.async.AsyncCallback;
import no.statnett.larm.core.repository.RepositoryAsync;
import no.statnett.larm.nettmodell.Elspotområde;

public class ReservekraftBudListDialog extends ListDialog<ReservekraftBud> {

    private static final long serialVersionUID = -7467356788876660356L;

    public ReservekraftBudListDialog(RepositoryAsync repositoryAsync) {
        super(repositoryAsync, new ReservekraftBudSpecificationPanel());
        repositoryAsync.findAll(Elspotområde.class, new AsyncCallback<List<Elspotområde>>() {
            @Override
            public void onFailure(Throwable e) {
                reportError("Kunne ikke laste elspotområder", e);
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

    @Override
    protected TableColumnModel createColumnModel(List<ReservekraftBud> searchResults) {
        DefaultTableColumnModel columnModel = new DefaultTableColumnModel();

        int index = 0;
        TableColumn stasjonsgruppeColumn = newTableColumn(index++, "Stasjonsgruppe");
        stasjonsgruppeColumn.setMinWidth(150);
        columnModel.addColumn(stasjonsgruppeColumn);
        columnModel.addColumn(newTableColumn(index++, "Elspotområde"));
        columnModel.addColumn(newTableColumn(index++, "Pris"));
        columnModel.addColumn(newTableColumn(index++, "Varighet"));
        columnModel.addColumn(newTableColumn(index++, "Hviletid"));
        columnModel.addColumn(newTableColumn(index++, "Opp/Ned"));

        for (int i=0; i<24; i++) {
            TableColumn column = newTableColumn(index++, "t" + (i+1));
            column.setMaxWidth(30);
            columnModel.addColumn(column);
        }

        return columnModel;
    }

    private DefaultTableCellRenderer retningsMarkeringCellRenderer() {
        return new DefaultTableCellRenderer() {

            private static final long serialVersionUID = -3750649726956873277L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component renderer = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                Object oppNed = table.getModel().getValueAt(row, 5);
                if ("Opp".equals(oppNed)) {
                    renderer.setBackground(new Color(128, 128, 255, 128));
                } else {
                    renderer.setBackground(new Color(128, 128, 255, 192));
                }

                return renderer;
            }
        };
    }

    private TableColumn newTableColumn(int i, String string) {
        TableColumn tableColumn = new TableColumn(i);
        tableColumn.setHeaderValue(string);
        tableColumn.setCellRenderer(retningsMarkeringCellRenderer());
        return tableColumn;
    }

}
