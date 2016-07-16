package japicmp.config;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Encapsulates all options regarding ignore missing classes.
 */
public class IgnoreMissingClasses {
	private boolean ignoreAllMissingClasses = false;
	private List<Pattern> ignoreMissingClassRegularExpression = new ArrayList<>();

	public boolean isIgnoreAllMissingClasses() {
		return ignoreAllMissingClasses;
	}

	public List<Pattern> getIgnoreMissingClassRegularExpression() {
		return ignoreMissingClassRegularExpression;
	}

	public void setIgnoreAllMissingClasses(boolean ignoreAllMissingClasses) {
		this.ignoreAllMissingClasses = ignoreAllMissingClasses;
	}

	public void setIgnoreMissingClassRegularExpression(List<Pattern> ignoreMissingClassRegularExpression) {
		this.ignoreMissingClassRegularExpression = ignoreMissingClassRegularExpression;
	}

	public boolean ignoreClass(String className) {
		if (this.ignoreAllMissingClasses) {
			return true;
		} else if (!this.ignoreMissingClassRegularExpression.isEmpty()) {
			for (Pattern pattern : this.ignoreMissingClassRegularExpression) {
				Matcher matcher = pattern.matcher(className);
				if (matcher.matches()) {
					return true;
				}
			}
		}
		return false;
	}
}
