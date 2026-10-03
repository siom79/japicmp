package japicmp.output;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;

class OutputFilterTest {

	@Test
	void testClassModifiedOnlyByPrivateMethodIsFilteredWithOnlyModifications() throws Exception {
		List<JApiClass> jApiClasses = compareClassWithNewMethod(true);
		MatcherAssert.assertThat(getJApiClass(jApiClasses, CtClassBuilder.DEFAULT_CLASS_NAME).getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		Options options = Options.newDefault();
		options.setOutputOnlyModifications(true);
		new OutputFilter(options).filter(jApiClasses);
		MatcherAssert.assertThat(jApiClasses.size(), is(0));
	}

	@Test
	void testClassModifiedOnlyByPrivateMethodIsKeptWithAccessModifierPrivate() throws Exception {
		List<JApiClass> jApiClasses = compareClassWithNewMethod(true);
		Options options = Options.newDefault();
		options.setOutputOnlyModifications(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		new OutputFilter(options).filter(jApiClasses);
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		MatcherAssert.assertThat(jApiClasses.get(0).getMethods().size(), is(1));
	}

	@Test
	void testClassModifiedOnlyByPrivateMethodIsKeptWithoutOnlyModifications() throws Exception {
		List<JApiClass> jApiClasses = compareClassWithNewMethod(true);
		Options options = Options.newDefault();
		new OutputFilter(options).filter(jApiClasses);
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		MatcherAssert.assertThat(jApiClasses.get(0).getMethods().size(), is(0));
	}

	@Test
	void testClassModifiedByPublicMethodIsKeptWithOnlyModifications() throws Exception {
		List<JApiClass> jApiClasses = compareClassWithNewMethod(false);
		Options options = Options.newDefault();
		options.setOutputOnlyModifications(true);
		new OutputFilter(options).filter(jApiClasses);
		MatcherAssert.assertThat(jApiClasses.size(), is(1));
		MatcherAssert.assertThat(jApiClasses.get(0).getMethods().size(), is(1));
	}

	private List<JApiClass> compareClassWithNewMethod(final boolean privateMethod) throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		//compare with access modifier private, otherwise the class is not MODIFIED by a private method
		options.setAccessModifier(AccessModifier.PRIVATE);
		return ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
				CtClass ctClass = new CtClassBuilder().addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = new CtClassBuilder().addToClassPool(classPool);
				CtMethodBuilder builder = CtMethodBuilder.create();
				if (privateMethod) {
					builder.privateAccess();
				} else {
					builder.publicAccess();
				}
				builder.addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
	}
}
