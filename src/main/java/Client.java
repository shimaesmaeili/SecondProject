import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.ArrayList;
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

    public void connectToServer(Transaction trans){
        System.out.println(trans.getType());
        try {
            out.writeUTF(String.valueOf(JSONObject.wrap(trans)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static String ip;
    static int port;
    static String outPath;
    static List<Transaction> listOfTransactions;

    public static void parseFile(Document document) {
        Node serverNode = document.getElementsByTagName("server").item(0);
        ip = ((Element) serverNode).getAttribute("ip");
        port = Integer.parseInt(((Element) serverNode).getAttribute("port"));

        Node outNode = document.getElementsByTagName("outLog").item(0);
        outPath = ((Element) outNode).getAttribute("path");

        NodeList nodeList = document.getElementsByTagName("transaction");
        for (int i = 0; i < nodeList.getLength(); i++) {
            int id = Integer.parseInt(((Element)nodeList.item(i)).getAttribute("id"));
            String type = ((Element)nodeList.item(i)).getAttribute("type");
            BigDecimal amount = new BigDecimal(((Element)nodeList.item(i)).getAttribute("amount"));
            BigDecimal deposit = new BigDecimal(((Element) nodeList.item(i)).getAttribute("deposit"));

            listOfTransactions.add(new Transaction(id, type, amount, deposit));
        }
    }

    public static void main(String[] args) throws Exception {
        listOfTransactions = new ArrayList<Transaction>();
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("terminal.xml");
        parseFile(document);

        for (Transaction trans : listOfTransactions){
            new Client(trans);
        }
    }
}
