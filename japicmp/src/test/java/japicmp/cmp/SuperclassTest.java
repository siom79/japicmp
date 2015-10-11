package japicmp.cmp;

import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.util.CtClassBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SuperclassTest {

	@Test
	public void testClassHasNoSuperclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = classPool.get("java.lang.Object");
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = classPool.get("java.lang.Object");
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "java.lang.Object");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
	}

	@Test
	public void testNewClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				return Collections.EMPTY_LIST;
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtClass ctClassSuper = CtClassBuilder.create().name("japicmp.Super").addToClassPool(classPool);
				ctClass.setSuperclass(ctClassSuper);
				return Arrays.asList(ctClass, ctClassSuper);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiClass.getSuperclass().getChangeStatus(), is(JApiChangeStatus.NEW));
		assertThat(jApiClass.getSuperclass().getOldSuperclass().isPresent(), is(false));
		assertThat(jApiClass.getSuperclass().getNewSuperclass().isPresent(), is(true));
		assertThat(jApiClass.getSuperclass().getNewSuperclass().get().getName(), is("japicmp.Super"));
	}

	@Test
	public void testRemovedClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtClass ctClassSuper = CtClassBuilder.create().name("japicmp.Super").addToClassPool(classPool);
				ctClass.setSuperclass(ctClassSuper);
				return Arrays.asList(ctClass, ctClassSuper);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				return Collections.EMPTY_LIST;
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiClass.getSuperclass().getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiClass.getSuperclass().getOldSuperclass().isPresent(), is(true));
		assertThat(jApiClass.getSuperclass().getNewSuperclass().isPresent(), is(false));
		assertThat(jApiClass.getSuperclass().getOldSuperclass().get().getName(), is("japicmp.Super"));
	}
}
