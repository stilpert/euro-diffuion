package defusion;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;

public class Main {
    private static final String INPUT_FILE = "input.txt";
    private static final String OUTPUT_FILE = "output.txt";

    private static final Comparator<Country> countryComparator = (o1, o2) -> {
        int compareByDay = o1.getCompleteDay() - o2.getCompleteDay();
        if (compareByDay == 0) {
            return o1.name.compareTo(o2.name);
        } else {
            return compareByDay;
        }
    };

    public static void main(String[] args) throws Exception {
        try (PrintWriter writer = new PrintWriter(OUTPUT_FILE)) {
            int testCaseNum = 0;
            Path path = Paths.get(INPUT_FILE);
            List<String> strings = Files.readAllLines(path);
            for (int i = 0; i < strings.size(); i++) {
                testCaseNum++;
                int numberCountries = Integer.parseInt(strings.get(i));
                EuropeanUnion europeanUnion = EuropeanUnion.buildFromStrings(strings.subList(i + 1, i + numberCountries + 1));
                i += numberCountries;

                writer.println("Case Number " + testCaseNum);
                europeanUnion.runSimulation();

                europeanUnion.getCountries().sort(countryComparator);
                for (Country country : europeanUnion.getCountries()) {
                    writer.println(country.name + " " + country.getCompleteDay());
                }
            }
        }
    }
}
