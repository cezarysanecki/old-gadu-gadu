package pl.edu.pw.s251957.client.util;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pl.edu.pw.s251957.client.util.model.ServerConnectionConfig;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;

/** Klasa odpowiedzialna za odczytanie i sparsowanie danych do połączenia z serwerem
 *
 * @author Cezary Sanecki
 * @version 1.0
 * @since 01.11.2019r.
 */
public class ServerConfigurator {
    /**
     * Odczytuje dane konfiguracyjne XML z danego katalogu dla pliku config.xml
     *
     * @return dane połączenia z serwerem
     * @throws UnacceptableClientConfigException wyjątek informujący, dlaczego nie można było odczytać danych połączenia
     * z serwerem
     */
    public static ServerConnectionConfig readConfigFile() throws UnacceptableClientConfigException {
        try {
            String xmlConfigPath = System.getProperty("user.dir") + System.getProperty("file.separator") + "config.xml";
            File inputFile = new File(xmlConfigPath);
            NodeList nList = getConfigElementsList(inputFile);

            String hostElement = null;
            String portElement = null;

            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);

                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) nNode;
                    hostElement = element.getElementsByTagName("host").item(0).getTextContent();
                    portElement = element.getElementsByTagName("port").item(0).getTextContent();
                }
            }

            int port = Integer.parseInt(portElement);

            return new ServerConnectionConfig(hostElement, port);
        } catch (ParserConfigurationException e) {
            throw new UnacceptableClientConfigException(e.getMessage());
        } catch (NumberFormatException e) {
            throw new UnacceptableClientConfigException("Błędny numer portu w pliku konfiguracyjnym");
        }
    }

    /**
     * Parsuje dane z danego pliku XML
     *
     * @param configFile plik XML zawierający danego połączenia z serwerem
     * @return lista odczytanych wartości z pliku
     * @throws ParserConfigurationException wyjątek informujący o niewłaściwej strukturze pliku XML
     */
    private static NodeList getConfigElementsList(File configFile) throws ParserConfigurationException {
        Document document;

        try {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(configFile);
            document.getDocumentElement().normalize();
        } catch (Exception exception) {
            throw new ParserConfigurationException("Błąd parsowania pliku konfiguracyjnego");
        }

        return document.getElementsByTagName("config");
    }
}
