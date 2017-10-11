package japicmp.test;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static japicmp.test.util.Helper.getArchive;
import static japicmp.test.util.Helper.getJApiClass;
import static japicmp.test.util.Helper.getJApiMethod;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LineNumberTest {
	private static List<JApiClass> jApiClasses;

	@BeforeClass
	public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(AccessModifier.PRIVATE);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	@Test
	public void testLineNumberDoesNotChange() {
		JApiClass jApiClass = getJApiClass(jApiClasses, LineNumbers.class.getName());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "lineNumberDoesNotChange");
		assertThat(jApiMethod.getOldLineNumber().isPresent(), is(true));
		assertThat(jApiMethod.getOldLineNumber().get(), is(7));
		assertThat(jApiMethod.geNewLineNumber().isPresent(), is(true));
		assertThat(jApiMethod.geNewLineNumber().get(), is(7));
	}

	@Test
	public void testLineNumberChanges() {
		JApiClass jApiClass = getJApiClass(jApiClasses, LineNumbers.class.getName());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "lineNumberChanges");
		assertThat(jApiMethod.getOldLineNumber().isPresent(), is(true));
		assertThat(jApiMethod.getOldLineNumber().get(), is(11));
		assertThat(jApiMethod.geNewLineNumber().isPresent(), is(true));
		assertThat(jApiMethod.geNewLineNumber().get(), is(12));
	}

	@Test
	public void testMethodRemoved() {
		JApiClass jApiClass = getJApiClass(jApiClasses, LineNumbers.class.getName());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodRemoved");
		assertThat(jApiMethod.getOldLineNumber().isPresent(), is(true));
		assertThat(jApiMethod.getOldLineNumber().get(), is(15));
		assertThat(jApiMethod.geNewLineNumber().isPresent(), is(false));
	}

	@Test
	public void testMethodAdded() {
		JApiClass jApiClass = getJApiClass(jApiClasses, LineNumbers.class.getName());
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodAdded");
		assertThat(jApiMethod.getOldLineNumber().isPresent(), is(false));
		assertThat(jApiMethod.geNewLineNumber().isPresent(), is(true));
		assertThat(jApiMethod.geNewLineNumber().get(), is(16));
	}
}
