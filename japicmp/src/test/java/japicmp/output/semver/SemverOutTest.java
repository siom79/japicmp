package japicmp.output.semver;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SemverOutTest {

	@Test
	public void testNoChangesAtAll() throws Exception {
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(new JarArchiveComparatorOptions(), new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				return Collections.emptyList();
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				return Collections.emptyList();
			}
		});
		assertThat(jApiClasses.size(), is(0));
		Options options = Options.newDefault();
		SemverOut semverOut = new SemverOut(options, jApiClasses);
		String output = semverOut.generate();
		assertThat(output, is("0.0.0"));
	}
}
