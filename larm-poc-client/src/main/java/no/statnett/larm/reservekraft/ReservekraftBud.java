package no.statnett.larm.reservekraft;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

@Entity
public class ReservekraftBud {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    private Stasjonsgruppe stasjonsgruppe;

    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime startTid;

    @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime sluttTid;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "reserveKraftBud_id", nullable = false)
    @OrderBy("startTid")
    private List<Volumperiode> volumPerioder = new ArrayList<Volumperiode>();

    private String budreferanse;

    private Period aktiveringstid;

    private Period hviletid;

    ReservekraftBud() {
    }

    public ReservekraftBud(Stasjonsgruppe stasjonsgruppe) {
        this.stasjonsgruppe = stasjonsgruppe;
    }

    public String getBudreferanse() {
        return budreferanse;
    }

    public void setBudreferanse(String budreferanse) {
        this.budreferanse = budreferanse;
    }

    public Stasjonsgruppe getStasjonsgruppe() {
        return stasjonsgruppe;
    }

    public String getStasjonsgruppeId() {
        return stasjonsgruppe != null ? stasjonsgruppe.getNavn() : null;
    }

    public void setStartTid(DateTime startTid) {
        this.startTid = startTid;
    }

    public void setSluttTid(DateTime sluttTid) {
        this.sluttTid = sluttTid;
    }

    public void setVolumForTidsrom(Interval period, Long volum) {
        setVolumForTidsrom(period.getStart(), period.getEnd(), volum);
    }

    public void setVolumForTidsrom(DateTime startTid, DateTime sluttTid,
            Long tilgjengeligMw) {
        volumPerioder.add(new Volumperiode(this, startTid, sluttTid,
                tilgjengeligMw));
    }

    @Override
    public String toString() {
        return "ReservekraftBud<" + stasjonsgruppe + ",fra " + startTid + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReservekraftBud))
            return false;
        ReservekraftBud bud = (ReservekraftBud) obj;

        if (id == null)
            return (this == bud);
        return id.equals(bud.id);
    }

    @Override
    public int hashCode() {
        return stasjonsgruppe.hashCode();
    }

    public Elspotområde getElspotområde() {
        return stasjonsgruppe.getElspotområde();
    }

    public Interval getBudperiode() {
        return new Interval(startTid, sluttTid);
    }

    public void setBudperiode(Interval interval) {
        this.startTid = interval.getStart();
        this.sluttTid = interval.getEnd();
    }

    public List<Volumperiode> getVolumPerioder() {
        return volumPerioder;
    }

    public Period getAktiveringstid() {
        return aktiveringstid;
    }

    public Period getHviletid() {
        return hviletid;
    }

    public void setVarighet(Period aktiveringstid) {
        this.aktiveringstid = aktiveringstid;
    }

    public void setHviletid(Period hviletid) {
        this.hviletid = hviletid;
    }

}
