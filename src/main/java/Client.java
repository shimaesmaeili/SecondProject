import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Client {
    DataInputStream in;
    DataOutputStream out;
    Socket socket;

    public Client(Transaction trans) throws IOException {
        socket = new Socket(ip, port);
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());

        connectToServer(trans);
    }

    public void connectToServer(Transaction trans) {
        System.out.println(trans.getType());
        try {
            out.writeUTF(String.valueOf(JSONObject.wrap(trans)));
            logFile.writeBytes("TransactionID=" + trans.getId() + ": sent to server.\n");
            String respond = in.readUTF();

            responses.put(trans.getId(), respond);

            String logMessage = "";
            if (respond.equals("done")) {
                logMessage = "TransactionID=" + trans.getId() + ": done successfully!\n";
            } else if (respond.equals("insufficient")) {
                logMessage = "TransactionID=" + trans.getId() + ": can not be done due to lack of account balance!\n";
            } else if (respond.equals("exceeded")) {
                logMessage = "TransactionID=" + trans.getId() + ": can not be done due to exceeding the upper bound!\n";
            } else if (respond.equals("wrong")) {
                logMessage = "TransactionID=" + trans.getId() + ": encountered error!\n";
            }
            synchronized (logFile) {
                logFile.writeBytes(logMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static int port, terminalID;
    static String outPath, ip, terminalType;
    static DataOutputStream logFile, responseFile;
    static List<Transaction> listOfTransactions;
    static HashMap<Integer, String> responses;

    public static void parseFile(Document document) {
        terminalID = Integer.parseInt(document.getDocumentElement().getAttribute("id"));
        terminalType = document.getDocumentElement().getAttribute("type");

        Node serverNode = document.getElementsByTagName("server").item(0);
        ip = ((Element) serverNode).getAttribute("ip");
        port = Integer.parseInt(((Element) serverNode).getAttribute("port"));

        Node outNode = document.getElementsByTagName("outLog").item(0);
        outPath = ((Element) outNode).getAttribute("path");

        NodeList nodeList = document.getElementsByTagName("transaction");
        for (int i = 0; i < nodeList.getLength(); i++) {
            int id = Integer.parseInt(((Element) nodeList.item(i)).getAttribute("id"));
            String type = ((Element) nodeList.item(i)).getAttribute("type");
            BigDecimal amount = new BigDecimal(((Element) nodeList.item(i)).getAttribute("amount"));
            BigDecimal deposit = new BigDecimal(((Element) nodeList.item(i)).getAttribute("deposit"));

            listOfTransactions.add(new Transaction(terminalID, terminalType, id, type, amount, deposit));
        }
    }

    private static void saveToXML() throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document dom = db.newDocument();

        Element root = dom.createElement("terminal");
        root.setAttribute("id", String.valueOf(terminalID));
        root.setAttribute("type", terminalType);

        Element mainElement = dom.createElement("responses");

        Object[] keySets = responses.keySet().toArray();
        for (int i = 0; i < responses.size(); i++) {
            Element e = dom.createElement("response");
            e.setAttribute("id", String.valueOf(keySets[i]));
            e.setAttribute("status", responses.get(keySets[i]));
            mainElement.appendChild(e);
        }

        root.appendChild(mainElement);
        dom.appendChild(root);

        Transformer tr = TransformerFactory.newInstance().newTransformer();
        tr.setOutputProperty(OutputKeys.INDENT, "yes");
        tr.setOutputProperty(OutputKeys.METHOD, "xml");
        tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        tr.transform(new DOMSource(dom), new StreamResult(responseFile));
    }

    public static void main(String[] args) throws Exception {
        listOfTransactions = new ArrayList<Transaction>();
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("terminal.xml");
        parseFile(document);

        logFile = new DataOutputStream(new FileOutputStream(outPath));
        responses = new HashMap<Integer, String>();
        for (Transaction trans : listOfTransactions) {
            new Client(trans);
        }

        responseFile = new DataOutputStream(new FileOutputStream("response.xml"));
        saveToXML();
    }
}
