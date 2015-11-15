package japicmp.filter;

import japicmp.exception.JApiCmpException;
import javassist.CtBehavior;
import javassist.CtClass;

public class AnnotationBehaviorFilter implements BehaviorFilter {
	private final Class<?> annotation;

	public AnnotationBehaviorFilter(String filterString) {
		String clazz = filterString.substring(1);
		try {
			annotation = Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			throw new JApiCmpException(JApiCmpException.Reason.ClassLoading, "Unable to load specified annotation filter: " + clazz, e);
		}
	}

	@Override
	public boolean matches(CtBehavior ctBehavior) {
		return ctBehavior.hasAnnotation(annotation);
	}
}
