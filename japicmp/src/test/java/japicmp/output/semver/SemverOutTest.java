package japicmp.output.semver;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import japicmp.cmp.AnnotationsTest.Include;
import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtInterfaceBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;

public class SemverOutTest {

	@Test
	public void testNoChangesAtAll() throws Exception {
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(new JarArchiveComparatorOptions(),
				new ClassesHelper.ClassesGenerator() {
					@Override
					public List<CtClass> createOldClasses(final ClassPool classPool) throws Exception {
						return Collections.emptyList();
					}

					@Override
					public List<CtClass> createNewClasses(final ClassPool classPool) throws Exception {
						return Collections.emptyList();
					}
				});
		assertThat(jApiClasses.size(), is(0));
		Options options = Options.newDefault();
		SemverOut semverOut = new SemverOut(options, jApiClasses);
		String output = semverOut.generate();
		assertThat(output, is("0.0.0"));
	}

	@Test
	public void testAllowBinaryCompatibleElementsInPatchAddPublicMethodToClass() throws Exception {
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(new JarArchiveComparatorOptions(),
				new ClassesHelper.ClassesGenerator() {
					@Override
					public List<CtClass> createOldClasses(final ClassPool classPool) throws Exception {
						CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon")
								.addToClassPool(classPool);
						return Arrays.asList(ctClass1);
					}

					@Override
					public List<CtClass> createNewClasses(final ClassPool classPool) throws Exception {
						CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon")
								.addToClassPool(classPool);
						CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("excel")
								.withAnnotation(Include.class.getName()).addToClass(ctClass1);
						return Arrays.asList(ctClass1);
					}
				});
		Options options = Options.newDefault();
		SemverOut semverOut = new SemverOut(options, jApiClasses);
		String output = semverOut.generate();
		assertThat(output, is("0.1.0"));

		semverOut.setAllowBinaryCompatibleElementsInPatch(true);
		assertThat(semverOut.generate(), is("0.0.1"));
	}

	@Test
	public void testAllowBinaryCompatibleElementsInPatchAddPublicClass() throws Exception {
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(new JarArchiveComparatorOptions(),
				new ClassesHelper.ClassesGenerator() {
					@Override
					public List<CtClass> createOldClasses(final ClassPool classPool) throws Exception {
						CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon")
								.addToClassPool(classPool);
						return Arrays.asList(ctClass1);
					}

					@Override
					public List<CtClass> createNewClasses(final ClassPool classPool) throws Exception {
						CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon")
								.addToClassPool(classPool);
						CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard")
								.addToClassPool(classPool);
						return Arrays.asList(ctClass1, ctClass2);
					}
				});
		Options options = Options.newDefault();
		SemverOut semverOut = new SemverOut(options, jApiClasses);
		String output = semverOut.generate();
		assertThat(output, is("0.1.0"));

		semverOut.setAllowBinaryCompatibleElementsInPatch(true);
		assertThat(semverOut.generate(), is("0.0.1"));
	}

	@Test
	public void testAllowNewAbstractElementsInMinorAddAbstractMethod() throws Exception {
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(new JarArchiveComparatorOptions(),
				new ClassesHelper.ClassesGenerator() {
					@Override
					public List<CtClass> createOldClasses(final ClassPool classPool) throws Exception {
						CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon")
								.addToClassPool(classPool);
						return Arrays.asList(ctClass1);
					}

					@Override
					public List<CtClass> createNewClasses(final ClassPool classPool) throws Exception {
						CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon")
								.addToClassPool(classPool);
						CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).abstractMethod()
								.name("excel").addToClass(ctClass1);
						return Arrays.asList(ctClass1);
					}
				});
		Options options = Options.newDefault();
		SemverOut semverOut = new SemverOut(options, jApiClasses);
		String output = semverOut.generate();
		assertThat(output, is("1.0.0"));

		semverOut.setAllowNewAbstractElementsInMinor(true);
		assertThat(semverOut.generate(), is("0.1.0"));

	}

	@Test
	public void testAllowNewAbstractElementsInMinorAddMethodToInterface() throws Exception {
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(new JarArchiveComparatorOptions(),
				new ClassesHelper.ClassesGenerator() {
					@Override
					public List<CtClass> createOldClasses(final ClassPool classPool) throws Exception {
						CtClass ctClass1 = CtInterfaceBuilder.create().name("big.bang.theory.Sheldon")
								.addToClassPool(classPool);
						return Arrays.asList(ctClass1);
					}

					@Override
					public List<CtClass> createNewClasses(final ClassPool classPool) throws Exception {
						CtClass ctClass1 = CtInterfaceBuilder.create().name("big.bang.theory.Sheldon")
								.addToClassPool(classPool);
						CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("excel")
								.addToClass(ctClass1);
						return Arrays.asList(ctClass1);
					}
				});
		Options options = Options.newDefault();
		SemverOut semverOut = new SemverOut(options, jApiClasses);
		String output = semverOut.generate();
		assertThat(output, is("1.0.0"));

		semverOut.setAllowNewAbstractElementsInMinor(true);
		assertThat(semverOut.generate(), is("0.1.0"));

	}

}
