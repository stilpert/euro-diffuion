package defusion;

import lombok.Data;

@Data
public class Account {
    private static final int TRANSACTION_COEFFICIENT = 1000;
    private final Country country;
    private int balance = 0;
    private int transactionSum;

    void setTransactionSum() {
        transactionSum = balance / TRANSACTION_COEFFICIENT;
    }

    public boolean nonEmpty() {
        return this.getBalance() == 0;
    }
}
