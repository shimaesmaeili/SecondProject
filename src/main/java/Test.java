//import org.json.JSONArray;
//import org.json.JSONObject;
//import org.w3c.dom.Document;
//import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//import javax.xml.parsers.DocumentBuilderFactory;
//import javax.xml.parsers.ParserConfigurationException;
//import java.io.*;
//import java.math.BigDecimal;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class Test extends Thread {
//
//    String threadName;
//    String request;
//
//    Test(String name, String request) {
//        threadName = name;
//        this.request = request;
//    }
//
//    public void run() {
//        JSONObject obj = new JSONObject(request);
//        String type = obj.getString("type");
//        Deposit dep = listOfDeposits.get(obj.getInt("deposit"));
//
//        synchronized (dep) {
//            if (type.equals("deposit")) {
//                System.out.println(type);
//                System.out.println("initial: " + dep.getInitialBalane());
//                BigDecimal initial = dep.getInitialBalane();
//                System.out.println("amount: " + obj.getInt("amount"));
//                try {
//                    join(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                initial = initial.add(new BigDecimal(obj.getInt("amount")));
//                dep.setInitialBalane(initial);
//                System.out.println("final: " + dep.getInitialBalane());
//            }
//        }
//
//    }
//
//    static Map<Integer, Deposit> listOfDeposits;
//    static int port, terminalID;
//    static String outLog, terminalType;
//
//    public static void readFile(String fileName) {
//        String jsonData = "";
//        BufferedReader br = null;
//        try {
//            String line;
//            br = new BufferedReader(new FileReader("core.json"));
//            while ((line = br.readLine()) != null) {
//                jsonData += line + "\n";
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        JSONObject obj = new JSONObject(jsonData);
//        port = obj.getInt("port");
//        outLog = obj.getString("outLog");
//
//        JSONArray arr = obj.getJSONArray("deposit");
//        for (int i = 0; i < arr.length(); i++) {
//            JSONObject depObj = arr.getJSONObject(i);
//            listOfDeposits.put(depObj.getInt("id"), new Deposit(depObj.getString("customer"), depObj.getInt("id"), new BigDecimal(depObj.getString("initialBalance")), new BigDecimal(depObj.getString("upperBound"))));
//        }
//    }
//
//    static List<Transaction> listOfTransactions;
//
//    public static void parseFile(Document document) {
//        terminalID = Integer.parseInt(document.getDocumentElement().getAttribute("id"));
//        terminalType = document.getDocumentElement().getAttribute("type");
//
//        NodeList nodeList = document.getElementsByTagName("transaction");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            int id = Integer.parseInt(((Element) nodeList.item(i)).getAttribute("id"));
//            String type = ((Element) nodeList.item(i)).getAttribute("type");
//            BigDecimal amount = new BigDecimal(((Element) nodeList.item(i)).getAttribute("amount"));
//            BigDecimal deposit = new BigDecimal(((Element) nodeList.item(i)).getAttribute("deposit"));
//
//            listOfTransactions.add(new Transaction(terminalID, terminalType, id, type, amount, deposit));
//        }
//    }
//
//    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
//        listOfDeposits = new HashMap<Integer, Deposit>();
//        readFile("core.json");
//
//        listOfTransactions = new ArrayList<Transaction>();
//        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("testTerminal.xml");
//        parseFile(document);
//
//        Test T1 = new Test("First", String.valueOf(JSONObject.wrap(listOfTransactions.get(0))));
//        T1.start();
//
//        Test T2 = new Test("Second", String.valueOf(JSONObject.wrap(listOfTransactions.get(1))));
//        T2.start();
//    }
//}
