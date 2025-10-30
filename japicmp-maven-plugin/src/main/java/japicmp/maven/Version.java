package japicmp.maven;

public class Version {
	private Dependency dependency;
	private ConfigurationFile file;

	public Version() {
		/* Intentionally left blank. */
	}

	public Version(final Dependency dependency, final ConfigurationFile file) {
		this.dependency = dependency;
		this.file = file;
	}

	public Dependency getDependency() {
		return dependency;
	}

	public ConfigurationFile getFile() {
		return file;
	}
}
