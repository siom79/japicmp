package japicmp.output.markdown.config;

import japicmp.config.Options;
import japicmp.util.Optional;

public class MarkdownOptions {

	public Options options;

	public Optional<String> targetOldVersion = Optional.absent();
	public Optional<String> targetNewVersion = Optional.absent();

	public MarkdownTitleOptions title = new MarkdownTitleOptions();
	public MarkdownHeaderOptions header = new MarkdownHeaderOptions();
	public MarkdownSortOptions sort = new MarkdownSortOptions();
	public MarkdownMessageOptions message = new MarkdownMessageOptions();

	MarkdownOptions(Options options) {
		this.options = options != null ? options : Options.newDefault();
	}

	public static MarkdownOptions newDefault() {
		return new MarkdownOptions(null);
	}

	public static MarkdownOptions newDefault(Options options) {
		return new MarkdownOptions(options);
	}

	public void setTargetOldVersion(String targetOldVersion) {
		this.targetOldVersion = Optional.fromNullable(targetOldVersion);
	}

	public void setTargetNewVersion(String targetNewVersion) {
		this.targetNewVersion = Optional.fromNullable(targetNewVersion);
	}
}
