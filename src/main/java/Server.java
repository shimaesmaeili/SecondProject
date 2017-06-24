import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread {
    private Socket socket;
    private DataInputStream in;
    private DataOutputStream out;

    Server(Socket s) throws IOException {
        this.socket = s;
        in = new DataInputStream(socket.getInputStream());
        out = new DataOutputStream(socket.getOutputStream());
    }

    public void run() {
        try {
            String request = in.readUTF();
            JSONObject obj = new JSONObject(request);
            String type = obj.getString("type");
            Deposit dep = listOfDeposits.get(obj.getInt("deposit"));
            synchronized (dep) {
                if (type.equals("deposit")) {
                    System.out.println(type);
                    System.out.println(dep.getInitialBalane());
                    dep.setInitialBalane(dep.getInitialBalane().add(new BigDecimal(obj.getInt("amount"))));
                    System.out.println(dep.getInitialBalane());
                } else if (type.equals("withdraw")) {
                    System.out.println(type);
                    System.out.println(dep.getInitialBalane());
                    listOfDeposits.get(obj.getInt("deposit")).setInitialBalane(listOfDeposits.get(obj.getInt("deposit")).getInitialBalane().subtract(new BigDecimal(obj.getInt("amount"))));
                    System.out.println(dep.getInitialBalane());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static Map<Integer, Deposit> listOfDeposits;
    static int port;
    static String outLog;

    public static void readFile(String fileName) {
        String jsonData = "";
        BufferedReader br = null;
        try {
            String line;
            br = new BufferedReader(new FileReader("core.json"));
            while ((line = br.readLine()) != null) {
                jsonData += line + "\n";
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        JSONObject obj = new JSONObject(jsonData);
        port = obj.getInt("port");
        outLog = obj.getString("outLog");

        JSONArray arr = obj.getJSONArray("deposit");
        for (int i = 0; i < arr.length(); i++) {
            JSONObject depObj = arr.getJSONObject(i);
            listOfDeposits.put(depObj.getInt("id"), new Deposit(depObj.getString("customer"), depObj.getInt("id"), new BigDecimal(depObj.getString("initialBalance")), new BigDecimal(depObj.getString("upperBound"))));
        }
    }

    public static void main(String[] args) throws IOException {
        listOfDeposits = new HashMap<Integer, Deposit>();
        readFile("core.json");
        ServerSocket listener = new ServerSocket(port);

        try {
            while (true) {
                new Server(listener.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            listener.close();
        }
    }
}
