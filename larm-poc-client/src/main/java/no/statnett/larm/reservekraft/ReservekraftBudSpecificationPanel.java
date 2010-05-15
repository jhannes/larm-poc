package no.statnett.larm.reservekraft;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import no.statnett.larm.client.SpecificationPanel;
import no.statnett.larm.nettmodell.Elspotområde;

import org.joda.time.DateMidnight;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ReservekraftBudSpecificationPanel extends SpecificationPanel<ReservekraftBud> {

    private static final long serialVersionUID = -45528769624669923L;
    private JTextField driftsdøgnField = new JTextField();
    private SortedMap<Elspotområde, JCheckBox> elspotområdeCheckboxlist = new TreeMap<Elspotområde, JCheckBox>();
    private JButton searchButton = new JButton();

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
        DateTimeFormatter format = DateTimeFormat.forPattern("dd.MM.yyyy");
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
        for (Elspotområde elspotområde : elspotområder) {
            elspotområdeCheckboxlist.put(elspotområde, new JCheckBox(elspotområde.getNavn()));
        }
    }

    @Override
    public JButton getSearchButton() {
        return searchButton;
    }

}
