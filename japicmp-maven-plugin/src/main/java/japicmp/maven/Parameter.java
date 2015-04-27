package japicmp.maven;

public class Parameter {
    private String accessModifier;
    private String packagesToInclude;
    private String packagesToExclude;
    private String onlyBinaryIncompatible;
    private String onlyModified;
    private String breakBuildOnModifications;
    private String breakBuildOnBinaryIncompatibleModifications;
    private String includeSynthetic;

    public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
    }

    public String getPackagesToInclude() {
        return packagesToInclude;
    }

    public void setPackagesToInclude(String packagesToInclude) {
        this.packagesToInclude = packagesToInclude;
    }

    public String getPackagesToExclude() {
        return packagesToExclude;
    }

    public void setPackagesToExclude(String packagesToExclude) {
        this.packagesToExclude = packagesToExclude;
    }

    public String getOnlyModified() {
        return onlyModified;
    }

    public void setOnlyModified(String onlyModified) {
        this.onlyModified = onlyModified;
    }

    public String getOnlyBinaryIncompatible() {
        return onlyBinaryIncompatible;
    }

    public void setOnlyBinaryIncompatible(String onlyBinaryIncompatible) {
        this.onlyBinaryIncompatible = onlyBinaryIncompatible;
    }
    public String getBreakBuildOnModifications() {
        return breakBuildOnModifications;
    }

    public void setBreakBuildOnModifications(String breakBuildOnModifications) {
        this.breakBuildOnModifications = breakBuildOnModifications;
    }

	public String getBreakBuildOnBinaryIncompatibleModifications() {
		return breakBuildOnBinaryIncompatibleModifications;
	}

	public void setBreakBuildOnBinaryIncompatibleModifications(String breakBuildOnBinaryIncompatibleModifications) {
		this.breakBuildOnBinaryIncompatibleModifications = breakBuildOnBinaryIncompatibleModifications;
	}

	public String getIncludeSynthetic() {
		return includeSynthetic;
	}

	public void setIncludeSynthetic(String includeSynthetic) {
		this.includeSynthetic = includeSynthetic;
	}
}
