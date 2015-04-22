package japicmp.cmp;

import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import javassist.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.*;

public class ClassesComparatorTest {

	@Test
	public void testMethodAdded() throws NotFoundException, CannotCompileException {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		ClassPool classPool = jarArchiveComparator.getClassPool();
		ClassesComparator classesComparator = new ClassesComparator(jarArchiveComparator, options);
		CtClass classWithoutMethod = createClassWithoutMethod(classPool);
		CtClass classWithMethod = createClassWithMethod(classPool);
		classesComparator.compare(Arrays.asList(classWithoutMethod), Arrays.asList(classWithMethod));
		List<JApiClass> jApiClasses = classesComparator.getClasses();
		assertThat(jApiClasses.size(), is(1));
		assertThat(jApiClasses.get(0).getMethods().size(), is(1));
		assertThat(jApiClasses.get(0).getMethods().get(0).getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiClasses.get(0).getMethods().get(0).isBinaryCompatible(), is(true));
		assertThat(jApiClasses.get(0).isBinaryCompatible(), is(true));
		assertThat(jApiClasses.get(0).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
	}

	private CtClass createClassWithoutMethod(ClassPool classPool) {
		return classPool.makeClass("japicmp.test.Testee");
	}

	private CtClass createClassWithMethod(ClassPool classPool) throws NotFoundException, CannotCompileException {
		CtClass ctClass = classPool.makeClass("japicmp.test.Testee");
		CtMethod ctMethod = CtNewMethod.make(Modifier.PUBLIC, classPool.get("java.lang.String"), "method", new CtClass[]{}, new CtClass[]{}, "return null;", ctClass);
		ctClass.addMethod(ctMethod);
		return ctClass;
	}
}