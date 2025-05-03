package japicmp.filter;

import japicmp.exception.JApiCmpException;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtFieldBuilder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.Is.is;

class FieldFilterTest {

	@Test
	void testOneFieldMatches() throws CannotCompileException {
		JavadocLikeFieldFilter fieldFilter = new JavadocLikeFieldFilter("japicmp.Test#field");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
		CtField ctField = CtFieldBuilder.create().name("field").addToClass(ctClass);
		MatcherAssert.assertThat(fieldFilter.matches(ctField), is(true));
	}

	@Test
	void testOneFieldMatchesNot() throws CannotCompileException {
		JavadocLikeFieldFilter fieldFilter = new JavadocLikeFieldFilter("japicmp.Test#field42");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
		CtField ctField = CtFieldBuilder.create().name("field").addToClass(ctClass);
		MatcherAssert.assertThat(fieldFilter.matches(ctField), is(false));
	}

	@Test
	void testTwoHashSigns() throws CannotCompileException {
		Assertions.assertThrows(JApiCmpException.class, () -> new JavadocLikeFieldFilter("japicmp.Test##field42"));
	}
}
