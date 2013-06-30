package fr.tunaki.compressito.view;

import javax.swing.JMenuItem;

/**
 * Extension of a {@link JMenuItem} that mimick the behavior of a drop-down list. Instead of just having a displayed text, this class adds a
 * <code>code</code> property that contains the meaningful value selected by the user.
 * @author gboue
 */
public class DropDownMenuItem extends JMenuItem {

    private static final long serialVersionUID = -6365871350464083772L;

    private final String code;

    /**
     * Construct a DropDownMenuItem with the given code.
     * @param code Code of this item.
     * @param text Label of this item.
     * @see JMenuItem
     */
    public DropDownMenuItem(String code, String text) {
        super(text);
        this.code = code;
    }

    /**
     * @return Gets the code of this menu item.
     */
    public String getCode() {
        return code;
    }

}