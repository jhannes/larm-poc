package no.statnett.larm.poc.client.stasjon;

import no.statnett.larm.core.repository.Repository;
import org.junit.Test;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StasjonListDialogTest {

    @Test
    public void shouldCreateSpecification() {
        StasjonListDialog dialog = new StasjonListDialog();
        dialog.getSearchPanel().getIncludeF01Checkbox().setSelected(true);
        dialog.getSearchPanel().getIncludeF02Checkbox().setSelected(false);

        StasjonSpecification specification = dialog.getSearchPanel().getSpecification();

        assertThat(specification.getIncludeF01()).isTrue();
        assertThat(specification.getIncludeF02()).isFalse();
    }

    @Test
    public void shouldDisplaySearchResults() throws Exception {
        StasjonListDialog dialog = new StasjonListDialog();
        Repository repository = mock(Repository.class);
        dialog.setRepository(repository);

        List<Stasjon> stasjoner = Arrays.asList(Stasjon.medNavnOgFastområde("Foo", "F01"), Stasjon.medNavnOgFastområde("Bar", "F09"));
        when(repository.find(any(StasjonSpecification.class))).thenReturn(stasjoner);

        dialog.getSearchPanel().getSearchButton().doClick();

        JTable searchResult = dialog.getSearchResult();
        assertThat(searchResult.getModel().getColumnName(0)).isEqualTo("Stasjonsnavn");
        assertThat(searchResult.getModel().getColumnName(1)).isEqualTo("Fastområde");
        assertThat(searchResult.getModel().getValueAt(0, 0)).isEqualTo("Foo");
        assertThat(searchResult.getModel().getValueAt(1, 1)).isEqualTo("F09");
    }


}
