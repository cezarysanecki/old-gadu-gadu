package pl.edu.pw.s251957.client;

import pl.edu.pw.s251957.client.chat.ClientHandler;
import pl.edu.pw.s251957.client.chat.ClientSenderSocket;
import pl.edu.pw.s251957.client.chat.ClientUI;
import pl.edu.pw.s251957.client.gui.Client;
import pl.edu.pw.s251957.client.gui.ClientSwing;

/** Klasa uruchomieniowa klienta.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @since 01.11.2019r.
 */
public class ClientMain {
	/**
	 * Inicjalizuje klienta i uruchamia go.
	 * 
	 * @param args parametry wejściowe
	 */
    public static void main(String[] args) {
        Client client = new ClientHandler(new ClientSenderSocket());
        ClientUI clientUI = new ClientSwing(client);
        client.setClientUI(clientUI);
    }
}
