package japicmp.cli;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.JApiClass;
import japicmp.output.incompatible.IncompatibleErrorOutput;
import japicmp.output.semver.SemverOut;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.output.xml.XmlOutputGeneratorOptions;

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
		if (options.getXmlOutputFile().isPresent() || options.getHtmlOutputFile().isPresent()) {
			SemverOut semverOut = new SemverOut(options, jApiClasses);
			XmlOutputGeneratorOptions xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
			xmlOutputGeneratorOptions.setCreateSchemaFile(true);
			xmlOutputGeneratorOptions.setSemanticVersioningInformation(semverOut.generate());
			XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(jApiClasses, options, xmlOutputGeneratorOptions);
			try (XmlOutput xmlOutput = xmlGenerator.generate()) {
				XmlOutputGenerator.writeToFiles(options, xmlOutput);
			} catch (Exception e) {
				throw new JApiCmpException(JApiCmpException.Reason.IoException, "Could not close output streams: " + e.getMessage(), e);
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
