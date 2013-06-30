package fr.tunaki.compressito.i18n;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Central class for implementing internationalization capabilites.
 * <p>
 * This class holds the current locale of the user and listens to locale change event in order to update the resource bundle used.
 * @author gboue
 * @see ResourceBundle
 */
public final class Msg implements LocaleChangeListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(Msg.class);
    private static final String MESSAGES = "messages.messages";
    private static final String LOCALE_KEY = "user.locale";
    private static Locale locale = new Locale(Preferences.userNodeForPackage(Msg.class).get(LOCALE_KEY, "fr"));
    private static ResourceBundle bundle = ResourceBundle.getBundle(MESSAGES, locale);

    /**
     * Returns the text corresponding to the given i18n code in the current locale.
     * @param code I18n code.
     * @return Text corresponding to the given i18n code in the current locale.
     */
    public static String get(String code) {
        return bundle.getString(code);
    }

    @Override
    public void actionPerformed(LocaleChangeEvent e) {
        String newLocale = e.getLocale();
        LOGGER.info("Changement de la langue utilisée - nouvelle langue : " + newLocale);
        locale = new Locale(newLocale);
        bundle = ResourceBundle.getBundle(MESSAGES, locale);
        Preferences.userNodeForPackage(Msg.class).put(LOCALE_KEY, newLocale);
    }

}