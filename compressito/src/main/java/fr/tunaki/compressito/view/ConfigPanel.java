package fr.tunaki.compressito.view;

import java.awt.GridLayout;
import java.util.prefs.Preferences;

import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the configuration panel of the main frame. This panel holds file chooser instances for the selection of ImageMagick home and
 * the path to resources to treat.
 * @author gboue
 */
public class ConfigPanel extends JPanel {

    private static final long serialVersionUID = 8208671429048628823L;
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigPanel.class);
    private static final String IMAGE_MAGICK_PREF_KEY = "imagemagick.home";

    private TFileChooser imageMagickFileChooser;
    private TFileChooser imagesFileChooser;

    /**
     * Construct this panel with the given parent frame.
     * @param parentFrame Parent frame.
     */
    public ConfigPanel(MainFrame parentFrame) {
        setLayout(new GridLayout(2, 1));
        imageMagickFileChooser = new ImageMagickFileChooser(parentFrame, "title.imagemagick");
        imagesFileChooser = new ImagesFileChooser(parentFrame, "title.images");
        add(imageMagickFileChooser);
        add(imagesFileChooser);
    }

    /**
     * @return Returns the path to the images.
     */
    public String getImagesPath() {
        return imagesFileChooser.getPath();
    }

    /**
     * @return Returns the path to ImageMagick.
     */
    public String getImageMagickPath() {
        return imageMagickFileChooser.getPath();
    }

    private static final class ImageMagickFileChooser extends TFileChooser {

        private static final long serialVersionUID = -1602782998433351297L;

        private ImageMagickFileChooser(MainFrame parentFrame, String title) {
            super(parentFrame, title);
            setText(Preferences.userNodeForPackage(getClass()).get(IMAGE_MAGICK_PREF_KEY, null));
        }

        @Override
        protected void onFileSelect(String path) {
            LOGGER.info("Chemin vers ImageMagick modifié : " + path);
            Preferences.userNodeForPackage(getClass()).put(IMAGE_MAGICK_PREF_KEY, path);
        }

    }

    private static final class ImagesFileChooser extends TFileChooser {

        private static final long serialVersionUID = 8129668960817621677L;

        private ImagesFileChooser(MainFrame parentFrame, String title) {
            super(parentFrame, title);
        }

        @Override
        protected void onFileSelect(String path) {
            LOGGER.info("Chemin vers les images à traiter modifié : " + path);
        }

    }

}