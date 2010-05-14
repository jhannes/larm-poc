package no.statnett.larm.fitnesse.fixtures;

import static util.ListUtility.list;

import java.util.List;


public class PositiveAPERAK {

    public static List<Object> query() {
        return list(
                list(
                   list("Melding", "APERAK"),
                   list("Resultat", "29"),
                   list("Budgiver", ""),
                   list("Idref", "")
                ));
    }
}
