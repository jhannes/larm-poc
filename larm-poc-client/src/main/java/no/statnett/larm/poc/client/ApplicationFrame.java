package no.statnett.larm.poc.client;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import no.statnett.larm.LarmHibernateRepository;
import no.statnett.larm.budservice.EdielService;
import no.statnett.larm.budservice.FileListener;
import no.statnett.larm.budservice.FileScanner;
import no.statnett.larm.core.async.SwingWorkerAsyncProxy;
import no.statnett.larm.core.repository.Repository;
import no.statnett.larm.core.repository.RepositoryAsync;
import no.statnett.larm.core.web.service.LarmHessianProxyFactory;
import no.statnett.larm.nettmodell.Elspotområde;
import no.statnett.larm.nettmodell.Stasjonsgruppe;
import no.statnett.larm.poc.client.stasjon.Stasjon;
import no.statnett.larm.reservekraft.ReservekraftBudListDialog;

public class ApplicationFrame {
    public static void display(final String title, final JPanel panel) {
        final int height = 600;
        final int width = 800;
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                JFrame frame = new JFrame(title);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
                frame.setContentPane(panel);
                frame.setSize(width, height);
                frame.setVisible(true);
            }
        });
    }

    public static void main(String[] args) {
        String clientUrl = args.length > 0 ? args[0] : null;

        ReservekraftBudListDialog dialog = new ReservekraftBudListDialog(createClientRepository(clientUrl));
        display("Visning av reservekraftbud: " + clientUrl, dialog);

    }

    private static RepositoryAsync createClientRepository(String clientUrl) {
        if (clientUrl == null) {
            final Repository repository = LarmHibernateRepository.withFileDb();

            if (repository.findAll(Elspotområde.class).isEmpty()) {
                repository.insertAll(new Elspotområde("NO1"), new Elspotområde("NO2"), new Elspotområde("NO3"), new Elspotområde("NO4"));
            }
            if (repository.findAll(Stasjonsgruppe.class).isEmpty()) {
                Elspotområde elspotområde = repository.findAll(Elspotområde.class).get(0);
                repository.insertAll(new Stasjonsgruppe("NOKG00116", "Sørfjord", elspotområde),
                        new Stasjonsgruppe("NOKG00056", "Borgund", elspotområde),
                        new Stasjonsgruppe("NOKG00049", "Sima", elspotområde));
            }

            FileScanner.scheduleEverySecond(new File("data/ediel/input"), new File("data/ediel/output"), new FileListener() {
                @Override
                public void processFile(String fileName, Reader inputFile, Writer outputFile) throws IOException {
                    new EdielService(repository).process(fileName, inputFile, outputFile);
                }
            });

            repository.insert(Stasjon.medNavnOgFastområde("Stasjon 1", "F01"));
            repository.insert(Stasjon.medNavnOgFastområde("Stasjon 2", "F01"));
            repository.insert(Stasjon.medNavnOgFastområde("Stasjon 3", "F02"));
            repository.insert(Stasjon.medNavnOgFastområde("Stasjon 4", "F03"));
            return SwingWorkerAsyncProxy.createAsyncProxy(RepositoryAsync.class, repository);
        }
        if (clientUrl.startsWith("jdbc:")) {
            return SwingWorkerAsyncProxy.createAsyncProxy(RepositoryAsync.class, LarmHibernateRepository.withJdbcUrl(clientUrl, "org.h2.Driver"));
        } else if (clientUrl.startsWith("http:") || clientUrl.startsWith("https:")) {
            return SwingWorkerAsyncProxy.createAsyncProxy(RepositoryAsync.class, LarmHessianProxyFactory.createProxy(Repository.class, clientUrl));
        } else {
            throw new IllegalArgumentException("Illegal repository URL " + clientUrl);
        }
    }


}
