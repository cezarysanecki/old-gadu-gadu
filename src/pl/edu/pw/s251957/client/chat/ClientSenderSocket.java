package pl.edu.pw.s251957.client.chat;

import pl.edu.pw.s251957.common.Command;

import java.io.IOException;
import java.io.ObjectOutputStream;

/** Klasa klienta odpowiadająca za wysyłanie kumunikatów do serwera.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @see ObjectOutputStream
 * @since 01.11.2019r.
 */
public class ClientSenderSocket {
    /** Strumień wyjściowy połączenia */
    private ObjectOutputStream objectOutputStream;

    /**
     * Ustawia strumień wychodzący połączenia do serwera.
     *
     * @param objectOutputStream strumień wyjściowy
     */
    public void setObjectOutputStream(ObjectOutputStream objectOutputStream) {
        this.objectOutputStream = objectOutputStream;
    }

    /**
     * Wysyła żądanie {@link Command} do serwera.
     *
     * @param cmd żądanie do serwera
     * @throws IOException błąd wysyłania żądania
     */
    public void send(Command cmd) throws IOException {
        objectOutputStream.writeObject(cmd);
    }

    /**
     * Zamyka strumień wychodzący połaczenia z serwerem.
     *
     * @throws IOException błąd zamykania strumienia wyjściowego
     */
    public void closeSender() throws IOException {
        if(objectOutputStream != null) {
            objectOutputStream.close();
        }
    }
}
