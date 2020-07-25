import querybuilder.methodGenerator;

import java.io.IOException;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) throws IOException {
        methodGenerator methodGenerator = new methodGenerator("src/main/queries.sql", "");
        try {
            methodGenerator.parseInput();
        } catch (ParseException e) {
            System.out.println(e.getMessage() + " at line " + e.getErrorOffset());
        }
    }
}
