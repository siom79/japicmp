package japicmp.util;

import java.util.Optional;

public class OptionalHelper {
	public static final String N_A = "n.a.";

	private OptionalHelper() {

	}

	public static <T> String optionalToString(Optional<T> optional) {
		return optional.map(Object::toString).orElse(N_A);
	}
}
