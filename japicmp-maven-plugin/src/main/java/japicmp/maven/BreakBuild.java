package japicmp.maven;

public class BreakBuild {

	private boolean onSemanticVersioning;
	private boolean onSemanticVersioningForMajorVersionZero;
	private boolean onModifications;
	private boolean onBinaryIncompatibleModifications;
	private boolean onSourceIncompatibleModifications;

	BreakBuild() {
		// Intentionally left blank.
	}

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

	public boolean onSemanticVersioning() {
		return onSemanticVersioning;
	}

	public boolean onSemanticVersioningForMajorVersionZero() {
		return onSemanticVersioningForMajorVersionZero;
	}

	public boolean onModifications() {
		return onModifications;
	}

	public boolean onBinaryIncompatibleModifications() {
		return onBinaryIncompatibleModifications;
	}

	public boolean onSourceIncompatibleModifications() {
		return onSourceIncompatibleModifications;
	}

	void setOnSemanticVersioning(final boolean onSemanticVersioning) {
		this.onSemanticVersioning = onSemanticVersioning;
	}

	void setOnSemanticVersioningForMajorVersionZero(
			final boolean onSemanticVersioningForMajorVersionZero) {
		this.onSemanticVersioningForMajorVersionZero = onSemanticVersioningForMajorVersionZero;
	}

	void setOnModifications(final boolean onModifications) {
		this.onModifications = onModifications;
	}

	void setOnBinaryIncompatibleModifications(final boolean onBinaryIncompatibleModifications) {
		this.onBinaryIncompatibleModifications = onBinaryIncompatibleModifications;
	}

	void setOnSourceIncompatibleModifications(final boolean onSourceIncompatibleModifications) {
		this.onSourceIncompatibleModifications = onSourceIncompatibleModifications;
	}
}
