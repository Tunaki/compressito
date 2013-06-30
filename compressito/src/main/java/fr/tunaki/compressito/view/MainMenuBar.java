package fr.tunaki.compressito.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import fr.tunaki.compressito.i18n.LocaleChangeEvent;
import fr.tunaki.compressito.i18n.LocaleChangeListener;
import fr.tunaki.compressito.i18n.Msg;

/**
 * Encapsultates the menu bar of the main frame.
 * @author gboue
 */
public class MainMenuBar extends JMenuBar implements LocaleChangeListener {

    private static final long serialVersionUID = 7293110499232112639L;

    private MainFrame frame;
    private JMenu fileMenu;
    private JMenu langMenu;
    private JMenuItem quitMenuItem;
    private JMenuItem frMenuItem;
    private JMenuItem enMenuItem;

    /**
     * Construct a new menu bar attached to the given frame.
     * @param frame Main frame.
     */
    public MainMenuBar(MainFrame frame) {
        this.frame = frame;
        fileMenu = new JMenu(Msg.get("menu.file"));
        langMenu = new JMenu(Msg.get("menu.lang"));
        quitMenuItem = new JMenuItem(Msg.get("menu.lang"));
        frMenuItem = new DropDownMenuItem("fr", Msg.get("menu.lang.french"));
        enMenuItem = new DropDownMenuItem("en", Msg.get("menu.lang.english"));
        frMenuItem.addActionListener(new LocaleMenuListener());
        enMenuItem.addActionListener(new LocaleMenuListener());
        quitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainMenuBar.this.frame.dispatchEvent(new WindowEvent(MainMenuBar.this.frame, WindowEvent.WINDOW_CLOSING));
            }
        });
        fileMenu.add(quitMenuItem);
        langMenu.add(frMenuItem);
        langMenu.add(enMenuItem);
        add(fileMenu);
        add(langMenu);
        frame.addLocaleChangeListener(this);
    }

    private final class LocaleMenuListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String locale = ((DropDownMenuItem) e.getSource()).getCode();
            frame.fireLocaleChangeEvent(locale);
        }
    }

    @Override
    public void actionPerformed(LocaleChangeEvent e) {
        fileMenu.setText(Msg.get("menu.file"));
        langMenu.setText(Msg.get("menu.lang"));
        frMenuItem.setText(Msg.get("menu.lang.french"));
        enMenuItem.setText(Msg.get("menu.lang.english"));
        quitMenuItem.setText(Msg.get("menu.file.quit"));
    }

}