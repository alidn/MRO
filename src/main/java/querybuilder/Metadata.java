package querybuilder;

public class Metadata {
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
}
