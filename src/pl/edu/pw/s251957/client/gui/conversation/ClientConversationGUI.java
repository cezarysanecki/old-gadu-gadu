package pl.edu.pw.s251957.client.gui.conversation;

import pl.edu.pw.s251957.client.gui.ClientSwing;
import pl.edu.pw.s251957.common.Command;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Interfejs użytkownika wykorzystujący Swing'a do wyświetlania konwersacji pomiędzy dwoma klientami. Rozszerza klasę
 * {@link JFrame}.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @see ClientSwing
 * @since 01.11.2019r.
 */
public class ClientConversationGUI extends JFrame {
    /** Wersjonowanie serializacji */
	private static final long serialVersionUID = 456789123L;
    /** Pole tekstowe czatu */
    private JTextArea chatTextArea;
    /** Pole tekstowe wiadomości */
    private JTextArea messageTextArea;

    /** Informacja czy konwersacja jest w toku */
    private boolean conversationRunning;
    /** Główny interfejs użytkownika */
    private ClientSwing clientSwing;
    /** Nick użytkownika, z którym prowadzona jest konwersacja */
    private String addresseeNick;

    /**
     * Tworzy instancję interfejsu użytkownika opowiedzialnego za konwersację pomiędzy dwoma klientami. Przypisuje
     * akcje odpowiednim elementom interfejsu.
     *
     * @param title tytuł dla kowersacji
     * @param message początkowa wiadomość konwersacji
     * @param addresseeNick nick adresata
     * @param clientSwing główny interfejs użytownika klienta
     */
    public ClientConversationGUI(String title, String message, String addresseeNick, ClientSwing clientSwing) {
        super(title);

        this.addresseeNick = addresseeNick;
        this.clientSwing = clientSwing;
        this.conversationRunning = true;

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if(conversationRunning) {
                    clientSwing.endConversation(addresseeNick);
                }

                clientSwing.removeConversation(addresseeNick);
                dispose();
            }
        });

        initLayout(message);
        setVisible(true);

        addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowGainedFocus(WindowEvent e) {
                messageTextArea.requestFocusInWindow();
            }
        });
    }

    /**
     * Rozmieszcza interfejs konwersacji. Przyjmuje jako parametr wejściową wiadomość {@code message}. Ustawia
     * opowiednie akcje polu tekstowemu wiadomości.
     *
     * @param message początkowa wiadomość konwersacji
     */
    private void initLayout(String message) {
        setSize(500, 465);
        setResizable(false);
        setLayout(null);

        chatTextArea = new JTextArea();
        chatTextArea.setLineWrap(true);
        chatTextArea.setEditable(false);
        JScrollPane centerPanel = new JScrollPane(chatTextArea);
        centerPanel.setBounds(10, 10 ,475 ,350);
        add(centerPanel);

        messageTextArea = new JTextArea();
        messageTextArea.setLineWrap(true);
        JScrollPane bottomPanel = new JScrollPane(messageTextArea);
        bottomPanel.setBounds(10, 375 ,475 ,50);
        add(bottomPanel);

        messageTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !messageTextArea.getText().equals("")) {
                    String message = messageTextArea.getText() + '\n';
                    chatTextArea.append("Ty >> " + message);
                    sendMessage(message);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && !messageTextArea.getText().equals("")) {
                    messageTextArea.setText("");
                }
            }
        });

        chatTextArea.setText(message);
    }

    /**
     * Wysyła wiadomość przekazując ją do głównego interfejsu użytkownika.
     *
     * @param message treść wysyłanej wiadomości
     */
    private void sendMessage(String message) {
        if(conversationRunning) {
            clientSwing.sendMessage(new Command(Command.CommandType.SEND_MESSAGE, clientSwing.getClientNick(), addresseeNick, message));
        } else {
        	chatTextArea.append("[SYSTEM] >> Koniec rozmowy\n");
        }
    }

    /**
     * Wyświetla otrzymaną wiadomość {@code message} od użytkownika {@code nick}.
     *
     * @param nick nick nadawcy
     * @param message wiadomość klienta nadawcy
     */
    public void showReceivedMessage(String nick, String message) {
    	if(!conversationRunning) {
    		conversationRunning = true;
    	}
    	
        chatTextArea.append(nick + " >> " + message);
    }

    /**
     * Wyświetla otrzymaną, końcową wiadomość {@code message} od użytkownika {@code nick}.
     *
     * @param nick nick nadawcy
     * @param message wiadomość klienta nadawcy
     */
    public void showEndingMessage(String nick, String message) {
        chatTextArea.append(nick + " >> " + message);
        conversationRunning = false;
    }
}
