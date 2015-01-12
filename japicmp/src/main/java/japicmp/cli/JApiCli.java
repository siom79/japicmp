package japicmp.cli;

import javax.inject.Inject;
import java.io.File;
import java.util.List;

import io.airlift.command.Command;
import io.airlift.command.Option;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.JApiClass;
import japicmp.output.OutputFilter;
import japicmp.output.OutputGenerator;
import japicmp.output.SemverOut;
import japicmp.output.StdOut;
import japicmp.output.XmlOut;

public class JApiCli {

	@Command(name = "java -jar japicmp.jar", description = "Compares jars")
	public static class Compare implements Runnable {
		@Inject
		public ShortHelpOption helpOption;
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
		@Option(name = { "--only-semver-diff"}, //
				description = "Shows only the semver difference between new and old", //
				hidden = true)
		public boolean showOnlySemverDiff = false;

		@Override
		public void run() {
			Options options = OptionsBuilder.create() //
					.newJar(pathToNewVersionJar) //
					.oldJar(pathToOldVersionJar) //
					.xmlOutputFilename(pathToXmlOutputFile) //
					.htmlOutputFilename(pathToHtmlOutputFile) //
					.showModifiedOnly(modifiedOnly) //
					.useOnlyVisibilty(accessModifier) //
					.includePackages(packagesToInclude) //
					.excludePackages(packagesToExclude) //
					.showOnlyBinaryIncompatibleModifications(onlyBinaryIncompatibleModifications) //
					.showOnlySeverDiff(showOnlySemverDiff) //
					.build();

			File oldArchive = options.getOldArchive();
			File newArchive = options.getNewArchive();
			verifyFiles(oldArchive, newArchive);

			JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(copyOptions(options));
			List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldArchive, newArchive);

			generateOutput(options, jApiClasses);
		}

		private JarArchiveComparatorOptions copyOptions(Options options) {
			JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
			comparatorOptions.setModifierLevel(options.getAccessModifier());
			comparatorOptions.getPackagesInclude().addAll(options.getPackagesInclude());
			comparatorOptions.getPackagesExclude().addAll(options.getPackagesExclude());
			return comparatorOptions;
		}

		private void generateOutput(Options options, List<JApiClass> jApiClasses) {
			OutputFilter.sortClassesAndMethods(jApiClasses);
			OutputGenerator out = selectGenerator(options, jApiClasses);
			out.generate();
		}

		private OutputGenerator selectGenerator(Options options, List<JApiClass> jApiClasses) {
			if (options.getXmlOutputFile().isPresent() || options.getHtmlOutputFile().isPresent()) {
				return new XmlOut(options, jApiClasses);
			} else if (options.isOnlySemverDiff()) {
				return new SemverOut(options, jApiClasses);
			} else {
				return new StdOut(options, jApiClasses);
			}
		}

		private void verifyFiles(File oldArchive, File newArchive) {
			if (oldArchive.equals(newArchive)) {
				throw JApiCmpException.of("Files '%s' and '%s' are the same.", oldArchive.getAbsolutePath(), newArchive.getAbsolutePath());
			}
		}

	}
}
