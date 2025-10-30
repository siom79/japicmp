package japicmp.maven;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.List;

public class SkipModuleStrategy {

	private final PluginParameters pluginParameters;
	private final MavenParameters mavenParameters;
	private final Log log;

	public SkipModuleStrategy(final PluginParameters pluginParameters,
							  final MavenParameters mavenParameters,
							  final Log log) {
		this.pluginParameters = pluginParameters;
		this.mavenParameters = mavenParameters;
		this.log = log;
	}

	public boolean skip() {
		final MavenProject mavenProject = mavenParameters.mavenProject();
		final ConfigParameters parameters = pluginParameters.parameter();
		if (mavenProject!=null && parameters!=null) {
			final List<String> packagingSupporteds = parameters.getPackagingSupporteds();
			if ((packagingSupporteds!=null) && !packagingSupporteds.isEmpty()) {
				if (!packagingSupporteds.contains(mavenProject.getPackaging())) {
					log.info("Filtered according to packagingFilter");
					return true;
				}
			} else {
				log.debug("No packaging support defined, no filtering");
			}

			if ("pom".equals(mavenProject.getPackaging()) && parameters.getSkipPomModules()) {
				log.info("Skipping module because packaging is 'pom'.");
				return true;
			}

			final String artifactId = mavenProject.getArtifactId();
			if (artifactId!=null) {
				final List<String> excludeModules = parameters.getExcludeModules();
				if (excludeModules!=null) {
					for (String excludeModule : excludeModules) {
						if (excludeModule!=null) {
							if (artifactId.matches(excludeModule)) {
								log.info("Skipping module because artifactId matches exclude expression: "
										+ excludeModule);
								return true;
							}
						}
					}
				}

				final List<String> includeModules = parameters.getIncludeModules();
				int includeCount = 0;
				if (includeModules!=null) {
					for (String includeModule : includeModules) {
						if (includeModule!=null) {
							includeCount++;
							if (artifactId.matches(includeModule)) {
								log.debug("Including module because it is explicitly included: " + includeModule);
								return false;
							}
						}
					}
				}

				if (includeCount > 0) {
					log.info(
							"Skipping module because explicit includes are defined but artifactId did not match.");
					return true; // it has not been included up to now, and we have includes -> skip
				}
			} else {
				log.debug("Name of maven project is null.");
			}
		}

		return false;
	}
}
