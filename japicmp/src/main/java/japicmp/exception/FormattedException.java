package japicmp.exception;

public class FormattedException {

	public static IllegalArgumentException ofIAE(String format, Object... args) {
		return new IllegalArgumentException(String.format(format, args));
	}
}
