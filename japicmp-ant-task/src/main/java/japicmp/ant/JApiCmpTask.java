package japicmp.ant;

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
import japicmp.util.Optional;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.Reference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static japicmp.model.AccessModifier.toModifier;
import static japicmp.util.FileHelper.createFileList;

public class JApiCmpTask extends Task {
	private boolean onlyBinaryIncompatible = false;
	private boolean onlyModified = false;
	private boolean includeSynthetic = false;
	private boolean noAnnotations = false;
	private boolean semanticVersioning = false;
	private boolean reportOnlyFilename = false;
	private boolean ignoreMissingClasses = false;
	private boolean includeExclusively = false;
	private boolean excludeExclusively = false;
	private final List<String> ignoreMissingClassesByRegularExpressions = new ArrayList<>();
	private String accessModifier = "protected";
	private String semanticVersionProperty;
	private String oldJar;
	private String newJar;
	private Path oldClassPath;
	private Path newClassPath;
	private String includes;
	private String excludes;
	private String xmlOutputFile;
	private String htmlOutputFile;
	private String htmlStylesheet;
	private boolean errorOnSemanticIncompatibility = false;
	private boolean errorOnExclusionIncompatibility = false;
	private boolean errorOnSourceIncompatibility = false;
	private boolean errorOnBinaryIncompatibility = false;
	private boolean errorOnModifications = false;
	private boolean ignoreMissingOldVersion = false;
	private boolean ignoreMissingNewVersion = false;

	public void setOnlyBinaryIncompatible(String onlyBinaryIncompatible) {
		this.onlyBinaryIncompatible = Project.toBoolean(onlyBinaryIncompatible);
	}

	public void setOnlyModified(String onlyModified) {
		this.onlyModified = Project.toBoolean(onlyModified);
	}

	public void setIncludeSynthetic(String includeSynthetic) {
		this.includeSynthetic = Project.toBoolean(includeSynthetic);
	}

	public void setNoAnnotations(String noAnnotations) {
		this.noAnnotations = Project.toBoolean(noAnnotations);
	}

	public void setSemanticVersioning(String semanticVersioning) {
		this.semanticVersioning = Project.toBoolean(semanticVersioning);
	}

	public void setSemVerProperty(String semverProperty) {
		semanticVersioning = Boolean.TRUE;
		semanticVersionProperty = semverProperty;
	}

	public void setReportOnlyFilename(String reportOnlyFilename) {
		this.reportOnlyFilename = Project.toBoolean(reportOnlyFilename);
	}

	public void setIgnoreMissingClasses(String ignoreMissingClasses) {
		this.ignoreMissingClasses = Project.toBoolean(ignoreMissingClasses);
	}

	public void setIgnoreMissingClassesByRegularExpressions(String ignoreMissingClassesByRegularExpressions) {
		this.ignoreMissingClassesByRegularExpressions.addAll(Arrays.asList(ignoreMissingClassesByRegularExpressions.split("[,\\s]+")));
	}

	public void setIncludeExclusively(String includeExclusively) {
		this.includeExclusively = Project.toBoolean(includeExclusively);
	}

