package japicmp.output.markdown;

import java.util.Objects;
import java.util.Optional;

class MarkdownReference extends Markdown {

	final String href;
	final String title;

	MarkdownReference(String href, String title) {
		this.href = Optional.ofNullable(href).orElse(HASH);
		this.title = title;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj instanceof MarkdownReference) {
			final MarkdownReference that = (MarkdownReference) obj;
			return Objects.equals(href, that.href) && Objects.equals(title, that.title);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(href, title);
	}

	@Override
	public String toString() {
		if (title == null || title.isEmpty()) {
			return href;
		}
		final String sanitizedTitle = title.replace(QUOTE, BACKSLASH + QUOTE);
		return href + SPACE + quotes(sanitizedTitle);
	}
}
