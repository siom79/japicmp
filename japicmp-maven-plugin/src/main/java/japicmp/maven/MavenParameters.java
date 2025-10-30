package japicmp.maven;

import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

public class MavenParameters {
	private final List<ArtifactRepository> artifactRepositories;
	private final MavenProject mavenProject;
	private final MojoExecution mojoExecution;
	private final String versionRangeWithProjectVersion;
	private final RepositorySystem repoSystem;
	private final RepositorySystemSession repoSession;
	private final List<RemoteRepository> remoteRepos;

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

	public List<ArtifactRepository> artifactRepositories() {
		return artifactRepositories;
	}

	public MavenProject mavenProject() {
		return mavenProject;
	}

	public MojoExecution mojoExecution() {
		return mojoExecution;
	}

	public String versionRangeWithProjectVersion() {
		return versionRangeWithProjectVersion;
	}

	public RepositorySystem repoSystem() {
		return this.repoSystem;
	}

	public RepositorySystemSession repoSession() {
		return this.repoSession;
	}

	public List<RemoteRepository> remoteRepos() {
		return this.remoteRepos;
	}
}
