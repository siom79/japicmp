package japicmp.cmp;

import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChangeType;
import japicmp.model.JApiMethod;
import japicmp.model.JApiSemanticVersionLevel;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static japicmp.util.Helper.getJApiMethod;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

class JarArchiveComparatorTest {

	@Test
	void testOverrideCompatibilityChangeClassRemoved() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.addOverrideCompatibilityChange(new JarArchiveComparatorOptions.OverrideCompatibilityChange(JApiCompatibilityChangeType.CLASS_REMOVED, true, true, JApiSemanticVersionLevel.MAJOR));
		options.addOverrideCompatibilityChange(new JarArchiveComparatorOptions.OverrideCompatibilityChange(JApiCompatibilityChangeType.SUPERCLASS_REMOVED, true, true, JApiSemanticVersionLevel.MAJOR));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				return Collections.emptyList();
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	void testOverrideCompatibilityChangeMethodRemoved() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.addOverrideCompatibilityChange(new JarArchiveComparatorOptions.OverrideCompatibilityChange(JApiCompatibilityChangeType.METHOD_REMOVED, true, true, JApiSemanticVersionLevel.MAJOR));
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
	}
}
