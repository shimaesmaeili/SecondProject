import java.math.BigDecimal;

public class Transaction {
    int id;
    String type;
    BigDecimal amount;
    BigDecimal deposit;

    public Transaction(int id, String type, BigDecimal amount, BigDecimal deposit){
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
}
