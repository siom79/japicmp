package japicmp.cli;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.JApiClass;
import japicmp.output.html.HtmlOutput;
import japicmp.output.html.HtmlOutputGenerator;
import japicmp.output.html.HtmlOutputGeneratorOptions;
import japicmp.output.incompatible.IncompatibleErrorOutput;
import japicmp.output.semver.SemverOut;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.output.xml.XmlOutputGeneratorOptions;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class JApiCli {

	public enum ClassPathMode {
		ONE_COMMON_CLASSPATH, TWO_SEPARATE_CLASSPATHS
	}

	public void run(String[] args) {
		CliParser cliParser = new CliParser();
		Options options = cliParser.parse(args);
		if (options.isHelpRequested()) {
			CliParser.printHelp();
			return;
		}
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(JarArchiveComparatorOptions.of(options));
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(options.getOldArchives(), options.getNewArchives());
		generateOutput(options, jApiClasses, jarArchiveComparator);
	}

	private void generateOutput(Options options, List<JApiClass> jApiClasses, JarArchiveComparator jarArchiveComparator) {
		if (options.isSemanticVersioning()) {
			SemverOut semverOut = new SemverOut(options, jApiClasses);
			String output = semverOut.generate();
			System.out.println(output);
			return;
		}
		SemverOut semverOut = new SemverOut(options, jApiClasses);
		if (options.getXmlOutputFile().isPresent()) {
			XmlOutputGeneratorOptions xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
			xmlOutputGeneratorOptions.setCreateSchemaFile(true);
			xmlOutputGeneratorOptions.setSemanticVersioningInformation(semverOut.generate());
			XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(jApiClasses, options, xmlOutputGeneratorOptions);
			try (XmlOutput xmlOutput = xmlGenerator.generate()) {
				XmlOutputGenerator.writeToFiles(options, xmlOutput);
			} catch (Exception e) {
				throw new JApiCmpException(JApiCmpException.Reason.IoException, "Could not write XML file: " + e.getMessage(), e);
			}
		}
		if (options.getHtmlOutputFile().isPresent()) {
			HtmlOutputGeneratorOptions htmlOutputGeneratorOptions = new HtmlOutputGeneratorOptions();
			htmlOutputGeneratorOptions.setSemanticVersioningInformation(semverOut.generate());
			HtmlOutputGenerator outputGenerator = new HtmlOutputGenerator(jApiClasses, options, htmlOutputGeneratorOptions);
			HtmlOutput htmlOutput = outputGenerator.generate();
            try {
                Files.write(Paths.get(options.getHtmlOutputFile().get()), htmlOutput.getHtml().getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
				throw new JApiCmpException(JApiCmpException.Reason.IoException, "Could not write HTML file: " + e.getMessage(), e);
            }
        }
		StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options, jApiClasses);
		String output = stdoutOutputGenerator.generate();
		System.out.println(output);

		if (options.isErrorOnBinaryIncompatibility()
			|| options.isErrorOnSourceIncompatibility()
			|| options.isErrorOnExclusionIncompatibility()
			|| options.isErrorOnModifications()
			|| options.isErrorOnSemanticIncompatibility()) {
			IncompatibleErrorOutput errorOutput = new IncompatibleErrorOutput(options, jApiClasses, jarArchiveComparator);
			errorOutput.generate();
		}
	}
}
