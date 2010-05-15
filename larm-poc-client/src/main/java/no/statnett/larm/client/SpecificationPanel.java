package no.statnett.larm.client;

import javax.swing.JButton;
import javax.swing.JPanel;

import no.statnett.larm.core.repository.Specification;

public abstract class SpecificationPanel<T> extends JPanel {

    private static final long serialVersionUID = -2062031807233155087L;

    public abstract JButton getSearchButton();

    public abstract Specification<T> getSpecification();

}
