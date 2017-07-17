import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.math.BigInteger;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Terminal extends Thread {
	private ArrayList<Transaction> transactions;
	private HashMap<Integer, String> responses;
	private int port, terminalID;
	private String logFileName, ip, terminalType;
	private BufferedWriter responseFile;

	public Terminal(String fileName) throws ParserConfigurationException, IOException, SAXException {
		transactions = new ArrayList<Transaction>();
		responses = new HashMap<Integer, String>();

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(fileName);
		parseFile(document);
		responseFile = new BufferedWriter(new FileWriter("response.xml"));
	}

	private void parseFile(Document document) {
		terminalID = Integer.parseInt(document.getDocumentElement().getAttribute("id"));
		terminalType = document.getDocumentElement().getAttribute("type");

		Node serverNode = document.getElementsByTagName("server").item(0);
		ip = ((Element) serverNode).getAttribute("ip");
		port = Integer.parseInt(((Element) serverNode).getAttribute("port"));

		Node outNode = document.getElementsByTagName("outLog").item(0);
		logFileName = ((Element) outNode).getAttribute("path");

		NodeList nodeList = document.getElementsByTagName("transaction");
		for (int i = 0; i < nodeList.getLength(); i++) {
			int id = Integer.parseInt(((Element) nodeList.item(i)).getAttribute("id"));
			String type = ((Element) nodeList.item(i)).getAttribute("type");
			BigInteger amount = new BigInteger(((Element) nodeList.item(i)).getAttribute("amount"));
			int deposit = Integer.parseInt(((Element) nodeList.item(i)).getAttribute("deposit"));

			Transaction transaction = new Transaction();
			transaction.setTerminalID(terminalID);
			transaction.setTerminalType(terminalType);
			transaction.setId(id);
			transaction.setType(type);
			transaction.setAmount(amount);
			transaction.setDeposit(deposit);
			transactions.add(transaction);
		}
	}

	private void saveToXML() throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document dom = documentBuilder.newDocument();

		Element root = dom.createElement("terminal");
		root.setAttribute("id", String.valueOf(terminalID));
		root.setAttribute("type", terminalType);

		Element mainElement = dom.createElement("responses");

		Object[] keySets = responses.keySet().toArray();
		for (int i = 0; i < responses.size(); i++) {
			Element element = dom.createElement("response");
			element.setAttribute("id", String.valueOf(keySets[i]));
			element.setAttribute("status", responses.get(keySets[i]));
			mainElement.appendChild(element);
		}

		root.appendChild(mainElement);
		dom.appendChild(root);

		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

		transformer.transform(new DOMSource(dom), new StreamResult(responseFile));
	}

	@Override
	public void run() {
		try {
			Socket socket = new Socket(ip, port);
			DataInputStream in = new DataInputStream(socket.getInputStream());
			DataOutputStream out = new DataOutputStream(socket.getOutputStream());

			for (Transaction transaction : transactions){
				out.writeUTF(JSONObject.toJSONString(transaction.toMap()));
				Logging.log(logFileName, "TransactionID=" + transaction.getId() + ": sent to server.\n");
				String respond = in.readUTF();
				System.out.println(respond);
				responses.put(transaction.getId(), respond);
			}

			saveToXML();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}
}
