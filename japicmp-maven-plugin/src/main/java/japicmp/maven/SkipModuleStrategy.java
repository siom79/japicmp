package japicmp.maven;

import java.util.List;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Class for identifying when to skip a module.
 */
public class SkipModuleStrategy {

  private final PluginParameters pluginParameters;
  private final MavenParameters mavenParameters;
  private final Log log;

  /**
   * Constructs a {@code SkipModuleStrategy} object.
   *
   * @param pluginParameters the defined plugin parameters
   * @param mavenParameters  the defined Maven parameters
   * @param log              the Maven log for writing messages
   */
  public SkipModuleStrategy(final PluginParameters pluginParameters,
                            final MavenParameters mavenParameters,
                            final Log log) {
    this.pluginParameters = pluginParameters;
    this.mavenParameters = mavenParameters;
    this.log = log;
  }

  /**
   * Returns {@code true} if the module should be skipped.
   *
   * @return {@code true} if the module should be skipped
   */
  public boolean skip() {
    MavenProject mavenProject = mavenParameters.mavenProject();
    ConfigParameters parameters = pluginParameters.parameter();
    if (mavenProject != null && parameters != null) {
      List<String> packagingSupporteds = parameters.getPackagingSupporteds();
      if ((packagingSupporteds != null) && !packagingSupporteds.isEmpty()) {
        if (!packagingSupporteds.contains(mavenProject.getPackaging())) {
          log.info("Filtered according to packagingFilter");
          return true;
        }
      } else {
        log.debug("No packaging support defined, no filtering");
      }
      if ("pom".equals(mavenProject.getPackaging())) {
        boolean skipPomModules = parameters.getSkipPomModules();
        if (skipPomModules) {
          log.info("Skipping execution because packaging of this module is 'pom'.");
          return true;
        }
      }

      String artifactId = mavenProject.getArtifactId();
      if (artifactId != null) {
        List<String> excludeModules = parameters.getExcludeModules();
        if (excludeModules != null) {
          for (String excludeModule : excludeModules) {
            if (excludeModule != null) {
              if (artifactId.matches(excludeModule)) {
                log.info("Skipping module because artifactId matches exclude expression: "
                        + excludeModule);
                return true;
              }
            }
          }
        }

        int includeCount = 0;
        List<String> includeModules = parameters.getIncludeModules();
        if (includeModules != null) {
          for (String includeModule : includeModules) {
            if (includeModule != null) {
              includeCount++;
              if (artifactId.matches(includeModule)) {
                if (this.log.isDebugEnabled()) {
                  log.debug("Including module because it is explicitly included: " + includeModule);
                }
                return false;
              }
            }
          }
        }

        if (includeCount > 0) {
          log.info("Skipping module because explicit includes are defined but artifactId did not match.");
          return true; // it has not been included up to now, and we have includes -> skip
        }
      } else {
        log.debug("Name of maven project is null.");
      }
    }
    return false;
  }
}
