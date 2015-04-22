package japicmp.cli;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;

import javax.inject.Inject;

import com.google.common.base.Optional;
import io.airlift.airline.Command;
import io.airlift.airline.HelpOption;
import io.airlift.airline.Option;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.FormattedException;
import japicmp.exception.JApiCmpException;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.output.OutputFilter;
import japicmp.output.semver.SemverOut;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutputGenerator;

public class JApiCli {

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
		@Option(name = { "-i", "--include" }, description = "Comma separated list of package names to include, * can be used as wildcard.")
		public String packagesToInclude;
		@Option(name = { "-e", "--exclude" }, description = "Comma separated list of package names to exclude, * can be used as wildcard.")
		public String packagesToExclude;
		@Option(name = { "-x", "--xml-file" }, description = "Provides the path to the xml output file.")
		public String pathToXmlOutputFile;
		@Option(name = { "--html-file" }, description = "Provides the path to the html output file.")
		public String pathToHtmlOutputFile;
		@Option(name = { "-s", "--semantic-versioning" }, description ="Tells you which part of the version to increment.")
		public boolean semanticVersioning;

		@Override
		public void run() {

			Options options = create(pathToOldVersionJar, pathToNewVersionJar,
					pathToXmlOutputFile, pathToHtmlOutputFile,
					modifiedOnly, toModifier(accessModifier),
					packagesToInclude, packagesToExclude,
					onlyBinaryIncompatibleModifications);

			File oldArchive = options.getOldArchive();
			File newArchive = options.getNewArchive();
			verifyFiles(oldArchive, newArchive);
			JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(copyOptions(options));
			List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);
			generateOutput(options, oldArchive, newArchive, jApiClasses);
		}

		private JarArchiveComparatorOptions copyOptions(Options options) {
			JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
			comparatorOptions.getPackagesInclude().addAll(options.getPackagesInclude());
			comparatorOptions.getPackagesExclude().addAll(options.getPackagesExclude());
			comparatorOptions.setAccessModifier(options.getAccessModifier());
			return comparatorOptions;
		}

		private void generateOutput(Options options, File oldArchive, File newArchive, List<JApiClass> jApiClasses) {
			if (semanticVersioning) {
				SemverOut semverOut = new SemverOut(options, jApiClasses);
				semverOut.generate();
				return;
			}
			if (options.getXmlOutputFile().isPresent() || options.getHtmlOutputFile().isPresent()) {
				XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(oldArchive.getAbsolutePath(), newArchive.getAbsolutePath(), jApiClasses, options);
				xmlGenerator.generate();
			}
			StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options, jApiClasses, oldArchive, newArchive);
			String output = stdoutOutputGenerator.generate();
			System.out.println(output);
		}

		private Options create(String oldArchive, String newArchive,
				String xmlOutputFile, String htmlOutputFile,
				boolean onlyModifications, Optional<AccessModifier> accessModifier,
				String packagesIncludeArg, String packagesExcludeArg,
				boolean onlyBinaryIncompatibleModifications) throws IllegalArgumentException {

			Options options = new Options();
			try {
				options.setNewArchive(validFile(newArchive, "Required option -n is missing."));
				options.setOldArchive(validFile(oldArchive, "Required option -o is missing."));
				options.setXmlOutputFile(Optional.fromNullable(xmlOutputFile));
				options.setHtmlOutputFile(Optional.fromNullable(htmlOutputFile));
				options.setOutputOnlyModifications(onlyModifications);
				options.setAccessModifier(accessModifier);
				options.addPackageIncludeFromArgument(Optional.fromNullable(packagesIncludeArg));
				options.addPackagesExcludeFromArgument(Optional.fromNullable(packagesExcludeArg));
				options.setOutputOnlyBinaryIncompatibleModifications(onlyBinaryIncompatibleModifications);
			} catch (Exception e) {
				throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, e.getMessage());
			}
			return options;
		}

		private File validFile(String archive, String errorMessage) {
			File file = new File(checkNonNull(archive, errorMessage));
			verifyExisting(file);
			verifyCanRead(file);
			verifyJarArchive(file);
			return file;
		}

		private void verifyCanRead(File file) {
			if (!file.canRead()) {
				throw JApiCmpException.of("Cannot read file '%s'.", file.getAbsolutePath());
			}
		}

		private <T> T checkNonNull(T in, String errorMessage) {
			if (in == null) {
				throw new IllegalArgumentException(errorMessage);
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
					throw FormattedException
							.ofIAE("Invalid value for option -a: %s. Possible values are: %s.",
									accessModifierArg, AccessModifier.listOfAccessModifier());
				}
			} else {
				return Optional.of(AccessModifier.PROTECTED);
			}
		}

		private void verifyFiles(File oldArchive, File newArchive) {
			if (oldArchive.equals(newArchive)) {
				throw JApiCmpException.of("Files '%s' and '%s' are the same.", oldArchive.getAbsolutePath(), newArchive.getAbsolutePath());
			}
		}

		private void verifyExisting(File newArchive) {
			if (!newArchive.exists()) {
				throw JApiCmpException.of("File '%s' does not exist.", newArchive.getAbsolutePath());
			}
		}

		private void verifyJarArchive(File file) {
			try {
				new JarFile(file);
			} catch (IOException e) {
				throw JApiCmpException.of("File '%s' could not be opened as a jar file: %s", file.getAbsolutePath(), e.getMessage());
			}
		}
	}
}
