package no.statnett.larm.reservekraft;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.Interval;

@Entity
public class ReservekraftBud {

    @Id @GeneratedValue
    private Integer id;

    @ManyToOne
    private Stasjonsgruppe stasjonsgruppe;

    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime startTid;

    @Type(type="org.joda.time.contrib.hibernate.PersistentDateTime")
    private DateTime sluttTid;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @OrderBy("startTid")
    private List<Volumperiode> volumPerioder = new ArrayList<Volumperiode>();

    ReservekraftBud() {
    }

    public ReservekraftBud(Stasjonsgruppe stasjonsgruppe) {
        this.stasjonsgruppe = stasjonsgruppe;
        // TODO Auto-generated constructor stub
    }

    public String getBudreferanse() {
        // TODO Auto-generated method stub
        return null;
    }
    
    public Stasjonsgruppe getStasjonsgruppe() {
		return stasjonsgruppe;
	}

    public String getStasjonsgruppeId() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setStartTid(DateTime startTid) {
        this.startTid = startTid;
    }

    public void setSluttTid(DateTime sluttTid) {
        this.sluttTid = sluttTid;
    }

    public void setVolumForTidsrom(DateTime startTid, DateTime sluttTid, int tilgjengeligMw) {
        volumPerioder.add(new Volumperiode(this, startTid, sluttTid, tilgjengeligMw));
    }

    @Override
    public String toString() {
        return "ReservekraftBud<" + stasjonsgruppe + ",fra " + startTid + ">";
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ReservekraftBud)) return false;
        ReservekraftBud bud = (ReservekraftBud)obj;

        if (id == null) return (this == bud);
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

    public List<Volumperiode> getVolumPerioder() {
        return volumPerioder;
    }

}
