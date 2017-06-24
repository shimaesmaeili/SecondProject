import java.math.BigDecimal;

public class Transaction {
    int terminalID;
    String terminalType;
    int id;
    String type;
    BigDecimal amount;
    BigDecimal deposit;

    public Transaction(int termID, String termType, int id, String type, BigDecimal amount, BigDecimal deposit){
        this.terminalID = termID;
        this.terminalType = termType;
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.deposit = deposit;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getDeposit() {
        return deposit;
    }

    public int getTerminalID() {
        return terminalID;
    }

    public String getTerminalType() {
        return terminalType;
    }
}
