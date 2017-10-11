package japicmp.cmp;

import japicmp.config.IgnoreMissingClasses;
import japicmp.exception.JApiCmpException;
import japicmp.model.JApiClass;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtConstructorBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import static japicmp.util.Helper.toJApiCmpArchive;
import static japicmp.util.JarUtil.createJarFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

public class IgnoreMissingClassesTest {

	@Test
	public void testNotFoundExceptionContainsClassName() {
		ClassPool cp = new ClassPool(true);
		String className = "not.existing.class";
		try {
			cp.get(className);
			fail("No exception thrown.");
		} catch (NotFoundException e) {
			assertThat(e.getMessage(), containsString(className));
		}
	}

	@Test
	public void testClassMissingWithoutIgnore() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		ClassPool classPool = jarArchiveComparator.getCommonClassPool();
		CtClass ctSuperclass = CtClassBuilder.create().name("SuperclassNotExisting").addToClassPool(classPool);
		CtConstructorBuilder.create().publicAccess().addToClass(ctSuperclass);
		CtClass ctClass = CtClassBuilder.create().withSuperclass(ctSuperclass).name("Test").addToClassPool(classPool);
		Path oldPath = Paths.get(System.getProperty("user.dir"), "target", IgnoreMissingClasses.class.getSimpleName() + "_old.jar");
		createJarFile(oldPath, ctClass);
		Path newPath = Paths.get(System.getProperty("user.dir"), "target", IgnoreMissingClasses.class.getSimpleName() + "_new.jar");
		createJarFile(newPath, ctClass);
		jarArchiveComparator = new JarArchiveComparator(options);
		try {
			jarArchiveComparator.compare(toJApiCmpArchive(oldPath.toFile()), toJApiCmpArchive(newPath.toFile()));
			fail("No exception thrown");
		} catch (Exception e) {
			JApiCmpException jApiCmpException = (JApiCmpException) e;
			assertThat(jApiCmpException.getReason(), is(JApiCmpException.Reason.ClassLoading));
		}
	}

	@Test
	public void testClassMissingWithIgnoreAllMissingClasses() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getIgnoreMissingClasses().setIgnoreAllMissingClasses(true);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		ClassPool classPool = jarArchiveComparator.getCommonClassPool();
		CtClass ctSuperclass = CtClassBuilder.create().name("SuperclassNotExisting").addToClassPool(classPool);
		CtConstructorBuilder.create().publicAccess().addToClass(ctSuperclass);
		CtClass ctClass = CtClassBuilder.create().withSuperclass(ctSuperclass).name("Test").addToClassPool(classPool);
		Path oldPath = Paths.get(System.getProperty("user.dir"), "target", IgnoreMissingClasses.class.getSimpleName() + "_old.jar");
		createJarFile(oldPath, ctClass);
		Path newPath = Paths.get(System.getProperty("user.dir"), "target", IgnoreMissingClasses.class.getSimpleName() + "_new.jar");
		createJarFile(newPath, ctClass);
		jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(toJApiCmpArchive(oldPath.toFile()), toJApiCmpArchive(newPath.toFile()));
		assertThat(jApiClasses.size(), is(1));
	}

	@Test
	public void testClassMissingWithIgnoreClassByRegex() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getIgnoreMissingClasses().setIgnoreMissingClassRegularExpression(Collections.singletonList(Pattern.compile(".*NotExisting")));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		ClassPool classPool = jarArchiveComparator.getCommonClassPool();
		CtClass ctSuperclass = CtClassBuilder.create().name("SuperclassNotExisting").addToClassPool(classPool);
		CtConstructorBuilder.create().publicAccess().addToClass(ctSuperclass);
		CtClass ctClass = CtClassBuilder.create().withSuperclass(ctSuperclass).name("Test").addToClassPool(classPool);
		Path oldPath = Paths.get(System.getProperty("user.dir"), "target", IgnoreMissingClasses.class.getSimpleName() + "_old.jar");
		createJarFile(oldPath, ctClass);
		Path newPath = Paths.get(System.getProperty("user.dir"), "target", IgnoreMissingClasses.class.getSimpleName() + "_new.jar");
		createJarFile(newPath, ctClass);
		jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(toJApiCmpArchive(oldPath.toFile()), toJApiCmpArchive(newPath.toFile()));
		assertThat(jApiClasses.size(), is(1));
	}

	@Test
	public void testClassMissingWithIgnoreByRegexThatDoesNotMatch() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getIgnoreMissingClasses().setIgnoreMissingClassRegularExpression(Collections.singletonList(Pattern.compile("WrongPattern")));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		ClassPool classPool = jarArchiveComparator.getCommonClassPool();
		CtClass ctSuperclass = CtClassBuilder.create().name("SuperclassNotExisting").addToClassPool(classPool);
		CtConstructorBuilder.create().publicAccess().addToClass(ctSuperclass);
		CtClass ctClass = CtClassBuilder.create().withSuperclass(ctSuperclass).name("Test").addToClassPool(classPool);
		Path oldPath = Paths.get(System.getProperty("user.dir"), "target", IgnoreMissingClasses.class.getSimpleName() + "_old.jar");
		createJarFile(oldPath, ctClass);
		Path newPath = Paths.get(System.getProperty("user.dir"), "target", IgnoreMissingClasses.class.getSimpleName() + "_new.jar");
		createJarFile(newPath, ctClass);
		jarArchiveComparator = new JarArchiveComparator(options);
		try {
			jarArchiveComparator.compare(toJApiCmpArchive(oldPath.toFile()), toJApiCmpArchive(newPath.toFile()));
			fail("No exception thrown");
		} catch (Exception e) {
			JApiCmpException jApiCmpException = (JApiCmpException) e;
			assertThat(jApiCmpException.getReason(), is(JApiCmpException.Reason.ClassLoading));
		}
	}


}
