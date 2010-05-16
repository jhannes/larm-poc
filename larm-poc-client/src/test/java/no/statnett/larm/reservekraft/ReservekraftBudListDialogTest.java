package no.statnett.larm.reservekraft;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.swing.JTable;

import no.statnett.larm.core.async.SyncAsyncProxy;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.RepositoryAsync;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;

import org.joda.time.DateMidnight;
import org.junit.Test;

public class ReservekraftBudListDialogTest {

    @Test
    public void shouldCreateSpecification() throws Exception {
        Elspotområde no1 = new Elspotområde("NO1"), no2 = new Elspotområde("NO2"), no3 = new Elspotområde("NO3");
        ReservekraftBudListDialog dialog = new ReservekraftBudListDialog(mock(RepositoryAsync.class));
        dialog.setElspotområder(Arrays.asList(no1, no2, no3));

        dialog.getSearchPanel().getDriftsdøgnField().setText("20.03.2010");
        dialog.getSearchPanel().getElspotområdeCheckbox(no1).setSelected(true);
        dialog.getSearchPanel().getElspotområdeCheckbox(no2).setSelected(false);

        ReservekraftBudSpecification specification = dialog.getSearchPanel().getSpecification();
        assertThat(specification.getDriftsdøgn()).isEqualTo(new DateMidnight(2010, 3, 20));
        assertThat(specification.getElspotområder())
            .contains(no1)
            .excludes(no2, no3);
    }


    @Test
    public void shouldDisplaySearchResults() throws Exception {
        Repository repository = mock(Repository.class);
        ReservekraftBudListDialog dialog = new ReservekraftBudListDialog(SyncAsyncProxy.createAsyncProxy(RepositoryAsync.class, repository));

        List<ReservekraftBud> bud = Arrays.asList(
                new ReservekraftBud(new Stasjonsgruppe("NOKG100000", "Foostasjon", new Elspotområde("NO1"))),
                new ReservekraftBud(new Stasjonsgruppe("NOKG100001", "Barstasjon", new Elspotområde("NO3"))));
        when(repository.find(any(ReservekraftBudSpecification.class))).thenReturn(bud);

        dialog.getSearchPanel().getSearchButton().doClick();
        dialog.getSearchPanel().getSearchButton().doClick();

        JTable searchResult = dialog.getSearchResult();
        assertThat(searchResult.getModel().getColumnName(0)).isEqualTo("Stasjonsgruppe");
        assertThat(searchResult.getModel().getColumnName(1)).isEqualTo("Elspotområde");
        assertThat(searchResult.getModel().getColumnName(2)).isEqualTo("Pris");
        assertThat(searchResult.getModel().getColumnName(3)).isEqualTo("Varighet");
        assertThat(searchResult.getModel().getColumnName(4)).isEqualTo("Hviletid");
        assertThat(searchResult.getModel().getColumnName(5)).isEqualTo("Opp/Ned");
        assertThat(searchResult.getModel().getValueAt(0, 0)).isEqualTo("Foostasjon");
        assertThat(searchResult.getModel().getValueAt(1, 1)).isEqualTo("NO3");
        assertThat(searchResult.getModel().getRowCount()).isEqualTo(bud.size());
    }


}
