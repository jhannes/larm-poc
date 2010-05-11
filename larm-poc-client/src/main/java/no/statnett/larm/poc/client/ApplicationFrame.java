package no.statnett.larm.poc.client;

import javax.naming.NamingException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.core.async.SwingWorkerAsyncProxy;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.RepositoryAsync;
import no.statnett.larm.core.web.service.LarmHessianProxyFactory;
import no.statnett.larm.poc.client.stasjon.Stasjon;
import no.statnett.larm.poc.client.stasjon.StasjonListDialog;

public class ApplicationFrame {
    public static void display(final String title, final JPanel panel) {
        final int height = 600;
        final int width = 800;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(title);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setContentPane(panel);
                frame.setSize(width, height);
                frame.setVisible(true);
            }
        });
    }

    public static void main(String[] args) throws NamingException {
        String clientUrl = args.length > 0 ? args[0] : null;

        StasjonListDialog dialog = new StasjonListDialog(createClientRepository(clientUrl));
        display("Visning av stasjoner: " + clientUrl, dialog);

    }

    private static RepositoryAsync createClientRepository(String clientUrl) throws NamingException {
        if (clientUrl == null) {
            Repository repository = LarmHibernateRepository.withFileDb();
            repository.insert(Stasjon.medNavnOgFastområde("Stasjon 1", "F01"));
            repository.insert(Stasjon.medNavnOgFastområde("Stasjon 2", "F01"));
            repository.insert(Stasjon.medNavnOgFastområde("Stasjon 3", "F02"));
            repository.insert(Stasjon.medNavnOgFastområde("Stasjon 4", "F03"));
            return SwingWorkerAsyncProxy.createAsyncProxy(RepositoryAsync.class, repository);
        }
        if (clientUrl.startsWith("jdbc:")) {
            return SwingWorkerAsyncProxy.createAsyncProxy(RepositoryAsync.class, LarmHibernateRepository.withJdbcUrl(clientUrl));
        } else if (clientUrl.startsWith("http:") || clientUrl.startsWith("https:")) {
            return SwingWorkerAsyncProxy.createAsyncProxy(RepositoryAsync.class, LarmHessianProxyFactory.createProxy(Repository.class, clientUrl));
        } else {
            throw new IllegalArgumentException("Illegal repository URL " + clientUrl);
        }
    }


}
