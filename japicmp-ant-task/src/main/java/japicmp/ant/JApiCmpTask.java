package japicmp.ant;

import com.google.common.base.Optional;
import japicmp.cmp.JApiCmpArchive;
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
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JApiCmpTask extends Task {
	private boolean onlyBinaryIncompatibleModifications = false;
	private boolean onlyModified = false;
	private boolean includeSynthetic = false;
	private boolean noAnnotations = false;
	private boolean semanticVersioning = false;
	private boolean reportOnlyFilename = false;
	private boolean ignoreMissingClasses = false;
	private List<String> ignoreMissingClassesByRegEx = new ArrayList<>();
	private String accessModifier = "protected";
	private String semanticVersionProperty;
	private File oldVersionJar;
	private File newVersionJar;
	private Path oldClassPath;
	private Path newClassPath;
	private String includes;
	private String excludes;
	private String pathToXmlOutputFile;
	private String pathToHtmlOutputFile;
	private String pathToHtmlStylesheet;

	public void setOnlyBinaryIncompatibleModifications(String biModified) {
		onlyBinaryIncompatibleModifications = Project.toBoolean(biModified);
	}

	public void setOnlyModifications(String modified) {
		onlyModified = Project.toBoolean(modified);
	}

	public void setIncludeSynthetic(String synthetic) {
		includeSynthetic = Project.toBoolean(synthetic);
	}

	public void setExcludeAnnotations(String annotations) {
		noAnnotations = Project.toBoolean(annotations);
	}

	public void setSemanticVersion(String semver) {
		semanticVersioning = Project.toBoolean(semver);
	}

	public void setSemVerProperty(String semverProperty) {
		semanticVersioning = Boolean.TRUE;
		semanticVersionProperty = semverProperty;
	}

	public void setReportOnlyFilename(String onlyFilename) {
		reportOnlyFilename = Project.toBoolean(onlyFilename);
	}

	public void setIgnoreMissingClasses(String missingClasses) {
		ignoreMissingClasses = Project.toBoolean(missingClasses);
	}

	public void setIgnoreMissingClassesRegEx(String regexList) {
		ignoreMissingClassesByRegEx.addAll(Arrays.asList(regexList.split("[,\\s]+")));
	}

	public void setAccessModifier(String modifier) {
		accessModifier = modifier;
	}

	public void setOldJar(File oldJar) {
		oldVersionJar = oldJar;
	}

	public void setNewJar(File newJar) {
		newVersionJar = newJar;
	}

	public Path getOldClassPath() {
		if (oldClassPath == null) {
			oldClassPath = new Path(getProject());
		}
		return oldClassPath;
	}

	public Path getNewClassPath() {
		if (newClassPath == null) {
			newClassPath = new Path(getProject());
		}
		return newClassPath;
	}

	public void setOldClassPathRef(Reference oldClassPathRef) {
		getOldClassPath().setRefid(oldClassPathRef);
	}

	public void setNewClassPathRef(Reference newClassPathRef) {
		getNewClassPath().setRefid(newClassPathRef);
	}

	public void setClassPathRef(Reference classPathRef) {
		getOldClassPath().setRefid(classPathRef);
		getNewClassPath().setRefid(classPathRef);
	}

	public void setIncludeFilter(String includeFilter) {
		includes = includeFilter;
	}

	public void setExcludeFilter(String excludeFilter) {
		excludes = excludeFilter;
	}

	public void setXmlOutputFile(String xmlOutputFile) {
		pathToXmlOutputFile = xmlOutputFile;
	}

	public void setHtmlOutputFile(String htmlOutputFile) {
		pathToHtmlOutputFile = htmlOutputFile;
	}

	public void setHtmlStylesheet(String htmlStylesheet) {
		pathToHtmlStylesheet = htmlStylesheet;
	}

	@Override
	public void execute() {
		if (oldVersionJar == null) {
			throw new BuildException("Path to old jar must be specified using the oldjar attribute.");
		}

		if (newVersionJar == null) {
			throw new BuildException("Path to new jar must be specified using the newjar attribute.");
		}

		if (oldClassPath != null) {

		}
		Options options = createOptionsFromAntAttrs();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(JarArchiveComparatorOptions.of(options));
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(options.getOldArchives(), options.getNewArchives());
		generateOutput(options, jApiClasses);
	}

	private Options createOptionsFromAntAttrs() {
		Options options = Options.newDefault();
		options.getOldArchives().add(new JApiCmpArchive(oldVersionJar, "n.a."));
		options.getNewArchives().add(new JApiCmpArchive(newVersionJar, "n.a."));
		options.setXmlOutputFile(Optional.fromNullable(pathToXmlOutputFile));
		options.setHtmlOutputFile(Optional.fromNullable(pathToHtmlOutputFile));
		options.setHtmlStylesheet(Optional.fromNullable(pathToHtmlStylesheet));
		options.setOutputOnlyModifications(onlyModified);
		options.setAccessModifier(toModifier(accessModifier));
		options.addIncludeFromArgument(Optional.fromNullable(includes));
		options.addExcludeFromArgument(Optional.fromNullable(excludes));
		options.setOutputOnlyBinaryIncompatibleModifications(onlyBinaryIncompatibleModifications);
		options.setIncludeSynthetic(includeSynthetic);
		options.setIgnoreMissingClasses(ignoreMissingClasses);
		options.setOldClassPath(Optional.fromNullable(getOldClassPath().size() > 0 ? getOldClassPath().toString() : null));
		options.setNewClassPath(Optional.fromNullable(getNewClassPath().size() > 0 ? getNewClassPath().toString() : null));
		options.setNoAnnotations(noAnnotations);
		for (String missingClassRegEx : ignoreMissingClassesByRegEx) {
			options.addIgnoreMissingClassRegularExpression(missingClassRegEx);
		}
		options.setReportOnlyFilename(reportOnlyFilename);
		options.verify();
		return options;
	}

	private void generateOutput(Options options, List<JApiClass> jApiClasses) {
		if (semanticVersioning) {
			SemverOut semverOut = new SemverOut(options, jApiClasses);
			String semver = semverOut.generate();
			if (semanticVersionProperty != null) {
				getProject().setProperty(semanticVersionProperty, semver);
			}
			log(semver);
			return;
		}

		if (!options.getXmlOutputFile().isPresent() && !options.getHtmlOutputFile().isPresent()) {
			StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options, jApiClasses);
			log(stdoutOutputGenerator.generate());
			return;
		}

		SemverOut semverOut = new SemverOut(options, jApiClasses);
		XmlOutputGeneratorOptions xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
		xmlOutputGeneratorOptions.setCreateSchemaFile(true);
		xmlOutputGeneratorOptions.setSemanticVersioningInformation(semverOut.generate());
		XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(jApiClasses, options, xmlOutputGeneratorOptions);
		try (XmlOutput xmlOutput = xmlGenerator.generate()) {
            XmlOutputGenerator.writeToFiles(options, xmlOutput);
        } catch (Exception e) {
            throw new BuildException("Could not close output streams: " + e.getMessage(), e);
        }
	}

	private Optional<AccessModifier> toModifier(String accessModifierArg) {
		Optional<String> stringOptional = Optional.fromNullable(accessModifierArg);
		if (stringOptional.isPresent()) {
			try {
				return Optional.of(AccessModifier.valueOf(stringOptional.get().toUpperCase()));
			} catch (IllegalArgumentException e) {
				throw new JApiCmpException(JApiCmpException.Reason.CliError, String.format("Invalid value for option accessModifier: %s. Possible values are: %s.",
					accessModifierArg, AccessModifier.listOfAccessModifier()), e);
			}
		} else {
			return Optional.of(AccessModifier.PROTECTED);
		}
	}
}
