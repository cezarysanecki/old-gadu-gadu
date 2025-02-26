package pl.edu.pw.s251957.client.util;

/** Wyjątek informujacy o nieodpowiednich danych konfiguracyjnych klienta. Rozszerza klasę {@link Exception}.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @since 01.11.2019r.
 */
public class UnacceptableClientConfigException extends Exception {
    /** Wersjonowanie serializacji */
	private static final long serialVersionUID = 579846123L;
    /**
     * Tworzy instację wyjątku.
     *
     * @param message wiadomość o błędzie
     */
    public UnacceptableClientConfigException(String message) {
        super(message);
    }
}
