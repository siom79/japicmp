package japicmp.cli;

import com.google.common.base.Optional;
import io.airlift.command.Command;
import io.airlift.command.HelpOption;
import io.airlift.command.Option;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.FormattedException;
import japicmp.exception.JApiCmpException;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.output.OutputFilter;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutputGenerator;
import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.jar.JarFile;

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
		@Option(name = { "-x", "--xml-to-file" }, description = "Provides the path to the xml output file. If not given, stdout is used.")
		public String pathToXmlOutputFile;

		@Override
		public void run() {

			Options options = create(pathToOldVersionJar, pathToNewVersionJar, pathToXmlOutputFile, modifiedOnly, //
				toModifier(accessModifier), packagesToInclude, packagesToExclude, //
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
			comparatorOptions.setModifierLevel(options.getAccessModifier());
			comparatorOptions.getPackagesInclude().addAll(options.getPackagesInclude());
			comparatorOptions.getPackagesExclude().addAll(options.getPackagesExclude());
			return comparatorOptions;
		}

		private void generateOutput(Options options, File oldArchive, File newArchive, List<JApiClass> jApiClasses) {
			OutputFilter.sortClassesAndMethods(jApiClasses);
			if (options.getXmlOutputFile().isPresent()) {
				XmlOutputGenerator xmlGenerator = new XmlOutputGenerator();
				xmlGenerator.generate(oldArchive, newArchive, jApiClasses, options);
			}
			StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options);
			String output = stdoutOutputGenerator.generate(oldArchive, newArchive, jApiClasses);
			System.out.println(output);
		}

		private Options create(String oldArchive, String newArchive, String xmlOutputFile, //
			boolean onlyModifications, Optional<AccessModifier> accessModifier, //
			String packagesIncludeArg, String packagesExcludeArg, //
			boolean onlyBinaryIncompatibleModifications) throws IllegalArgumentException {

			Options options = new Options();
			try {
				options.setNewArchive(validFile(newArchive, "no valid new archive found"));
				options.setOldArchive(validFile(oldArchive, "no valid old archive found"));
				options.setXmlOutputFile(Optional.fromNullable(xmlOutputFile));
				options.setOutputOnlyModifications(onlyModifications);
				options.setAccessModifier(accessModifier);
				options.addPackageIncludeFromArgument(Optional.fromNullable(packagesIncludeArg));
				options.addPackagesExcludeFromArgument(Optional.fromNullable(packagesExcludeArg));
				options.setOutputOnlyBinaryIncompatibleModifications(onlyBinaryIncompatibleModifications);
			} catch (IllegalArgumentException e) {
				throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, e.getMessage());
			} catch (Exception e) {
				throw new IllegalStateException(e);
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
					throw FormattedException //
						.ofIAE("Invalid value for option -a: %s. Possible values are: %s.", //
							accessModifierArg, AccessModifier.listOfAccessModifier());
				}
			} else {
				return Optional.of(AccessModifier.PUBLIC);
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
