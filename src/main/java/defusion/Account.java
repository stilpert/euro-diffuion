package defusion;

import lombok.Data;

@Data
public class Account {
    private final Country country;
    private int balance = 0;
    private int transactionSum;

    void setTransactionSum() {
        transactionSum = balance / 1000;
    }

    public boolean nonEmpty() {
        return this.getBalance() == 0;
    }
}
