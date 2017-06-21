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
}
