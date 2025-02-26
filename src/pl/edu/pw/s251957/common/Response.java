package pl.edu.pw.s251957.common;

import java.io.Serializable;

/** Klasa przedstawiające dane odpowiedzi serwera. Implementuje interfejs {@link Serializable}, aby można byłą ją
 * wysyłąć przez połączenie.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @since 01.11.2019r.
 */
public class Response implements Serializable {
    /** Wersjonowanie serializacji */
	private static final long serialVersionUID = 987654321L;
    /** Typ odpowiedzi*/
    final ResponseType responseType;
    /** Nick adresata */
    final String addresseeNick;
    /** Treść odpowiedzi */
    final String message;
    /** Dane odpowiedzi */
    final Object data;

    /**
     * Tworzy instację odpowiedzi serwera.
     *
     * @param responseType typ odpowiedzi
     * @param addresseeNick nick adresata
     * @param message treść odpowiedzi
     * @param data dane odpowiedzi
     */
    public Response(ResponseType responseType, String addresseeNick, String message, Object data) {
        this.responseType = responseType;
        this.addresseeNick = addresseeNick;
        this.message = message;
        this.data = data;
    }

    /**
     * Zwraca typ odpowiedzi.
     *
     * @return typ odpowiedzi
     */
    public ResponseType getResponseType() {
        return responseType;
    }

    /**
     * Zwraca nick adresata.
     *
     * @return nick adresata
     */
    public String getAddresseeNick() {
        return addresseeNick;
    }

    /**
     * Zwraca treść odpowiedzi.
     *
     * @return treść odpowiedzi
     */
    public String getMessage() {
        return message;
    }

    /**
     * Zwraca dane odpowiedzi.
     *
     * @return dane odpowiedzi
     */
    public Object getData() {
        return data;
    }

    /**
     * Typ odpowiedzi.
     */
    public enum ResponseType {
        /** Odpowiedź odrzucenia połączenia */
        REJECT_CONNECTION,
        /** Odpowiedź z listą użytkowników */
        USERS,
        /** Odpowiedź z wiadomością klienta */
        MESSAGE,
        /** Odpowiedź zakończenia konwersacji */
        END_CONVERSATION,
        /** Odpowiedź z informacją o błędzie */
        ERROR,
        /** Odpowiedź z informacją */
        INFO,
        /** Odpowiedź o zamknięciu serwera */
        SERVER_CLOSE
    }
}
