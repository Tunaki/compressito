package fr.tunaki.compressito.exec;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible for doing the actual work of compression. It is done by invoking ImageMagick's <code>mogrify</code> with
 * several parameters on every images in the work directory.
 * <p>
 * The actual call made is
 * 
 * <pre>
 * mogrify -format jpg -strip -quality 75 -compress JPEG *.*
 * </pre>
 * <p>
 * The image is converted to a lossy format : <code>jpg</code>.
 * @author gboue
 */
public class CompressExec {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompressExec.class);

    private String imagesPath;
    private String imageMagickPath;

    /**
     * Construct this object with the given path to images and ImageMagick
     * @param imagesPath Path to images
     * @param imageMagickPath Path to ImageMagick installation
     */
    public CompressExec(String imagesPath, String imageMagickPath) {
        this.imageMagickPath = imageMagickPath;
        this.imagesPath = imagesPath;
    }

    /**
     * Start the compression.
     * <p>
     * A {@link CompressionException} is thrown if an exception occurs during the compression process.
     */
    public void doCompress() {
        try {
            Process p = new ProcessBuilder()//
                    .command(Paths.get(imageMagickPath, "mogrify.exe").toString(), "-format", "jpg", "-strip", "-quality", "75", "-compress", "JPEG", "*.*")//
                    .directory(Paths.get(imagesPath).toFile())//
                    .start();
            p.waitFor();
            deleteAllNonJpegImages(Paths.get(imagesPath));
        } catch (IOException | InterruptedException e) {
            String message = "Exception lors de la compression des images";
            LOGGER.error(message, e);
            throw new CompressionException(message, e);
        }
    }

    private void deleteAllNonJpegImages(Path imagesPath) {
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(imagesPath, new DirectoryStream.Filter<Path>() {
            @Override
            public boolean accept(Path entry) throws IOException {
                return !entry.toString().endsWith(".jpg");
            }
        });) {
            for (Path path : ds) {
                Files.delete(path);
            }
        } catch (IOException e) {
            String message = "Exception lors de la suppression des fichiers non compressés";
            LOGGER.error(message, e);
            throw new CompressionException(message, e);
        }
    }
}