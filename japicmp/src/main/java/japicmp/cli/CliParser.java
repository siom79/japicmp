package japicmp.cli;

import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.util.Optional;
import japicmp.util.StringArrayEnumeration;

import static japicmp.model.AccessModifier.toModifier;
import static japicmp.util.FileHelper.createFileList;

public class CliParser {
	public static final String IGNORE_MISSING_CLASSES = "--ignore-missing-classes";
	public static final String IGNORE_MISSING_CLASSES_BY_REGEX = "--ignore-missing-classes-by-regex";
	public static final String OLD_CLASSPATH = "--old-classpath";
	public static final String NEW_CLASSPATH = "--new-classpath";

	public Options parse(String[] args) throws IllegalArgumentException {
		Options options = Options.newDefault();
		boolean includeExclusively = false;
		boolean excludeExclusively = false;
		StringArrayEnumeration sae = new StringArrayEnumeration(args);
		while (sae.hasMoreElements()) {
			String arg = sae.nextElement();
			if ("--include-exclusively".equals(arg)) {
				includeExclusively = true;
			} else if ("--exclude-exclusively".equals(arg)) {
				excludeExclusively = true;
			} else if ("-h".equals(arg) || "--help".equals(arg)) {
				options.setHelpRequested(true);
			}
		}
		if (options.isHelpRequested()) {
			return options;
		}
		sae = new StringArrayEnumeration(args);
		while (sae.hasMoreElements()) {
			String arg = sae.nextElement();
			if ("-n".equals(arg) || "--new".equals(arg)) {
				String newArchive = getOptionWithArgument("-n, --new", sae);
				options.getNewArchives().addAll(createFileList(checkNonNull(newArchive, "Required option -n is missing.")));
			} else if ("-o".equals(arg) || "--old".equals(arg)) {
				String oldArchive = getOptionWithArgument("-o, --old", sae);
				options.getOldArchives().addAll(createFileList(checkNonNull(oldArchive, "Required option -o is missing.")));
			} else if ("-m".equals(arg) || "--only-modified".equals(arg)) {
				options.setOutputOnlyModifications(true);
			} else if ("-b".equals(arg) || "--only-incompatible".equals(arg)) {
				options.setOutputOnlyBinaryIncompatibleModifications(true);
			} else if ("-a".equals(arg)) {
				String accessModifier = getOptionWithArgument("-a", sae);
				options.setAccessModifier(toModifier(accessModifier));
			} else if ("-i".equals(arg) || "--include".equals(arg)) {
				String includes = getOptionWithArgument("-i, --include", sae);
				options.addIncludeFromArgument(Optional.fromNullable(includes), includeExclusively);
			} else if ("-e".equals(arg) || "--exclude".equals(arg)) {
				String excludes = getOptionWithArgument("-e, --exclude", sae);
				options.addExcludeFromArgument(Optional.fromNullable(excludes), excludeExclusively);
			} else if ("-x".equals(arg) || "--xml-file".equals(arg)) {
				String pathToXmlOutputFile = getOptionWithArgument("-x, --xml-file", sae);
				options.setXmlOutputFile(Optional.fromNullable(pathToXmlOutputFile));
			} else if ("--html-file".equals(arg)) {
				String pathToHtmlOutputFile = getOptionWithArgument("--html-file", sae);
				options.setHtmlOutputFile(Optional.fromNullable(pathToHtmlOutputFile));
			} else if ("-s".equals(arg) || "--semantic-versioning".equals(arg)) {
				options.setSemanticVersioning(true);
			} else if ("--include-synthetic".equals(arg)) {
				options.setIncludeSynthetic(true);
			} else if (IGNORE_MISSING_CLASSES.equals(arg)) {
				options.setIgnoreMissingClasses(true);
			} else if (IGNORE_MISSING_CLASSES_BY_REGEX.equals(arg)) {
				while (sae.hasMoreElements()) {
					String nextElement = sae.inspectNextElement();
					if (!nextElement.startsWith("-")) {
						nextElement = sae.nextElement();
						options.addIgnoreMissingClassRegularExpression(nextElement);
					}
				}
			} else if ("--html-stylesheet".equals(arg)) {
				String htmlStylesheet = getOptionWithArgument("--html-stylesheet", sae);
				options.setHtmlStylesheet(Optional.fromNullable(htmlStylesheet));
			} else if (OLD_CLASSPATH.equals(arg)) {
				String oldClassPath = getOptionWithArgument(OLD_CLASSPATH, sae);
				options.setOldClassPath(Optional.fromNullable(oldClassPath));
			} else if (NEW_CLASSPATH.equals(arg)) {
				String newClassPath = getOptionWithArgument(NEW_CLASSPATH, sae);
				options.setNewClassPath(Optional.fromNullable(newClassPath));
			} else if ("--no-annotations".equals(arg)) {
				options.setNoAnnotations(true);
			} else if ("--report-only-filename".equals(arg)) {
				options.setReportOnlyFilename(true);
			} else {
				throw new JApiCmpException(JApiCmpException.Reason.CliError, "Unknown argument: " + arg);
			}
		}
		options.verify();
		return options;
	}

