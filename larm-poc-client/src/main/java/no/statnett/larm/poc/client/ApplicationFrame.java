package no.statnett.larm.poc.client;

import no.statnett.larm.core.async.SwingWorkerAsyncProxy;
import no.statnett.larm.core.repository.HibernateRepository;
import no.statnett.larm.core.repository.RepositoryAsync;
import no.statnett.larm.poc.client.stasjon.Stasjon;
import no.statnett.larm.poc.client.stasjon.StasjonListDialog;

import javax.swing.*;

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

    public static void main(String[] args) {
        HibernateRepository repository = HibernateRepository.withFileDatabase(Stasjon.class);
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 1", "F01"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 2", "F01"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 3", "F02"));
        repository.insert(Stasjon.medNavnOgFastområde("Stasjon 4", "F03"));

        StasjonListDialog dialog = new StasjonListDialog(SwingWorkerAsyncProxy.createAsyncProxy(RepositoryAsync.class, repository));
        display("Stasjoner", dialog);

    }


}
