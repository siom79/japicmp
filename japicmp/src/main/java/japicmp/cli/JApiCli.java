package japicmp.cli;

import javax.inject.Inject;
import com.google.common.base.Optional;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.output.semver.SemverOut;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.output.xml.XmlOutputGeneratorOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

public class JApiCli {
	public static final String IGNORE_MISSING_CLASSES = "--ignore-missing-classes";
	static final String OLD_CLASSPATH = "--old-classpath";
	static final String NEW_CLASSPATH = "--new-classpath";

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
		@Option(name = { "-i", "--include" },
			description = "Semicolon separated list of elements to include in the form package.Class#classMember, * can be used as wildcard. Annotations are given as FQN starting with @. Examples: mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation.")
		public String includes;
		@Option(name = { "-e", "--exclude" },
			description = "Semicolon separated list of elements to exclude in the form package.Class#classMember, * can be used as wildcard. Annotations are given as FQN starting with @. Examples: mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation.")
		public String excludes;
		@Option(name = { "-x", "--xml-file" }, description = "Provides the path to the xml output file.")
		public String pathToXmlOutputFile;
		@Option(name = { "--html-file" }, description = "Provides the path to the html output file.")
		public String pathToHtmlOutputFile;
		@Option(name = { "-s", "--semantic-versioning" }, description = "Tells you which part of the version to increment.")
		public boolean semanticVersioning = false;
		@Option(name = { "--include-synthetic" }, description = "Include synthetic classes and class members that are hidden per default.")
		public boolean includeSynthetic = false;
		@Option(name = { IGNORE_MISSING_CLASSES }, description = "Ignores superclasses/interfaces missing on the classpath.")
		public boolean ignoreMissingClasses = false;
		@Option(name = { "--html-stylesheet" }, description = "Provides the path to your own stylesheet.")
		public String pathToHtmlStylesheet;
		@Option(name = { OLD_CLASSPATH }, description = "The classpath for the old version.")
		public String oldClassPath;
		@Option(name = { NEW_CLASSPATH }, description = "The classpath for the new version.")
		public String newClassPath;
		@Option(name = "--no-annotations", description = "Do not evaluate annotations.")
		public boolean noAnnotations = false;

		@Override
		public void run() {
			Options options = createOptions();
			verifyOptions(options);
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

		private Options createOptions() {
			Options options = new Options();
			options.getOldArchives().addAll(createFileList(checkNonNull(pathToOldVersionJar, "Required option -o is missing.")));
			options.getNewArchives().addAll(createFileList(checkNonNull(pathToNewVersionJar, "Required option -n is missing.")));
			options.setXmlOutputFile(Optional.fromNullable(pathToXmlOutputFile));
			options.setHtmlOutputFile(Optional.fromNullable(pathToHtmlOutputFile));
			options.setOutputOnlyModifications(modifiedOnly);
			options.setAccessModifier(toModifier(accessModifier));
			options.addIncludeFromArgument(Optional.fromNullable(includes));
			options.addExcludeFromArgument(Optional.fromNullable(excludes));
			options.setOutputOnlyBinaryIncompatibleModifications(onlyBinaryIncompatibleModifications);
			options.setIncludeSynthetic(includeSynthetic);
			options.setIgnoreMissingClasses(ignoreMissingClasses);
			options.setHtmlStylesheet(Optional.fromNullable(pathToHtmlStylesheet));
			options.setOldClassPath(Optional.fromNullable(oldClassPath));
			options.setNewClassPath(Optional.fromNullable(newClassPath));
			options.setNoAnnotations(noAnnotations);
			return options;
		}

		private List<File> createFileList(String option) {
			String[] parts = option.split(";");
			List<File> files = new ArrayList<>(parts.length);
			for (String part : parts) {
				File file = new File(part);
				files.add(file);
			}
			return files;
		}

		private void verifyOptions(Options options) {
			for (File file : options.getOldArchives()) {
				verifyExistsCanReadAndJar(file);
			}
			for (File file : options.getNewArchives()) {
				verifyExistsCanReadAndJar(file);
			}
			if (options.getHtmlStylesheet().isPresent()) {
				String pathname = options.getHtmlStylesheet().get();
				File stylesheetFile = new File(pathname);
				if (!stylesheetFile.exists()) {
					throw JApiCmpException.of(JApiCmpException.Reason.CliError, "HTML stylesheet '%s' does not exist.", pathname);
				}
			}
			if (options.getOldClassPath().isPresent() && options.getNewClassPath().isPresent()) {
				options.setClassPathMode(ClassPathMode.TWO_SEPARATE_CLASSPATHS);
			} else {
				if (options.getOldClassPath().isPresent() || options.getNewClassPath().isPresent()) {
					throw JApiCmpException.of(JApiCmpException.Reason.CliError, "Please provide both options: " + OLD_CLASSPATH + " and " + NEW_CLASSPATH);
				} else {
					options.setClassPathMode(ClassPathMode.ONE_COMMON_CLASSPATH);
				}
			}
		}

		private void verifyExistsCanReadAndJar(File file) {
			verifyExisting(file);
			verifyCanRead(file);
			verifyJarArchive(file);
		}

		private void verifyExisting(File newArchive) {
			if (!newArchive.exists()) {
				throw JApiCmpException.of(JApiCmpException.Reason.CliError, "File '%s' does not exist.", newArchive.getAbsolutePath());
			}
		}

		private void verifyCanRead(File file) {
			if (!file.canRead()) {
				throw JApiCmpException.of(JApiCmpException.Reason.CliError, "Cannot read file '%s'.", file.getAbsolutePath());
			}
		}

		private void verifyJarArchive(File file) {
			JarFile jarFile = null;
			try {
				jarFile = new JarFile(file);
			} catch (IOException e) {
				throw JApiCmpException.of(JApiCmpException.Reason.CliError, "File '%s' could not be opened as a jar file: %s", file.getAbsolutePath(), e.getMessage());
			} finally {
				if (jarFile != null) {
					try {
						jarFile.close();
					} catch (IOException e) {
					}
				}
			}
		}

		private <T> T checkNonNull(T in, String errorMessage) {
			if (in == null) {
				throw new JApiCmpException(JApiCmpException.Reason.CliError, errorMessage);
			} else {
				return in;
			}
		}

		private Optional<AccessModifier> toModifier(String accessModifierArg) {
			Optional<String> stringOptional = Optional.fromNullable(accessModifierArg);
			if (stringOptional.isPresent()) {
				try {
					return Optional.of(AccessModifier.valueOf(stringOptional.get().toUpperCase()));
				} catch (IllegalArgumentException e) {
					throw new JApiCmpException(JApiCmpException.Reason.CliError, String.format("Invalid value for option -a: %s. Possible values are: %s.", accessModifierArg, AccessModifier.listOfAccessModifier()));
				}
			} else {
				return Optional.of(AccessModifier.PROTECTED);
			}
		}
	}
}
