package querybuilder;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

public class Query {
    private Metadata metadata;
    private String query;
    private Param[] params;

    static class Param {
        String name;
        int[] indices;
    }

    public Query(String query, Metadata metadata) {
        // generate params and replace param names with question mark.
        this.query = query;
        this.metadata = metadata;
    }

    public Query(ArrayList<Token> queryTokens, ArrayList<Token> metadataTokens) throws ParseException {
        this(queryStringFromTokens(queryTokens), new Metadata(metadataTokens));
    }

    public static Query fromTokens(ArrayList<Token> tokens) throws ParseException {
        ArrayList<Token> metadataTokens = new ArrayList<>();
        ArrayList<Token> queryTokens = new ArrayList<>();
        for (Token token : tokens) {
            switch (token.getType()) {
                case NAME, RESULT_TYPE, QUERY_TYPE -> metadataTokens.add(token);
                case SQL -> queryTokens.add(token);
            }
        }
        return new Query(queryTokens, metadataTokens);
    }

    private static String queryStringFromTokens(ArrayList<Token> queryTokens) {
        StringBuilder query = new StringBuilder();
        for (Token token : queryTokens) {
            query.append(token.getValue());
        }
        return query.toString();
    }

    @Override
    public String toString() {
        return "Query{" +
                "metadata=" + metadata +
                ", query='" + query + '\'' +
                ", params=" + Arrays.toString(params) +
                '}';
    }
}
