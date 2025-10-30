package japicmp.maven;

public class SkipReport {

	private boolean skipDiffReport;
	private boolean skipHtmlReport;
	private boolean skipMarkdownReport;
	private boolean skipXmlReport;

	SkipReport() {
		// Intentionally left blank.
	}

	public SkipReport(
			final boolean skipDiffReport,
			final boolean skipHtmlReport,
			final boolean skipMarkdownReport,
			final boolean skipXmlReport) {
		this.skipDiffReport = skipDiffReport;
		this.skipHtmlReport = skipHtmlReport;
		this.skipMarkdownReport = skipMarkdownReport;
		this.skipXmlReport = skipXmlReport;
	}

	public boolean diffReport() {
		return skipDiffReport;
	}

	public boolean htmlReport() {
		return skipHtmlReport;
	}

	public boolean markdownReport() {
		return skipMarkdownReport;
	}

	public boolean xmlReport() {
		return skipXmlReport;
	}

	void setSkipDiffReport(final boolean skipDiffReport) {
		this.skipDiffReport = skipDiffReport;
	}

	void setSkipHtmlReport(final boolean skipHtmlReport) {
		this.skipHtmlReport = skipHtmlReport;
	}

	void setSkipMarkdownReport(final boolean skipMarkdownReport) {
		this.skipMarkdownReport = skipMarkdownReport;
	}

	void setSkipXmlReport(final boolean skipXmlReport) {
		this.skipXmlReport = skipXmlReport;
	}
}
