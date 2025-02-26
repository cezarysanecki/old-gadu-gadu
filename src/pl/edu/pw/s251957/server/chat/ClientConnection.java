package pl.edu.pw.s251957.server.chat;

import pl.edu.pw.s251957.common.Command;
import pl.edu.pw.s251957.common.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/** Klasa serwera odpowiadająca za połączenie z konkretnym klientem. Rozszerza klasę {@link Thread}.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @see ObjectOutputStream
 * @see ObjectInputStream
 * @see ChatServer
 * @since 01.11.2019r.
 */
public class ClientConnection extends Thread {
    /** Gniazdo połączenia z klientem */
    private Socket socket;
    /** Strumień wyjściowy połączenia z klientem */
    private ObjectOutputStream objectOutputStream;
    /** Strumień wejściowy połączenia z klientem */
    private ObjectInputStream objectInputStream;

    /** Nick obsługiwanego klienta */
    private String clientNick;
    /** Główna, zarządzająca część serwera */
    private ChatServer chatServer;

    /**
     * Inicjalizuje nowy obiekt klasy {@link ClientConnection} serwera odpowiadający za połączenie z konkretnym
     * klientem. Przyjmuje jako argument obiekt odpowiedzialny za zarządzenie serwerem {@code chatServer} oraz gniazdo
     * połączenia z konkretnym klientem.
     *
     * @param chatServer główna, zarządzająca część serwera
     * @param socket gniazdo połączenia z konkretnym klientem
     */
    public ClientConnection(ChatServer chatServer, Socket socket) {
        this.chatServer = chatServer;
        this.socket = socket;
        openStreams();
    }

    /**
     * Otwiera wszystkie strumienie połączenia z konkretnym klientem
     */
    private void openStreams() {
        try {
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Błąd otwierania strumieni");
        }
    }

    /**
     * Odpowiada za obieranie żądań od klienta dopóki gniazdo połączenia nie jest zamknięte. Metoda pochodząca z
     * rozszerzanej klasy {@link Thread}
     */
    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                Command cmd = (Command) objectInputStream.readObject();
                handleCommand(cmd);
            } catch (IOException e) {
                closeConnection();
            } catch (ClassNotFoundException e) {
                System.out.println("Błąd przy odczytywaniu polecenia od klienta");
            }
        }
    }

    /**
     * Obsługa żądania klienta.
     *
     * @param cmd żądanie klienta
     */
    private void handleCommand(Command cmd) {
        switch (cmd.getCommandType()) {
            case CONNECT:
                handleConnectionCommand(cmd);
                break;
            case START_CONVERSATION:
            case SEND_MESSAGE:
                chatServer.sendMessage(cmd);
                break;
            case END_CONVERSATION:
                chatServer.sendEndingMessage(cmd);
                break;
            case DISCONNECT:
                stopConnection();
                break;
        }
    }

    /**
     * Obsługa żadania połączenia z serwerem.
     *
     * @param cmd żadanie klienta
     */
    private void handleConnectionCommand(Command cmd) {
        if(chatServer.checkNickIsUsed(cmd.getClientNick())) {
            Response response = new Response(Response.ResponseType.REJECT_CONNECTION, cmd.getClientNick(), "Nick jest zajęty", null);
            closeConnectionWithResponse(response);
        } else {
            welcomeNewUser(cmd.getClientNick());
        }
    }

    /**
     * Obsługuje dodawanie nowego użytkownika.
     *
     * @param nick nick użytkownika
     */
    private void welcomeNewUser(String nick) {
        setClientNick(nick);
        chatServer.addClient(clientNick, this);
    }

    /**
     * Ustawia nick nowego użytkownika.
     *
     * @param clientNick nick użytkownika
     */
    private void setClientNick(String clientNick) {
        this.clientNick = clientNick;
    }

    /**
     * Kończy połączenie z serwerem.
     */
    private void stopConnection() {
        chatServer.removeClient(clientNick);
        closeConnection();
    }

    /**
     * Wysyła odpowiedź do klienta.
     *
     * @param response odpowiedź serwera
     */
    public void sendResponse(Response response) {
        try{
            objectOutputStream.writeObject(response);
        } catch (IOException e) {
            System.out.println("Błąd przy wysyle komunikatu");
        }
    }

    /**
     * Zamyka połączenie z klientem wysyłając przy tym odpowiedź serwera.
     *
     * @param response odpowiedź serwera
     */
    public void closeConnectionWithResponse(Response response) {
        sendResponse(response);
        closeConnection();
    }

    /**
     * Zamyka połączenie z klientem zamykając przy tym gniazdo, strumienie oraz przerywając wątek.
     */
    private void closeConnection() {
        try {
            objectInputStream.close();
            objectOutputStream.close();
            socket.close();
        } catch (IOException e) {
            System.out.println("Błąd przy zamykaniu połączenia: " + e.getMessage());
        }

        interrupt();
    }
}
