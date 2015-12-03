package japicmp.filter;

import japicmp.cmp.JarArchiveComparator;
import japicmp.exception.JApiCmpException;
import javassist.CtBehavior;

public class AnnotationBehaviorFilter implements BehaviorFilter {
	private final Class<?> annotation;

	public AnnotationBehaviorFilter(String filterString) {
		String clazz = filterString.substring(1);
		try {
			annotation = Class.forName(clazz);
		} catch (ClassNotFoundException e) {
			throw JApiCmpException.forClassLoading(e, clazz);
		}
	}

	@Override
	public boolean matches(CtBehavior ctBehavior) {
		return ctBehavior.hasAnnotation(annotation);
	}
}
