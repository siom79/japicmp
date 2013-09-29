package japicmp;

import japicmp.cli.CliParser;
import japicmp.cmp.JarArchiveComparator;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.OutputTransformer;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutputGenerator;

import java.io.File;
import java.util.List;

public class JApiCmp {

    public static void main(String[] args) {
        Options options = parseCliOptions(args);
        File oldArchive = new File(options.getOldArchive());
        File newArchive = new File(options.getNewArchive());
        verifyFilesExist(oldArchive, newArchive);
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator();
        List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);
        generateOutput(options, oldArchive, newArchive, jApiClasses);
    }

    private static void generateOutput(Options options, File oldArchive, File newArchive, List<JApiClass> jApiClasses) {
        OutputTransformer.sortClassesAndMethods(jApiClasses);
        if(options.getXmlOutputFile().isPresent()) {
            XmlOutputGenerator xmlGenerator = new XmlOutputGenerator();
            xmlGenerator.generate(oldArchive, newArchive, jApiClasses, options);
        }
        StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator();
        String output = stdoutOutputGenerator.generate(oldArchive, newArchive, jApiClasses, options);
        System.out.println(output);
    }

    private static Options parseCliOptions(String[] args) {
        Options options = new Options();
        try {
            CliParser cliParser = new CliParser();
            options = cliParser.parse(args);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.exit(-1);
        } catch (Exception e) {
            System.err.println("Failed to parse command line options: " + e.getMessage());
            System.exit(-1);
        }
        return options;
    }

    private static void verifyFilesExist(File oldArchive, File newArchive) {
        if (!oldArchive.exists()) {
            System.err.println(String.format("File '%s' does not exist.", oldArchive.getAbsolutePath()));
            System.exit(-1);
        }
        if (!newArchive.exists()) {
            System.err.println(String.format("File '%s' does not exist.", newArchive.getAbsolutePath()));
            System.exit(-1);
        }
    }
}
