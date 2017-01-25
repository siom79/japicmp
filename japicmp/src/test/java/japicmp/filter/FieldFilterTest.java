package japicmp.filter;

import japicmp.exception.JApiCmpException;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtFieldBuilder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FieldFilterTest {

	@Test
	public void testOneFieldMatches() throws CannotCompileException {
		JavadocLikeFieldFilter fieldFilter = new JavadocLikeFieldFilter("japicmp.Test#field");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
		CtField ctField = CtFieldBuilder.create().name("field").addToClass(ctClass);
		assertThat(fieldFilter.matches(ctField), is(true));
	}

	@Test
	public void testOneFieldMatchesNot() throws CannotCompileException {
		JavadocLikeFieldFilter fieldFilter = new JavadocLikeFieldFilter("japicmp.Test#field42");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
		CtField ctField = CtFieldBuilder.create().name("field").addToClass(ctClass);
		assertThat(fieldFilter.matches(ctField), is(false));
	}

	@Test(expected = JApiCmpException.class)
	public void testTwoHashSigns() throws CannotCompileException {
		JavadocLikeFieldFilter fieldFilter = new JavadocLikeFieldFilter("japicmp.Test##field42");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
		CtField ctField = CtFieldBuilder.create().name("field").addToClass(ctClass);
		assertThat(fieldFilter.matches(ctField), is(false));
	}

	public void testFieldOfInnerClass() throws CannotCompileException {
		JavadocLikeFieldFilter fieldFilter = new JavadocLikeFieldFilter("japicmp.Test$InnerClass#field");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test$InnerClass").addToClassPool(new ClassPool());
		CtField ctField = CtFieldBuilder.create().name("field").addToClass(ctClass);
		assertThat(fieldFilter.matches(ctField), is(false));
	}
}
