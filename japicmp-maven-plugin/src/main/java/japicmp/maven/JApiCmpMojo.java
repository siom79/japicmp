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
        if(breakBuildOnModificationsParameter()) {
            for(JApiClass jApiClass : jApiClasses) {
                if(jApiClass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
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
        StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator();
        String diffOutput = stdoutOutputGenerator.generate(oldVersionFile, newVersionFile, jApiClasses, options);
        getLog().info(diffOutput);
        return diffOutput;
    }

    private void generateXmlOutput(File newVersionFile, File oldVersionFile, List<JApiClass> jApiClasses, File jApiCmpBuildDir, Options options) throws IOException {
        XmlOutputGenerator xmlGenerator = new XmlOutputGenerator();
        options.setXmlOutputFile(Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + "japicmp.xml"));
        xmlGenerator.generate(oldVersionFile, newVersionFile, jApiClasses, options);
    }

    private List<JApiClass> compareArchives(File newVersionFile, File oldVersionFile) {
        JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
        return jarArchiveComparator.compare(oldVersionFile, newVersionFile);
    }

    private File retrieveFileFromConfiguration(Version version, String parameterName) throws MojoFailureException {
        if (version != null) {
            Dependency dependency = version.getDependency();
            if (dependency != null) {
                String descriptor = dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion();
                getLog().debug(parameterName + ": " + descriptor);
                File file = resolveArtifact(dependency);
                if (file == null) {
                    throw new MojoFailureException(String.format("Could not resolve dependency with descriptor '%s'.", descriptor));
                }
                return file;
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
        notNull(artifactResolver, "Maven parameter artifactResolver should be provided by maven container.");
        notNull(localRepository, "Maven parameter localRepository should be provided by maven container.");
        notNull(artifactRepositories, "Maven parameter artifactRepositories should be provided by maven container.");
        Artifact artifact = artifactFactory.createBuildArtifact(dependency.getGroupId(), dependency.getArtifactId(), dependency.getVersion(), "jar");
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
