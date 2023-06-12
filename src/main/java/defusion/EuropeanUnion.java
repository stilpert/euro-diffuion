package defusion;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EuropeanUnion {
  private int[][] matrix;
  private HashMap<Integer, City> cities;
  private HashMap<Integer, Country> countries;
  private HashMap<City, LinkedList<City>> citiesNeighbourship;
  private static final int maxCoord = 9;
  private static final int minCoord = 0;

  private EuropeanUnion() {
    cities = new HashMap<>();
    countries = new HashMap<>();
    matrix = new int[10][10];
    citiesNeighbourship = new HashMap<>();
  }

  public static EuropeanUnion buildFromStrings(List<String> countryStrings) {
    EuropeanUnion europeanUnion = new EuropeanUnion();
    countryStrings.stream()
      .map(Country::buildFromString)
      .forEach(europeanUnion::addCountry);
    return europeanUnion;
  }

  public void prepareToStart() {
    setCitiesNeighbourship();
    setInitialBalances();
  }

  public void doDiffusion() {
    int count = 0;
    do {
      count++;
      prepareNewDay();
      doNewDay();
      closeDay();
    }
    while (!checkCitiesDone(count));

  }


  public boolean checkCountriesAccessibility() {
    LinkedList<Integer> accessibleCountries = new LinkedList<>();
    LinkedList<Integer> currentCountryNeighbours = new LinkedList<>();
    Integer currentCountry = getSomeCountryFromMap();

    while (accessibleCountries.size() < countries.size()) {
      accessibleCountries.add(currentCountry);
      findLeftAndRightNeighboursForCountry(currentCountry, accessibleCountries, currentCountryNeighbours);
      findLowerAndUpperNeighboursForCountry(currentCountry, accessibleCountries, currentCountryNeighbours);
      if (currentCountryNeighbours.size() != 0) {
        currentCountry = currentCountryNeighbours.get(0);
      }
      else {
        break;
      }
    }
    return accessibleCountries.size() == countries.size();
  }

  public void addCountry(Country country) {
    int id = countries.size() + 1;
    countries.put(id, country);
    for (int y = country.getLowerLeftPosition().getY(); y <= country.getUpperRightPosition().getY(); y++) {//creating a list of cities for current country
      for (int x = country.getLowerLeftPosition().getX(); x <= country.getUpperRightPosition().getX(); x++) {
        City city = new City(new Coordinate(x, y), country);
        cities.put(getHashForCity(city.getPosition().getX(), city.getPosition().getY()), city);
        matrix[x][y] = id;
      }
    }
  }

  public boolean validateCoordinates() {
    for (Country country : countries.values()) {
      if (!validateCountryCoordinates(country)) {
        return false;
      }
    }
    return true;
  }

  private boolean validateCountryCoordinates(Country country) {
    return !((country.getUpperRightPosition().getY() < minCoord || country.getUpperRightPosition().getY() > maxCoord) ||
      (country.getUpperRightPosition().getX() < minCoord || country.getUpperRightPosition().getX() > maxCoord) ||
      (country.getLowerLeftPosition().getY() < minCoord || country.getLowerLeftPosition().getY() > maxCoord) ||
      (country.getLowerLeftPosition().getX() < minCoord || country.getLowerLeftPosition().getX() > maxCoord));
  }

  public void showResults(PrintWriter writer) {
    List<Country> countriesToOutput = new LinkedList<>();
    countriesToOutput.addAll(countries.values());
    countriesToOutput.sort((o1, o2) -> {
      int compareByDay = o1.getFilledDay() - o2.getFilledDay();
        if (compareByDay == 0) {
            return o1.name.compareTo(o2.name);
        }
        else {
            return compareByDay;
        }
    });
    for (Country country : countriesToOutput) {
      writer.println(country.name + " " + country.getFilledDay());
    }
  }

  private void doNewDay() {
    prepareNewDay();
    doTransactionsWithNeighbours();
    closeDay();
  }

  private void closeDay() {
    for (City city : cities.values()) {
      city.fillBalances();
      city.clearIncoming();
    }
  }

  private boolean checkCitiesDone(int day) {
    for (Country country : countries.values()) {
      if (country.getFilledDay() < 0) {
        country.setFilledDay(day);
      }
    }
    boolean allCitiesDone = true;
    for (City city : cities.values()) {
      if (!city.checkIfDone(countries.values())) {
        allCitiesDone = false;
        city.getCountry().setFilledDay(-1);
      }
    }
    return allCitiesDone;
  }

  private void doTransactionsWithNeighbours() {
    for (Map.Entry<City, LinkedList<City>> entry : citiesNeighbourship.entrySet()) {
      for (City neighbour : entry.getValue()) {
        for (Country country : countries.values()) {
          doTransaction(entry.getKey(), neighbour, country);
        }
      }
    }
  }

  private void prepareNewDay() {
    for (City city : cities.values()) {
      city.setAmountsToPay();
    }
  }

  private void doTransaction(City debit, City credit, Country country) {
    int amount = debit.withdrawalCoins(country);
    credit.acceptCoins(country, amount);
  }

  private void setCitiesNeighbourship() {
    for (City city : cities.values()) {
      int x = city.getPosition().getX();
      int y = city.getPosition().getY();
      LinkedList<City> neighbours = new LinkedList<>();
      if (checkLeftNeighbourCity(x, y)) {
        neighbours.add(cities.get(getHashForCity(x - 1, y)));
      }
      if (checkRightNeighbourCity(x, y)) {
        neighbours.add(cities.get(getHashForCity(x + 1, y)));
      }
      if (checkHigherNeighbourCity(x, y)) {
        neighbours.add(cities.get(getHashForCity(x, y + 1)));
      }
      if (checkLowerNeighbourCity(x, y)) {
        neighbours.add(cities.get(getHashForCity(x, y - 1)));
      }
      citiesNeighbourship.put(city, neighbours);
    }
  }

  private void setInitialBalances() {
    for (City city : cities.values()) {
      city.setInitialBalances(countries.values());
    }
  }

  private Integer getSomeCountryFromMap() {
    for (Integer key : countries.keySet()) {
      return key;
    }
    return null;
  }

  private int getHashForCity(int x, int y) {
    return 10 * x + y;
  }

  private boolean checkLeftNeighbourCity(int x, int y) {
    return x != minCoord && matrix[x - 1][y] != 0;
  }

  private boolean checkRightNeighbourCity(int x, int y) {
    return x != maxCoord && matrix[x + 1][y] != 0;
  }

  private boolean checkHigherNeighbourCity(int x, int y) {
    return y != maxCoord && matrix[x][y + 1] != 0;
  }

  private boolean checkLowerNeighbourCity(int x, int y) {
    return y != minCoord && matrix[x][y - 1] != 0;
  }


  private void findLeftAndRightNeighboursForCountry(Integer currentCountryNumber, List<Integer> accessibleCountries, List<Integer> currentCountryNeighbours) {
    Country currentCountry = countries.get(currentCountryNumber);
    for (int i = 0; i <= currentCountry.getUpperRightPosition().getY() - currentCountry.getLowerLeftPosition().getY(); i++) {
      if (currentCountry.getLowerLeftPosition().getX() != minCoord) {
        int country = matrix[currentCountry.getLowerLeftPosition().getX() - 1][currentCountry.getLowerLeftPosition().getY() + i];
        addCountryToNeighboursList(country, accessibleCountries, currentCountryNeighbours);
      }
      if (currentCountry.getUpperRightPosition().getX() != maxCoord) {
        int country = matrix[currentCountry.getUpperRightPosition().getX() + 1][i + currentCountry.getLowerLeftPosition().getY() + i];
        addCountryToNeighboursList(country, accessibleCountries, currentCountryNeighbours);
      }
    }


  }


  private void findLowerAndUpperNeighboursForCountry(Integer currentCountryNumber, List<Integer> accessibleCountries, List<Integer> currentCountryNeighbours) {
    Country currentCountry = countries.get(currentCountryNumber);

    for (int i = currentCountry.getLowerLeftPosition().getX(); i <= currentCountry.getUpperRightPosition().getX() - currentCountry.getLowerLeftPosition().getX(); i++) {
      if (currentCountry.getLowerLeftPosition().getY() != minCoord) {
        int country = matrix[currentCountry.getLowerLeftPosition().getX() + i][currentCountry.getLowerLeftPosition().getY() - 1];
        addCountryToNeighboursList(country, accessibleCountries, currentCountryNeighbours);
      }
      if (currentCountry.getUpperRightPosition().getY() != maxCoord) {
        int country = matrix[currentCountry.getLowerLeftPosition().getX() + i][currentCountry.getUpperRightPosition().getY() + 1];
        addCountryToNeighboursList(country, accessibleCountries, currentCountryNeighbours);
      }
    }

  }


  private void addCountryToNeighboursList(int countryNumber, List<Integer> accessibleCountries, List<Integer> currentCountryNeighbours) {
    if (countryNumber != 0) {
      if (!(accessibleCountries.contains(countryNumber) ||
        currentCountryNeighbours.contains(countryNumber))) {
        currentCountryNeighbours.add(countryNumber);
      }
    }
  }
}
