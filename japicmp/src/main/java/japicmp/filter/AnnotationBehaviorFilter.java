package japicmp.filter;

import japicmp.cmp.JarArchiveComparator;
import japicmp.exception.JApiCmpException;
import javassist.*;

public class AnnotationBehaviorFilter extends AnnotationFilterBase implements BehaviorFilter {

	public AnnotationBehaviorFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtBehavior ctBehavior) {
		return ctBehavior.hasAnnotation(resolveAnnotation(ctBehavior.getDeclaringClass().getClassPool()));
	}
}
