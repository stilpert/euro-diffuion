package defusion;

import lombok.Getter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EuropeanUnion {

  @Getter
  private List<Country> countries;
  private Map<Integer, City> cities = new HashMap<>();

  private EuropeanUnion(List<Country> countries) {
    this.countries = countries;
    this.countries.forEach(this::createCities);

    for (City city : cities.values()) {
      int x = city.getPosition().getX();
      int y = city.getPosition().getY();

      city.addNeighbourIfPresent(cities.get(City.positionHash(x - 1, y)));
      city.addNeighbourIfPresent(cities.get(City.positionHash(x + 1, y)));
      city.addNeighbourIfPresent(cities.get(City.positionHash(x, y + 1)));
      city.addNeighbourIfPresent(cities.get(City.positionHash(x, y - 1)));

      city.setInitialBalances(countries);
    }

  }

  public static EuropeanUnion buildFromStrings(List<String> countryStrings) {
    List<Country> countries = countryStrings.stream()
            .map(Country::buildFromString)
            .collect(Collectors.toList());

    return new EuropeanUnion(countries);
  }

  public void createCities(Country country) {
    for (int y = country.getLowerLeftPosition().getY(); y <= country.getUpperRightPosition().getY(); y++) {
      for (int x = country.getLowerLeftPosition().getX(); x <= country.getUpperRightPosition().getX(); x++) {
        City city = new City(new Coordinate(x, y), country);
        cities.put(City.positionHash(city.getPosition().getX(), city.getPosition().getY()), city);
      }
    }
  }

  public void runSimulation() {
    int count = 0;
    do {
      count++;
      for (City city : cities.values()) {
        city.setAmountsToPay();
        for (City neighbour : city.getNeihgbours()) {
          for (Country country : countries) {
            int amount = city.withdrawalCoins(country);
            neighbour.acceptCoins(country, amount);
          }
        }
      }
      for (City city : cities.values()) {
        city.fillBalances();
        city.clearIncoming();
      }
    }
    while (!checkCitiesDone(count));
  }

  private boolean checkCitiesDone(int day) {
    for (Country country : countries) {
      if (country.getFilledDay() < 0) {
        country.setFilledDay(day);
      }
    }
    boolean allCitiesDone = true;
    for (City city : cities.values()) {
      if (!city.checkIfDone(countries)) {
        allCitiesDone = false;
        city.getCountry().setFilledDay(-1);
      }
    }
    return allCitiesDone;
  }
}
