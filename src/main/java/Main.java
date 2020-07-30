import cli.CliApp;
import codegenerator.Parser;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;
import java.text.ParseException;

public class Main {
    public static void main(String[] args) {
        int exitCode = new CommandLine(new CliApp()).execute(args);
        System.exit(exitCode);
    }
}
