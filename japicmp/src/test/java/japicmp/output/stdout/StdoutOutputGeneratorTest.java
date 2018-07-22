package japicmp.output.stdout;

import japicmp.cli.CliParser;
import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.util.CtInterfaceBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;

public class StdoutOutputGeneratorTest {

	@Test
	public void testNoChanges() {
		Options options = Options.newDefault();
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, new ArrayList<JApiClass>(0));
		String generated = generator.generate();
		assertThat(generated, containsString(StdoutOutputGenerator.NO_CHANGES));
	}

	@Test
	public void testWarningWhenIgnoreMissingClasses() {
		Options options = Options.newDefault();
		options.setIgnoreMissingClasses(true);
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, new ArrayList<JApiClass>(0));
		String generated = generator.generate();
		assertThat(generated, containsString(StdoutOutputGenerator.WARNING));
		assertThat(generated, containsString(CliParser.IGNORE_MISSING_CLASSES));
	}

	@Test
	public void testNoClassFileFormatVersionIfInterfaceRemoved() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass myInterface = CtInterfaceBuilder.create().name("MyInterface").addToClassPool(classPool);
				return Collections.singletonList(myInterface);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				return Collections.emptyList();
			}
		});
		Options options = Options.newDefault();
		StdoutOutputGenerator generator = new StdoutOutputGenerator(options, jApiClasses);
		String generated = generator.generate();
		assertThat(generated, not(containsString("-1.-1")));
	}
}
