package japicmp.filter;

import japicmp.cmp.JarArchiveComparator;
import japicmp.exception.JApiCmpException;
import javassist.*;

public class AnnotationFieldFilter extends AnnotationFilterBase implements FieldFilter {

	public AnnotationFieldFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtField ctField) {
		return ctField.hasAnnotation(resolveAnnotation(ctField.getDeclaringClass().getClassPool()));
	}
}
