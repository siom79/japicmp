package japicmp.maven;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.util.List;

public class MavenParameters {
	private final List<ArtifactRepository> artifactRepositories;
	private final ArtifactFactory artifactFactory;
	private final ArtifactRepository localRepository;
	private final MavenProject mavenProject;
	private final MojoExecution mojoExecution;
	private final String versionRangeWithProjectVersion;
	private final ArtifactMetadataSource metadataSource;
	private final RepositorySystem repoSystem;
	private final RepositorySystemSession repoSession;
	private final List<RemoteRepository> remoteRepos;
	
	public MavenParameters(List<ArtifactRepository> artifactRepositories, ArtifactFactory artifactFactory, ArtifactRepository localRepository,
						   MavenProject mavenProject, MojoExecution mojoExecution, String versionRangeWithProjectVersion, ArtifactMetadataSource metadataSource,
						   final RepositorySystem repoSystem, final RepositorySystemSession repoSession, final List<RemoteRepository> remoteRepos) {
		this.artifactRepositories = artifactRepositories;
		this.artifactFactory = artifactFactory;
		this.localRepository = localRepository;
		this.mavenProject = mavenProject;
		this.mojoExecution = mojoExecution;
		this.versionRangeWithProjectVersion = versionRangeWithProjectVersion;
		this.metadataSource = metadataSource;
		this.repoSystem = repoSystem;
		this.repoSession = repoSession;
		this.remoteRepos = remoteRepos;
	}
	
	public List<ArtifactRepository> getArtifactRepositories() {
		return artifactRepositories;
	}

	public ArtifactFactory getArtifactFactory() {
		return artifactFactory;
	}

	public ArtifactRepository getLocalRepository() {
		return localRepository;
	}

	public MavenProject getMavenProject() {
		return mavenProject;
	}

	public MojoExecution getMojoExecution() {
		return mojoExecution;
	}

	public String getVersionRangeWithProjectVersion() {
		return versionRangeWithProjectVersion;
	}

	public ArtifactMetadataSource getMetadataSource() {
		return metadataSource;
	}
	
	public RepositorySystem getRepoSystem() {
		return this.repoSystem;
	}
	
	public RepositorySystemSession getRepoSession() {
		return this.repoSession;
	}
	
	public List<RemoteRepository> getRemoteRepos() {
		return this.remoteRepos;
	}
}
