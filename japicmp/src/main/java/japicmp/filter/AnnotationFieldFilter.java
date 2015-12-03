package japicmp.filter;

import japicmp.cmp.JarArchiveComparator;
import japicmp.exception.JApiCmpException;
import javassist.CtField;

public class AnnotationFieldFilter implements FieldFilter {
	private final Class<?> annotation;

	public AnnotationFieldFilter(String filterString) {
		String clazz = filterString.substring(1);
		try {
			annotation = Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			throw JApiCmpException.forClassLoading(e, clazz);
		}
	}

	@Override
	public boolean matches(CtField ctField) {
		return ctField.hasAnnotation(annotation);
	}
}
