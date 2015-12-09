package japicmp.filter;

import javassist.CtClass;

import java.util.List;

public class AnnotationClassFilter extends AnnotationFilterBase implements ClassFilter {

	public AnnotationClassFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtClass ctClass) {
		List attributes = ctClass.getClassFile().getAttributes();
		return hasAnnotation(attributes);
	}

	@Override
	public String toString() {
		return "Annotation filter: @" + annotationClassName;
	}
}
