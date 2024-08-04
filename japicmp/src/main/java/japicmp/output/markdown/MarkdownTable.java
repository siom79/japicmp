package japicmp.output.markdown;

import static java.util.stream.Collectors.joining;

import java.util.*;
import java.util.function.Function;
import java.util.stream.*;

final class MarkdownTable extends Markdown {

	final int columns;
	final List<List<String>> rows = new ArrayList<>();

	MarkdownTable(List<String> headers) {
		columns = headers.size();
		rows.add(headers);
	}

	static String getColumnFormat(int width) {
		return width < 1 ? "%s" : "%-" + width + "s";
	}

	void addRow(List<String> row) {
		rows.add(row);
	}

	@Override
	public String toString() {
		if (rows.size() == 1) {
			return EMPTY;
		}
		final Map<Integer, Integer> colWidths = calculateColumnWidths();
		return IntStream.range(0, rows.size()).mapToObj(r -> {
			final List<String> row = rows.get(r);
			final StringBuilder tmp = new StringBuilder();
			tmp.append(new Row(row, colWidths));
			if (r == 0) {
				tmp.append(PIPE);
				IntStream.range(0, columns).forEach(index -> tmp
					.append(DASH)
					.append(String.format(getColumnFormat(colWidths.get(index)), EMPTY).replace(SPACE, DASH))
					.append(DASH)
					.append(PIPE));
				tmp.append(EOL);
			}
			return tmp.toString();
		}).collect(joining()) + EOL;
	}

	private Map<Integer, Integer> calculateColumnWidths() {
		return IntStream.range(0, columns).mapToObj(Integer.class::cast)
			.collect(Collectors.toMap(Function.identity(), this::calculateMaxWidth));
	}

	private int calculateMaxWidth(int index) {
		if (index + 1 < columns) {
			// Columns adjust dynamically up to a fixed limit
			return rows.stream().map(x -> x.get(index)).mapToInt(String::length).filter(x -> x <= 64).max().orElse(0);
		}
		// Last column adjusts to heading size
		return rows.get(0).get(index).length();
	}

	private class Row {

		final List<String> data;
		final Map<Integer, Integer> widths;
		int offset = 0;

		Row(List<String> data, Map<Integer, Integer> widths) {
			this.data = data;
			this.widths = widths;
		}

		String formatColumn(int index) {
			final int width = widths.get(index);
			final int maxWidth = Math.max(0, width - offset);
			final String formatted = String.format(getColumnFormat(maxWidth), data.get(index));
			final int length = formatted.length();
			if (length <= width) {
				offset -= width - length;
			} else if (length > maxWidth) {
				offset += length - maxWidth;
			}
			offset = Math.max(0, offset);
			return formatted;
		}

		@Override
		public String toString() {
			return PIPE + IntStream.range(0, columns)
				.mapToObj(index -> SPACE + formatColumn(index) + SPACE + PIPE)
				.collect(joining()) +
				EOL;
		}
	}
}
