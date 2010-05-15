package no.statnett.larm.poc.client.stasjon;

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

import org.junit.Test;

public class StasjonListDialogTest {

    @Test
    public void shouldCreateSpecification() {
        StasjonListDialog dialog = new StasjonListDialog(null);
        StasjonSpecificationPanel searchPanel = (StasjonSpecificationPanel) dialog.getSearchPanel();
        searchPanel.getIncludeF01Checkbox().setSelected(true);
        searchPanel.getIncludeF02Checkbox().setSelected(false);

        StasjonSpecification specification = searchPanel.getSpecification();

        assertThat(specification.getIncludeF01()).isTrue();
        assertThat(specification.getIncludeF02()).isFalse();
    }

    @Test
    public void shouldDisplaySearchResults() throws Exception {
        Repository repository = mock(Repository.class);
        StasjonListDialog dialog = new StasjonListDialog(SyncAsyncProxy.createAsyncProxy(RepositoryAsync.class, repository));

        List<Stasjon> stasjoner = Arrays.asList(Stasjon.medNavnOgFastområde("Foo", "F01"), Stasjon.medNavnOgFastområde("Bar", "F09"));
        when(repository.find(any(StasjonSpecification.class))).thenReturn(stasjoner);

        dialog.getSearchPanel().getSearchButton().doClick();
        dialog.getSearchPanel().getSearchButton().doClick();

        JTable searchResult = dialog.getSearchResult();
        assertThat(searchResult.getModel().getColumnName(0)).isEqualTo("Stasjonsnavn");
        assertThat(searchResult.getModel().getColumnName(1)).isEqualTo("Fastområde");
        assertThat(searchResult.getModel().getValueAt(0, 0)).isEqualTo("Foo");
        assertThat(searchResult.getModel().getValueAt(1, 1)).isEqualTo("F09");
        assertThat(searchResult.getModel().getRowCount()).isEqualTo(stasjoner.size());
    }


}
