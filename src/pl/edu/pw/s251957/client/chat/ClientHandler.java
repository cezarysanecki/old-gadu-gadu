package pl.edu.pw.s251957.client.chat;

import pl.edu.pw.s251957.client.gui.Client;
import pl.edu.pw.s251957.client.util.ServerConfigurator;
import pl.edu.pw.s251957.client.util.model.ServerConnectionConfig;
import pl.edu.pw.s251957.common.Command;
import pl.edu.pw.s251957.common.Response;
import pl.edu.pw.s251957.client.util.UnacceptableClientConfigException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Główna klasa klienta odpowiadająca za nawiązanie połączenia z serwerem, wysyłanie i odbieranie kumunikatów z
 *  i do interfejsu użytkownika oraz zarządzanie operacjami wejścia i wyjścia z serwera. Implementuje interfejs
 *  {@link Client}.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @see Client
 * @see ClientUI
 * @see ClientSenderSocket
 * @see ClientReceiverSocket
 * @since 01.11.2019r.
 */
public class ClientHandler implements Client {
    /** Interfejs uzytkownika */
    private ClientUI clientUI;

    /** Zmienna odpowiedzialna za wysyłanie komunikatów do serwera */
    private ClientSenderSocket clientSender;
    /** Zmienna odpowiedzialna za odbieranie komunikatów z serwera */
    private ClientReceiverSocket clientReceiver;

    /** Nick reprezentujący klienta */
    private String nick;
    /** Gniazdo połączenia z serwerem */
    private Socket socket;

    /** Zbiór użytkowników, z którymi prowadzona jest obecnie komunikacja w komunikatorze */
    private Set<String> nickConversations;

    /**
     * Inicjalizuje nowy obiekt klasy {@code ClientHandler}. Przyjmuje jako argument obiekt odpowiedzialny za wysyłanie
     * komunikatów do serwera {@code clientSender}. Tworzy nowy, pusty zbiór nicków osób, z którymi może być
     * prowadzona rozmowa.
     *
     * @param clientSender zmienna odpowiedzialna za wysyłanie komuniaktów do serwera
     */
    public ClientHandler(ClientSenderSocket clientSender) {
        this.nickConversations = new HashSet<>();
        this.clientSender = clientSender;
    }

    /**
     * Ustanawia nowe połączenie z serwerem przyjmując jako parametr nick użytkownika {@code nick}. Metoda pochodząca z
     * interfejsu {@link Client}. Korzysta z klasy {@link ServerConfigurator}, aby odczytać plik konfiguracyjny.
     *
     * @param nick podany nick przez klienta
     */
    @Override
    public void connect(String nick) {
        try {
            makeConnection(ServerConfigurator.readConfigFile());
            runClientReceiver();
            setUserInfo(nick);
        } catch (UnacceptableClientConfigException e) {
            clientUI.showDialog("Błąd", e.getMessage(), true);
        }
    }

    /**
     * Przy pomocy pliku konfiguracyjnego ustanawia nowe połączenie z serwerem. Następnie pobiera strumienie i
     * deleguje je do opowiednich klas - {@link ClientSenderSocket} i {@link ClientReceiverSocket}
     *
     * @param config konfiguracja połączenia z serwerem
     * @throws UnacceptableClientConfigException wyjątek informujący o tym czy danego połączenie jest akceptowalne
     */
    private void makeConnection(ServerConnectionConfig config) throws UnacceptableClientConfigException {
        try {
            socket = new Socket(config.getHost(), config.getPort());
            clientReceiver = new ClientReceiverSocket(this);
            clientReceiver.setObjectInputStream(new ObjectInputStream(socket.getInputStream()));
            clientSender.setObjectOutputStream(new ObjectOutputStream(socket.getOutputStream()));
        } catch (IOException e) {
            throw new UnacceptableClientConfigException("Błąd połączenia z serwerem");
        }
    }

    /**
     * Uruchamia obiekt klasy {@link ClientReceiverSocket} opowiedzialny za odbieranie komunikatów z serwera.
     */
    private void runClientReceiver() {
        clientReceiver.startReceiving();
    }

    /**
     * Ustawia podstawowe informacje o kliencie. Wysyła informację do serwera z żądaniem  {@link Command}
     * o podłącznie do komunikatora.
     *
     * @param nick nick użytkownika
     */
    private void setUserInfo(String nick) {
        try {
            clientSender.send(new Command(Command.CommandType.CONNECT, nick, "", ""));
            nickConversations.add(nick);
            this.nick = nick;
        } catch (IOException e) {
            clientUI.showDialog("Błąd", "Błąd wysłania komunikatu połączenia", true);
        }
    }

    /**
     * Wysyła kommunikat {@link Command} do serwera. Metoda pochodząca z interfejsu {@link Client}.
     *
     * @param command żądanie do serwera
     */
    @Override
    public void send(Command command) {
        try {
            clientSender.send(command);
        } catch(IOException e) {
            clientUI.showDialog("Błąd", "Błąd wysłania komunikatu", true);
        }
    }

    /**
     * W przypadku odrzucenia połączenia do komunikatora przez serwer klient zostaje o tym poinformowany i kończy
     * połączenie informując użytkownika przez interfejsu użytkownika.
     *
     * @param message informacja, dlaczego połączenie zostało odrzucone
     */
    public void rejectConnection(String message) {
        clientUI.showDialog("Błąd", message, true);
        nickConversations.remove(nick);
        nick = null;
        closeConnection();
    }

    /**
     * Wysyła polecenie do interfejsu użytkownika, aby pokazał uzyskanych, aktywnych użytkowników.
     *
     * @param users aktywni użytkownicy
     */
    public void showActiveUsers(List<String> users) {
        clientUI.showUsers(users);
    }

