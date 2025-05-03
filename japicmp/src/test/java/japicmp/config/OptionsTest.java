package japicmp.config;

import japicmp.cmp.JApiCmpArchive;
import japicmp.exception.JApiCmpException;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtConstructorBuilder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static japicmp.util.JarUtil.createJarFile;

class OptionsTest {

	private static Path oldPath;
	private static Path newPath;

	@BeforeAll
	public static void beforeClass() throws IOException, CannotCompileException {
		ClassPool cp = new ClassPool(true);
		CtClass ctClassSuperclass = CtClassBuilder.create().name("NotExistingSuperclass").addToClassPool(cp);
		CtConstructorBuilder.create().addToClass(ctClassSuperclass);
		CtClass ctClass = CtClassBuilder.create().name("Test").withSuperclass(ctClassSuperclass).addToClassPool(cp);
		oldPath = Paths.get(System.getProperty("user.dir"), "target", OptionsTest.class.getSimpleName() + "_old.jar");
		createJarFile(oldPath, ctClass);
		newPath = Paths.get(System.getProperty("user.dir"), "target", OptionsTest.class.getSimpleName() + "_new.jar");
		createJarFile(newPath, ctClass);
	}

	@Test
	void testVerify() {
		// GIVEN
		Options options = Options.newDefault();
		options.getOldArchives().add(new JApiCmpArchive(oldPath.toFile(), "1.0.0"));
		options.getNewArchives().add(new JApiCmpArchive(newPath.toFile(), "2.0.0"));
		// WHEN
		options.verify();
		// THEN
		// -- no Exception
	}

	@Test
	void testVerifyNotExistingHtmlStylesheet() {
		// GIVEN
		Options options = Options.newDefault();
		options.getOldArchives().add(new JApiCmpArchive(oldPath.toFile(), "1.0.0"));
		options.getNewArchives().add(new JApiCmpArchive(newPath.toFile(), "2.0.0"));
		options.setHtmlStylesheet(Optional.of("none.css"));
		options.setHtmlOutputFile(Optional.of("test.html"));
		try {
			// WHEN
			options.verify();
			Assertions.fail();
		} catch (JApiCmpException e) {
			// THEN
			Assertions.assertEquals("HTML stylesheet 'none.css' does not exist.", e.getMessage());
		}
	}

	@Test
	void testVerifyCssFileWithoutHtmlOutput() {
		// GIVEN
		Options options = Options.newDefault();
		options.getOldArchives().add(new JApiCmpArchive(oldPath.toFile(), "1.0.0"));
		options.getNewArchives().add(new JApiCmpArchive(newPath.toFile(), "2.0.0"));
		options.setHtmlStylesheet(Optional.of("none.css"));
		try {
			// WHEN
			options.verify();
			Assertions.fail();
		} catch (JApiCmpException e) {
			// THEN
			Assertions.assertEquals("Define a HTML output file, if you want to apply a stylesheet.", e.getMessage());
		}
	}
}
