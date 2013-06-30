package fr.tunaki.compressito.i18n;

import java.util.EventListener;

/**
 * Listener to locale change event.
 * @author gboue
 */
public interface LocaleChangeListener extends EventListener {

    /**
     * Invoked when the current locale has changed.
     * @param e locale change event.
     */
    void actionPerformed(LocaleChangeEvent e);

}