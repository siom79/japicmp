package japicmp.filter;

import javassist.CtClass;

import java.util.regex.Pattern;

public class JavadocLikePackageFilter implements ClassFilter {
	private final Pattern pattern;
	private final String packageName;

	public JavadocLikePackageFilter(String packageName, boolean exclusive) {
		this.packageName = packageName;
		String regEx = packageName.replace(".", "\\.");
		regEx = regEx.replace("*", ".*");
		regEx += exclusive ? "" : "(\\.[^\\.]+)*";
		pattern = Pattern.compile(regEx);
	}

	@Override
	public String toString() {
		return this.packageName;
	}

	@Override
	public boolean matches(CtClass ctClass) {
		String name = ctClass.getPackageName();
		name = name == null ? "" : name;
		return pattern.matcher(name).matches();
	}
}
