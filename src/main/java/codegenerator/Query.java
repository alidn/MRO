package codegenerator;

import java.text.ParseException;
import java.util.ArrayList;

public class Query {
    private final Metadata metadata;
    private final String query;
    private final ArrayList<Param> params;

    public static class Param {
        private final String name;
        private final int index;

        public Param(String name, int index) {
            this.name = name;
            this.index = index;
        }

        public String getName() {
            return name;
        }

        public int getIndex() {
            return index;
        }

        @Override
        public String toString() {
            return "Param{" +
                    "name='" + name + '\'' +
                    ", index=" + index +
                    '}';
        }
    }

    public Query(String query, Metadata metadata) {
        // generate params and replace param names with question mark.
        this.params = getParamsFromNamedQuery(query);
        this.query = namedQueryToStandard(query);

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
                case NAME, RESULT_TYPE, QUERY_TYPE, RETURN -> metadataTokens.add(token);
                case SQL -> queryTokens.add(token);
            }
        }
        return new Query(queryTokens, metadataTokens);
    }

    private static String namedQueryToStandard(String namedQuery) {
        StringBuilder resultQuery = new StringBuilder();
        boolean isParamName = false;
        char[] chars = namedQuery.toCharArray();

        for (char c : chars) {
            switch (c) {
                case ':' -> isParamName = true;
                case ';', '\n', ',', ')', ' ' -> {
                    if (isParamName)
                        resultQuery.append('?');
                    isParamName = false;
                    resultQuery.append(c);
                }
                default -> {
                    if (!isParamName) resultQuery.append(c);
                }
            }
        }
        return resultQuery.toString();
    }

    private static ArrayList<Param> getParamsFromNamedQuery(String namedQuery) {
        String[] words = namedQuery.split("[ \n]");
        ArrayList<Param> parameters = new ArrayList<>();
        for (String word : words) {
            if (word.startsWith(":")) {
                String param = word.substring(1);
                if (param.endsWith(");")) {
                    param = param.substring(0, param.length() - 2);
                } else if (param.endsWith(")") || param.endsWith(",")
                        || param.endsWith(";")) {
                    param = param.substring(0, param.length() - 1);
                }
                parameters.add(new Param(param, parameters.size()));
            } else if (word.startsWith("(:")) {
                String param = word.substring(2);
                if (param.endsWith(");")) {
                    param = param.substring(0, param.length() - 2);
                } else if (param.endsWith(")") || param.endsWith(",")
                        || param.endsWith(";")) {
                    param = param.substring(0, param.length() - 1);
                }
                parameters.add(new Param(param, parameters.size()));
            }
        }
        return parameters;
    }

    private static String queryStringFromTokens(ArrayList<Token> queryTokens) {
        StringBuilder query = new StringBuilder();
        for (Token token : queryTokens) {
            query.append(token.getValue()).append("\n");
        }
        return query.toString();
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public String getQuery() {
        return query;
    }

    public ArrayList<Param> getParams() {
        return params;
    }

    @Override
    public String toString() {
        return "Query{" +
                "metadata=" + metadata +
                ", query='" + query + '\'' +
                ", params=" + params +
                '}';
    }
}
