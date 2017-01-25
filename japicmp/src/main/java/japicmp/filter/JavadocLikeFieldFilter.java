package japicmp.filter;

import japicmp.exception.JApiCmpException;
import javassist.CtClass;
import javassist.CtField;

import java.util.regex.Pattern;

public class JavadocLikeFieldFilter implements FieldFilter {
	private final String filterString;
	private final Pattern patternClass;
	private final Pattern patternField;

	public JavadocLikeFieldFilter(String filterString) {
		this.filterString = filterString;
		String regEx = filterString.replace(".", "\\.");
		regEx = regEx.replace("*", ".*");
		regEx = regEx.replace("$", "\\$");
		String[] parts = regEx.split("#");
		if (parts.length != 2) {
			throw new JApiCmpException(JApiCmpException.Reason.CliError, "Filter option '" + filterString + "' contains more than one '#'.");
		}
		this.patternClass = Pattern.compile(parts[0]);
		this.patternField = Pattern.compile(parts[1]);
	}

	@Override
	public boolean matches(CtField ctField) {
		CtClass declaringClass = ctField.getDeclaringClass();
		String className = declaringClass.getName();
		if (!this.patternClass.matcher(className).matches()) {
			return false;
		}
		String fieldName = ctField.getName();
		return this.patternField.matcher(fieldName).matches();
	}

	@Override
	public String toString() {
		return this.filterString;
	}
}
