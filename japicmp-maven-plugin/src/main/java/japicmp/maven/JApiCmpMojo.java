package japicmp.maven;

import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutputGenerator;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.settings.Settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @goal cmp
 * @phase verify
 */
public class JApiCmpMojo extends AbstractMojo {
	/**
	 * @parameter
	 * @required
	 */
	private Version oldVersion;

	/**
	 * @parameter
	 * @required
	 */
	private Version newVersion;

	/**
	 * @parameter
	 */
	private Parameter parameter;

	/**
	 * @parameter
	 */
	private List<Dependency> dependencies;

	/**
	 * @parameter expression="${project.build.directory}"
	 * @required
	 */
	private File projectBuildDir;

	/**
	 * @component
	 */
	private ArtifactFactory artifactFactory;

	/**
	 * @component
	 */
	private ArtifactResolver artifactResolver;

	/**
	 * @parameter default-value="${localRepository}"
	 */
	private ArtifactRepository localRepository;

	/**
	 * @parameter default-value="${project.remoteArtifactRepositories}"
	 */
	private List<ArtifactRepository> artifactRepositories;

	/**
	 * The system settings for Maven. This is the instance resulting from
	 * merging global and user-level settings files.
	 *
	 * @parameter expression="${settings}"
	 * @readonly
	 * @required
	 */
	private Settings settings;

	/**
	 * @parameter default-value="${project}"
	 */
	private org.apache.maven.project.MavenProject mavenProject;

	public void execute() throws MojoExecutionException, MojoFailureException {
		File newVersionFile = retrieveFileFromConfiguration(newVersion, "newVersion");
		File oldVersionFile = retrieveFileFromConfiguration(oldVersion, "oldVersion");
		List<JApiClass> jApiClasses = compareArchives(newVersionFile, oldVersionFile);
		if (projectBuildDir != null && projectBuildDir.exists()) {
			try {
				File jApiCmpBuildDir = createJapiCmpBaseDir();
				Options options = createOptions();
				String diffOutput = generateDiffOutput(newVersionFile, oldVersionFile, jApiClasses, options);
				createFileAndWriteTo(diffOutput, jApiCmpBuildDir);
				generateXmlOutput(newVersionFile, oldVersionFile, jApiClasses, jApiCmpBuildDir, options);
				breakBuildIfNecessary(jApiClasses);
			} catch (IOException e) {
				throw new MojoFailureException(String.format("Failed to construct output directory: %s", e.getMessage()), e);
			}
		} else {
			throw new MojoFailureException("Could not determine the location of the build directory.");
		}
	}

	private void breakBuildIfNecessary(List<JApiClass> jApiClasses) throws MojoFailureException {
		if (breakBuildOnModificationsParameter()) {
			for (JApiClass jApiClass : jApiClasses) {
				if (jApiClass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					throw new MojoFailureException(String.format("Breaking the build because there is at least one modified class: %s", jApiClass.getFullyQualifiedName()));
				}
			}
		}
		if (breakBuildOnBinaryIncompatibleModifications()) {
			for (JApiClass jApiClass : jApiClasses) {
				if (jApiClass.getChangeStatus() != JApiChangeStatus.UNCHANGED && !jApiClass.isBinaryCompatible()) {
					throw new MojoFailureException(String.format("Breaking the build because there is at least one modified class: %s", jApiClass.getFullyQualifiedName()));
				}
			}
		}
	}

