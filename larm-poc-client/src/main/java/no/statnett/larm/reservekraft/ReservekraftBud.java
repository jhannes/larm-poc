package no.statnett.larm.reservekraft;

import no.statnett.larm.nettmodell.Stasjonsgruppe;

import org.joda.time.DateTime;

public class ReservekraftBud {

    private Stasjonsgruppe stasjonsgruppe;
    private DateTime startTid;
    private DateTime sluttTid;

    public ReservekraftBud(Stasjonsgruppe stasjonsgruppe) {
        this.stasjonsgruppe = stasjonsgruppe;
        // TODO Auto-generated constructor stub
    }

    public String getBudreferanse() {
        // TODO Auto-generated method stub
        return null;
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
        // TODO Auto-generated method stub on May 8, 2010
        throw new UnsupportedOperationException("Not implemented yet" + startTid +  " " + sluttTid + " " + tilgjengeligMw);
    }

}
