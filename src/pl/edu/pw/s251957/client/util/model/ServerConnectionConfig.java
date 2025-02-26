package pl.edu.pw.s251957.client.util.model;

/** Klasa przedstawiające dane połączenia z serwerem.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @since 01.11.2019r.
 */
public class ServerConnectionConfig {
    /** Adres hosta */
    private String host;
    /** Port hosta */
    private int port;

    /**
     * Inicjalizuje nowy obiekt danych połączenia z serwerem.
     *
     * @param host adres hosta
     * @param port port hosta
     */
    public ServerConnectionConfig(String host, int port) {
        this.host = host;
        this.port = port;
    }

    /**
     * Zwraca adres hosta.
     *
     * @return adres hosta
     */
    public String getHost() {
        return host;
    }

    /**
     * Zwraca port hosta.
     *
     * @return port hosta
     */
    public int getPort() {
        return port;
    }
}
