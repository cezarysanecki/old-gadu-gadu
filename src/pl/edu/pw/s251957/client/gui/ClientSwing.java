package pl.edu.pw.s251957.client.gui;

import pl.edu.pw.s251957.client.chat.ClientUI;
import pl.edu.pw.s251957.client.gui.conversation.ClientConversationGUI;
import pl.edu.pw.s251957.common.Command;
import pl.edu.pw.s251957.common.Response;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Interfejs użytkownika wykorzystujący Swing'a do zarządzania główną częścią klienta. Rozszerza klasę
 * {@link JFrame} oraz implementuje interfejs {@link ClientUI}
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @see Client
 * @since 01.11.2019r.
 */
public class ClientSwing extends JFrame implements ClientUI {
    /** Wersjonowanie serializacji */
	private static final long serialVersionUID = 321654987L;
    /** Klient łączący się z serwerem. Implementuje interfejs {@link Client} */
    private Client client;

    /** Model listy użytkowników */
    private DefaultListModel<String> nicksListModel;
    /** Lista użytkowników */
    private JList<String> nicksList;
    /** Guzik odpowiedzialny za inicjalizację połączenia z serwerem */
    private JButton connectBtn;

    /** Mapa nicków obcych użytkowników z danymi konwersacjami */
    private Map<String, ClientConversationGUI> nickToConversations;

    /** Flaga czy użytkownik jest połączony z serwerem */
    private boolean userConnected;

    /**
     * Tworzy instancję interfejsu użytkownika opowiedzialnego za wizualizację połączenia klienta z serwerem.
     *
     * @param client klient łączący się z serwerem implementujący interfejs {@link Client}
     */
    public ClientSwing(Client client) {
        super("ChatClient");

        this.client = client;
        this.userConnected = false;
        this.nickToConversations = new HashMap<>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeClientChat();
            }
        });

        initLayout();
        setVisible(true);
    }

    /**
     * Wyświetla przekazanych użytkowników. Metoda pochodząca z interfejsu {@link ClientUI}.
     *
     * @param users lista użytkowników do wyświetlenia
     */
    @Override
    public void showUsers(List<String> users) {
        nicksListModel.clear();

        for (String user : users) {
            nicksListModel.addElement(user);
        }
    }

    /**
     * Zmienia status połączenia {@code isConnected}. Metoda pochodząca z interfejsu {@link ClientUI}.
     *
     * @param isConnected flaga czy istnieje połączenie
     */
    @Override
    public void changeConnectionStatus(boolean isConnected) {
        userConnected = isConnected;
        String connectBtnText = isConnected ? "Rozłącz" : "Połącz";
        connectBtn.setText(connectBtnText);
    }

    /**
     * Pokazuje graficznie infromację użytkownikowi.
     *
     * @param title tytuł informacji
     * @param message treść informacji
     * @param error flaga czy jest to błąd czy informacja
     */
    @Override
    public void showDialog(String title, String message, boolean error) {
        int dialogType = error ? JOptionPane.ERROR_MESSAGE : JOptionPane.INFORMATION_MESSAGE;
        JOptionPane.showMessageDialog(this, message, title, dialogType);
    }

    /**
     * Zamyka czat klienta.
     */
    @Override
    public void closeClientChat() {
        nicksListModel.clear();
        client.closeClientConnection();
    }

    /**
     * Przekazuje nick użytkownika
     *
     * @return nick użytkownika
     */
    public String getClientNick() {
        return client.getNick();
    }

    /**
     * Rozmieszcza graficzny interfejs użytkownika. Ustawia opowiednie akcje polu tekstowemu wiadomości.
     */
    private void initLayout() {
        int widthScreen = Toolkit.getDefaultToolkit().getScreenSize().width;
        int heightScreen = Toolkit.getDefaultToolkit().getScreenSize().height;

        setSize(300, 600);
        setBounds((widthScreen - getWidth()) / 2, (heightScreen - getHeight()) / 2, getWidth(), getHeight());
        setResizable(false);
        setLayout(null);

        nicksListModel = new DefaultListModel<>();
        nicksList = new JList<>(nicksListModel);
        JScrollPane panelRight = new JScrollPane(nicksList);
        panelRight.setBounds(10, 10 ,275 ,500);
        add(panelRight);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 3));
        buttonPanel.setBounds(10, 520, 275, 40);
        add(buttonPanel);

        connectBtn = new JButton("Połącz");
        connectBtn.addActionListener(connectButtonListener());
        buttonPanel.add(connectBtn);

        nicksList.addMouseListener(nicksListMouseListener(this));
    }

    /**
     * Ustawia listener dla guzika połączenia.
     *
     * @return listener guzika połączenia
     */
    private ActionListener connectButtonListener() {
        return (e) -> {
            if(userConnected) {
                closeClientChat();
            } else {
                ClientInputDialogSwing clientInputDialogSwing = new ClientInputDialogSwing(this);

                if(clientInputDialogSwing.isOkClicked()) {
                    client.connect(clientInputDialogSwing.getNick());
                }
            }
        };
    }

    /**
     * Ustawia listener listy użytkowników dla myszki
     *
     * @param clientSwing główna część interfejsu użytkownika
     * @return listener listy użytkowników dla myszki
     */
    private MouseAdapter nicksListMouseListener(ClientSwing clientSwing) {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                @SuppressWarnings("unchecked")
				JList<String> theList = (JList<String>) e.getSource();

                if (e.getClickCount() == 2) {
                    int index = theList.locationToIndex(e.getPoint());

                    if (index >= 0) {
                        String clickedElement = (String) theList.getModel().getElementAt(index);

                        if(!client.isConversationSet(clickedElement)) {
                            String systemMessage = client.startConversation(clickedElement);
                            nickToConversations.put(clickedElement, new ClientConversationGUI("Konsersacja z " + clickedElement,
                                    systemMessage, clickedElement, clientSwing));
                        }
                    }
                }
            }
        };
    }

    /**
     * Usuwa konwersację z klienta i interfejsu użytkownika
     *
     * @param addresseeNick nick adresata
     */
    public void removeConversation(String addresseeNick) {
        nickToConversations.remove(addresseeNick);
        client.removeEndedConversation(addresseeNick);
    }

    /**
     * Kończy konwersację z danym użytkownikiem
     *
     * @param addresseeNick nick adresata
     */
    public void endConversation(String addresseeNick) {
        client.endConversation(addresseeNick);
    }

    /**
     * Zarządza wyświetleniem otrzymanej wiadmości.
     *
     * @param response odpowiedź z serwera
     */
    @Override
    public void receiveMessage(Response response) {
        String hostNick = (String) response.getData();

        if(!nickToConversations.containsKey(hostNick)) {
            nickToConversations.put(hostNick, new ClientConversationGUI("Konsersacja z " + hostNick,
                    "[SYSTEM] Rozpoczęto konwersację z " + hostNick + '\n', hostNick, this));
        } else {
            nickToConversations.get(hostNick).showReceivedMessage(hostNick, response.getMessage());
        }
    }

    /**
     * Zarządza wyświetleniem otrzymanej, końcowej wiadmości.
     *
     * @param response odpowiedź z serwera
     */
    @Override
    public void receiveEndingMessage(Response response) {
        String hostNick = (String) response.getData();

        if(nickToConversations.containsKey(hostNick)) {
            nickToConversations.get(hostNick).showEndingMessage(hostNick, response.getMessage());
        }
    }

    /**
     * Wysyła wiadomośc do klienta.
     *
     * @param command żądanie do serwera
     */
    public void sendMessage(Command command) {
        client.send(command);
    }
}
