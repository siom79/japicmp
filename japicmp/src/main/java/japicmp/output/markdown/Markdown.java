package japicmp.output.markdown;

import static java.util.stream.Collectors.joining;

import java.util.stream.Collector;

public abstract class Markdown {

	public static final String EOL = "\n";
	public static final String PARAGRAPH = "\n\n";
	public static final String SPACE = " ";
	public static final String EMPTY = "";
	public static final String DASH = "-";
	public static final String UNDERSCORE = "_";
	public static final String QUOTE = "\"";
	public static final String BACKSLASH = "\\";
	public static final String PARENTHESIS_OPEN = "(";
	public static final String PARENTHESIS_CLOSE = ")";
	public static final String ANGLE_OPEN = "<";
	public static final String ANGLE_CLOSE = ">";
	public static final String BRACKET_OPEN = "[";
	public static final String BRACKET_CLOSE = "]";
	public static final String BANG = "!";
	public static final String HASH = "#";
	public static final String COLON = ":";
	public static final String EQUAL = "=";
	public static final String DOT = ".";
	public static final char PIPE = '|';
	public static final String BACKTICK = "`";
	public static final String MARKDOWN_HORIZONTAL_RULE = "___";
	public static final Collector<CharSequence, ?, String> LINES = joining(EOL);
	public static final Collector<CharSequence, ?, String> SPACES = joining(SPACE);
	public static final Collector<CharSequence, ?, String> CSV = joining(", ");
	public static final Collector<CharSequence, ?, String> BR = joining("<br>");

	public static String quotes(String text) {
		return QUOTE + text + QUOTE;
	}

	public static String backticks(String text) {
		return BACKTICK + text + BACKTICK;
	}

	public static String angles(String text) {
		return ANGLE_OPEN + text + ANGLE_CLOSE;
	}

	public static String brackets(String text) {
		return BRACKET_OPEN + text + BRACKET_CLOSE;
	}

	public static String parenthesis(String text) {
		return PARENTHESIS_OPEN + text + PARENTHESIS_CLOSE;
	}
}
