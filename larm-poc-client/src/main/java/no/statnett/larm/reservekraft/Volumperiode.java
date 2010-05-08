package no.statnett.larm.reservekraft;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Interval;

@Entity
public class Volumperiode {
    @Id @GeneratedValue
    private Integer id;

    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime startTid;
    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime sluttTid;

    private int tilgjengeligVolum;

    @ManyToOne
    private ReservekraftBud reservekraftBud;

    Volumperiode() {
    }

    public Volumperiode(ReservekraftBud reservekraftBud, DateTime startTid, DateTime sluttTid, int tilgjengeligVolum) {
        this.reservekraftBud = reservekraftBud;
        this.startTid = startTid;
        this.sluttTid = sluttTid;
        this.tilgjengeligVolum = tilgjengeligVolum;
    }

    public DateTime getStartTid() {
        return startTid;
    }

    public DateTime getSluttTid() {
        return sluttTid;
    }

    public int getTilgjengeligVolum() {
        return tilgjengeligVolum;
    }

    public Interval getPeriode() {
        return new Interval(startTid, sluttTid);
    }

}
