package defusion;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import lombok.Getter;


public class City {
  private final static int START_COINS_NUMBER = 1000000;
  @Getter
  private final Coordinate position;
  @Getter
  private final Country country;
  private HashMap<Country, Integer> currentBalance = new HashMap<>();
  private HashMap<Country, Integer> incomingBalance = new HashMap<>();
  private HashMap<Country, Integer> amountToPay = new HashMap<>();
  @Getter
  private List<City> neihgbours = new ArrayList<>();

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
        currentBalance.put(country, START_COINS_NUMBER);
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

  public void addNeighbourIfPresent(City neighbour) {
    if(Objects.nonNull(neighbour)) neihgbours.add(neighbour);
  }

  public static int positionHash(int x, int y) {
    return 10 * x + y;
  }
}
