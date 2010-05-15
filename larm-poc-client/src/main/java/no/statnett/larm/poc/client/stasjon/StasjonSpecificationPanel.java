package no.statnett.larm.poc.client.stasjon;

import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JCheckBox;

import no.statnett.larm.client.SpecificationPanel;

public class StasjonSpecificationPanel extends SpecificationPanel<Stasjon> {
    private static final long serialVersionUID = 5610231349517185680L;
    private JCheckBox includeF01Checkbox = new JCheckBox("F01");
    private JCheckBox includeF02Checkbox = new JCheckBox("F02");
    private JButton searchButton = new JButton("Search");

    public StasjonSpecificationPanel() {
        setLayout(new FlowLayout());
        add(includeF01Checkbox);
        add(includeF02Checkbox);
        add(searchButton);
    }

    public JCheckBox getIncludeF01Checkbox() {
        return includeF01Checkbox;
    }

    public JCheckBox getIncludeF02Checkbox() {
        return includeF02Checkbox;
    }

    @Override
    public StasjonSpecification getSpecification() {
        StasjonSpecification specification = new StasjonSpecification();
        specification.setIncludeF01(includeF01Checkbox.isSelected());
        specification.setIncludeF02(includeF02Checkbox.isSelected());
        return specification;
    }

    @Override
    public JButton getSearchButton() {
        return searchButton;
    }
}
