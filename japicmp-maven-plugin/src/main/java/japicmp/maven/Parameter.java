package japicmp.maven;

public class Parameter {
    private String accessModifier;
    private String include;
    private String exclude;
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

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getExclude() {
        return exclude;
    }

    public void setExclude(String exclude) {
        this.exclude = exclude;
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
