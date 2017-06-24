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

            synchronized (logFile) {
                logFile.writeBytes("TerminalID=" + obj.getInt("terminalID") + ", TransactionID=" + obj.getInt("id") + ": A request is received!\n");
            }

            boolean valid = false;

            if (listOfDeposits.containsKey(obj.getInt("deposit"))) {
                Deposit dep = listOfDeposits.get(obj.getInt("deposit"));
                String type = obj.getString("type");
                BigDecimal amount = new BigDecimal(obj.getInt("amount"));

                if (type.equals("deposit")) {
                    if (dep.getInitialBalane().add(amount).compareTo(dep.getUpperBound()) <= 0) {
                        valid = true;
                    }
                    else{
                        out.writeUTF("exceeded");
                    }
                } else if (type.equals("withdraw")) {
                    if (dep.getInitialBalane().compareTo(amount) >= 0) {
                        valid = true;
                    }
                    else{
                        out.writeUTF("insufficient");
                    }
                }

                synchronized (logFile) {
                    logFile.writeBytes("TerminalID=" + obj.getInt("terminalID") + ", TransactionID=" + obj.getInt("id") + ": validation checked.\n");
                }

                System.out.println(valid);

                if (valid) {
                    synchronized (dep) {
                        if (type.equals("deposit")) {
                            dep.setInitialBalane(dep.getInitialBalane().add(new BigDecimal(obj.getInt("amount"))));
                        } else if (type.equals("withdraw")) {
                            dep.setInitialBalane(dep.getInitialBalane().subtract(new BigDecimal(obj.getInt("amount"))));
                        }
                    }
                    out.writeUTF("done");
                    synchronized (logFile){
                        logFile.writeBytes("TerminalID=" + obj.getInt("terminalID") + ", TransactionID=" + obj.getInt("id") + ": The request for deposit number " + obj.getInt("deposit") + " was done successfully!\n");
                    }
                }
                else{
                    synchronized (logFile){
                        logFile.writeBytes("TerminalID=" + obj.getInt("terminalID") + ", TransactionID=" + obj.getInt("id") + ": The request for deposit number " + obj.getInt("deposit") + " was not valid!\n");
                    }
                }
            }

            else{
                out.writeUTF("wrong");
                synchronized (logFile){
                    logFile.writeBytes("A deposit with specified number: " + obj.getInt("deposit") + " does not exist!\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Map<Integer, Deposit> listOfDeposits;
    static int port;
    static String outLog;
    static DataOutputStream logFile;

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
        logFile = new DataOutputStream(new FileOutputStream(outLog));

        logFile.writeBytes("Server is initialized successfully!\n");

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
