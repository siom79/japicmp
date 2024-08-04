package japicmp.output.markdown;

public class MarkdownBadge extends Markdown {

	static final String BASE_URL = "https://img.shields.io/badge/";

	final String label;
	final String message;
	final String color;
	final String logo;

	public MarkdownBadge(String label, String message, String color, String logo) {
		this.label = label;
		this.message = message;
		this.color = color;
		this.logo = logo;
	}

	public MarkdownBadge(String label, String message, String color) {
		this(label, message, color, null);
	}

	@Override
	public String toString() {
		final String alt = label != null ? label + SPACE + message : message;
		return new MarkdownImage(alt, getHref(), alt).toString();
	}

	public MarkdownRefImage toRefImage(MarkdownReferences references, String alt, String title) {
		return references.make(getHref(), alt, title).toImage(alt);
	}

	public MarkdownRefImage toRefImage(MarkdownReferences references, String title) {
		return toRefImage(references, title, title);
	}

	public MarkdownRefImage toRefImage(MarkdownReferences references) {
		return toRefImage(references, message);
	}

	private String getHref() {
		final String pre = label == null ? EMPTY : label
			.replace(DASH, UNDERSCORE)
			.replace(SPACE, UNDERSCORE)
			.replace(HASH, COLON) + DASH;
		final String msg = message
			.replace(DASH, UNDERSCORE)
			.replace(SPACE, UNDERSCORE)
			.replace(HASH, COLON);
		final String arg = logo == null ? EMPTY : "?logo" + EQUAL + logo;
		return BASE_URL + pre + msg + DASH + color + arg;
	}
}
