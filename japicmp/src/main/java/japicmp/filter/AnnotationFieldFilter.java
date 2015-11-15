package japicmp.filter;

import japicmp.exception.JApiCmpException;
import javassist.CtClass;
import javassist.CtField;

public class AnnotationFieldFilter implements FieldFilter {
	private final Class<?> annotation;

	public AnnotationFieldFilter(String filterString) {
		String clazz = filterString.substring(1);
		try {
			annotation = Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			throw new JApiCmpException(JApiCmpException.Reason.ClassLoading, "Unable to load specified annotation filter: " + clazz, e);
		}
	}

	@Override
	public boolean matches(CtField ctField) {
		return ctField.hasAnnotation(annotation);
	}

	@Override
	public boolean matches(CtClass ctClass) {
		return true;
	}
}
