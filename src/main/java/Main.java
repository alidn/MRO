import querybuilder.QueryBuilder;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        QueryBuilder queryBuilder = new QueryBuilder("src/main/queries.sql", "");
        queryBuilder.generateCode();
    }
}
