package fr.tunaki.compressito;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.tunaki.compressito.i18n.Msg;
import fr.tunaki.compressito.view.MainFrame;

/**
 * Main entry-point for this program.
 * @author gboue
 */
public final class Launcher {

    private static final Logger LOGGER = LoggerFactory.getLogger(Launcher.class);

    private Launcher() {
        super();
    }

    /**
     * Main method.
     * <p>
     * Does not take any parameters. They are ignored if given.
     * @param args Program arguments.
     */
    public static void main(String[] args) {
        LOGGER.info("Lancement de l'application");
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                    LOGGER.error("Erreur lors de l'installation du look and feel", e);
                }
                MainFrame frame = new MainFrame();
                frame.addLocaleChangeListener(new Msg());
            }
        });
    }
}