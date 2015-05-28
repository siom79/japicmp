package japicmp.maven;

import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.project.MavenProject;

import java.util.List;

public class MavenParameters {
	private final List<ArtifactRepository> artifactRepositories;
	private final ArtifactFactory artifactFactory;
	private final ArtifactRepository localRepository;
	private final ArtifactResolver artifactResolver;
	private final MavenProject mavenProject;

	public MavenParameters(List<ArtifactRepository> artifactRepositories, ArtifactFactory artifactFactory, ArtifactRepository localRepository, ArtifactResolver artifactResolver, MavenProject mavenProject) {
		this.artifactRepositories = artifactRepositories;
		this.artifactFactory = artifactFactory;
		this.localRepository = localRepository;
		this.artifactResolver = artifactResolver;
		this.mavenProject = mavenProject;
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

	public ArtifactResolver getArtifactResolver() {
		return artifactResolver;
	}

	public MavenProject getMavenProject() {
		return mavenProject;
	}
}
