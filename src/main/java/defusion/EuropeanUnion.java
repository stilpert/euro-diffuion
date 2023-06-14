package defusion;

import lombok.Getter;

import java.util.HashMap;
import java.util.InputMismatchException;
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

            city.createAccounts(countries);
        }

        if(countries.size() > 1 && countries.stream().anyMatch(Country::isIsolated)) {
            throw new InputMismatchException("One of a countries is isolated");
        }

    }

    public static EuropeanUnion buildFromStrings(List<String> countryStrings) {
        List<Country> countries = countryStrings.stream().map(Country::buildFromString).collect(Collectors.toList());

        return new EuropeanUnion(countries);
    }

    public void createCities(Country country) {
        for (int y = country.getLowerLeftPosition().getY(); y <= country.getUpperRightPosition().getY(); y++) {
            for (int x = country.getLowerLeftPosition().getX(); x <= country.getUpperRightPosition().getX(); x++) {
                City city = new City(new Coordinate(x, y), country);
                country.getCities().add(city);
                cities.put(City.positionHash(city.getPosition().getX(), city.getPosition().getY()), city);
            }
        }
    }

    public void runSimulation() {
        int day = 0;
        do {
            day++;

            setTransactionSumsForCities();
            sendTransactionsFromCitiesToNeighbours();

        } while (!isCitiesComplete(day));
    }

    private void sendTransactionsFromCitiesToNeighbours() {
        for (City city : cities.values()) {
            for (City neighbour : city.getNeihgbours()) {
                city.sendCoins(neighbour);
            }
        }
    }

    private void setTransactionSumsForCities() {
        for (City city : cities.values()) {
            city.setTransactionSums();
        }
    }

    private boolean isCitiesComplete(int day) {
        for (Country country : countries) {
            if (country.getCompleteDay() < 0) {
                country.setCompleteDay(day);
            }
        }
        boolean allCitiesComplete = true;
        for (City city : cities.values()) {
            if (!city.isComplete()) {
                allCitiesComplete = false;
                city.getCountry().setCompleteDay(-1);
            }
        }
        return allCitiesComplete;
    }
}
