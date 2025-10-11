package japicmp.maven;

/**
 * Convenience class to store flags for breaking the build.
 */
public class BreakBuild {

  private boolean onSemanticVersioning;
  private boolean onSemanticVersioningForMajorVersionZero;
  private boolean onModifications;
  private boolean onBinaryIncompatibleModifications;
  private boolean onSourceIncompatibleModifications;

  /**
   * Convenience constructor for unit testing.
   */
  BreakBuild() {
    // Intentionally left blank.
  }

  /**
   * Creates a new instance with the given values.
   *
   * @param breakBuildOnSemanticVersioning                    break the build on semantic
   *                                                          versioning
   * @param breakBuildOnSemanticVersioningForMajorVersionZero break the build on semantic versioning
   *                                                          for major version zero
   * @param breakBuildOnBinaryIncompatibleModifications       break the build on binary
   *                                                          incompatibilities
   * @param breakBuildOnSourceIncompatibleModifications       break the build on source
   *                                                          incompatibilities
   * @param breakBuildOnModifications                         break the build on modifications
   */
  public BreakBuild(
          final boolean breakBuildOnSemanticVersioning,
          final boolean breakBuildOnSemanticVersioningForMajorVersionZero,
          final boolean breakBuildOnBinaryIncompatibleModifications,
          final boolean breakBuildOnSourceIncompatibleModifications,
          final boolean breakBuildOnModifications) {
    this.onSemanticVersioning = breakBuildOnSemanticVersioning;
    this.onSemanticVersioningForMajorVersionZero = breakBuildOnSemanticVersioningForMajorVersionZero;
    this.onModifications = breakBuildOnModifications;
    this.onBinaryIncompatibleModifications = breakBuildOnBinaryIncompatibleModifications;
    this.onSourceIncompatibleModifications = breakBuildOnSourceIncompatibleModifications;
  }

  /**
   * Returns if the build should be broken on semantic versioning.
   *
   * @return {@code true} if the build should be broken on semantic versioning
   */
  public boolean onSemanticVersioning() {
    return onSemanticVersioning;
  }

  /**
   * Returns {@code true} if the build should be broken on semantic versioning for major version
   * zero.
   *
   * @return {@code true} if the build should be broken on semantic versioning for major version
   *         zero
   */
  public boolean onSemanticVersioningForMajorVersionZero() {
    return onSemanticVersioningForMajorVersionZero;
  }

  /**
   * Returns {@code true} if the build should be broken on modifications.
   *
   * @return {@code true} if the build should be broken on modifications
   */
  public boolean onModifications() {
    return onModifications;
  }

  /**
   * Returns {@code true} if the build should be broken on binary incompatible modifications.
   *
   * @return {@code true} if the build should be broken on binary incompatible modifications
   */
  public boolean onBinaryIncompatibleModifications() {
    return onBinaryIncompatibleModifications;
  }

  /**
   * Returns {@code true} if the build should be broken on source incompatible modifications.
   *
   * @return {@code true} if the build should be broken on source incompatible modifications
   */
  public boolean onSourceIncompatibleModifications() {
    return onSourceIncompatibleModifications;
  }

  /*
   * Setter defined for unit testing.
   */
  void setOnSemanticVersioning(final boolean onSemanticVersioning) {
    this.onSemanticVersioning = onSemanticVersioning;
  }

  /*
   * Setter defined for unit testing.
   */
  void setOnSemanticVersioningForMajorVersionZero(
          final boolean onSemanticVersioningForMajorVersionZero) {
    this.onSemanticVersioningForMajorVersionZero = onSemanticVersioningForMajorVersionZero;
  }

  /*
   * Setter defined for unit testing.
   */
  void setOnModifications(final boolean onModifications) {
    this.onModifications = onModifications;
  }

  /*
   * Setter defined for unit testing.
   */
  void setOnBinaryIncompatibleModifications(final boolean onBinaryIncompatibleModifications) {
    this.onBinaryIncompatibleModifications = onBinaryIncompatibleModifications;
  }

  /*
   * Setter defined for unit testing.
   */
  void setOnSourceIncompatibleModifications(final boolean onSourceIncompatibleModifications) {
    this.onSourceIncompatibleModifications = onSourceIncompatibleModifications;
  }
}
