package japicmp.filter;

import javassist.CtBehavior;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AnnotationBehaviorFilter extends AnnotationFilterBase implements BehaviorFilter {
	private static final Logger LOGGER = Logger.getLogger(AnnotationBehaviorFilter.class.getName());

	public AnnotationBehaviorFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtBehavior ctBehavior) {
		List attributes = ctBehavior.getMethodInfo().getAttributes();
		boolean hasAnnotation = hasAnnotation(attributes);
		if (!hasAnnotation) {
			CtClass declaringClass = ctBehavior.getDeclaringClass();
			hasAnnotation = hasAnnotation(declaringClass.getClassFile().getAttributes());
			if (!hasAnnotation) {
				try {
					declaringClass = declaringClass.getDeclaringClass();
					if (declaringClass != null) {
						hasAnnotation = hasAnnotation(declaringClass.getClassFile().getAttributes());
					}
				} catch (NotFoundException e) {
					LOGGER.log(Level.FINE, "Failed to load class '" + declaringClass.getName() + "': " + e.getLocalizedMessage(), e);
				}
			}
		}
		return hasAnnotation;
	}

	@Override
	public String toString() {
		return "@" + annotationClassName;
	}
}
