package japicmp.maven;

import java.util.List;

public class Parameter {
    private String accessModifier;
    private List<String> includes;
    private List<String> excludes;
    private String onlyBinaryIncompatible;
    private String onlyModified;
    private String breakBuildOnModifications;
    private String breakBuildOnBinaryIncompatibleModifications;
    private String includeSynthetic;
	private String ignoreMissingClasses;
	private String skipPomModules;

	//skip generation of outputfile generation if flag is set to true
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean skipNoChange;

	// skip XML file production
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean skipXml;

	// skip HTML file production
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private boolean skipHtml;

	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String htmlStylesheet;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String htmlTitle;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String noAnnotations;
	@org.apache.maven.plugins.annotations.Parameter(required = false)
	private String ignoreNonResolvableArtifacts;

	public String getNoAnnotations() {
		return noAnnotations;
	}

	public void setNoAnnotations(String noAnnotations) {
		this.noAnnotations = noAnnotations;
	}

	public String getAccessModifier() {
        return accessModifier;
    }

    public void setAccessModifier(String accessModifier) {
        this.accessModifier = accessModifier;
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

	public List<String> getIncludes() {
		return includes;
	}

	public void setIncludes(List<String> includes) {
		this.includes = includes;
	}

	public List<String> getExcludes() {
		return excludes;
	}

	public void setExcludes(List<String> excludes) {
		this.excludes = excludes;
	}

	public String getIgnoreMissingClasses() {
		return ignoreMissingClasses;
	}

	public void setIgnoreMissingClasses(String ignoreMissingClasses) {
		this.ignoreMissingClasses = ignoreMissingClasses;
	}

	public String getSkipPomModules() {
		return skipPomModules;
	}

	public void setSkipPomModules(String skipPomModules) {
		this.skipPomModules = skipPomModules;
	}

	public boolean isSkipNoChange() {
		return skipNoChange;
	}

	public void setSkipNoChange(boolean skipNoChange) {
			this.skipNoChange = skipNoChange;
	}

	public boolean isSkipXml() {
		return skipXml;
	}

	public void setSkipXml(boolean skipXml) {
		this.skipXml = skipXml;
	}

	public boolean isSkipHtml() {
		return skipHtml;
	}

	public void setSkipHtml(boolean skipHtml) {
		this.skipHtml = skipHtml;
	}

	public String getHtmlStylesheet() {
		return htmlStylesheet;
	}

	public void setHtmlStylesheet(String htmlStylesheet) {
		this.htmlStylesheet = htmlStylesheet;
	}

	public String getHtmlTitle() {
		return htmlTitle;
	}

	public void setHtmlTitle(String htmlTitle) {
		this.htmlTitle = htmlTitle;
	}

	public String getIgnoreNonResolvableArtifacts() {
		return ignoreNonResolvableArtifacts;
	}

	public void setIgnoreNonResolvableArtifacts(String ignoreNonResolvableArtifacts) {
		this.ignoreNonResolvableArtifacts = ignoreNonResolvableArtifacts;
	}
}
