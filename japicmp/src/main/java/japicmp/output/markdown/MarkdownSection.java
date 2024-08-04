package japicmp.output.markdown;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

final class MarkdownSection<T> extends Markdown {

	private final String title;
	private final List<T> list;
	private final Comparator<? super T> comparator;
	private final List<Column<T>> columns = new ArrayList<>();

	MarkdownSection(String title, List<T> list, Comparator<? super T> comparator) {
		this.title = title;
		this.list = list;
		this.comparator = comparator;
	}

	MarkdownSection(String title, T one) {
		this(title, singletonList(one), null);
	}

	MarkdownSection<T> column(String header, Function<T, String> mapper) {
		this.columns.add(new Column<>(header, mapper));
		return this;
	}

	@Override
	public String toString() {
		if (list.isEmpty()) {
			return EMPTY;
		}
		final Stream<T> stream = comparator == null ? list.stream() : list.stream().sorted(comparator);
		final MarkdownTable table = new MarkdownTable(columns.stream().map(column -> column.header).collect(toList()));
		stream.forEach(x -> table.addRow(columns.stream().map(column -> column.mapper.apply(x)).collect(toList())));
		return title + table;
	}

	private static final class Column<T> {
		final String header;
		final Function<T, String> mapper;

		Column(String header, Function<T, String> mapper) {
			this.header = header;
			this.mapper = mapper;
		}
	}
}
