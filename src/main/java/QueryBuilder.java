import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

class Token {
    private final String value;
    private final Type type;

    public enum Type {
        SQL,
        EMPTY_LINE,
        NAME,
        QUERY_TYPE,
        RESULT_TYPE
    }

    public Token(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    public static Token fromLine(String line) {
        if (line.length() == 0) {
            return new Token("", Type.EMPTY_LINE);
        } else if (line.matches("-- @name.* .*")) {
            return new Token(getCommentValue(line), Type.NAME);
        } else if (line.matches("-- @type.* .*")) {
            return new Token(getCommentValue(line), Type.QUERY_TYPE);
        } else if (line.matches("-- @result.* .*")) {
            return new Token(getCommentValue(line), Type.RESULT_TYPE);
        } else {
            return new Token(line, Type.SQL);
        }
    }

    private static String getCommentValue(String line) {
        String[] s = line.split("-- @.* ");
        return s[1];
    }

    public String getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Token{" +
                "value='" + value + '\'' +
                ", type=" + type +
                '}';
    }
}

class Metadata {
    private String name;
    private QueryType queryType;
    private ResultType resultType;

    public Metadata(String name, QueryType queryType, ResultType resultType) {
        this.name = name;
        this.queryType = queryType;
        this.resultType = resultType;
    }

    public static Metadata fromTokens(Token[] comments) {
        String name = null;
        QueryType queryType = null;
        ResultType resultType = null;
        for (Token comment : comments) {
            switch (comment.getType()) {
                case NAME:
                    name = comment.getValue();
                    break;
                case QUERY_TYPE:
                    queryType = QueryType.fromString(comment.getValue());
                    break;
                case RESULT_TYPE:
                    resultType = ResultType.fromString(comment.getValue());
                default:
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("there should be a comment stating the name of the query");
        }
        if (queryType == null) {
            throw new IllegalArgumentException("there should be a comment stating the type of the query");
        }
        if (resultType == null) {
            throw new IllegalArgumentException("there should be a comment stating the result type of the query");
        }
        return new Metadata(name, queryType, resultType);
    }

    public enum QueryType {
        QUERY_TYPE,
        RETURNING_EXECUTE,
        INSERT;

        public static QueryType fromString(String str) {
            return switch (str) {
                case "query" -> QUERY_TYPE;
                case "returning_execute" -> RETURNING_EXECUTE;
                case "insert" -> INSERT;
                default -> throw new IllegalArgumentException("Query type not valid");
            };
        }
    }

    public enum ResultType {
        ONE,
        MANY,
        AFFECTED;

        public static ResultType fromString(String str) {
            return switch (str) {
                case "one" -> ONE;
                case "many" -> MANY;
                case "affected" -> AFFECTED;
                default -> throw new IllegalArgumentException("Result type no valid");
            };
        }
    }
}

class Param {
    String name;
    int[] indices;
}

class Query {
    private Metadata metadata;
    private String query;
    private Param[] params;
}

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
