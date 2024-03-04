package japicmp.util;

import com.google.common.base.Joiner;
import japicmp.filter.Filter;

import java.util.List;

public class StringHelper {

	private StringHelper() {
		// private constructor
	}

	public static String filtersAsString(List<Filter> filters, boolean include) {
		String join;
		if (filters.isEmpty()) {
			if (include) {
				join = "all";
			} else {
				join = "n.a.";
			}
		} else {
			join = Joiner.on(";").skipNulls().join(filters);
		}
		return join;
	}
}
