package defusion;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class City {
    private final static int START_COINS_NUMBER = 1000000;
    @Getter
    private final Coordinate position;
    @Getter
    private final Country country;
    @Getter
    private Map<Country, Account> accounts = new HashMap<>();
    @Getter
    private List<City> neihgbours = new ArrayList<>();

    public City(Coordinate position, Country country) {
        this.position = position;
        this.country = country;
    }

    public void sendCoins(City neighbour) {
        for (Account account : accounts.values()) {
            int transactionSum = account.getTransactionSum();
            account.setBalance(account.getBalance() - transactionSum);
            Account neighbourAccount = neighbour.accounts.get(account.getCountry());
            neighbourAccount.setBalance(neighbourAccount.getBalance() + transactionSum);
        }
    }

    public void createAccounts(Collection<Country> countries) {
        for (Country cntr : countries) {
            Account account = new Account(cntr);
            if (this.country.equals(cntr)) {
                account.setBalance(START_COINS_NUMBER);
            }
            accounts.put(cntr, account);
        }
    }

    public boolean isComplete() {
        return accounts.values().stream().noneMatch(Account::nonEmpty);
    }

    public void addNeighbourIfPresent(City neighbour) {
        if (Objects.nonNull(neighbour))
            neihgbours.add(neighbour);
    }

    public static int positionHash(int x, int y) {
        return 10 * x + y;
    }

    public void setTransactionSums() {
        accounts.values().forEach(Account::setTransactionSum);
    }
}
