package japicmp.output.semver;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import javassist.ClassPool;
import javassist.CtClass;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;

class SemverOutTest {

	@Test
	void testNoChangesAtAll() throws Exception {
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(new JarArchiveComparatorOptions(), new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
				return Collections.emptyList();
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) {
				return Collections.emptyList();
			}
		});
		MatcherAssert.assertThat(jApiClasses.size(), is(0));
		Options options = Options.newDefault();
		SemverOut semverOut = new SemverOut(options, jApiClasses);
		String output = semverOut.generate();
		MatcherAssert.assertThat(output, is(SemverOut.SEMVER_COMPATIBLE));
	}
}
