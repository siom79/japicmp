package japicmp.filter;

import javassist.*;
import javassist.bytecode.ClassFile;

import java.util.List;

public class AnnotationBehaviorFilter extends AnnotationFilterBase implements BehaviorFilter {

	public AnnotationBehaviorFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtBehavior ctBehavior) {
		List attributes = ctBehavior.getMethodInfo().getAttributes();
		return hasAnnotation(attributes);
	}

	@Override
	public String toString() {
		return "Annotation filter: @" + annotationClassName;
	}
}
