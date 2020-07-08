package querybuilder;

import java.util.ArrayList;

public class Metadata {
    private String name;
    private QueryType queryType;
    private ResultType resultType;

    public Metadata(String name, QueryType queryType, ResultType resultType) {
        this.name = name;
        this.queryType = queryType;
        this.resultType = resultType;
    }

    public Metadata(ArrayList<Token> comments) {
        for (Token comment : comments) {
            switch (comment.getType()) {
                case NAME:
                    name = comment.getValue();
                    break;
                case QUERY_TYPE:
                    try {
                        queryType = QueryType.fromString(comment.getValue());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("invalid token at line: " +
                                comment.getLineNumber() + " " + e.getMessage());
                    }
                    break;
                case RESULT_TYPE:
                    try {
                        resultType = ResultType.fromString(comment.getValue());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("invalid token at line: " +
                                comment.getLineNumber() + " " + e.getMessage());
                    }
                default:
            }
        }
        if (name == null) {
            throw new IllegalArgumentException("there should be a comment stating the name of the query at line: "
                    + comments.get(0).getLineNumber());
        }
        if (queryType == null) {
            throw new IllegalArgumentException("there should be a comment stating the type of the query at line: "
                    + comments.get(0).getLineNumber());
        }
        if (resultType == null) {
            throw new IllegalArgumentException("there should be a comment stating the result type of the query at line: "
                    + comments.get(0).getLineNumber());
        }
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
                default -> throw new IllegalArgumentException("querybuilder.Query type not valid");
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

    @Override
    public String toString() {
        return "Metadata{" +
                "name='" + name + '\'' +
                ", queryType=" + queryType +
                ", resultType=" + resultType +
                '}';
    }
}
