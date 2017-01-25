package japicmp.filter;

import javassist.CtClass;

import java.util.regex.Pattern;

public class JavaDocLikeClassFilter implements ClassFilter {
	private final Pattern pattern;
	private final String className;

	public JavaDocLikeClassFilter(String className) {
		int indexOfSharp = className.indexOf('#');
		if (indexOfSharp >= 0) {
			className = className.substring(0, indexOfSharp);
		}
		this.className = className;
		String regEx = className.replace(".", "\\.");
		regEx = regEx.replace("*", ".*");
		regEx = regEx.replace("$", "\\$");
		regEx += "(\\$.*)?";
		pattern = Pattern.compile(regEx);
	}

	@Override
	public String toString() {
		return this.className;
	}

	@Override
	public boolean matches(CtClass ctClass) {
		String name = ctClass.getName();
		return pattern.matcher(name).matches();
	}
}
