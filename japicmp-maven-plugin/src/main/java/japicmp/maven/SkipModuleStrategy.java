package japicmp.maven;

import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

import java.util.List;

public class SkipModuleStrategy {

	private final PluginParameters pluginParameters;
	private final MavenParameters mavenParameters;
	private final Log log;

	public SkipModuleStrategy(PluginParameters pluginParameters, MavenParameters mavenParameters, Log log) {
		this.pluginParameters = pluginParameters;
		this.mavenParameters = mavenParameters;
		this.log = log;
	}

	public boolean skip() {
		MavenProject mavenProject = mavenParameters.getMavenProject();
		Parameter parameterParam = pluginParameters.getParameterParam();
		if (mavenProject != null && parameterParam !=null) {
			List<String> packagingSupporteds = parameterParam.getPackagingSupporteds();
			if ((packagingSupporteds != null) && !packagingSupporteds.isEmpty()) {
				if (!packagingSupporteds.contains(mavenProject.getPackaging())) {
					this.log.info("Filtered according to packagingFilter");
					return true;
				}
			} else {
				this.log.debug("No packaging support defined, no filtering");
			}
			if ("pom".equals(mavenProject.getPackaging())) {
				boolean skipPomModules = true;
				String skipPomModulesAsString = parameterParam.getSkipPomModules();
				if (skipPomModulesAsString != null) {
					skipPomModules = Boolean.valueOf(skipPomModulesAsString);
				}
				if (skipPomModules) {
					this.log.info("Skipping execution because packaging of this module is 'pom'.");
					return true;
				}
			}
			String artifactId = mavenProject.getArtifactId();
			if (artifactId != null) {
				List<String> excludeModules = parameterParam.getExcludeModules();
				if (excludeModules != null) {
					for (String excludeModule : excludeModules) {
						if (excludeModule != null) {
							if (artifactId.matches(excludeModule)) {
								this.log.info("Skipping module because artifactId matches exclude expression: " + excludeModule);
								return true;
							}
						}
					}
				}
				int includeCount = 0;
				List<String> includeModules = parameterParam.getIncludeModules();
				if (includeModules != null) {
					for (String includeModule : includeModules) {
						if (includeModule != null) {
							includeCount++;
							if (artifactId.matches(includeModule)) {
								if (this.log.isDebugEnabled()) {
									this.log.debug("Including module because it is explicitly included: " + includeModule);
								}
								return false;
							}
						}
					}
				}
				if (includeCount > 0) {
					this.log.info("Skipping module because explicit includes are defined but artifactId did not match.");
					return true; // it has not been included up to now and we have includes -> skip
				}
			} else {
				this.log.debug("Name of maven project is null.");
			}
		}
		return false;
	}
}
