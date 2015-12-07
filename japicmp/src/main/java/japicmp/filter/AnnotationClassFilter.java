package japicmp.filter;

import japicmp.cmp.JarArchiveComparator;
import japicmp.exception.JApiCmpException;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class AnnotationClassFilter extends AnnotationFilterBase implements ClassFilter {

	public AnnotationClassFilter(String filterString) {
		super(filterString.substring(1));
	}

	@Override
	public boolean matches(CtClass ctClass) {
		return ctClass.hasAnnotation(resolveAnnotation(ctClass.getClassPool()));
	}

	@Override
	public String toString() {
		return "Annotation filter: @" + annotationClassName;
	}
}
