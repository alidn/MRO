package querybuilder;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;

import javax.lang.model.element.Modifier;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        for (int i = 0; i < queries.length; i++) {
            var token = Token.fromLine(queries[i], i);
            tokens.add(token);
        }
        return tokens;
    }

    public void generateCode() throws IOException, ParseException {
        ArrayList<Token> tokens = tokenize();

        ArrayList<ArrayList<Token>> queryTokens = partitionIntoQueryTokens(tokens);
        ArrayList<Query> queries = new ArrayList<>();

        for (var queryToken : queryTokens) {
            queries.add(Query.fromTokens(queryToken));
        }

        queries.forEach(query -> {
//            System.out.println(query);
            System.out.println(methodFromQuery(query));
        });
    }

    private static String methodFromQuery(Query query) {
        return CodeGenerator.methodFromQuery(query);
    }

    private static ArrayList<ArrayList<Token>> partitionIntoQueryTokens(ArrayList<Token> tokens) {
        ArrayList<ArrayList<Token>> queries = new ArrayList<>();
        ArrayList<Token> currentQuery = new ArrayList<>();
        tokens.add(new Token("", Token.Type.EMPTY_LINE, tokens.size()));
        for (var token : tokens) {
            if (token.getType().equals(Token.Type.EMPTY_LINE)) {
                // handling the case when there are more than line empty line between queries.
                if (currentQuery.isEmpty()) continue;

                queries.add(currentQuery);
                currentQuery = new ArrayList<>();
            } else {
                currentQuery.add(token);
            }
        }
        return queries;
    }

    private String readInput() throws IOException {
        Path path = Path.of(inputPath);
        return Files.readString(path);
    }
}
