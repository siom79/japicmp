package japicmp.util;

import japicmp.util.Optional;

public class OptionalHelper {
	public static final String N_A = "n.a.";

	private OptionalHelper() {

	}

	public static <T> String optionalToString(Optional<T> optional) {
		if (optional.isPresent()) {
			return optional.get().toString();
		}
		return N_A;
	}
}
