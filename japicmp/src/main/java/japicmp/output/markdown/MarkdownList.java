package japicmp.output.markdown;

import static java.util.Arrays.*;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

class MarkdownList extends Markdown {

	private final String pad;
	private final List<String> elements;

	MarkdownList(String... elements) {
		this.pad = EMPTY;
		this.elements = asList(elements);
	}

	MarkdownList(int level, Stream<String> elements) {
		final String format = "%-" + (level * 2) + "s";
		this.pad = String.format(format, EMPTY);
		this.elements = elements.collect(toList());
	}

	MarkdownList(int level, String... elements) {
		this(level, stream(elements));
	}

	@Override
	public String toString() {
		return (pad.isEmpty() || elements.isEmpty() ? EMPTY : EOL) + elements.stream().filter(x -> x != null && !x.isEmpty()).map(e -> pad + "- " + e).collect(LINES);
	}
}
