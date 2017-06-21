import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.math.BigDecimal;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread{
    private Socket socket;

    Server(Socket socket){
        this.socket = socket;
    }

    public void run() {

    }

    static List<Deposit> listOfDeposits;
    static int port;
    static String outLog;

    public static void readFile(String fileName){
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
        for(int i = 0; i < arr.length(); i++) {
            JSONObject depObj = arr.getJSONObject(i);
            listOfDeposits.add(new Deposit(depObj.getString("customer"), depObj.getInt("id"), new BigDecimal(depObj.getString("initialBalance")), new BigDecimal(depObj.getString("upperBound"))));
        }
    }

    public static void main(String[] args) throws IOException {
        listOfDeposits = new ArrayList<Deposit>();
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
