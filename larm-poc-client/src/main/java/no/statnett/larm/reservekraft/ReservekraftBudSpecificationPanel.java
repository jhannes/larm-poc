package no.statnett.larm.reservekraft;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import no.statnett.larm.client.SpecificationPanel;
import no.statnett.larm.nettmodell.Elspotområde;

import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ReservekraftBudSpecificationPanel extends SpecificationPanel<ReservekraftBud> {

    private DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyy");
    private static final long serialVersionUID = -45528769624669923L;
    private JTextField driftsdøgnField = new JTextField(12);
    private SortedMap<Elspotområde, JCheckBox> elspotområdeCheckboxlist = new TreeMap<Elspotområde, JCheckBox>();
    private JButton searchButton = new JButton("Søk");
    private JPanel elspotområderPanel = new JPanel();

    public ReservekraftBudSpecificationPanel() {
        setLayout(new GridLayout(0, 1));

        JPanel baseOptions = new JPanel();
        baseOptions.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Finn bud"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        baseOptions.add(new JLabel("Driftsdøgn"));
        //driftsdøgnField.setText(format.print(new DateTime()));
        baseOptions.add(driftsdøgnField);
        baseOptions.add(searchButton);

        add(baseOptions);

        elspotområderPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Elspotområder"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
        add(elspotområderPanel);
    }


    public JTextField getDriftsdøgnField() {
        return driftsdøgnField;
    }

    public JCheckBox getElspotområdeCheckbox(Elspotområde elspotområde) {
        return elspotområdeCheckboxlist.get(elspotområde);
    }

    @Override
    public ReservekraftBudSpecification getSpecification() {
        DateMidnight driftsdøgn = getDriftsdøgn();

        ReservekraftBudSpecification reservekraftBudSpecification = ReservekraftBudSpecification.forOmråder(getCheckedOmråder());
        reservekraftBudSpecification.setDriftsdøgn(driftsdøgn);
        return reservekraftBudSpecification;
    }

    private DateMidnight getDriftsdøgn() {
        String driftsdøgnValue = this.driftsdøgnField.getText();
        return driftsdøgnValue.isEmpty() ? null : format.parseDateTime(driftsdøgnValue).toDateMidnight();
    }

    private Collection<Elspotområde> getCheckedOmråder() {
        ArrayList<Elspotområde> result = new ArrayList<Elspotområde>();
        for (SortedMap.Entry<Elspotområde, JCheckBox> checkboxEntry : elspotområdeCheckboxlist.entrySet()) {
            if (checkboxEntry.getValue().isSelected()) {
                result.add(checkboxEntry.getKey());
            }
        }
        return result;
    }

    public void setElspotområder(List<Elspotområde> elspotområder) {
        elspotområdeCheckboxlist.clear();
        elspotområderPanel.removeAll();
        for (Elspotområde elspotområde : elspotområder) {
            JCheckBox checkbox = new JCheckBox(elspotområde.getNavn());
            elspotområdeCheckboxlist.put(elspotområde, checkbox);
            elspotområderPanel.add(checkbox);
        }
    }

    @Override
    public JButton getSearchButton() {
        return searchButton;
    }

}
