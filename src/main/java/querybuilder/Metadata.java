package querybuilder;

import java.rmi.activation.ActivationInstantiator;
import java.text.ParseException;
import java.util.ArrayList;

public class Metadata {
    private String name;
    private QueryType queryType;
    private ResultType resultType;
    private String returnType;

    public Metadata(String name, QueryType queryType, ResultType resultType, String returnType) {
        this.name = name;
        this.queryType = queryType;
        this.resultType = resultType;
        this.returnType = returnType;
    }

    public Metadata(ArrayList<Token> comments) throws ParseException {
        for (Token comment : comments) {
            switch (comment.getType()) {
                case NAME:
                    name = comment.getValue();
                    break;
                case QUERY_TYPE:
                    try {
                        queryType = QueryType.fromString(comment.getValue());
                    } catch (IllegalArgumentException e) {
                        throw new ParseException(e.getMessage(), comment.getLineNumber());
                    }
                    break;
                case RESULT_TYPE:
                    try {
                        resultType = ResultType.fromString(comment.getValue());
                    } catch (IllegalArgumentException e) {
                        throw new ParseException(e.getMessage(), comment.getLineNumber());
                    }
                case RETURN:
                    returnType = comment.getValue();
                default:
            }
        }
        if (name == null) {
            throw new ParseException("there should be a comment stating the name of the query",
                    comments.get(0).getLineNumber());
        }
        if (queryType == null) {
            throw new ParseException("there should be a comment stating the type of the query",
                    comments.get(0).getLineNumber());
        }
        if (resultType == null) {
            throw new ParseException("there should be a comment stating the result type of the query",
                    comments.get(0).getLineNumber());
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

    public String getName() {
        return name;
    }

    public QueryType getQueryType() {
        return queryType;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public String getReturnType() {
        return returnType;
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
