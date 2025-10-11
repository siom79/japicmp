package japicmp.maven;

/**
 * Convenience class to store flags for skipping reports.
 */
public class SkipReport {

  private boolean skipDiffReport;
  private boolean skipHtmlReport;
  private boolean skipMarkdownReport;
  private boolean skipXmlReport;

  /**
   * Convenience constructor for unit testing.
   */
  SkipReport() {
    // Intentionally left blank.
  }

  /**
   * Creates a new instance with the given values.
   *
   * @param skipDiffReport     skip the Diff report
   * @param skipHtmlReport     skip the HTML report
   * @param skipMarkdownReport skip the Markdown report
   * @param skipXmlReport      skip the XML report
   */
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

  /**
   * Returns {@code true} if the Diff report should be skipped.
   *
   * @return {@code true} if the Diff report should be skipped
   */
  public boolean diffReport() {
    return skipDiffReport;
  }

  /**
   * Returns  {@code true} if the HTML report should be skipped.
   *
   * @return {@code true} if the HTML report should be skipped
   */
  public boolean htmlReport() {
    return skipHtmlReport;
  }

  /**
   * Returns {@code true} if the Markdown report should be skipped.
   *
   * @return {@code true} if the Markdown report should be skipped
   */
  public boolean markdownReport() {
    return skipMarkdownReport;
  }

  /**
   * Returns {@code true} if the XML report should be skipped.
   *
   * @return {@code true} if the XML report should be skipped
   */
  public boolean xmlReport() {
    return skipXmlReport;
  }

  /*
   * Setter defined for unit testing.
   */
  void setSkipDiffReport(final boolean skipDiffReport) {
    this.skipDiffReport = skipDiffReport;
  }

  /*
   * Setter defined for unit testing.
   */
  void setSkipHtmlReport(final boolean skipHtmlReport) {
    this.skipHtmlReport = skipHtmlReport;
  }

  /*
   * Setter defined for unit testing.
   */
  void setSkipMarkdownReport(final boolean skipMarkdownReport) {
    this.skipMarkdownReport = skipMarkdownReport;
  }

  /*
   * Setter defined for unit testing.
   */
  void setSkipXmlReport(final boolean skipXmlReport) {
    this.skipXmlReport = skipXmlReport;
  }
}
