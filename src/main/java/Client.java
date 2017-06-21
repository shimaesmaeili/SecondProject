import org.omg.CORBA.SystemException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;

public class Client {
//    private BufferedReader in;
//    private PrintWriter out;
//    private JFrame frame = new JFrame("Capitalize Client");
//    private JTextField dataField = new JTextField(40);
//    private JTextArea messageArea = new JTextArea(8, 60);
//
//    /**
//     * Constructs the client by laying out the GUI and registering a
//     * listener with the textfield so that pressing Enter in the
//     * listener sends the textfield contents to the server.
//     */
//    public Client() {
//
//        // Layout GUI
//        messageArea.setEditable(false);
//        frame.getContentPane().add(dataField, "North");
//        frame.getContentPane().add(new JScrollPane(messageArea), "Center");
//
//        // Add Listeners
//        dataField.addActionListener(new ActionListener() {
//            /**
//             * Responds to pressing the enter key in the textfield
//             * by sending the contents of the text field to the
//             * server and displaying the response from the server
//             * in the text area.  If the response is "." we exit
//             * the whole application, which closes all sockets,
//             * streams and windows.
//             */
//            public void actionPerformed(ActionEvent e) {
//                out.println(dataField.getText());
//                String response;
//                try {
//                    response = in.readLine();
//                    if (response == null || response.equals("")) {
//                        System.exit(0);
//                    }
//                } catch (IOException ex) {
//                    response = "Error: " + ex;
//                }
//                messageArea.append(response + "\n");
//                dataField.selectAll();
//            }
//        });
//    }
//
//    /**
//     * Implements the connection logic by prompting the end user for
//     * the server's IP address, connecting, setting up streams, and
//     * consuming the welcome messages from the server.  The Capitalizer
//     * protocol says that the server sends three lines of text to the
//     * client immediately after establishing a connection.
//     */
//    public void connectToServer() throws IOException {
//
//        Socket socket = new Socket(serverAddress, 9898);
//        in = new BufferedReader(
//                new InputStreamReader(socket.getInputStream()));
//        out = new PrintWriter(socket.getOutputStream(), true);
//
//        // Consume the initial welcoming messages from the server
//        for (int i = 0; i < 3; i++) {
//            ;
//        }
//    }

    static String ip;
    static String port;
    static String outPath;

    public static void parseFile(Document document) {
        Node serverNode = document.getElementsByTagName("server").item(0);
        ip = ((Element) serverNode).getAttribute("ip");
        port = ((Element) serverNode).getAttribute("port");
        System.out.println(ip + " - " + port);

        Node outNode = document.getElementsByTagName("outLog").item(0);
        outPath = ((Element) outNode).getAttribute("path");

        NodeList nodeList = document.getElementsByTagName("transaction");
        for (int i = 0; i < nodeList.getLength(); i++) {
            System.out.println(((Element)nodeList.item(i)).getAttribute("amount"));
            int id = Integer.parseInt(((Element)nodeList.item(i)).getAttribute("id"));
            String type = ((Element)nodeList.item(i)).getAttribute("type");
            BigDecimal amount = new BigDecimal(((Element)nodeList.item(i)).getAttribute("amount"));
            BigDecimal deposit = new BigDecimal(((Element) nodeList.item(i)).getAttribute("deposit"));
        }
    }

    public static void main(String[] args) throws Exception {
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("terminal.xml");
        parseFile(document);
//        Client client = new Client();
//        client.connectToServer();
    }
}
