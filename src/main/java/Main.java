import querybuilder.QueryBuilder;

import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        QueryBuilder queryBuilder = new QueryBuilder("src/main/queries.sql", "");
        queryBuilder.generateCode();
    }
}
