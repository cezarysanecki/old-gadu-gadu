package pl.edu.pw.s251957.common;

import java.io.Serializable;

/** Klasa przedstawiające dane żadania do serwera. Implementuje interfejs {@link Serializable}, aby można byłą ją
 * wysyłąć przez połączenie.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @since 01.11.2019r.
 */
public class Command implements Serializable {
    /** Wersjonowanie serializacji */
	private static final long serialVersionUID = 123456789L;
	/** Typ żądania */
    private CommandType commandType;
    /** Nick użytkownika wysyłającego żądanie */
    private String clientNick;
    /** Nick użytkownika, do którego adresowane jest żądanie */
    private String addresseeNick;
    /** Treść żądania */
    private String message;

    /**
     * Tworzy instację żądania do serwera.
     *
     * @param commandType typ żądania
     * @param clientNick nick użytkownika wysyłąjącego
     * @param addresseeNick nick użytkownika adresowanego
     * @param message treść żądania
     */
    public Command(CommandType commandType, String clientNick, String addresseeNick, String message) {
        this.commandType = commandType;
        this.clientNick = clientNick;
        this.addresseeNick = addresseeNick;
        this.message = message;
    }

    /**
     * Zwraca typ żądania.
     *
     * @return typ żądania
     */
    public CommandType getCommandType() {
        return commandType;
    }

    /**
     * Zwraca nick użytkownika wysyłąjącego żądanie.
     *
     * @return nick użytkownika wysyłąjącego żądanie
     */
    public String getClientNick() {
        return clientNick;
    }

    /**
     * Zwraca nick użytkownika adresowanego.
     *
     * @return nick użytkownika adresowanego
     */
    public String getAddresseeNick() {
        return addresseeNick;
    }

    /**
     * Zwraca wiadomość żądania.
     *
     * @return wiadomość żądania.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Typ żadania.
     */
    public enum CommandType {
        /** Żądanie połączenia z serwerem */
        CONNECT,
        /** Żądanie rozpoczęcia konwersacji */
        START_CONVERSATION,
        /** Żądanie wysłania wiadomości */
        SEND_MESSAGE,
        /** Żądanie zakończenia konwersacji */
        END_CONVERSATION,
        /** Żądanie rozłączenia z serwerem */
        DISCONNECT
    }
}
