package japicmp;

import japicmp.cli.CliParser;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.JApiClass;
import japicmp.output.OutputTransformer;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutputGenerator;

import java.io.File;
import java.util.List;

public class JApiCmp {

    public static void main(String[] args) {
        try {
            JApiCmp app = new JApiCmp();
            app.run(args);
        } catch (JApiCmpException e) {
            if (e.getReason() != JApiCmpException.Reason.NormalTermination) {
                System.err.println(e.getMessage());
                System.exit(-1);
            }
        } catch (Exception e) {
            System.err.println(String.format("Execution of %s failed: %s", JApiCmp.class.getSimpleName(), e.getMessage()));
            System.exit(-2);
        }
    }

    private void run(String[] args) {
        Options options = parseCliOptions(args);
        File oldArchive = new File(options.getOldArchive());
        File newArchive = new File(options.getNewArchive());
        verifyFilesExist(oldArchive, newArchive);
        JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
        copyOptions(options, comparatorOptions);
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
        List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);
        generateOutput(options, oldArchive, newArchive, jApiClasses);
    }

    private void copyOptions(Options options, JarArchiveComparatorOptions comparatorOptions) {
        comparatorOptions.setModifierLevel(options.getAccessModifier());
        comparatorOptions.getPackagesInclude().addAll(options.getPackagesInclude());
        comparatorOptions.getPackagesExclude().addAll(options.getPackagesExclude());
    }

    private void generateOutput(Options options, File oldArchive, File newArchive, List<JApiClass> jApiClasses) {
        OutputTransformer.sortClassesAndMethods(jApiClasses);
        if (options.getXmlOutputFile().isPresent()) {
            XmlOutputGenerator xmlGenerator = new XmlOutputGenerator();
            xmlGenerator.generate(oldArchive, newArchive, jApiClasses, options);
        }
        StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator();
        String output = stdoutOutputGenerator.generate(oldArchive, newArchive, jApiClasses, options);
        System.out.println(output);
    }

    private Options parseCliOptions(String[] args) {
        try {
            CliParser cliParser = new CliParser();
            return cliParser.parse(args);
        } catch (IllegalArgumentException e) {
            throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, e.getMessage());
        } catch (Exception e) {
            throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, "Failed to parse command line options: " + e.getMessage());
        }
    }

    private void verifyFilesExist(File oldArchive, File newArchive) {
        if (!oldArchive.exists()) {
            String msg = String.format("File '%s' does not exist.", oldArchive.getAbsolutePath());
            throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, msg);
        }
        if (!newArchive.exists()) {
            String msg = String.format("File '%s' does not exist.", newArchive.getAbsolutePath());
            throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, msg);
        }
        if(oldArchive.equals(newArchive)) {
            String msg = String.format("Files '%s' and '%s' are the same.", oldArchive.getAbsolutePath(), newArchive.getAbsolutePath());
            throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, msg);
        }
    }
}
