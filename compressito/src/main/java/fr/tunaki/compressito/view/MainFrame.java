package fr.tunaki.compressito.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.EventListenerList;

import fr.tunaki.compressito.exec.CompressExec;
import fr.tunaki.compressito.i18n.LocaleChangeEvent;
import fr.tunaki.compressito.i18n.LocaleChangeListener;
import fr.tunaki.compressito.i18n.Msg;

/**
 * Main frame of this application.
 * @author gboue
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 4680572811590070535L;
    private static final int MINIMUM_SIZE = 500;

    private EventListenerList localeChangeListenerList = new EventListenerList();
    private MainMenuBar menuBar;
    private ConfigPanel configPanel;
    private JButton compressButton;

    /**
     * Construct this frame.
     */
    public MainFrame() {
        getContentPane().setLayout(new BorderLayout());
        menuBar = new MainMenuBar(this);
        setJMenuBar(menuBar);
        configPanel = new ConfigPanel(this);
        compressButton = new JButton(Msg.get("button.compress"));
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(compressButton);
        add(configPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        compressButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent event) {
                final OnGoingCompressionDialog compressionDialog = new OnGoingCompressionDialog(MainFrame.this);
                CompressExec exec = new CompressExec(configPanel.getImagesPath(), configPanel.getImageMagickPath());
                exec.addStartRunnable(new Runnable() {
                    @Override
                    public void run() {
                        compressionDialog.open();
                    }
                });
                exec.addStopRunnable(new Runnable() {
                    @Override
                    public void run() {
                        compressionDialog.close();
                        JOptionPane.showMessageDialog(MainFrame.this, Msg.get("compression.success.message"), Msg.get("compression.success.title"), JOptionPane.INFORMATION_MESSAGE);
                    }
                });
                exec.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
                    @Override
                    public void uncaughtException(Thread t, Throwable e) {
                        compressionDialog.close();
                        JOptionPane.showMessageDialog(MainFrame.this, e.getCause().getMessage(), e.getMessage(), JOptionPane.ERROR_MESSAGE);
                    }
                });
                exec.start();
            }
        });
        setVisible(true);
        setTitle("Compressito!");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setExtendedState(MAXIMIZED_BOTH);
        setMinimumSize(new Dimension(MINIMUM_SIZE, MINIMUM_SIZE));
    }

    /**
     * Add a listener to the locale change event.
     * @param l Listener to add.
     */
    public void addLocaleChangeListener(LocaleChangeListener l) {
        localeChangeListenerList.add(LocaleChangeListener.class, l);
    }

    /**
     * Remove the given listener from the list of listeners to the locale change event.
     * @param l Listener to remove.
     */
    public void removeFooListener(LocaleChangeListener l) {
        localeChangeListenerList.remove(LocaleChangeListener.class, l);
    }

    /**
     * Fires the locale change event with the given new locale.
     * @param locale New locale.
     */
    public void fireLocaleChangeEvent(String locale) {
        Object[] listeners = localeChangeListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == LocaleChangeListener.class) {
                ((LocaleChangeListener) listeners[i + 1]).actionPerformed(new LocaleChangeEvent(this, locale));
            }
        }
        compressButton.setText(Msg.get("button.compress"));
    }

}