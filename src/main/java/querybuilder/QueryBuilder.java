package querybuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class QueryBuilder {
    private final String inputPath;
    private final String outputPath;

    public QueryBuilder(String inputPath, String outputPath) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
    }

    private ArrayList<Token> tokenize() throws IOException {
        String content = readInput();
        String[] queries = content.split("\n");
        ArrayList<Token> tokens = new ArrayList<>();
        for (String s : queries) {
            var token = Token.fromLine(s);
            tokens.add(token);
            System.out.println(token);
        }
        return tokens;
    }

    public void generateCode() throws IOException {
        tokenize();
    }

    private boolean isEmptyLine(String line) {
        return line.length() == 0;
    }

    private boolean isComment(String line) {
        return line.matches("-- @.* .*");
    }

    private String readInput() throws IOException {
        Path path = Path.of(inputPath);
        return Files.readString(path);
    }
}
