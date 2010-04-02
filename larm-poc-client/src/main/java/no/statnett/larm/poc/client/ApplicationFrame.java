package no.statnett.larm.poc.client;

import javax.swing.JFrame;
import javax.swing.JPanel;

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
}