	private Options createOptions() throws MojoFailureException {
		Options options = new Options();
		if (parameter != null) {
			String accessModifierArg = parameter.getAccessModifier();
			if (accessModifierArg != null) {
				try {
					AccessModifier accessModifier = AccessModifier.valueOf(accessModifierArg.toUpperCase());
					options.setAccessModifier(accessModifier);
				} catch (IllegalArgumentException e) {
					throw new MojoFailureException(String.format("Invalid value for option accessModifier: %s. Possible values are: %s.", accessModifierArg, AccessModifier.listOfAccessModifier()));
				}
			}
			String onlyBinaryIncompatible = parameter.getOnlyBinaryIncompatible();
			if (onlyBinaryIncompatible != null) {
				Boolean booleanOnlyBinaryIncompatible = Boolean.valueOf(onlyBinaryIncompatible);
				options.setOutputOnlyBinaryIncompatibleModifications(booleanOnlyBinaryIncompatible);
			}
			String onlyModified = parameter.getOnlyModified();
			if (onlyModified != null) {
				Boolean booleanOnlyModified = Boolean.valueOf(onlyModified);
				options.setOutputOnlyModifications(booleanOnlyModified);
			}
			String packagesToExclude = parameter.getPackagesToExclude();
			if (packagesToExclude != null) {
				try {
					options.addPackagesExcludeFromArgument(packagesToExclude);
				} catch (Exception e) {
					throw new MojoFailureException(e.getMessage());
				}
			}
			String packagesToInclude = parameter.getPackagesToInclude();
			if (packagesToInclude != null) {
				try {
					options.addPackageIncludeFromArgument(packagesToInclude);
				} catch (Exception e) {
					throw new MojoFailureException(e.getMessage());
				}
			}
		}
		return options;
	}

	private boolean breakBuildOnModificationsParameter() {
		boolean retVal = false;
		if (parameter != null) {
			retVal = Boolean.valueOf(parameter.getBreakBuildOnModifications());
		}
		return retVal;
	}

	private boolean breakBuildOnBinaryIncompatibleModifications() {
		boolean retVal = false;
		if (parameter != null) {
			retVal = Boolean.valueOf(parameter.getBreakBuildOnBinaryIncompatibleModifications());
		}
		return retVal;
	}

	private void createFileAndWriteTo(String diffOutput, File jApiCmpBuildDir) throws IOException, MojoFailureException {
		File outputfile = new File(jApiCmpBuildDir.getCanonicalPath() + File.separator + "japicmp.diff");
		writeToFile(diffOutput, outputfile);
	}

	private File createJapiCmpBaseDir() throws IOException {
		File jApiCmpBuildDir = new File(projectBuildDir.getCanonicalPath() + File.separator + "japicmp");
		jApiCmpBuildDir.mkdirs();
		return jApiCmpBuildDir;
	}

