package cli;

import codegenerator.Parser;
import picocli.CommandLine;

import java.io.File;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "genfile", description = "Generates a Java file from the given sql file")
public class CliApp implements Callable<String> {

    @CommandLine.Parameters(index = "0", description = "The sql file to parse")
    private Path inputPath;

    @CommandLine.Parameters(index = "1", description = "package name of the generated file")
    private String packageName;

    @Override
    public String call() throws Exception {
        String outputFileName = inputPath.getFileName().toString().split("\\.")[0];
        Parser parser = new Parser(inputPath, packageName, outputFileName);
        try {
            parser.generate();
        } catch (ParseException e) {
            System.out.println(e.getMessage() + " at line " + e.getErrorOffset());
        }

        return outputFileName + " was generated in package " + packageName;
    }
}