    /**
     * Wysyła informację do interfejsu użytkownika.
     *
     * @param message informacja
     */
    public void showInfo(String message) {
        clientUI.showDialog("Informacja", message, false);
    }

    /**
     * Wysyła informację o błędzie do interfejsu użytkownika.
     *
     * @param message błąd
     */
    public void showError(String message) {
        clientUI.showDialog("Błąd", message, true);
    }

    /**
     * Zamyka połączenie z serwerem. W przypadku klient jest połączony z serwerem wysyła do serwera stosowny
     * komunikat {@link Command}. Metoda pochodząca z interfejsu {@link Client}.
     */
    @Override
    public void closeClientConnection() {
        if(isConnected()) {
            send(new Command(Command.CommandType.DISCONNECT, nick, "", "Koniec połączenia"));
        }

        closeConnection();
    }

    /**
     * Metoda ustawiająca interfejs użytkownika. Metoda pochodząca z interfejsu {@link Client}.
     *
     * @param clientUI interfejs użytykownika implementujący {@link ClientUI}
     */
    @Override
    public void setClientUI(ClientUI clientUI) {
        this.clientUI = clientUI;
    }

    /**
     * Informuje czy istnieje połączenie między serwerem a klientem. Metoda pochodząca z interfejsu {@link Client}.
     *
     * @return zwraca informację czy klient jest połączony z serwerem
     */
    @Override
    public boolean isConnected() {
        return socket != null && !socket.isClosed();
    }

    /**
     * Zamyka połączenie z serwerem oraz wszystkie strumienie. Informuje o zakończeniu połączenia interfejs użytkownika.
     */
    private void closeConnection() {
        if(isConnected()) {
            try {
                clientReceiver.closeReceiver();
                clientSender.closeSender();
                closeSocket();
            } catch(IOException e) {
                clientUI.showDialog("Błąd", "Błąd zamykania połączenia", true);
            }
        }

        clientUI.changeConnectionStatus(false);
    }

    /**
     * Zamyka połączenie z serwerem {@link Socket}.
     */
    private void closeSocket() {
        try {
            if(socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            clientUI.showDialog("Błąd", "Błąd zamykania połączenia", true);
        }
    }

    /**
     * Informuje interfejs użytkownika o zmianie statusu połączenia z serwerem.
     */
    public void informUserConnected() {
        clientUI.changeConnectionStatus(true);
    }

    /**
     * W przypadku, gdy serwer zostaje zamknięty informuje on o tym klientów.
     *
     * @param message treść błędu dla użytkownika
     */
    public void closeServer(String message) {
        showError(message);
        clientUI.closeClientChat();
    }

    /**
     * Podaje nick użytkownika.
     *
     * @return nick użytkownika
     */
    @Override
    public String getNick() {
        return nick;
    }

    /**
     * Podaje informację czy jest ustanowiona konwersacja z podanym użytkownikiem. Metoda pochodząca z
     * interfejsu {@link Client}.
     *
     * @param addresseeNick nick adresata
     * @return czy jest połączenie z użytkownikiem {@code addresseeNick}
     */
    @Override
    public boolean isConversationSet(String addresseeNick) {
        return nickConversations.contains(addresseeNick);
    }

    /**
     * Rozpoczęcie konwersacji z danym użytkownikiem {@code addresseeNick}. Metoda pochodząca z interfejsu {@link Client}.
     *
     * @param addresseeNick nick adresata
     * @return informacja z systemu
     */
    @Override
    public String startConversation(String addresseeNick) {
        nickConversations.add(addresseeNick);
        String systemMessage = "[SYSTEM] Rozpoczęto konwersację\n";

        try {
            clientSender.send(new Command(Command.CommandType.START_CONVERSATION, nick, addresseeNick, systemMessage));
        } catch (IOException e) {
            clientUI.showDialog("Błąd", "Błąd rozpoczynania konsersacji", true);
        }

        return systemMessage;
    }

    /**
     * Zakończenie konwersacji z danym użytkownikiem {@code addresseeNick}. Metoda pochodząca z interfejsu
     * {@link Client}.
     *
     * @param addresseeNick nick adresata
     * @return informacja z systemu
     */
    @Override
    public String endConversation(String addresseeNick) {
        String systemMessage = "[SYSTEM] Zakończono konwersację\n";

        try {
            clientSender.send(new Command(Command.CommandType.END_CONVERSATION, nick, addresseeNick, systemMessage));
        } catch (IOException e) {
            clientUI.showDialog("Błąd", "Błąd kończenia konsersacji", true);
        }

        return systemMessage;
    }

    /**
     * Usuwa zakończoną konwersację z użytkownikiem {@code addresseeNick} ze zbioru {@code addresseeNick}. Metoda
     * pochodząca z interfejsu {@link Client}.
     *
     * @param addresseeNick nick adresata
     */
    @Override
    public void removeEndedConversation(String addresseeNick) {
    	nickConversations.remove(addresseeNick);
    }

    /**
     * Przekazywanie otrzymanej wiadomości konwersacji z serwera do interfejsu użytkownika.
     *
     * @param response odpowiedź z serwera
     */
    public void receiveMessage(Response response) {
        String hostClient = (String) response.getData();

        if(!isConversationSet(hostClient)) {
            nickConversations.add(hostClient);
        }

        clientUI.receiveMessage(response);
    }

    /**
     * Otrzymanie informacji informującej interfejs użytkownia o zakończeniu konwersacji.
     *
     * @param response odpowiedź z serwera
     */
    public void receiveEndingMessage(Response response) {
        clientUI.receiveEndingMessage(response);
    }
}
