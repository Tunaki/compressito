package fr.tunaki.compressito.view;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import fr.tunaki.compressito.i18n.Msg;

/**
 * Represents the compression dialog.
 * @author gboue
 */
public class OnGoingCompressionDialog extends JDialog {

    private static final long serialVersionUID = 8454042887864194231L;

    /**
     * Constuct a modal dialog that indicates the compression process is in progress.
     * @param parent Frame parente.
     */
    public OnGoingCompressionDialog(JFrame parent) {
        super(parent, true);
        add(new JLabel(Msg.get("compression.progress")));
        setSize(200, 100);
        setLocationRelativeTo(parent);
    }

    /**
     * Makes this dialog visible.
     */
    public void open() {
        setVisible(true);
    }

    /**
     * Hides this dialog.
     */
    public void close() {
        setVisible(false);
    }

}