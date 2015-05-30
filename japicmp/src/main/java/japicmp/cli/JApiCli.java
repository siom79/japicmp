package japicmp.cli;

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

import javax.inject.Inject;
import java.io.*;
import java.util.List;
import java.util.jar.JarFile;

public class JApiCli {
	public static final String IGNORE_MISSING_CLASSES = "--ignore-missing-classes";

	@Command(name = "java -jar japicmp.jar", description = "Compares jars")
	public static class Compare implements Runnable {
		@Inject
		public HelpOption helpOption;
		@Option(name = { "-o", "--old" }, description = "Provides the path to the old version of the jar.")
		public String pathToOldVersionJar;
		@Option(name = { "-n", "--new" }, description = "Provides the path to the new version of the jar.")
		public String pathToNewVersionJar;
		@Option(name = { "-m", "--only-modified" }, description = "Outputs only modified classes/methods.")
		public boolean modifiedOnly;
		@Option(name = { "-b", "--only-incompatible" }, description = "Outputs only classes/methods that are binary incompatible. If not given, all classes and methods are printed.")
		public boolean onlyBinaryIncompatibleModifications;
		@Option(name = "-a", description = "Sets the access modifier level (public, package, protected, private), which should be used.")
		public String accessModifier;
		@Option(name = { "-i", "--include" }, description = "Semicolon separated list of elements to include in the form package.Class#classMember, * can be used as wildcard. Examples: mypackage;my.Class;other.Class#method(int,long);foo.Class#field")
		public String includes;
		@Option(name = { "-e", "--exclude" }, description = "Semicolon separated list of elements to exclude in the form package.Class#classMember, * can be used as wildcard. Examples: mypackage;my.Class;other.Class#method(int,long);foo.Class#field")
		public String excludes;
		@Option(name = { "-x", "--xml-file" }, description = "Provides the path to the xml output file.")
		public String pathToXmlOutputFile;
		@Option(name = { "--html-file" }, description = "Provides the path to the html output file.")
		public String pathToHtmlOutputFile;
		@Option(name = { "-s", "--semantic-versioning" }, description ="Tells you which part of the version to increment.")
		public boolean semanticVersioning = false;
		@Option(name = { "--include-synthetic" }, description = "Include synthetic classes and class members that are hidden per default.")
		public boolean includeSynthetic = false;
		@Option(name = { IGNORE_MISSING_CLASSES }, description = "Ignores superclasses/interfaces missing on the classpath.")
		public boolean ignoreMissingClasses = false;
		@Option(name = { "--html-stylesheet" }, description = "Provides the path to your own stylesheet.")
		public String pathToHtmlStylesheet;

		@Override
		public void run() {
			Options options = createOptions();
			verifyOptions(options);
			JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(JarArchiveComparatorOptions.of(options));
			List<JApiClass> jApiClasses = jarArchiveComparator.compare(options.getOldArchive(), options.getNewArchive());
			generateOutput(options, options.getOldArchive(), options.getNewArchive(), jApiClasses);
		}

		private void generateOutput(Options options, File oldArchive, File newArchive, List<JApiClass> jApiClasses) {
			if (semanticVersioning) {
				SemverOut semverOut = new SemverOut(options, jApiClasses);
				semverOut.generate();
				return;
			}
			if (options.getXmlOutputFile().isPresent() || options.getHtmlOutputFile().isPresent()) {
				XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(oldArchive.getAbsolutePath(), newArchive.getAbsolutePath(), jApiClasses, options, true);
				try (XmlOutput xmlOutput = xmlGenerator.generate()) {
					XmlOutputGenerator.writeToFiles(options, xmlOutput);
				} catch (Exception e) {
					throw new JApiCmpException(JApiCmpException.Reason.IoException, "Could not close output streams: " + e.getMessage(), e);
				}
			}
			StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options, jApiClasses, oldArchive, newArchive);
			String output = stdoutOutputGenerator.generate();
			System.out.println(output);
		}

		private Options createOptions() {
			Options options = new Options();
			options.setNewArchive(new File(checkNonNull(pathToNewVersionJar, "Required option -n is missing.")));
			options.setOldArchive(new File(checkNonNull(pathToOldVersionJar, "Required option -o is missing.")));
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
			return options;
		}

		private void verifyOptions(Options options) {
			File oldArchive = options.getOldArchive();
			File newArchive = options.getNewArchive();
			verifyJarFileExists(oldArchive);
			verifyJarFileExists(newArchive);
			if (oldArchive.equals(newArchive)) {
				throw JApiCmpException.of(JApiCmpException.Reason.CliError, "Files '%s' and '%s' are the same.", oldArchive.getAbsolutePath(), newArchive.getAbsolutePath());
			}
			if (options.getHtmlStylesheet().isPresent()) {
				String pathname = options.getHtmlStylesheet().get();
				File stylesheetFile = new File(pathname);
				if (!stylesheetFile.exists()) {
					throw JApiCmpException.of(JApiCmpException.Reason.CliError, "HTML stylesheet '%s' does not exist.", pathname);
				}
			}
		}

		private void verifyJarFileExists(File file) {
			verifyExisting(file);
			verifyCanRead(file);
			verifyJarArchive(file);
		}

		private void verifyCanRead(File file) {
			if (!file.canRead()) {
				throw JApiCmpException.of(JApiCmpException.Reason.CliError, "Cannot read file '%s'.", file.getAbsolutePath());
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
					throw new JApiCmpException(JApiCmpException.Reason.CliError, String.format("Invalid value for option -a: %s. Possible values are: %s.",
							accessModifierArg, AccessModifier.listOfAccessModifier()));
				}
			} else {
				return Optional.of(AccessModifier.PROTECTED);
			}
		}

		private void verifyExisting(File newArchive) {
			if (!newArchive.exists()) {
				throw JApiCmpException.of(JApiCmpException.Reason.CliError, "File '%s' does not exist.", newArchive.getAbsolutePath());
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
					} catch (IOException ignored) {}
				}
			}
		}
	}
}
