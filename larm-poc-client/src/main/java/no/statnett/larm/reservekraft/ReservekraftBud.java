package no.statnett.larm.reservekraft;

import java.io.Serializable;
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

import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.joda.time.Period;

@Entity
public class ReservekraftBud implements Serializable, Comparable<ReservekraftBud> {

    private static final long serialVersionUID = -6663639649491663160L;

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
    private List<Volumperiode> volumPerioder = new ArrayList<Volumperiode>();

    private String budreferanse;

    private Integer varighet;

    private Integer hviletid;

    private Integer pris;

    private String retning = "Opp";

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

    public void setStartTid(DateTime startTid) {
        this.startTid = startTid;
    }

    public void setSluttTid(DateTime sluttTid) {
        this.sluttTid = sluttTid;
    }

    public ReservekraftBud setVolumForTidsrom(Interval period, Integer volum) {
        return setVolumForTidsrom(period.getStart(), period.getEnd(), volum);
    }

    public ReservekraftBud setVolumForTidsrom(DateTime startTid, DateTime sluttTid, Integer tilgjengeligMw) {
        volumPerioder.add(new Volumperiode(this, startTid, sluttTid, tilgjengeligMw));
        for (Volumperiode periode : volumPerioder) {
            if (periode.getTilgjengeligVolum() > 0) retning = "Opp";
            if (periode.getTilgjengeligVolum() < 0) retning = "Ned";
        }

        return this;
    }

    @Override
    public String toString() {
        return "ReservekraftBud<" + stasjonsgruppe + ",startTid=" + startTid + ", pris=" + getPris() + ">";
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

    @Override
    public int compareTo(ReservekraftBud o) {
        if (!getRetning().equals(o.getRetning())) {
            return "Opp".equals(getRetning()) ? -1 : 1;
        }
        return o.getPris() - getPris();
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

    public Integer getVarighet() {
        return varighet;
    }

    public Integer getHviletid() {
        return hviletid;
    }

    public void setVarighet(Period aktiveringstid) {
        this.varighet = aktiveringstid.getMinutes();
    }

    public void setHviletid(Period hviletid) {
        this.hviletid = hviletid.getMinutes();
    }

    public String getRetning() {
        return retning;
    }

    public int getPris() {
        return pris != null ? pris.intValue() : 0;
    }

    public ReservekraftBud setPris(Integer pris) {
        this.pris = pris;
        return this;
    }

    public Integer getVolumForTime(int time) {
        for (Volumperiode periode : volumPerioder) {
            if (periode.getStartTid().getHourOfDay() <= time && periode.getSluttTid().minusHours(1).getHourOfDay() >= time) {
                return Math.abs(periode.getTilgjengeligVolum());
            }
        }
        return null;
    }

}
