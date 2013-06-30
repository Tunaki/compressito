package fr.tunaki.compressito.i18n;

import java.util.EventObject;

/**
 * Event encapsulating the new locale selected by the user.
 * @author gboue
 */
public class LocaleChangeEvent extends EventObject {

    private static final long serialVersionUID = -2778057549315420179L;

    private final String locale;

    /**
     * Construct this event with the given source and the new locale.
     * @param source Source of this event
     * @param locale New locale.
     */
    public LocaleChangeEvent(Object source, String locale) {
        super(source);
        this.locale = locale;
    }

    /**
     * @return Returns the locale encapsulated by this event.
     */
    public String getLocale() {
        return locale;
    }

}