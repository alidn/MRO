import querybuilder.QueryParser;

import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws IOException {
        QueryParser queryParser = new QueryParser("src/main/queries.sql", "");
        try {
            queryParser.parseInput();
        } catch (ParseException e) {
            System.out.println(e.getMessage() + " at line " + e.getErrorOffset());
        }
    }
}
