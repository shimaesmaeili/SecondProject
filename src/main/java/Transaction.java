import org.json.simple.JSONObject;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public class Transaction {
    int terminalID;
    String terminalType;
    int id;
    String type;
    BigInteger amount;
    int deposit;

	public Transaction() {

	}

	public Transaction(JSONObject object) {
		this.terminalID = Integer.parseInt(String.valueOf(object.get("terminalID")));
		this.terminalType = String.valueOf(object.get("terminalType"));
		this.id = Integer.parseInt(String.valueOf(object.get("id")));
		this.type = String.valueOf(object.get("type"));
		this.amount = new BigInteger(String.valueOf(object.get("amount")));
		this.deposit = Integer.parseInt(String.valueOf(object.get("deposit")));
	}

	public Map<String, String> toMap(){
		HashMap<String, String> jsonMap = new HashMap<String, String>();
		jsonMap.put("terminalID", String.valueOf(terminalID));
		jsonMap.put("terminalType", terminalType);
		jsonMap.put("id", String.valueOf(id));
		jsonMap.put("type", type);
		jsonMap.put("amount", String.valueOf(amount));
		jsonMap.put("deposit", String.valueOf(deposit));
		return jsonMap;
	}

	public int getTerminalID() {
		return terminalID;
	}

	public void setTerminalID(int terminalID) {
		this.terminalID = terminalID;
	}

	public String getTerminalType() {
		return terminalType;
	}

	public void setTerminalType(String terminalType) {
		this.terminalType = terminalType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public BigInteger getAmount() {
		return amount;
	}

	public void setAmount(BigInteger amount) {
		this.amount = amount;
	}

	public int getDeposit() {
		return deposit;
	}

	public void setDeposit(int deposit) {
		this.deposit = deposit;
	}
}