	public static void printHelp() {
		System.out.println("SYNOPSIS\n" +
			"        java -jar japicmp.jar [-a <accessModifier>] [(-b | --only-incompatible)]\n" +
			"                [(-e <excludes> | --exclude <excludes>)] [--exclude-exclusively]\n" +
			"                [(-h | --help)] [--html-file <pathToHtmlOutputFile>]\n" +
			"                [--html-stylesheet <pathToHtmlStylesheet>]\n" +
			"                [(-i <includes> | --include <includes>)] [--ignore-missing-classes]\n" +
			"                [--ignore-missing-classes-by-regex <ignoreMissingClassesByRegEx>...]\n" +
			"                [--include-exclusively] [--include-synthetic] [(-m | --only-modified)]\n" +
			"                [(-n <pathToNewVersionJar> | --new <pathToNewVersionJar>)]\n" +
			"                [--new-classpath <newClassPath>] [--no-annotations]\n" +
			"                [(-o <pathToOldVersionJar> | --old <pathToOldVersionJar>)]\n" +
			"                [--old-classpath <oldClassPath>] [--report-only-filename]\n" +
			"                [(-s | --semantic-versioning)]\n" +
			"                [(-x <pathToXmlOutputFile> | --xml-file <pathToXmlOutputFile>)]\n" +
			"\n" +
			"OPTIONS\n" +
			"        -a <accessModifier>\n" +
			"            Sets the access modifier level (public, package, protected,\n" +
			"            private), which should be used.\n" +
			"\n" +
			"        -b, --only-incompatible\n" +
			"            Outputs only classes/methods that are binary incompatible. If not\n" +
			"            given, all classes and methods are printed.\n" +
			"\n" +
			"        -e <excludes>, --exclude <excludes>\n" +
			"            Semicolon separated list of elements to exclude in the form\n" +
			"            package.Class#classMember, * can be used as wildcard. Annotations\n" +
			"            are given as FQN starting with @. Examples:\n" +
			"            mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation.\n" +
			"\n" +
			"        --exclude-exclusively\n" +
			"            Exclude only packages specified in the \"exclude\" option, include\n" +
			"            their sub-packages\n" +
			"\n" +
			"        -h, --help\n" +
			"            Display help information\n" +
			"\n" +
			"        --html-file <pathToHtmlOutputFile>\n" +
			"            Provides the path to the html output file.\n" +
			"\n" +
			"        --html-stylesheet <pathToHtmlStylesheet>\n" +
			"            Provides the path to your own stylesheet.\n" +
			"\n" +
			"        -i <includes>, --include <includes>\n" +
			"            Semicolon separated list of elements to include in the form\n" +
			"            package.Class#classMember, * can be used as wildcard. Annotations\n" +
			"            are given as FQN starting with @. Examples:\n" +
			"            mypackage;my.Class;other.Class#method(int,long);foo.Class#field;@my.Annotation.\n" +
			"\n" +
			"        --ignore-missing-classes\n" +
			"            Ignores all superclasses/interfaces missing on the classpath.\n" +
			"\n" +
			"        --ignore-missing-classes-by-regex <ignoreMissingClassesByRegEx>\n" +
			"            Ignores only those superclasses/interface missing on the classpath\n" +
			"            that are selected by a regular expression.\n" +
			"\n" +
			"        --include-exclusively\n" +
			"            Include only packages specified in the \"include\" option, exclude\n" +
			"            their sub-packages\n" +
			"\n" +
			"        --include-synthetic\n" +
			"            Include synthetic classes and class members that are hidden per\n" +
			"            default.\n" +
			"\n" +
			"        -m, --only-modified\n" +
			"            Outputs only modified classes/methods.\n" +
			"\n" +
			"        -n <pathToNewVersionJar>, --new <pathToNewVersionJar>\n" +
			"            Provides the path to the new version(s) of the jar(s). Use ; to\n" +
			"            separate jar files.\n" +
			"\n" +
			"        --new-classpath <newClassPath>\n" +
			"            The classpath for the new version.\n" +
			"\n" +
			"        --no-annotations\n" +
			"            Do not evaluate annotations.\n" +
			"\n" +
			"        -o <pathToOldVersionJar>, --old <pathToOldVersionJar>\n" +
			"            Provides the path to the old version(s) of the jar(s). Use ; to\n" +
			"            separate jar files.\n" +
			"\n" +
			"        --old-classpath <oldClassPath>\n" +
			"            The classpath for the old version.\n" +
			"\n" +
			"        --report-only-filename\n" +
			"            Reports just filenames (not full paths) in report description.\n" +
			"\n" +
			"        -s, --semantic-versioning\n" +
			"            Tells you which part of the version to increment.\n" +
			"\n" +
			"        -x <pathToXmlOutputFile>, --xml-file <pathToXmlOutputFile>\n" +
			"            Provides the path to the xml output file.");
	}

	private String getOptionWithArgument(String option, StringArrayEnumeration sae) {
		if (sae.hasMoreElements()) {
			String value = sae.nextElement();
			if (value.startsWith("-")) {
				throw new JApiCmpException(JApiCmpException.Reason.CliError, String.format("Missing argument for option '%s'.", option));
			}
			return value;
		} else {
			throw new JApiCmpException(JApiCmpException.Reason.CliError, String.format("Missing argument for option '%s'.", option));
		}
	}

	private <T> T checkNonNull(T in, String errorMessage) {
		if (in == null) {
			throw new JApiCmpException(JApiCmpException.Reason.CliError, errorMessage);
		} else {
			return in;
		}
	}
}
