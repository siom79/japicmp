package japicmp.cli;

import com.google.common.base.Optional;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.JApiClass;
import japicmp.output.semver.SemverOut;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.output.xml.XmlOutputGeneratorOptions;

import javax.inject.Inject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static japicmp.model.AccessModifier.toModifier;

public class JApiCli {
	public static final String IGNORE_MISSING_CLASSES = "--ignore-missing-classes";
	public static final String IGNORE_MISSING_CLASSES_BY_REGEX = "--ignore-missing-classes-by-regex";
	public static final String OLD_CLASSPATH = "--old-classpath";
	public static final String NEW_CLASSPATH = "--new-classpath";

	public enum ClassPathMode {
		ONE_COMMON_CLASSPATH, TWO_SEPARATE_CLASSPATHS
	}

	@Command(name = "java -jar japicmp.jar", description = "Compares jars")
	public static class Compare implements Runnable {
		@Inject
		public HelpOption helpOption;
		@Option(name = { "-o", "--old" }, description = "Provides the path to the old version(s) of the jar(s). Use ; to separate jar files.")
		public String pathToOldVersionJar;
		@Option(name = { "-n", "--new" }, description = "Provides the path to the new version(s) of the jar(s). Use ; to separate jar files.")
		public String pathToNewVersionJar;
		@Option(name = { "-m", "--only-modified" }, description = "Outputs only modified classes/methods.")
		public boolean modifiedOnly;
		@Option(name = { "-b", "--only-incompatible" }, description = "Outputs only classes/methods that are binary incompatible. If not given, all classes and methods are printed.")
		public boolean onlyBinaryIncompatibleModifications;
		@Option(name = "-a", description = "Sets the access modifier level (public, package, protected, private), which should be used.")
		public String accessModifier;
		@Option(name = {"-i", "--include"}, description = "Semicolon separated list of elements to include in the form package.Class#classMember, * can be used as wildcard. Annotations are given as FQN starting with @. Examples: mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation.")
		public String includes;
		@Option(name = {"-e", "--exclude"}, description = "Semicolon separated list of elements to exclude in the form package.Class#classMember, * can be used as wildcard. Annotations are given as FQN starting with @. Examples: mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation.")
		public String excludes;
		@Option(name = { "-x", "--xml-file" }, description = "Provides the path to the xml output file.")
		public String pathToXmlOutputFile;
		@Option(name = { "--html-file" }, description = "Provides the path to the html output file.")
		public String pathToHtmlOutputFile;
		@Option(name = { "-s", "--semantic-versioning" }, description = "Tells you which part of the version to increment.")
		public boolean semanticVersioning = false;
		@Option(name = { "--include-synthetic" }, description = "Include synthetic classes and class members that are hidden per default.")
		public boolean includeSynthetic = false;
		@Option(name = { IGNORE_MISSING_CLASSES }, description = "Ignores all superclasses/interfaces missing on the classpath.")
		public boolean ignoreMissingClasses = false;
		@Option(name = {IGNORE_MISSING_CLASSES_BY_REGEX}, description = "Ignores only those superclasses/interface missing on the classpath that are selected by a regular expression.")
		public List<String> ignoreMissingClassesByRegEx = new ArrayList<>();
		@Option(name = { "--html-stylesheet" }, description = "Provides the path to your own stylesheet.")
		public String pathToHtmlStylesheet;
		@Option(name = { OLD_CLASSPATH }, description = "The classpath for the old version.")
		public String oldClassPath;
		@Option(name = { NEW_CLASSPATH }, description = "The classpath for the new version.")
		public String newClassPath;
		@Option(name = "--no-annotations", description = "Do not evaluate annotations.")
		public boolean noAnnotations = false;
		@Option(name = "--report-only-filename", description = "Use just filename in report description.")
		public boolean reportOnlyFilename;
	        @Option(name = "--include-exclusively", description = "Include only packages specified in the \"include\" option, exclude their sub-packages")
	        public boolean includeExclusively = false;
	        @Option(name = "--exclude-exclusively", description = "Exclude only packages specified in the \"exclude\" option, include their sub-packages")
	        public boolean excludeExclusively = false;

		@Override
		public void run() {
			Options options = createOptionsFromCliArgs();
			JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(JarArchiveComparatorOptions.of(options));
			List<JApiClass> jApiClasses = jarArchiveComparator.compare(options.getOldArchives(), options.getNewArchives());
			generateOutput(options, jApiClasses);
		}

		private void generateOutput(Options options, List<JApiClass> jApiClasses) {
			if (semanticVersioning) {
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
		}

		private Options createOptionsFromCliArgs() {
			Options options = Options.newDefault();
			options.getOldArchives().addAll(createFileList(checkNonNull(pathToOldVersionJar, "Required option -o is missing.")));
			options.getNewArchives().addAll(createFileList(checkNonNull(pathToNewVersionJar, "Required option -n is missing.")));
			options.setXmlOutputFile(Optional.fromNullable(pathToXmlOutputFile));
			options.setHtmlOutputFile(Optional.fromNullable(pathToHtmlOutputFile));
			options.setOutputOnlyModifications(modifiedOnly);
			options.setAccessModifier(toModifier(accessModifier));
			options.addIncludeFromArgument(Optional.fromNullable(includes), includeExclusively);
			options.addExcludeFromArgument(Optional.fromNullable(excludes), excludeExclusively);
			options.setOutputOnlyBinaryIncompatibleModifications(onlyBinaryIncompatibleModifications);
			options.setIncludeSynthetic(includeSynthetic);
			options.setIgnoreMissingClasses(ignoreMissingClasses);
			options.setHtmlStylesheet(Optional.fromNullable(pathToHtmlStylesheet));
			options.setOldClassPath(Optional.fromNullable(oldClassPath));
			options.setNewClassPath(Optional.fromNullable(newClassPath));
			options.setNoAnnotations(noAnnotations);
			for (String missingClassRegEx : ignoreMissingClassesByRegEx) {
				options.addIgnoreMissingClassRegularExpression(missingClassRegEx);
			}
			options.setReportOnlyFilename(reportOnlyFilename);
			options.verify();
			return options;
		}

		private <T> T checkNonNull(T in, String errorMessage) {
			if (in == null) {
				throw new JApiCmpException(JApiCmpException.Reason.CliError, errorMessage);
			} else {
				return in;
			}
		}
	}

	public static List<JApiCmpArchive> createFileList(String option) {
		String[] parts = option.split(";");
		List<JApiCmpArchive> jApiCmpArchives = new ArrayList<>(parts.length);
		for (String part : parts) {
			File file = new File(part);
			JApiCmpArchive jApiCmpArchive = new JApiCmpArchive(file, "n.a.");
			jApiCmpArchives.add(jApiCmpArchive);
		}
		return jApiCmpArchives;
	}
}
