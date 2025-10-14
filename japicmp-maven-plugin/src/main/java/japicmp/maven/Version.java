package japicmp.maven;

/**
 * Class for storing project version information.
 */
public class Version {
	/* Dependency defining the version. */
	private Dependency dependency;
	/* Version configuration file. */
	private ConfigurationFile file;

	/**
	 * Default constructor.
	 */
	public Version() {
		/* Intentionally left blank. */
	}

	/**
	 * Constructs a Version object with the given dependency and configuration file.
	 *
	 * @param dependency the Version dependency
	 * @param file       the Version configuration file
	 */
	public Version(final Dependency dependency, final ConfigurationFile file) {
		this.dependency = dependency;
		this.file = file;
	}

	/**
	 * Returns the dependency defining the version.
	 *
	 * @return the dependency defining the version
	 */
	public Dependency getDependency() {
		return dependency;
	}

	/**
	 * Returns the version configuration file.
	 *
	 * @return the version configuration file
	 */
	public ConfigurationFile getFile() {
		return file;
	}

  /*
   * Sets the dependency defining the version.
   *
   * @param dependency the new dependency defining the version
  public void setDependency(final Dependency dependency) {
    this.dependency = dependency;
  }
   */

  /*
   * Sets the version configuration file.
   *
   * @param file the new version configuration file
  public void setFile(final ConfigurationFile file) {
    this.file = file;
  }
   */
}
