package japicmp.cli;

import japicmp.config.Options;
import japicmp.util.CtClassBuilder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import static japicmp.util.JarUtil.createJarFile;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class CliParserTest {

	private static Path oldPath;
	private static Path newPath;

	@BeforeAll
	public static void beforeClass() throws IOException, CannotCompileException {
		ClassPool cp = new ClassPool(true);
		CtClass ctClass = CtClassBuilder.create().name("Test").addToClassPool(cp);
		oldPath = Paths.get(System.getProperty("user.dir"), "target", CliParserTest.class.getSimpleName() + "_old.jar");
		createJarFile(oldPath, ctClass);
		newPath = Paths.get(System.getProperty("user.dir"), "target", CliParserTest.class.getSimpleName() + "_new.jar");
		createJarFile(newPath, ctClass);
	}

	@Test
	void testReproducibleReportIsDisabledByDefault() {
		Options options = new CliParser().parse(new String[]{"-o", oldPath.toString(), "-n", newPath.toString()});
		assertThat(options.isReproducibleReport(), is(false));
	}

	@Test
	void testReproducibleReport() {
		Options options = new CliParser().parse(new String[]{"-o", oldPath.toString(), "-n", newPath.toString(), "--reproducible-report"});
		assertThat(options.isReproducibleReport(), is(true));
		assertThat(options.joinOldArchives(), is(CliParserTest.class.getSimpleName() + "_old.jar"));
		assertThat(options.joinNewArchives(), is(CliParserTest.class.getSimpleName() + "_new.jar"));
	}
}
