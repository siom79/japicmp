/*
 * Copyright (c) 2023-2025 Bradley Larrick. All rights reserved.
 *
 * Licensed under the Apache License v2.0
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package japicmp.maven.util;

import java.io.File;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.Collections;
import java.util.Map;
import java.util.Properties;
import org.apache.maven.DefaultMaven;
import org.apache.maven.Maven;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequestPopulator;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.testing.junit5.InjectMojo;
import org.apache.maven.plugin.testing.junit5.MojoExtension;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.codehaus.plexus.DefaultPlexusContainer;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.internal.impl.SimpleLocalRepositoryManagerFactory;
import org.eclipse.aether.repository.LocalRepository;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;

/**
 * Junit 5 extension for testing Maven plugin mojos. The extension is automatically registered by
 * adding the {@link LocalMojoTest} annotation to the test class.
 *
 * @see LocalMojoTest
 * @see InjectMojo
 * @see org.apache.maven.plugin.testing.junit5.MojoParameter
 */
public class LocalMojoExtension extends MojoExtension {

  /** Default constructor. */
  public LocalMojoExtension() {
    super();
  }

  /**
   * Resolves the mojo parameter for a Junit 5 test. This overrides the default method to provide a
   * fully populated mojo according to the current test environment.
   *
   * @param parameterContext the parameter context
   * @param extensionContext the extension context
   *
   * @return the resolved parameter (a fully populated {@link org.apache.maven.plugin.AbstractMojo}
   *         object)
   *
   * @throws ParameterResolutionException if an error occurs
   */
  @Override
  public Object resolveParameter(
          final ParameterContext parameterContext, final ExtensionContext extensionContext) {
    final InjectMojo injectMojo =
            parameterContext
                    .findAnnotation(InjectMojo.class)
                    .orElseGet(
                            () -> parameterContext.getDeclaringExecutable().getAnnotation(
                                    InjectMojo.class));
    final File testPom = new File(injectMojo.pom());

    final MavenSession newSession = newMavenSession();
    final MavenProject newProject = newMavenProject(testPom, newSession);
    ((DefaultPlexusContainer) getContainer())
            .addPlexusInjector(
                    Collections.emptyList(),
                    binder -> {
                      binder.bind(MavenSession.class).toInstance(newSession);
                      binder.bind(MavenProject.class).toInstance(newProject);
                    });

    return super.resolveParameter(parameterContext, extensionContext);
  }

  /**
   * Returns a new {@link MavenProject} based on the given pom file and session.
   *
   * @param testPom the test pom file
   * @param session the current {@link MavenSession}
   *
   * @return a new {@code MavenProject}
   */
  private MavenProject newMavenProject(final File testPom, final MavenSession session) {
    try {
      final ProjectBuildingRequest buildingRequest = session.getProjectBuildingRequest();
      final MavenProject project =
              lookup(ProjectBuilder.class).build(testPom, buildingRequest).getProject();
      session.setCurrentProject(project);
      session.setProjects(Collections.singletonList(project));
      return project;
    } catch (ComponentLookupException | ProjectBuildingException e) {
      throw new RuntimeException(e); // NOPMD - Easiest response
    }
  }

  /**
   * Returns a new {@link MavenSession} to use during testing.
   *
   * @return a new {@code MavenSession}
   */
  protected MavenSession newMavenSession() {
    try {
      final MavenExecutionRequest request = new DefaultMavenExecutionRequest();
      final MavenExecutionResult result = new DefaultMavenExecutionResult();

      // populate sensible defaults, including repository basedir and remote repos
      final MavenExecutionRequestPopulator populator = getContainer().lookup(
              MavenExecutionRequestPopulator.class);
      populator.populateDefaults(request);

      // this is needed to allow java profiles to get resolved; i.e. avoid during project builds:
      // [ERROR] Failed to determine Java version for profile java-1.5-detected @
      // org.apache.commons:commons-parent:22,
      // /Users/alex/.m2/repository/org/apache/commons/commons-parent/22/commons-parent-22.pom, line
      // 909, column 14
      final Properties newProps = System.getProperties();
      final Map<String, String> envProps = System.getenv();
      envProps.forEach((k, v) -> newProps.setProperty("env." + k, v));
      request.setSystemProperties(newProps);

      // and this is needed so that the repo session in the maven session
      // has a repo manager, and it points at the local repo
      // (cf MavenRepositorySystemUtils.newSession() which is what is otherwise done)
      final DefaultMaven maven = (DefaultMaven) getContainer().lookup(Maven.class);
      final DefaultRepositorySystemSession repoSession = (DefaultRepositorySystemSession) maven.newRepositorySession(
              request);
//      final DefaultLocalPathComposer pathComposer = new DefaultLocalPathComposer();
      final SimpleLocalRepositoryManagerFactory repoMgrFactory =
              new SimpleLocalRepositoryManagerFactory();
      final LocalRepository localRepo = new LocalRepository(
              request.getLocalRepository().getBasedir());
      repoSession.setLocalRepositoryManager(repoMgrFactory.newInstance(repoSession, localRepo));

      final MavenSession session = new MavenSession(getContainer(),
                                                    repoSession,
                                                    request,
                                                    result);
      return session;
    } catch (Exception e) { // NOPMD - Simplify method signature
      throw new UndeclaredThrowableException(e);
    }
  }
}
