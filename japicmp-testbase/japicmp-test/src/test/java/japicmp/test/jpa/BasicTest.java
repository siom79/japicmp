package japicmp.test.jpa;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiClass;
import japicmp.output.extapi.jpa.JpaAnalyzer;
import japicmp.output.extapi.jpa.model.JpaTable;
import org.junit.Test;

import java.util.List;

import static japicmp.test.jpa.JpaHelper.getEntityByName;
import static japicmp.test.util.Helper.getArchive;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BasicTest {

	@Test
	public void basicTest() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		List<JApiClass> jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		JpaAnalyzer jpaAnalyzer = new JpaAnalyzer();
		List<JpaTable> jpaEntities = jpaAnalyzer.analyze(jApiClasses);
		assertThat(getEntityByName(jpaEntities, NewEntity.class.getCanonicalName()).getFullyQualifiedName(), is(NewEntity.class.getCanonicalName()));
	}
}
