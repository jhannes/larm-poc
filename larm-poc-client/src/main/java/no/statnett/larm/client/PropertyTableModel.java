package no.statnett.larm.client;

import java.util.List;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import ch.lambdaj.Lambda;

public class PropertyTableModel<T> extends DefaultTableModel {

    private static final long serialVersionUID = -9213407997505578219L;

    private final List<T> searchResults;

    public PropertyTableModel(List<T> searchResults) {
        this.searchResults = searchResults;
    }

    public <U> void addLambdaColumn(String columnName, U field) {
        addColumn(columnName, toVector(Lambda.collect(searchResults, field)));
    }

    private <U> Vector<U> toVector(List<U> list) {
        return new Vector<U>(list);
    }

}
