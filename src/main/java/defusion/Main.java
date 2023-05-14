package defusion;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
  public static void main(String[] args) throws Exception {
    PrintWriter writer = new PrintWriter("output.txt");
    int testCaseCount = 0;
    Path path = Paths.get("input.txt");
    List<String> strings = Files.readAllLines(path);
    for (int i = 0; i < strings.size(); i++) {
      testCaseCount++;
      int numberCountries = Integer.parseInt(strings.get(i));
      WorldMap worldMap = WorldMap.buildFromStrings(strings.subList(i + 1, i + numberCountries + 1));
      i += numberCountries;

      if (worldMap.validateCoordinates()) {
        if (worldMap.checkCountriesAccessibility()) {
          worldMap.prepareToStart();
          writer.println("Case Number " + testCaseCount);
          worldMap.doDiffusion();
          worldMap.showResults(writer);
        }
        else {
          writer.println("Case Number " + testCaseCount);
          writer.println("Some countries are isolated!");
        }
      }
      else {
        writer.println("Case Number " + testCaseCount);
        writer.println("Wrong input!");
      }
    }
    writer.close();
  }
}
