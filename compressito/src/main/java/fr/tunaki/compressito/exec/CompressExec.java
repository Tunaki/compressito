package fr.tunaki.compressito.exec;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
public class CompressExec extends AbstractThread {

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
	 * The compressing operation operates in its own thread.
	 * <p>
	 * A {@link CompressionException} is thrown if an exception occurs during the compression process.
	 */
	@Override
	public void doRun() {
		List<String> command = new ArrayList<>();
		Path executable = Paths.get(imageMagickPath, "mogrify");
		if (executable.toFile().exists()) {
			command.add(executable.toString());
		} else {
			command.add(Paths.get(imageMagickPath, "magick").toString());
			command.add("mogrify");
		}
		command.addAll(Arrays.asList("-format", "jpg", "-strip", "-quality", "75", "-compress", "JPEG", "*.*"));
		try {
			Process p = new ProcessBuilder()
			.command(command)
			.directory(Paths.get(imagesPath).toFile())
			.redirectErrorStream(true)
			.start();
			StringBuilder sb = new StringBuilder();
			int returnCode;
			try (
					InputStreamReader isr = new InputStreamReader(p.getInputStream());
					BufferedReader br = new BufferedReader(isr);) {
				String line;
				while ((line = br.readLine()) != null) {
					LOGGER.info(line);
					sb.append(line).append(System.lineSeparator());
				}
				returnCode = p.waitFor();
			}
			if (returnCode != 0) {
				throw new CompressionException("Une erreur est survenue lors de la compression des images : " + sb);
			}
			deleteAllNonJpegImages(Paths.get(imagesPath));
		} catch (IOException | InterruptedException e) {
			String message = "Exception lors de la compression des images : " + e.getMessage();
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