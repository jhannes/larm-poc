package no.statnett.larm.client;

import java.awt.event.ActionListener;

import javax.swing.JPanel;

import no.statnett.larm.core.repository.Specification;

public abstract class SpecificationPanel<T> extends JPanel {

    private static final long serialVersionUID = -2062031807233155087L;

    public abstract Specification<T> getSpecification();

    public abstract void addActionListener(ActionListener actionListener);

}
