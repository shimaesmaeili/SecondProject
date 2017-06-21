import java.math.BigDecimal;

public class Deposit {
    String customer;
    int id;
    BigDecimal initialBalane;
    BigDecimal upperBound;

    Deposit(String customer, int id, BigDecimal initialBalane, BigDecimal upperBound){
        this.customer = customer;
        this.id = id;
        this.initialBalane = initialBalane;
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

    public BigDecimal getInitialBalane() {
        return initialBalane;
    }

    public void setInitialBalane(BigDecimal initialBalane) {
        this.initialBalane = initialBalane;
    }

    public BigDecimal getUpperBound() {
        return upperBound;
    }

    public void setUpperBound(BigDecimal upperBound) {
        this.upperBound = upperBound;
    }
}
