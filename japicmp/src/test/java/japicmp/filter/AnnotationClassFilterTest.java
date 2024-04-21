package japicmp.filter;

import japicmp.util.CtClassBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class AnnotationClassFilterTest {

	@Test
    public void testMatchesAnnotation() {
		AnnotationClassFilter annotationClassFilter = new AnnotationClassFilter("@japicmp.Exclude");
		CtClass ctClass = CtClassBuilder.create()
			.name("japicmp.ExcludeMe")
			.withAnnotation("japicmp.Exclude")
			.addToClassPool(new ClassPool());

		boolean matches = annotationClassFilter.matches(ctClass);

		assertThat(matches, is(true));
	}

	@Test
	public void testMatchesNotAnnotation() {
		AnnotationClassFilter annotationClassFilter = new AnnotationClassFilter("@japicmp.Exclude");
		CtClass ctClass = CtClassBuilder.create()
			.name("japicmp.ExcludeMeNot")
			.addToClassPool(new ClassPool());
		ctClass.defrost();

		boolean matches = annotationClassFilter.matches(ctClass);

		assertThat(matches, is(false));
	}
}
