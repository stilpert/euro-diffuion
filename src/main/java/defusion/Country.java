package defusion;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Country {
    public final String name;
    @Getter
    private final Coordinate lowerLeftPosition;
    @Getter
    private final Coordinate upperRightPosition;
    @Getter
    private final List<City> cities = new ArrayList<>();

    @Getter
    @Setter
    private int completeDay = -1;

    private Country(String name, Coordinate lowerLeftPosition, Coordinate upperRightPosition) {
        this.name = name;
        this.lowerLeftPosition = lowerLeftPosition;
        this.upperRightPosition = upperRightPosition;
    }

    public static Country buildFromString(String string) {
        String[] stringParts = string.split(" ");
        return new Country(
                stringParts[0],
                new Coordinate(
                        Integer.parseInt(stringParts[1]),
                        Integer.parseInt(stringParts[2])
                ),
                new Coordinate(
                        Integer.parseInt(stringParts[3]),
                        Integer.parseInt(stringParts[4])
                )
        );
    }

    public boolean isIsolated() {
        return cities.stream()
                .flatMap(city -> city.getNeihgbours().stream())
                .map(City::getCountry)
                .distinct()
                .count() == 1;
    }
}
