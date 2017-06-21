package japicmp.cmp;

import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ClassesComparatorTest {
	@Test
	public void testMethodAdded() throws Exception {
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(new JarArchiveComparatorOptions(), new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				return Collections.singletonList(createClassWithoutMethod(classPool));
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				return Collections.singletonList(createClassWithMethod(classPool));
			}
		});
		assertThat(jApiClasses.size(), is(1));
		assertThat(jApiClasses.get(0).getMethods().size(), is(1));
		assertThat(jApiClasses.get(0).getMethods().get(0).getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiClasses.get(0).getMethods().get(0).isBinaryCompatible(), is(true));
		assertThat(jApiClasses.get(0).isBinaryCompatible(), is(true));
		assertThat(jApiClasses.get(0).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
	}

	private CtClass createClassWithoutMethod(ClassPool classPool) {
		return new CtClassBuilder().name("japicmp.Test").addToClassPool(classPool);
	}

	private CtClass createClassWithMethod(ClassPool classPool) throws CannotCompileException {
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
		CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
		return ctClass;
	}
}
