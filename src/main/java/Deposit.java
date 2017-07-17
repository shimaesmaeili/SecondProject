import java.math.BigInteger;

public class Deposit {
    String customer;
    int id;
    BigInteger initialBalance;
    BigInteger balance;
    BigInteger upperBound;

    public Deposit() {
    }

    Deposit(String customer, int id, BigInteger initialBalance, BigInteger upperBound){
        this.customer = customer;
        this.id = id;
        this.initialBalance = initialBalance;
		this.balance = initialBalance;
        this.upperBound = upperBound;
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
