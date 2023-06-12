package defusion;

import lombok.Getter;
import lombok.Setter;

public class Country {
  public final String name;
  @Getter
  private final Coordinate lowerLeftPosition;
  @Getter
  private final Coordinate upperRightPosition;

  @Getter @Setter
  private int filledDay;

  private Country(String name, Coordinate lowerLeftPosition, Coordinate upperRightPosition) {
    this.name = name;
    this.lowerLeftPosition = lowerLeftPosition;
    this.upperRightPosition = upperRightPosition;
    this.filledDay = -1;
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
}
