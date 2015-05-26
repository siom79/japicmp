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
import static org.junit.Assert.*;

public class FieldFilterTest {

    @Test
    public void testOneFieldMatches() throws CannotCompileException {
        FieldFilter fieldFilter = new FieldFilter("japicmp.Test#field");
        CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
        CtField ctField = CtFieldBuilder.create().name("field").addToClass(ctClass);
        assertThat(fieldFilter.matches(ctField), is(true));
    }

    @Test
    public void testOneFieldMatchesNot() throws CannotCompileException {
        FieldFilter fieldFilter = new FieldFilter("japicmp.Test#field42");
        CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
        CtField ctField = CtFieldBuilder.create().name("field").addToClass(ctClass);
        assertThat(fieldFilter.matches(ctField), is(false));
    }

    @Test(expected = JApiCmpException.class)
    public void testTwoHashSigns() throws CannotCompileException {
        FieldFilter fieldFilter = new FieldFilter("japicmp.Test##field42");
        CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
        CtField ctField = CtFieldBuilder.create().name("field").addToClass(ctClass);
        assertThat(fieldFilter.matches(ctField), is(false));
    }
}