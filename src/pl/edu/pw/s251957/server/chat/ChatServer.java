package pl.edu.pw.s251957.server.chat;

import pl.edu.pw.s251957.common.Command;
import pl.edu.pw.s251957.common.Response;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/** Klasa serwera odpowiadająca za odbieranie połączeń klientów i rozpoczynanie dla nich osobnych wątków obsługujących
 * połączenie. Rozszerza klasę {@link Thread}.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @see Thread
 * @since 01.11.2019r.
 */
public class ChatServer extends Thread {
    /** Wystawione gniazdo do połączenia z serwerem */
    private ServerSocket serverSocket;
    /** Mapa połączeń z użytkownikami */
    private Map<String, ClientConnection> nicksToClientConnections;

    /**
     * Tworzy instancję serwera i inicjalizuje pustą mapę użytkowników.
     */
    public ChatServer() {
        this.nicksToClientConnections = new HashMap<>();
    }

    /**
     * Uruchamia główny serwer.
     *
     * @param port numer portu, na którym ma być uruchomiony serwer
     */
    public void startServer(int port) {
        initializeServerSocket(port);
        
        if(serverSocket != null) {
        	start();
        }
    }

    /**
     * Inicjalizuje serwerowe gniazdo.
     *
     * @param port numer portu, na którym ma być uruchomiony serwer
     */
    private void initializeServerSocket(int port) {
        try {
            this.serverSocket = new ServerSocket(port);
            System.out.println("Uruchomiono server na porcie " + port);
        } catch (IOException e) {
            System.out.println("Nie można uruchomić serwera na porcie " + port);
        }
    }

    /**
     * Odpowiada za obieranie połączeń z serwerem dopóki jest on uruchomiony. Metoda pochodząca z
     * rozszerzanej klasy {@link Thread}
     */
    @Override
    public void run() {
        while (!serverSocket.isClosed()) {
            try {
                Socket socket = serverSocket.accept();
                handleIncomingConnection(socket);
            } catch (IOException ignored) {}
        }
    }

    /**
     * Obsługuje przychodzące połączenia do serwera.
     *
     * @param socket połączenie z klientem
     */
    private synchronized void handleIncomingConnection(Socket socket) {
        ClientConnection clientConnection = new ClientConnection(this, socket);
        clientConnection.start();
    }

    /**
     * Zatrzymanie serwera. Informuje o tym wszystkich podłączonych klientów.
     */
    public void stopServer() {
        notifyAllClients(Response.ResponseType.SERVER_CLOSE,"Serwer został wyłączony", null);

        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }

        System.out.println("Zamknięto połączenie serwera");
        interrupt();
    }

    /**
     * Dodaje nowego klienta do obsługiwanych połączeń.
     *
     * @param nick nick klienta
     * @param clientConnection połączenie serwerowe z klientem
     */
    public synchronized void addClient(String nick, ClientConnection clientConnection) {
        nicksToClientConnections.put(nick, clientConnection);
        String message = "Doszedł użytkownik " + nick;
        notifyAllClients(Response.ResponseType.USERS, message, new ArrayList<>(nicksToClientConnections.keySet()));
    }

    /**
     * Usuwa klienta z obsługiwanych połączeń.
     *
     * @param nick nick klienta
     */
    public synchronized void removeClient(String nick) {
        nicksToClientConnections.remove(nick);
        String message = "Odszedł użytkownik " + nick;
        notifyAllClients(Response.ResponseType.USERS, message, new ArrayList<>(nicksToClientConnections.keySet()));
    }

    /**
     * Informuje wszystkich użytkowników o danej odpowiedzi serwera.
     *
     * @param responseType typ odpowiedzi
     * @param message treść odpowiedzi
     * @param data dane odpowiedzi
     */
    private void notifyAllClients(Response.ResponseType responseType, String message, Object data) {
        for (String clientNick : nicksToClientConnections.keySet()) {
            Response response = new Response(responseType, clientNick, message, data);
            nicksToClientConnections.get(clientNick).sendResponse(response);
        }
    }

    /**
     * Sprawdza czy dany nick nie jest już zajęty.
     *
     * @param nick sprawdzany nick
     * @return flaga czy dany nick jest używany
     */
    public boolean checkNickIsUsed(String nick) {
        return nicksToClientConnections.containsKey(nick);
    }

    /**
     * Zwraca port, na którym jest uruchomiony serwer.
     *
     * @return numer portu
     */
    public int getPort() {
        return serverSocket.getLocalPort();
    }

    /**
     * Zwraca informację o tym czy serwer jest uruchomiony.
     *
     * @return flaga o tym czy serwer jest uruchiony
     */
    public boolean isRunning() {
        return serverSocket != null && serverSocket.isBound();
    }

    /**
     * Wyślij odpowiedź serwera do danego użytkownika.
     *
     * @param cmd żądanie klienta
     */
    public void sendMessage(Command cmd) {
        if(nicksToClientConnections.containsKey(cmd.getAddresseeNick())) {
            Response response = new Response(Response.ResponseType.MESSAGE, cmd.getAddresseeNick(), cmd.getMessage(), cmd.getClientNick());
            nicksToClientConnections.get(cmd.getAddresseeNick()).sendResponse(response);
        }
    }

    /**
     * Wyślij kończącą odpowiedź serwera do danego użytkownika.
     *
     * @param cmd żądanie klienta
     */
    public void sendEndingMessage(Command cmd) {
        if(nicksToClientConnections.containsKey(cmd.getAddresseeNick())) {
            Response response = new Response(Response.ResponseType.END_CONVERSATION, cmd.getAddresseeNick(), cmd.getMessage(), cmd.getClientNick());
            nicksToClientConnections.get(cmd.getAddresseeNick()).sendResponse(response);
        }
    }
}
