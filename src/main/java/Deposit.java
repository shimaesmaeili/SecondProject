import java.math.BigInteger;

public class Deposit {
    private String customer;
    private int id;
    private BigInteger initialBalance;
    private BigInteger balance;
    private BigInteger upperBound;

    public Deposit() {
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public BigInteger getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigInteger initialBalance) {
        this.initialBalance = initialBalance;
    }

	public BigInteger getBalance() {
		return balance;
	}

	public void setBalance(BigInteger balance) {
		this.balance = balance;
	}

    public BigInteger getUpperBound() {
        return upperBound;
    }

	public void setUpperBound(BigInteger upperBound) {
        this.upperBound = upperBound;
    }
}
