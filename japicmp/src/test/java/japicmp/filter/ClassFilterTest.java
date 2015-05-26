package japicmp.filter;

import japicmp.util.CtClassBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ClassFilterTest {

    @Test
    public void testOneClassMatches() {
        ClassFilter classFilter = new ClassFilter("japicmp.Test");
        CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
        assertThat(classFilter.matches(ctClass), is(true));
    }

    @Test
    public void testOneClassMatchesNot() {
        ClassFilter classFilter = new ClassFilter("japicmp.Homer");
        CtClass ctClass = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(new ClassPool());
        assertThat(classFilter.matches(ctClass), is(false));
    }
}