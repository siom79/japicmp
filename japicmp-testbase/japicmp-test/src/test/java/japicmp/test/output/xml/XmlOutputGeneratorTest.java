package japicmp.test.output.xml;

import static japicmp.test.util.Helper.getArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.xml.XmlOutputGenerator;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Optional;

public class XmlOutputGeneratorTest {
    private static List<JApiClass> jApiClasses;

    @BeforeClass
    public static void beforeClass() {
        JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(new JarArchiveComparatorOptions());
        jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
    }

	@Test
	public void testHtmlOutput() {
		XmlOutputGenerator generator = new XmlOutputGenerator();
		Options options = new Options();
		options.setXmlOutputFile(Optional.of("target/diff.xml"));
		options.setHtmlOutputFile(Optional.of("target/diff.html"));
		generator.generate("/old/Path", "/new/Path", jApiClasses, options);
	}
}
