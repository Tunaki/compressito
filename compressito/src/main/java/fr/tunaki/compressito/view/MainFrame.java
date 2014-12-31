package fr.tunaki.compressito.view;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	private JPanel centerPanel;
	private JButton compressButton;
	private JButton validateButton;
	private JButton cancelButton;
	private JComboBox<String> comboBox = new JComboBox<>();
	private JLabel leftCompareLabel;
	private JLabel rightCompareLabel;

	/**
	 * Construct this frame.
	 */
	public MainFrame() {
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
		menuBar = new MainMenuBar(this);
		setJMenuBar(menuBar);
		configPanel = new ConfigPanel(this);
		centerPanel = new JPanel();
		compressButton = new JButton(Msg.get("button.compress"));
		validateButton = new JButton(Msg.get("button.validate"));
		cancelButton = new JButton(Msg.get("button.cancel"));
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(compressButton);
		centerPanel.add(buttonPanel);
		comboBox.setVisible(false);
		validateButton.setVisible(false);
		cancelButton.setVisible(false);
		centerPanel.add(comboBox);
		centerPanel.add(validateButton);
		centerPanel.add(cancelButton);
		JPanel southPanel = new JPanel();
		JPanel comparePanel = new JPanel(new GridLayout(1, 2));
		leftCompareLabel = new JLabel();
		rightCompareLabel = new JLabel();
		comparePanel.add(leftCompareLabel);
		comparePanel.add(rightCompareLabel);
		southPanel.add(comparePanel);
		add(configPanel);
		add(centerPanel);
		add(southPanel);
		compressButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				Path backupDir = null;
				final OnGoingCompressionDialog compressionDialog = new OnGoingCompressionDialog(MainFrame.this);
				CompressExec exec = new CompressExec(configPanel.getImagesPath(), configPanel.getImageMagickPath());
				try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(configPanel.getImagesPath()), new DirectoryStream.Filter<Path>() {
					@Override
					public boolean accept(Path entry) throws IOException {
						return Files.isRegularFile(entry);
					}
				});) {
					backupDir = Files.createTempDirectory("compressito");
					for (Path path : ds) {
						Files.copy(path, backupDir.resolve(path.getFileName()), StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
					}
				} catch (IOException e) {
					String message = "<html><p style='width: 500px;'>" + Msg.get("compression.copy.error") + "</p><p style='width: 500px;'>" + e.getMessage();
					int answer = JOptionPane.showConfirmDialog(MainFrame.this, message, "Erreur", JOptionPane.ERROR_MESSAGE, JOptionPane.YES_NO_OPTION);
					if (answer == JOptionPane.NO_OPTION) {
						return;
					}
				}
				final Path backUpDirFinal = backupDir;
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
						if (backUpDirFinal != null) {
							addCompareView(backUpDirFinal, Paths.get(configPanel.getImagesPath()));
						}
						JOptionPane.showMessageDialog(MainFrame.this, Msg.get("compression.success.message"), Msg.get("compression.success.title"), JOptionPane.INFORMATION_MESSAGE);
					}
				});
				exec.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
					@Override
					public void uncaughtException(Thread t, Throwable e) {
						compressionDialog.close();
						String message = "<html><p style='width: 500px;'>" + e.getMessage();
						JOptionPane.showMessageDialog(MainFrame.this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
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
	
	private void addCompareView(final Path backupDir, final Path dir) {
		if (comboBox.getActionListeners().length > 0) {
			comboBox.removeActionListener(comboBox.getActionListeners()[0]);
		}
		if (validateButton.getActionListeners().length > 0) {
			validateButton.removeActionListener(validateButton.getActionListeners()[0]);
		}
		if (cancelButton.getActionListeners().length > 0) {
			cancelButton.removeActionListener(cancelButton.getActionListeners()[0]);
		}
		comboBox.removeAllItems();
		try (DirectoryStream<Path> ds = Files.newDirectoryStream(backupDir)) {
			for (Path path : ds) {
				comboBox.addItem(path.getFileName().toString());
			}
		} catch (IOException e) {
			String message = "<html><p style='width: 500px;'>" + e.getMessage();
			JOptionPane.showMessageDialog(MainFrame.this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}
		comboBox.setVisible(true);
		validateButton.setVisible(true);
		cancelButton.setVisible(true);
		validateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try (DirectoryStream<Path> ds = Files.newDirectoryStream(backupDir)) {
					for (Path path : ds) {
						Files.delete(path);
					}
					Files.delete(backupDir);
				} catch (IOException e) {
					String message = "<html><p style='width: 500px;'>" + e.getMessage();
					JOptionPane.showMessageDialog(MainFrame.this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
				}
				leftCompareLabel.setIcon(null);
				rightCompareLabel.setIcon(null);
				comboBox.setVisible(false);
				validateButton.setVisible(false);
				cancelButton.setVisible(false);
				JOptionPane.showMessageDialog(MainFrame.this, Msg.get("compression.validate.success.message"), Msg.get("compression.validate.success.title"), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir);
						DirectoryStream<Path> dsBackup = Files.newDirectoryStream(backupDir)) {
					for (Path path : ds) {
						Files.delete(path);
					}
					for (Path path : dsBackup) {
						Files.move(path, dir.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
					}
					Files.delete(backupDir);
				} catch (IOException e) {
					String message = "<html><p style='width: 500px;'>" + e.getMessage();
					JOptionPane.showMessageDialog(MainFrame.this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
				}
				leftCompareLabel.setIcon(null);
				rightCompareLabel.setIcon(null);
				comboBox.setVisible(false);
				validateButton.setVisible(false);
				cancelButton.setVisible(false);
				JOptionPane.showMessageDialog(MainFrame.this, Msg.get("compression.cancel.success.message"), Msg.get("compression.cancel.success.title"), JOptionPane.INFORMATION_MESSAGE);
			}
		});
		comboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateCompareView(backupDir, dir, (String) comboBox.getSelectedItem());
			}
		});
		updateCompareView(backupDir, dir, (String) comboBox.getSelectedItem());
	}
	
	private void updateCompareView(Path backupDir, Path dir, String selectedPicture) {
		try {
			BufferedImage leftImage = ImageIO.read(backupDir.resolve(selectedPicture).toFile());
			String newPictureName = selectedPicture.substring(0, selectedPicture.lastIndexOf('.')) + ".jpg";
			BufferedImage rightImage = ImageIO.read(dir.resolve(newPictureName).toFile());
			leftCompareLabel.setIcon(new ImageIcon(resize(leftImage, MainFrame.this.getWidth() / 2)));
			rightCompareLabel.setIcon(new ImageIcon(resize(rightImage, MainFrame.this.getWidth() / 2)));
		} catch (IOException e) {
			String message = "<html><p style='width: 500px;'>" + e.getMessage() + "<br>" + backupDir + "<br>" + dir + "<br>" + selectedPicture;
			JOptionPane.showMessageDialog(MainFrame.this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}
	}
	
	private static BufferedImage resize(BufferedImage image, int width) {
		int height = width * image.getHeight() / image.getWidth();
	    BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
	    Graphics2D g2d = (Graphics2D) bi.createGraphics();
	    g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
	    g2d.drawImage(image, 0, 0, width, height, null);
	    g2d.dispose();
	    return bi;
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
		validateButton = new JButton(Msg.get("button.validate"));
		cancelButton = new JButton(Msg.get("button.cancel"));
	}

}