	public void setExcludeExclusively(String excludeExclusively) {
		this.excludeExclusively = Project.toBoolean(excludeExclusively);
	}

	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}

	public void setOldJar(String oldJar) {
		this.oldJar = oldJar;
	}

	public void setNewJar(String newJar) {
		this.newJar = newJar;
	}

	public void setOldClassPath(Path oldClassPath) {
		this.oldClassPath = oldClassPath;
	}

	public void setNewClassPath(Path newClassPath) {
		this.newClassPath = newClassPath;
	}

	public void setClassPath(Path classPath) {
		oldClassPath = classPath;
		newClassPath = classPath;
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

	public void setIncludes(String includes) {
		this.includes = includes;
	}

	public void setExcludes(String excludes) {
		this.excludes = excludes;
	}

	public void setXmlOutputFile(String xmlOutputFile) {
		this.xmlOutputFile = xmlOutputFile;
	}

	public void setHtmlOutputFile(String htmlOutputFile) {
		this.htmlOutputFile = htmlOutputFile;
	}

	public void setHtmlStylesheet(String htmlStylesheet) {
		this.htmlStylesheet = htmlStylesheet;
	}

	public void setErrorOnSemanticIncompatibility(boolean errorOnSemanticIncompatibility) {
		this.errorOnSemanticIncompatibility = errorOnSemanticIncompatibility;
	}

	public void setErrorOnExclusionIncompatibility(boolean errorOnExclusionIncompatibility) {
		this.errorOnExclusionIncompatibility = errorOnExclusionIncompatibility;
	}

	public void setErrorOnSourceIncompatibility(boolean errorOnSourceIncompatibility) {
		this.errorOnSourceIncompatibility = errorOnSourceIncompatibility;
	}

	public void setErrorOnBinaryIncompatibility(boolean errorOnBinaryIncompatibility) {
		this.errorOnBinaryIncompatibility = errorOnBinaryIncompatibility;
	}

	public void setErrorOnModifications(boolean errorOnModifications) {
		this.errorOnModifications = errorOnModifications;
	}

	public void setIgnoreMissingOldVersion(boolean ignoreMissingOldVersion) {
		this.ignoreMissingOldVersion = ignoreMissingOldVersion;
	}

	public void setIgnoreMissingNewVersion(boolean ignoreMissingNewVersion) {
		this.ignoreMissingNewVersion = ignoreMissingNewVersion;
	}

	@Override
	public void execute() {
		if (oldJar == null) {
			throw new BuildException("Path to old jar must be specified using the oldjar attribute.");
		}
		if (newJar == null) {
			throw new BuildException("Path to new jar must be specified using the newjar attribute.");
		}
		Options options = createOptionsFromAntAttrs();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(JarArchiveComparatorOptions.of(options));
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(options.getOldArchives(), options.getNewArchives());
		generateOutput(options, jApiClasses, jarArchiveComparator);
	}

	private Options createOptionsFromAntAttrs() {
		Options options = Options.newDefault();
		options.getOldArchives().addAll(createFileList(this.oldJar));
		options.getNewArchives().addAll(createFileList(this.newJar));
		options.setXmlOutputFile(Optional.fromNullable(xmlOutputFile));
		options.setHtmlOutputFile(Optional.fromNullable(htmlOutputFile));
		options.setHtmlStylesheet(Optional.fromNullable(htmlStylesheet));
		options.setOutputOnlyModifications(onlyModified);
		options.setAccessModifier(toModifier(accessModifier));
		options.addIncludeFromArgument(Optional.fromNullable(includes), includeExclusively);
		options.addExcludeFromArgument(Optional.fromNullable(excludes), excludeExclusively);
		options.setOutputOnlyBinaryIncompatibleModifications(onlyBinaryIncompatible);
		options.setIncludeSynthetic(includeSynthetic);
		options.setIgnoreMissingClasses(ignoreMissingClasses);
		options.setOldClassPath(Optional.fromNullable(getOldClassPath().size() > 0 ? getOldClassPath().toString() : null));
		options.setNewClassPath(Optional.fromNullable(getNewClassPath().size() > 0 ? getNewClassPath().toString() : null));
		options.setNoAnnotations(noAnnotations);
		for (String missingClassRegEx : ignoreMissingClassesByRegularExpressions) {
			options.addIgnoreMissingClassRegularExpression(missingClassRegEx);
		}
		options.setReportOnlyFilename(reportOnlyFilename);
		options.setErrorOnSemanticIncompatibility(errorOnSemanticIncompatibility);
		options.setErrorOnExclusionIncompatibility(errorOnExclusionIncompatibility);
		options.setErrorOnSourceIncompatibility(errorOnSourceIncompatibility);
		options.setErrorOnBinaryIncompatibility(errorOnBinaryIncompatibility);
		options.setErrorOnModifications(errorOnModifications);
		options.setIgnoreMissingOldVersion(ignoreMissingOldVersion);
		options.setIgnoreMissingNewVersion(ignoreMissingNewVersion);
		options.verify();
		return options;
	}

	private void generateOutput(Options options, List<JApiClass> jApiClasses, JarArchiveComparator jarArchiveComparator) {
		if (options.isErrorOnBinaryIncompatibility()
			|| options.isErrorOnSourceIncompatibility()
			|| options.isErrorOnExclusionIncompatibility()
			|| options.isErrorOnModifications()
			|| options.isErrorOnSemanticIncompatibility()) {
			IncompatibleErrorOutput errorOutput = new IncompatibleErrorOutput(options, jApiClasses, jarArchiveComparator);
			try {
				errorOutput.generate();
			} catch (JApiCmpException e) {
				if (e.getReason() == JApiCmpException.Reason.IncompatibleChange) {
					throw new BuildException(e.getMessage());
				}
				throw e;
			}
		}

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
}
