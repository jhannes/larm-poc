package no.statnett.larm.reservekraft;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Interval;

@Entity
public class Volumperiode implements Serializable {
    private static final long serialVersionUID = 4370581362104597420L;

    @SuppressWarnings("unused")
    @Id
    @GeneratedValue
    private Integer id;

    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime startTid;

    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime sluttTid;

    private Integer tilgjengeligVolum;

    @SuppressWarnings("unused")
    @ManyToOne
    @JoinColumn(name = "reserveKraftBud_id", updatable = false, insertable = false, nullable = false)
    private ReservekraftBud reservekraftBud;

    Volumperiode() {
    }

    public Volumperiode(ReservekraftBud reservekraftBud, DateTime startTid, DateTime sluttTid, Integer tilgjengeligVolum) {
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

    public Integer getTilgjengeligVolum() {
        return tilgjengeligVolum;
    }

    public Interval getPeriode() {
        return new Interval(startTid, sluttTid);
    }

}
