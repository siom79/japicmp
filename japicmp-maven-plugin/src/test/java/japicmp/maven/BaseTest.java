package japicmp.maven;

import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Collections;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

abstract class BaseTest {

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
    final Version oldVersion = JApiCmpMojoTest.createVersion("groupId", "artifactId", "0.1.0");
    final Version newVersion = JApiCmpMojoTest.createVersion("groupId", "artifactId", "0.1.1");
    final ConfigParameters parameter = configParameters;
    return new PluginParameters(false, newVersion, oldVersion, parameter, new ArrayList<>(), null,
                                null, false, new ArrayList<>(), new ArrayList<>(),
                                new ArrayList<>(), new ArrayList<>(), new SkipReport(),
                                new BreakBuild());
  }

}
