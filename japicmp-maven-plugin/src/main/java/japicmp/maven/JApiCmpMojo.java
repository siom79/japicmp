package japicmp.maven;

import com.google.common.io.Files;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.stdout.StdoutOutputGenerator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResult;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

/**
 * @goal cmp
 */
public class JApiCmpMojo extends AbstractMojo {
    /**
     * @parameter
     */
    private Version oldVersion;

    /**
     * @parameter
     */
    private Version newVersion;

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private File projectBuildDir;

    /**
     * @component
     */
    private RepositorySystem repoSystem;

    /**
     * @component default-value="${project.remoteProjectRepositories}"
     */
    private List<RemoteRepository> remoteRepos;

    public void execute() throws MojoExecutionException, MojoFailureException {
        File oldVersionFile = null;
        File newVersionFile = null;
        if (oldVersion != null) {
            Dependency dependency = oldVersion.getDependency();
            if (dependency != null) {
                String descriptor = dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion();
                getLog().debug("oldVersion: " + descriptor);
                oldVersionFile = resolveArtifact(descriptor);
            }
        }
        if (newVersion != null) {
            Dependency dependency = newVersion.getDependency();
            if (dependency != null) {
                String descriptor = dependency.getGroupId() + ":" + dependency.getArtifactId() + ":" + dependency.getVersion();
                getLog().debug("newVersion: " + descriptor);
                newVersionFile = resolveArtifact(descriptor);
            }
        }
        if(oldVersionFile != null && newVersionFile != null) {
            JarArchiveComparatorOptions comparatorOptions = new JarArchiveComparatorOptions();
            JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
            List<JApiClass> jApiClasses = jarArchiveComparator.compare(oldVersionFile, newVersionFile);
            StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator();
            String output = stdoutOutputGenerator.generate(oldVersionFile, newVersionFile, jApiClasses, new Options());
            getLog().info(output);
            if(projectBuildDir != null && projectBuildDir.exists()) {
                try {
                    File japiBuildDir = new File(projectBuildDir.getCanonicalPath() + File.separator + "japicmp");
                    japiBuildDir.mkdirs();
                    File diffOutputfile = new File(japiBuildDir.getCanonicalPath() + File.separator + "japicmp.diff");
                    FileWriter fileWriter = null;
                    try {
                        fileWriter = new FileWriter(diffOutputfile);
                        fileWriter.write(output);
                    } catch(Exception e) {
                        throw new MojoFailureException(String.format("Failed to write diff file: %s", e.getMessage()), e);
                    } finally {
                        if(fileWriter != null) {
                            fileWriter.close();
                        }
                    }
                } catch (IOException e) {
                    throw new MojoFailureException(String.format("Failed to construct output directory: %s", e.getMessage()), e);
                }
            }
        } else {
            throw new MojoFailureException(String.format("At least one required parameter is missing."));
        }
    }

    private File resolveArtifact(String descriptor) throws MojoFailureException {
        try {
            ArtifactRequest request = new ArtifactRequest();
            request.setArtifact(new DefaultArtifact(descriptor));
            request.setRepositories(remoteRepos);
            ArtifactResult result = repoSystem.resolveArtifact(newSession(repoSystem), request);
            File file = result.getArtifact().getFile();
            getLog().info("Resolved artifact " + result + " to " + file + " from " + result.getRepository());
            return file;
        } catch (Exception e) {
            throw new MojoFailureException(String.format("Failed to load artifact from repository: %s", e.getMessage()), e);
        }
    }

    private static RepositorySystemSession newSession(RepositorySystem system) {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();
        LocalRepository localRepo = new LocalRepository(new File(System.getProperty("user.home") + "/.m2/repository"));
        session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo));
        return session;
    }
}
