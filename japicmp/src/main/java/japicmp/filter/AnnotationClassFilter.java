package japicmp.filter;

import javassist.CtClass;
import javassist.NotFoundException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationClassFilter extends AnnotationFilterBase implements ClassFilter {
	private static final Logger LOGGER = Logger.getLogger(AnnotationBehaviorFilter.class.getName());

	public AnnotationClassFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtClass ctClass) {
		boolean matches = ctClass.hasAnnotation(annotationClassName);
		if (!matches) {
            try {
				CtClass declaringClass = ctClass.getDeclaringClass();
				if (declaringClass != null) {
					matches = declaringClass.hasAnnotation(annotationClassName);
				}
            } catch (NotFoundException e) {
				LOGGER.log(Level.FINE, "Failed to load class '" + ctClass.getName() + "': " + e.getLocalizedMessage(), e);
            }
        }
		return matches;
	}

	@Override
	public String toString() {
		return "@" + annotationClassName;
	}
}
