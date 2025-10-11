package japicmp.maven;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

abstract class AbstractTest {

  /**
   * Creates MavenParameters for tests.
   *
   * @return the mocked MavenParameters
   */
  MavenParameters createMavenParameters() {
    final RemoteRepository remoteRepository = new RemoteRepository.Builder("id", "type",
                                                                           "http://example.org").build();
    return new MavenParameters(new ArrayList<>(), new MavenProject(),
                               mock(MojoExecution.class), "", mock(RepositorySystem.class),
                               mock(RepositorySystemSession.class),
                               Collections.singletonList(remoteRepository));
  }

  /**
   * Creates the PluginParameters for tests.
   *
   * @return the mocked PluginParameters
   */
  PluginParameters createPluginParameters(final ConfigParameters configParameters) {
    final Version oldVersion = createVersion("groupId", "artifactId", "0.1.0");
    final Version newVersion = createVersion("groupId", "artifactId", "0.1.1");
    return new PluginParameters(false, newVersion, oldVersion, configParameters, new ArrayList<>(),
                                null,
                                null, false, new ArrayList<>(), new ArrayList<>(),
                                new ArrayList<>(), new ArrayList<>(), new SkipReport(),
                                new BreakBuild());
  }

  /**
   * Creates a Version instance for testing.
   *
   * @param groupId    the group ID of the Version
   * @param artifactId the artifact ID of the Version
   * @param version    the version of the Version
   *
   * @return a new Version instance
   */
  Version createVersion(String groupId, String artifactId, String version) {
    final Dependency dependency = new Dependency();
    dependency.setGroupId(groupId);
    dependency.setArtifactId(artifactId);
    dependency.setVersion(version);
    return new Version(dependency, null);
  }

}
