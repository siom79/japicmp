package japicmp.maven;

import java.util.List;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

/** Class for storing Maven parameters. */
public class MavenParameters {
  private final List<ArtifactRepository> artifactRepositories;
  private final MavenProject mavenProject;
  private final MojoExecution mojoExecution;
  private final String versionRangeWithProjectVersion;
  private final RepositorySystem repoSystem;
  private final RepositorySystemSession repoSession;
  private final List<RemoteRepository> remoteRepos;

  /**
   * Constructs a {@code MavenParameters} instance with the given values.
   *
   * @param artifactRepositories           the artifact repositories
   * @param mavenProject                   the Maven project
   * @param mojoExecution                  the Mojo execution
   * @param versionRangeWithProjectVersion the version range
   * @param repoSystem                     the repository system
   * @param repoSession                    the repository system session
   * @param remoteRepos                    the remote repositories
   */
  public MavenParameters(
          final List<ArtifactRepository> artifactRepositories,
          final MavenProject mavenProject,
          final MojoExecution mojoExecution,
          final String versionRangeWithProjectVersion,
          final RepositorySystem repoSystem,
          final RepositorySystemSession repoSession,
          final List<RemoteRepository> remoteRepos) {
    this.artifactRepositories = artifactRepositories;
    this.mavenProject = mavenProject;
    this.mojoExecution = mojoExecution;
    this.versionRangeWithProjectVersion = versionRangeWithProjectVersion;
    this.repoSystem = repoSystem;
    this.repoSession = repoSession;
    this.remoteRepos = remoteRepos;
  }

  /**
   * Returns the artifact repositories.
   *
   * @return the artifact repositories
   */
  public List<ArtifactRepository> artifactRepositories() {
    return artifactRepositories;
  }

  /**
   * Returns the Maven project.
   *
   * @return the Maven project
   */
  public MavenProject mavenProject() {
    return mavenProject;
  }

  /**
   * Returns the Mojo execution object.
   *
   * @return the Mojo execution object
   */
  public MojoExecution mojoExecution() {
    return mojoExecution;
  }

  /**
   * Returns the version range with project versions.
   *
   * @return the version range with project versions
   */
  public String versionRangeWithProjectVersion() {
    return versionRangeWithProjectVersion;
  }

  /**
   * Returns the repository system.
   *
   * @return the repository system
   */
  public RepositorySystem repoSystem() {
    return this.repoSystem;
  }

  /**
   * Returns the repository system session.
   *
   * @return the repository system session
   */
  public RepositorySystemSession repoSession() {
    return this.repoSession;
  }

  /**
   * Returns the remote repositories.
   *
   * @return the remote repositories
   */
  public List<RemoteRepository> remoteRepos() {
    return this.remoteRepos;
  }
}
