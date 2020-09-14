package codegenerator;

import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {
    private final Path inputPath;
    private final String absolutePackagePath;
    private final String className;

    public Parser(Path inputPath, String packageName, String className) {
        this.inputPath = inputPath;
        this.absolutePackagePath = packageName;

        // the first letter should be capitalized
        this.className = className.substring(0, 1).toUpperCase() + className.substring(1);
    }

    private ArrayList<Token> tokenize() throws IOException {
        String content = readInput();
        String[] queryLines = content.split("\n");
        ArrayList<Token> tokens = new ArrayList<>();
        for (int i = 0; i < queryLines.length; i++) {
            var token = Token.fromLine(queryLines[i], i);
            tokens.add(token);
        }
        return tokens;
    }

    public void generate() throws IOException, ParseException {
        ArrayList<Token> tokens = tokenize();

        ArrayList<ArrayList<Token>> queryTokens = partitionIntoQueryTokens(tokens);
        ArrayList<Query> queries = new ArrayList<>();

        for (var queryToken : queryTokens) {
            Query query = Query.fromTokens(queryToken);
            queries.add(query);
        }

        TypeSpec typeSpec = getClassBuilder(queries).build();

        String packageName = getPackageNameFromPackagePath(absolutePackagePath);
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec)
                .build();

        Path path = Path.of(absolutePackagePath);
        javaFile.writeToPath(path);
    }

    private TypeSpec.Builder getClassBuilder(List<Query> queries) {
        TypeSpec.Builder typeSpecBuilder = TypeSpec.classBuilder(this.className)
                .addModifiers(Modifier.PUBLIC);
        var methods = queries
                .stream()
                .map(Parser::methodFromQuery)
                .collect(Collectors.toList());
        typeSpecBuilder.addMethods(methods);
        return typeSpecBuilder;
    }

    private static MethodSpec methodFromQuery(Query query) {
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
        return Files.readString(inputPath);
    }

    private static String getPackageNameFromPackagePath(String packagePath) {
        int indexOfLastSLash = packagePath.lastIndexOf(File.separator) + 1;
        return packagePath.substring(indexOfLastSLash);
    }
}
