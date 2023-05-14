package defusion;

import java.util.Collection;
import java.util.HashMap;
import lombok.Getter;


public class City {
  @Getter
  private final Coordinate position;
  @Getter
  private final Country country;
  private HashMap<Country, Integer> currentBalance = new HashMap<>();
  private HashMap<Country, Integer> incomingBalance = new HashMap<>();
  private HashMap<Country, Integer> amountToPay = new HashMap<>();
  private final int initialAmountOfCoins = 1000000;

  public City(Coordinate position, Country country) {
    this.position = position;
    this.country = country;
  }

  public void acceptCoins(Country country, int amount) {
    incomingBalance.put(country, incomingBalance.get(country) + amount);
  }

  public int withdrawalCoins(Country country) {
    currentBalance.put(country, currentBalance.get(country) - amountToPay.get(country));
    return amountToPay.get(country);
  }

  public void setAmountsToPay() {
    for (Country key : currentBalance.keySet()) {
      amountToPay.put(key, currentBalance.get(key) / 1000);
    }
  }

  public void fillBalances() {
    for (Country key : currentBalance.keySet()) {
      currentBalance.put(key, currentBalance.get(key) + incomingBalance.get(key));
    }
  }

  public void clearIncoming() {
    for (Country key : incomingBalance.keySet()) {
      incomingBalance.put(key, 0);
    }
  }

  public void setInitialBalances(Collection<Country> countries) {
    for (Country country : countries) {
      if (this.country.equals(country)) {
        currentBalance.put(country, initialAmountOfCoins);
      }
      else {
        currentBalance.put(country, 0);
      }
      amountToPay.put(country, 0);
      incomingBalance.put(country, 0);
    }
  }

  public boolean checkIfDone(Collection<Country> countries) {
    for (Country country : countries) {
      if (currentBalance.get(country) == 0) {
        return false;
      }
    }
    return true;
  }


}
