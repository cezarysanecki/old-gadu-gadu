package pl.edu.pw.s251957.client.gui;

import pl.edu.pw.s251957.client.chat.ClientUI;
import pl.edu.pw.s251957.common.Command;

/** Interfejs przedstawiający podstawowe metody odpowiedzialne za informowanie klienta użytkownika.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @since 01.11.2019r.
 */
public interface Client {
    /**
     * Łączy danego klienta o zadanym nicku.
     *
     * @param nick wybrany nick klienta
     */
    void connect(String nick);

    /**
     * Ustawia dany interfejs użytkownika.
     *
     * @param clientUI interfejs uzytkownika implementujący interfejs {@link ClientUI}
     */
    void setClientUI(ClientUI clientUI);

    /**
     * Informuje czy klient jest podłączony do serwera.
     *
     * @return czy istnieje połączenie
     */
    boolean isConnected();

    /**
     * Zamyka aktualne połączenie klienta z serwerem.
     */
    void closeClientConnection();

    /**
     * Wysyła dane żądanie {@link Command} do klienta.
     *
     * @param command żądanie do serwera
     */
    void send(Command command);

    /**
     * Pobiera nazwę klienta.
     *
     * @return nazwa klienta
     */
    String getNick();

    /**
     * Sprawdza czy istnieje aktualnie konwersacja z danym użytkownikiem {@code addresseeNick}.
     *
     * @param addresseeNick nick adresata
     * @return czy isnieje między nimi konwersacja
     */
    boolean isConversationSet(String addresseeNick);

    /**
     * Rozpoczyna konwersację z danym użytkownikiem {@code addresseeNick}.
     *
     * @param addresseeNick nick adresata
     * @return informacja z systemu
     */
    String startConversation(String addresseeNick);

    /**
     * Kończy konwersację z danym użytkownikiem {@code addresseeNick}.
     *
     * @param addresseeNick nick adresata
     * @return informacja z systemu
     */
    String endConversation(String addresseeNick);

    /**
     * Usuwa zakończoną konwersację z danym użytkownikiem {@code addresseeNick}.
     *
     * @param addresseeNick nick adresata
     */
    void removeEndedConversation(String addresseeNick);
}
