package japicmp.filter;

import japicmp.util.CtClassBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;

class ClassFilterTest {

	@Test
	void testOneClassMatches() {
		JavaDocLikeClassFilter classFilter = new JavaDocLikeClassFilter("japicmp.Test");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
		MatcherAssert.assertThat(classFilter.matches(ctClass), is(true));
	}

	@Test
	void testOneClassMatchesNot() {
		JavaDocLikeClassFilter classFilter = new JavaDocLikeClassFilter("japicmp.Homer");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(new ClassPool());
		MatcherAssert.assertThat(classFilter.matches(ctClass), is(false));
	}

	@Test
	void testInnerClass() {
		JavaDocLikeClassFilter classFilter = new JavaDocLikeClassFilter("japicmp.Homer");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Homer$InnerHomer").addToClassPool(new ClassPool());
		MatcherAssert.assertThat(classFilter.matches(ctClass), is(true));
	}

	@Test
	void testInnerClassAsFilter() {
		JavaDocLikeClassFilter classFilter = new JavaDocLikeClassFilter("japicmp.MyClass$InnerClass");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.MyClass$InnerClass").addToClassPool(new ClassPool());
		MatcherAssert.assertThat(classFilter.matches(ctClass), is(true));
	}
}
