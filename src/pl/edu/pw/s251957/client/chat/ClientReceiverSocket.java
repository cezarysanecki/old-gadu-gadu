package pl.edu.pw.s251957.client.chat;

import pl.edu.pw.s251957.common.Response;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;

/** Klasa klienta odpowiadająca za odbieranie komunikatów z serwera i informowanie o tym zarządcę
 * {@link ClientHandler}. Rozszerza klasę {@link Thread}.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @see Thread
 * @see ClientHandler
 * @see ObjectInputStream
 * @since 01.11.2019r.
 */
public class ClientReceiverSocket extends Thread {
    /** Zarządza połączeniem z serwerem */
    private ClientHandler clientHandler;

    /** Strumień wejściowy połaczenia */
    private ObjectInputStream objectInputStream;

    /**
     * Inicjalizuje nowy obiekt klasy {@code ClientReceiverSocket}. Przyjmuje jako argument obiekt odpowiedzialny za
     * zarządzenie połączeniem z serwerem {@code clientHandler}.
     *
     * @param clientHandler zarządca klienta
     */
    public ClientReceiverSocket(ClientHandler clientHandler) {
        this.clientHandler = clientHandler;
    }

    /**
     * Ustawia strumień wejściowy połączenia z serwerem.
     *
     * @param objectInputStream strumień wejściowy
     */
    public void setObjectInputStream(ObjectInputStream objectInputStream) {
        this.objectInputStream = objectInputStream;
    }

    /**
     * Odpowiada za obieranie komunikatów z serwera dopóki wystepuje połączenie z serwerem. Metoda pochodząca z
     * rozszerzanej klasy {@link Thread}
     */
    @Override
    public void run() {
        while (clientHandler.isConnected()) {
            try {
                Response response = (Response) objectInputStream.readObject();
                handleResponse(response);
            } catch (ClassNotFoundException e) {
                clientHandler.showError("Niepoprawny komunikat");
            } catch (IOException ignore) {}
        }
    }

    /**
     * Na podstawie danych z serwera {@link Response} podejmuje odpowiednie działania.
     *
     * @param response odpowiedź z serwera
     */
    @SuppressWarnings("unchecked")
	private void handleResponse(Response response) {
        switch (response.getResponseType()) {
            case USERS:
                clientHandler.showActiveUsers((List<String>) response.getData());
                clientHandler.informUserConnected();
                break;
            case REJECT_CONNECTION:
                clientHandler.rejectConnection(response.getMessage());
                break;
            case MESSAGE:
                clientHandler.receiveMessage(response);
                break;
            case END_CONVERSATION:
                clientHandler.receiveEndingMessage(response);
                break;
            case INFO:
                clientHandler.showInfo(response.getMessage());
                break;
            case SERVER_CLOSE:
                clientHandler.closeServer(response.getMessage());
                break;
            case ERROR:
                clientHandler.showError(response.getMessage());
                break;
            default:
                clientHandler.showError("Nie znana odpowiedź z serwera!");
        }
    }

    /**
     * Rozpoczyna pracę wątku {@link Thread} odpowiedzialną za obieranie komunikatów.
     */
    public void startReceiving() {
        start();
    }

    /**
     * Zamyka strumień wejściowy oraz zatrzmuje wątek.
     */
    public void closeReceiver() {
        try {
            if(objectInputStream != null) {
                objectInputStream.close();
            }
        } catch (IOException e) {
            clientHandler.showError("Błąd przy zamykaniu połączenia");
        }

        interrupt();
    }
}
