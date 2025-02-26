package pl.edu.pw.s251957.client.chat;

import pl.edu.pw.s251957.common.Response;

import java.util.List;

/** Interfejs przedstawiający podstawowe metody odpowiedzialne za komunikację z interfejsem użytkownika.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @since 01.11.2019r.
 */
public interface ClientUI {
    /**
     * Pokazuje dialog w interfejsie użytkownika.
     *
     * @param title tytuł informacji
     * @param message treść informacji
     * @param error flaga czy jest to błąd czy informacja
     */
    void showDialog(String title, String message, boolean error);

    /**
     * Pokazuje w interfejsie użytkownika podanych użytkowników.
     *
     * @param users lista użytkowników do wyświetlenia
     */
    void showUsers(List<String> users);

    /**
     * Informuje interfejs użytkownika o zmianie statusu połączenia.
     *
     * @param isConnected flaga czy istnieje połączenie
     */
    void changeConnectionStatus(boolean isConnected);

    /**
     * Zamyka komunikator klienta.
     */
    void closeClientChat();

    /**
     * Zarządza otrzymaną wiadomością {@link Response}
     *
     * @param response odpowiedź z serwera
     */
    void receiveMessage(Response response);

    /**
     * Zarządza otrzymaną kończącą wiadomością {@link Response}
     *
     * @param response odpowiedź z serwera
     */
    void receiveEndingMessage(Response response);
}
