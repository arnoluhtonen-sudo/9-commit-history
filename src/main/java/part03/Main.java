package part03;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    public record Commit(String id, String parent, String commit) {
        // record Commit ottaa talteen nuo tiedot
    }

    public static class CommitParser {

        public static Map<String, Commit> parse(String fileContents) {

            Map<String, Commit> commits = new HashMap<>();

            // jaetaan teksti rivinvaihdosta
            String[] chunks = fileContents.split("\\n\\n");

            // käydään läpi kappaleet
            for (String commit : chunks) {
                String[] lines = commit.split("\\n");
                // jaetaan commitin osiin ja eka=0 osa on otsikko
                String header = lines[0];
                // jaetaan otsikko välilyönneistä
                String[] parts = header.split(" ");
                // löytyy id
                String id = parts[1];
                // löytyy parent headerin substringin "(Parent " ja ")" välistä
                String parent = header.substring(
                        header.indexOf("(Parent: ") + 9,
                        header.indexOf(")"));

                    // laittaa noi mappiin
                commits.put(id, new Commit(id, parent, commit));
            }
            // palauttaa mapin
            return commits;

        }

        public static  List<Commit> sortCommits(Map<String, Commit> commits) {

            List<Commit> sorted = new ArrayList<>();

            // ettii ekan commitin missä parent = "None"
            Commit unsorted = commits.values().stream()
                    .filter(commit -> commit.parent().equals("None"))
                    .findFirst()
                    .get();


            // käy läpi ja lisää käyttäen jotain järjestystä
            // sitku lista loppuu tulee null ja silmukka päättyy
            while (unsorted != null) {

                sorted.add(unsorted);

                String currentId = unsorted.id();

                unsorted = commits.values().stream()
                        .filter(commit -> currentId.equals(commit.parent()))
                        .findFirst()
                        .orElse(null);

            }
            // palauttaa listan
            return sorted;
        }
    }

    public static void main(String[] args) throws IOException {
        Path logFile = Path.of("pizza-commits.txt");
        String fileContents = Files.readString(logFile, StandardCharsets.UTF_8);

        // ajetaan fileContents CommitParser luokan läpi
        // pilkotaan ja sit sortataan
        Map<String, Commit> commits = CommitParser.parse(fileContents);

        List<Commit> sorted = CommitParser.sortCommits(commits);

        // jokaista kommmittia kohtaan tulostaa kommitin+välilyönnin
        sorted.forEach(commit -> System.out.println(commit.commit() + "\n"));
    }
}
