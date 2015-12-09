package japicmp.filter;

import javassist.CtField;

import java.util.List;

public class AnnotationFieldFilter extends AnnotationFilterBase implements FieldFilter {

	public AnnotationFieldFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtField ctField) {
		List attributes = ctField.getFieldInfo().getAttributes();
		return hasAnnotation(attributes);
	}

	@Override
	public String toString() {
		return "Annotation filter: @" + annotationClassName;
	}
}
