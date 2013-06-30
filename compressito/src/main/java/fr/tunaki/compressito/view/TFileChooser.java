package fr.tunaki.compressito.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import fr.tunaki.compressito.i18n.LocaleChangeEvent;
import fr.tunaki.compressito.i18n.LocaleChangeListener;
import fr.tunaki.compressito.i18n.Msg;

/**
 * Panel that represents a custom file chooser.
 * <p>
 * This consists in a non editable text field followed by a button. The button opens a {@link JFileChooser} and the path to the selected
 * folder is shown in the text field.
 * @author gboue
 */
public abstract class TFileChooser extends JPanel implements LocaleChangeListener {

    private static final long serialVersionUID = -5827311177505012693L;

    private JButton button;
    private JTextField textField;
    private JFrame parentFrame;
    private String title;

    /**
     * Construct this object with the given main frame and the title for this panel border.
     * @param parentFrame Parent frame.
     * @param title Title of this panel border.
     */
    public TFileChooser(MainFrame parentFrame, String title) {
        super(new GridBagLayout());
        this.parentFrame = parentFrame;
        this.title = title;
        parentFrame.addLocaleChangeListener(this);
        setBorder(new TitledBorder(Msg.get(title)));
        textField = new JTextField();
        textField.setEditable(false);
        button = new JButton(Msg.get("button.select"));
        button.addActionListener(new ButtonActionListener());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = GridBagConstraints.RELATIVE;
        add(textField, gbc);
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        add(button, gbc);
    }

    private final class ButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            int returnState = fileChooser.showOpenDialog(parentFrame);
            if (returnState == JFileChooser.APPROVE_OPTION) {
                String path = fileChooser.getSelectedFile().getAbsolutePath();
                textField.setText(path);
                onFileSelect(path);
            }
        }
    }

    @Override
    public void actionPerformed(LocaleChangeEvent e) {
        setBorder(new TitledBorder(Msg.get(title)));
        button.setText(Msg.get("button.select"));
    }

    /**
     * Specifies what to do when the user selects a folder in the file chooser dialog.
     * @param path Path to the selected folder.
     */
    protected abstract void onFileSelect(String path);

    /**
     * @return Returns the path stored in the text field.
     */
    public String getPath() {
        return textField.getText();
    }

    /**
     * Sets the given text to the text field.
     * @param text Text.
     */
    public void setText(String text) {
        textField.setText(text);
    }
}