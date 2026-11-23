package part01;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommitParser {

    public static List<Commit> parseMany(String commitHistory) {
        if (commitHistory.isEmpty()) {
            return List.of();
        }

        String[] commits = commitHistory.split("\\n\\n");

        return Stream.of(commits)
                .map(CommitParser::parseOne)
                .toList();
    }

    public static Commit parseOne(String commitText) {
        String firstLine = commitText.lines().findFirst().get();

        String message = commitText.lines()
                .skip(1)
                .collect(Collectors.joining("\n"));

        String[] chunks = firstLine.split(" ");

        String id = chunks[1];
        String contributor = chunks[3];
        String parentId = chunks[5].replaceAll("[^a-zA-Z0-9]", "");

        return new Commit(id, contributor, parentId, message, commitText);
    }
}