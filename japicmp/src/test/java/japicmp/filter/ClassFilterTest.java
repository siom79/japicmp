package japicmp.filter;

import japicmp.util.CtClassBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClassFilterTest {

	@Test
	public void testOneClassMatches() {
		JavaDocLikeClassFilter classFilter = new JavaDocLikeClassFilter("japicmp.Test");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
		assertThat(classFilter.matches(ctClass), is(true));
	}

	@Test
	public void testOneClassMatchesNot() {
		JavaDocLikeClassFilter classFilter = new JavaDocLikeClassFilter("japicmp.Homer");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(new ClassPool());
		assertThat(classFilter.matches(ctClass), is(false));
	}

	@Test
	public void testInnerClass() {
		JavaDocLikeClassFilter classFilter = new JavaDocLikeClassFilter("japicmp.Homer");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Homer$InnerHomer").addToClassPool(new ClassPool());
		assertThat(classFilter.matches(ctClass), is(true));
	}

	@Test
	public void testInnerClassAsFilter() {
		JavaDocLikeClassFilter classFilter = new JavaDocLikeClassFilter("japicmp.MyClass$InnerClass");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.MyClass$InnerClass").addToClassPool(new ClassPool());
		assertThat(classFilter.matches(ctClass), is(true));
	}
}
