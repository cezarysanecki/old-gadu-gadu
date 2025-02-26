package pl.edu.pw.s251957.server.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/** Klasa serwera obsługująca komendy konsolowe pochodzące od administratora odpwiedzialne za zarządzanie serwerem.
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @see ChatServer
 * @see BufferedReader
 * @since 01.11.2019r.
 */
public class ConsoleServerRunner {
    /** Serwer komunikatora */
    private ChatServer chatServer;
    /** Strumień odczytujący komendy */
    private BufferedReader commandReader;

    /** Flaga informująca o tym czy administracja serwera jest uruchomiona */
    private boolean running;

    /**
     * Inicjalizuje nowy obiekt klasy zarządzającej serwerem wraz ze strumieniem odczytującym komendy oraz ustawia
     * flagę, że administracja serwera jest uruchomiona
     */
    public ConsoleServerRunner() {
        this.commandReader = new BufferedReader(new InputStreamReader(System.in));
        this.running = true;
    }

    /**
     * Odczytaj przychodzącą komendę i przekaż ją do obsłużenia.
     */
    public void readCommand() {
        String command = null;
        try {
            command = commandReader.readLine();
        } catch (IOException e) {
            System.out.println("Błąd odczytu z konsoli");
        }

        handleCommand(command);
    }

    /**
     * Obsługuje przychodzące komendy. Są takie opcje jak:
     *  - start - uruchamia serwer
     *  - stop - zatrzymuje serwer
     *  - info - wyświetla dostępne opcje
     *  - exit - natychmiastowe wyłączenie serwera
     *  - niedozwolone komendy
     *
     * @param command komenda
     */
    private void handleCommand(String command) {
        if(command.equals("start")) {
            startChatServer();
        } else if(command.equals("stop")) {
            stopChatServer();
        } else if(command.equals("info")) {
            showServerCommand();
        } else if(command.equals("exit")) {
            stopChatServer();
            System.out.println("Do zobaczenia!");
            running = false;
        } else {
            System.out.println("Niedozwolona komenda - wpisz 'info', aby sprawdzić dostępne komendy");
        }
    }

    /**
     * Uruchamia główny serwer odpowiedzialny za komunikator.
     */
    private void startChatServer() {
        if(chatServer == null) {
            this.chatServer = new ChatServer();
        }

        try {
            if (!chatServer.isRunning()) {
                System.out.print("Podaj numer portu: ");
                int port = parseReadPort(commandReader.readLine());
                chatServer.startServer(port);
            } else {
                System.out.println("Serwer jest już uruchomiony na porcie " + chatServer.getPort());
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Zatrzymuje działanie serwera komunikatora.
     */
    private void stopChatServer() {
        if(chatServer != null && chatServer.isRunning()) {
            chatServer.stopServer();
            chatServer = null;
        }
    }

    /**
     * Pokazuje dostępne komendy
     */
    private void showServerCommand() {
        System.out.println("Dozwolone komendy:");
        System.out.println("  start - uruchamia server");
        System.out.println("  stop - zatrzymuje serwer");
        System.out.println("  exit - kończy działanie serwera");
    }

    /**
     * Parsuje podany numer portu.
     *
     * @param readPort numer portu jako ciąg znaków
     * @return numer portu
     */
    private int parseReadPort(String readPort) {
        int port;

        try {
            port = Integer.parseInt(readPort);
        } catch(NumberFormatException exception) {
            throw new IllegalArgumentException("Podano nieprawidłowy numer portu");
        }

        return port;
    }

    /**
     * Uruchamia działanie administratora serwera.
     */
    public void start() {
        while (running) {
            System.out.print("Wpisz polecenie> ");
            readCommand();
        }
    }

    public static void main(String[] args) {
        ConsoleServerRunner consoleServerRunner = new ConsoleServerRunner();
        consoleServerRunner.start();
    }
}