	private String generateDiffOutput(File newVersionFile, File oldVersionFile, List<JApiClass> jApiClasses, Options options) {
		StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options, jApiClasses, oldVersionFile, newVersionFile);
		String diffOutput = stdoutOutputGenerator.generate();
		getLog().info(diffOutput);
		return diffOutput;
	}

	private void generateXmlOutput(File newVersionFile, File oldVersionFile, List<JApiClass> jApiClasses, File jApiCmpBuildDir, Options options) throws IOException {
		XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(oldVersionFile.getAbsolutePath(), newVersionFile.getAbsolutePath(), jApiClasses, options);
		options.setXmlOutputFile(Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + "japicmp.xml"));
		options.setHtmlOutputFile(Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + "japicmp.html"));
		xmlGenerator.generate();
	}

	private List<JApiClass> compareArchives(File newVersionFile, File oldVersionFile) throws MojoFailureException {
		JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
		setUpClassPath(comparatorOptions);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
		return jarArchiveComparator.compare(oldVersionFile, newVersionFile);
	}

	private void setUpClassPath(JarArchiveComparatorOptions comparatorOptions) throws MojoFailureException {
		if (dependencies != null) {
            for (Dependency dependency : dependencies) {
                File file = resolveDependencyToFile("dependencies", dependency);
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Resolved dependency " + dependency + " to file '" + file.getAbsolutePath() + "'.");
                }
                comparatorOptions.getClassPathEntries().add(file.getAbsolutePath());
            }
        }
        setUpClassPathUsingMavenProject(comparatorOptions);
    }

    private void setUpClassPathUsingMavenProject(JarArchiveComparatorOptions comparatorOptions) throws MojoFailureException {
        notNull(mavenProject, "Maven parameter mavenProject should be provided by maven container.");
        Set<Artifact> dependencyArtifacts = mavenProject.getDependencyArtifacts();
        for (Artifact artifact : dependencyArtifacts) {
            String scope = artifact.getScope();
            if (!"test".equals(scope)) {
                File file = resolveArtifact(artifact);
                if (file != null) {
                    getLog().info(file.getAbsolutePath() + "; scope: " + scope);
                    comparatorOptions.getClassPathEntries().add(file.getAbsolutePath());
                } else {
                    getLog().error("Could not resolve artifact " + artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion());
                }
            }
        }
    }

    private File retrieveFileFromConfiguration(Version version, String parameterName) throws MojoFailureException {
		if (version != null) {
			Dependency dependency = version.getDependency();
			if (dependency != null) {
				return resolveDependencyToFile(parameterName, dependency);
			} else if (version.getFile() != null) {
				ConfigurationFile configurationFile = version.getFile();
				String path = configurationFile.getPath();
				if (path == null) {
					throw new MojoFailureException(String.format("The path element in the configuration of the plugin is missing for %s.", parameterName));
				}
				File file = new File(path);
				if (!file.exists()) {
					throw new MojoFailureException(String.format("The path '%s' does not point to an existing file.", path));
				}
				if (!file.isFile() || !file.canRead()) {
					throw new MojoFailureException(String.format("The file given by path '%s' is either not a file or is not readable.", path));
				}
				return file;
			} else {
				throw new MojoFailureException(String.format("Missing configuration parameter 'dependency'."));
			}
		}
		throw new MojoFailureException(String.format("Missing configuration parameter: %s", parameterName));
	}

	private File resolveDependencyToFile(String parameterName, Dependency dependency) throws MojoFailureException {
		if (getLog().isDebugEnabled()) {
			getLog().debug("Trying to resolve dependency '" + dependency + "' to file.");
		}
		if (dependency.getSystemPath() == null) {
			String descriptor = dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion();
			getLog().debug(parameterName + ": " + descriptor);
			File file = resolveArtifact(dependency);
			if (file == null) {
				throw new MojoFailureException(String.format("Could not resolve dependency with descriptor '%s'.", descriptor));
			}
			return file;
		} else {
			String systemPath = dependency.getSystemPath();
			Pattern pattern = Pattern.compile("\\$\\{([^\\}])");
			Matcher matcher = pattern.matcher(systemPath);
			if (matcher.matches()) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					String property = matcher.group(i);
					String propertyResolved = mavenProject.getProperties().getProperty(property);
					if (propertyResolved != null) {
						systemPath = systemPath.replaceAll("${" + property + "}", propertyResolved);
					} else {
						throw new MojoFailureException("Could not resolve property '" + property + "'.");
					}
				}
			}
			File file = new File(systemPath);
			if (!file.exists()) {
				throw new MojoFailureException("File '" + file.getAbsolutePath() + "' does not exist.");
			}
			if (!file.canRead()) {
				throw new MojoFailureException("File '" + file.getAbsolutePath() + "' is not readable.");
			}
			return file;
		}
	}

	private void writeToFile(String output, File outputfile) throws MojoFailureException, IOException {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(outputfile);
			fileWriter.write(output);
		} catch (Exception e) {
			throw new MojoFailureException(String.format("Failed to write diff file: %s", e.getMessage()), e);
		} finally {
			if (fileWriter != null) {
				fileWriter.close();
			}
		}
	}

	private File resolveArtifact(Dependency dependency) throws MojoFailureException {
        notNull(artifactRepositories, "Maven parameter artifactRepositories should be provided by maven container.");
        Artifact artifact = artifactFactory.createBuildArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), "jar");
        return resolveArtifact(artifact);
	}

    private File resolveArtifact(Artifact artifact) throws MojoFailureException {
        notNull(localRepository, "Maven parameter localRepository should be provided by maven container.");
        notNull(artifactResolver, "Maven parameter artifactResolver should be provided by maven container.");
        ArtifactResolutionRequest request = new ArtifactResolutionRequest();
        request.setArtifact(artifact);
        request.setLocalRepository(localRepository);
        request.setRemoteRepositories(artifactRepositories);
        artifactResolver.resolve(request);
        return artifact.getFile();
    }

    private static <T> T notNull(T value, String msg) throws MojoFailureException {
		if (value == null) {
			throw new MojoFailureException(msg);
		}
		return value;
	}
}
