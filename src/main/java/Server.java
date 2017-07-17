import exception.exceedUpperBoundException;
import exception.notEnoughAmountException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Server extends Thread {
	private Socket socket;
	private DataInputStream in;
	private DataOutputStream out;
	private static Map<Integer, Deposit> listOfDeposits;
	private static int port;
	private static String logFileName;

	Server(Socket s) throws IOException {
		this.socket = s;
		in = new DataInputStream(socket.getInputStream());
		out = new DataOutputStream(socket.getOutputStream());
	}

	public void run() {
		for (int i = 0; i < 3; i++) {
			try {
				String request = in.readUTF();
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(request);
				Transaction transaction = new Transaction(obj);

				Logging.log(logFileName, "TerminalID=" + transaction.getTerminalID() + ", TransactionID=" + transaction.getId() + ": A request is received!\n");

				if (listOfDeposits.containsKey(transaction.getDeposit())) {
					Deposit deposit = listOfDeposits.get(transaction.getDeposit());
					String logMessage = "TerminalID=" + transaction.getTerminalID() + ", TransactionID=" + transaction.getId();
					synchronized (deposit) {
						if (transaction.getType().equals("deposit")) {
							if (deposit.getBalance().add(transaction.getAmount()).compareTo(deposit.getUpperBound()) <= 0) {
								deposit.setBalance(deposit.getBalance().add(transaction.getAmount()));
								out.writeUTF("done");
								Logging.log(logFileName, logMessage + ": The request for deposit number " + transaction.getDeposit() + " was done successfully!\n");
							} else {
								out.writeUTF("exceeded");
								Logging.log(logFileName, logMessage + ": The request for deposit number " + transaction.getDeposit() + " was not valid!\n");
								throw new exceedUpperBoundException();
							}
						} else if (transaction.getType().equals("withdraw")) {
							if (deposit.getBalance().compareTo(transaction.getAmount()) >= 0) {
								deposit.setBalance(deposit.getBalance().subtract(transaction.getAmount()));
								out.writeUTF("done");
								Logging.log(logFileName, logMessage + ": The request for deposit number " + transaction.getDeposit() + " was done successfully!\n");
							} else {
								out.writeUTF("insufficient");
								Logging.log(logFileName, logMessage + ": The request for deposit number " + transaction.getDeposit() + " was not valid!\n");
								throw new notEnoughAmountException();
							}
						}
					}
				} else {
					out.writeUTF("wrong");
					Logging.log(logFileName, "A deposit with specified number: " + transaction.getDeposit() + " does not exist!\n");
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (org.json.simple.parser.ParseException e) {
				e.printStackTrace();
			} catch (exceedUpperBoundException e) {
				System.err.println("by this transaction you exceed the upper bound!");
			} catch (notEnoughAmountException e) {
				System.err.println("the amount is not sufficient!");
			}
		}
	}

	public static void readFile(String fileName) throws IOException {
		JSONParser parser = new JSONParser();
		try {
			JSONObject obj = (JSONObject) parser.parse(new FileReader(fileName));
			port = Integer.parseInt(String.valueOf(obj.get("port")));
			logFileName = String.valueOf(obj.get("outLog"));

			JSONArray array = (JSONArray) obj.get("deposit");
			for (int i = 0; i < array.size(); i++) {
				JSONObject depositJSONObj = (JSONObject) array.get(i);
				Deposit deposit = new Deposit();
				deposit.setId(Integer.parseInt(String.valueOf(depositJSONObj.get("id"))));
				deposit.setCustomer(String.valueOf(depositJSONObj.get("customer")));
				deposit.setInitialBalance(new BigInteger(String.valueOf(depositJSONObj.get("initialBalance"))));
				deposit.setBalance(deposit.getInitialBalance());
				deposit.setUpperBound(new BigInteger(String.valueOf(depositJSONObj.get("upperBound"))));
				listOfDeposits.put(deposit.getId(), deposit);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (org.json.simple.parser.ParseException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws IOException {
		listOfDeposits = new HashMap<Integer, Deposit>();
		readFile("core.json");
		ServerSocket listener = new ServerSocket(port);

		try {
			while (true) {
				Server server = new Server(listener.accept());
				server.start();
			}
		} catch (IOException e) {
			Logging.log(logFileName, "Server can not get started!\n");
			e.printStackTrace();
		} finally {
			listener.close();
		}
	}
}
