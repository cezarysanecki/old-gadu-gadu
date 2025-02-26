package pl.edu.pw.s251957.client.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Interfejs użytkownika wykorzystujący Swing'a do wyświetlania formularza wejściowego dla klientami. Rozszerza klasę
 * {@link JDialog}.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @since 01.11.2019r.
 */
public class ClientInputDialogSwing extends JDialog {
    /** Wersjonowanie serializacji */
	private static final long serialVersionUID = 456123789L;
    /** Guzik potwierdzający */
    private JButton okBtn;
    /** Guzik anulujący */
    private JButton cancelBtn;
    /** Informacja o danej */
    private JLabel nickLabel;
    /** Pole do wprowadzenia nicku */
    private JTextField nickTextField;

    /** Flaga czy guzik potwierdzający został kliknięty */
    private boolean okClicked = false;
    /** Wprowadzony nick użytkownika */
    private String nick;

    /**
     * Tworzy instancję dialogu opowiedzialnego za wprowadzenie danych użytkownika.
     *
     * @param owner właściciel dialogu
     */
    public ClientInputDialogSwing(Frame owner) {
        super(owner, true);
        setTitle("Dane użytkownia");
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });

        this.nick = null;

        initLayout(owner);
        setVisible(true);
    }

    /**
     * Rozmieszcza interfejs dialogu. Ustawia opowiednie akcje guzikom.
     *
     * @param owner właściciel dialogu
     */
    private void initLayout(Frame owner) {
        setLayout(new GridLayout(3, 1, 15, 5));
        setSize(220, 130);
        setBounds(owner.getX() + ((owner.getWidth() - getWidth()) / 2), owner.getY() + ((owner.getHeight() - getHeight()) / 2), getWidth(), getHeight());
        setResizable(false);

        nickLabel = new JLabel("Podaj nick", SwingConstants.RIGHT);
        nickLabel.setHorizontalAlignment(SwingConstants.CENTER);

        nickTextField = new JTextField();

        add(nickLabel);
        add(nickTextField);

        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2));
        add(buttonsPanel);

        okBtn = new JButton("OK");
        okBtn.addActionListener(okButtonListener());
        buttonsPanel.add(okBtn);

        cancelBtn = new JButton("Anuluj");
        cancelBtn.addActionListener((e) -> {
            okClicked = false;
            dispose();
        });
        buttonsPanel.add(cancelBtn);
    }

    /**
     * Informuje czy guzik potwierdzający został kliknięty.
     *
     * @return flaga czy guzik potwierdzający został kliknięty.
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Zwraca nick użytkownika.
     *
     * @return nick użytkownika
     */
    public String getNick() {
        return nick;
    }

    /**
     * Ustawia akcję dla guzika potwierdzającego.
     *
     * @return listener dla guzika potwierdzającego
     */
    private ActionListener okButtonListener() {
        return (e -> {
            nick = nickTextField.getText();

            if(nick.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Nie podano nicku", "Brak nicku", JOptionPane.ERROR_MESSAGE);
            } else {
                okClicked = true;
                dispose();
            }
        });
    }
}
