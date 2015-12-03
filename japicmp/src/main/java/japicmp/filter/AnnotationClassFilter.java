package japicmp.filter;

import japicmp.cmp.JarArchiveComparator;
import japicmp.exception.JApiCmpException;
import javassist.CtClass;

public class AnnotationClassFilter implements ClassFilter {
	private final Class annotation;

	public AnnotationClassFilter(String filterString) {
		String clazz = filterString.substring(1);
		try {
			annotation = Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			throw JApiCmpException.forClassLoading(e, clazz);
		}
	}

	@Override
	public boolean matches(CtClass ctClass) {
		return ctClass.hasAnnotation(annotation);
	}

	@Override
	public String toString() {
		return "Annotation filter: @" + annotation.getName();
	}
}
