package no.statnett.larm.poc.client.stasjon;

import javax.swing.*;

public class StasjonSpecificationPanel {
    private JCheckBox includeF01Checkbox = new JCheckBox("F01");
    private JCheckBox includeF02Checkbox = new JCheckBox("F02");
    private JButton searchButton = new JButton("Search");

    public JCheckBox getIncludeF01Checkbox() {
        return includeF01Checkbox;
    }

    public JCheckBox getIncludeF02Checkbox() {
        return includeF02Checkbox;
    }

    public StasjonSpecification getSpecification() {
        StasjonSpecification specification = new StasjonSpecification();
        specification.setIncludeF01(includeF01Checkbox.isSelected());
        specification.setIncludeF02(includeF02Checkbox.isSelected());
        return specification;
    }

    public JButton getSearchButton() {
        return searchButton;
    }
}
