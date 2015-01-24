package japicmp.test.output.xml;

import static japicmp.test.util.Helper.getArchive;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.config.PackageFilter;
import japicmp.model.JApiClass;
import japicmp.output.xml.XmlOutputGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.base.Optional;

public class XmlOutputGeneratorTest {
    private static List<JApiClass> jApiClasses;

    @BeforeClass
    public static void beforeClass() {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getPackagesExclude().add(new PackageFilter("japicmp.test.semver001"));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
        jApiClasses = jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
		generateHtmlOutput();
    }

	private static void generateHtmlOutput() {
		XmlOutputGenerator generator = new XmlOutputGenerator();
		Options options = new Options();
		options.setXmlOutputFile(Optional.of("target/diff.xml"));
		options.setHtmlOutputFile(Optional.of("target/diff.html"));
		generator.generate("/old/Path", "/new/Path", jApiClasses, options);
	}

	@Test
	public void testMetaInformationTable() throws IOException {
		File htmlFile = Paths.get(System.getProperty("user.dir"), "target", "diff.html").toFile();
		Document document = Jsoup.parse(htmlFile, Charset.forName("UTF-8").toString());
		assertThat(document.select("div.meta-information > table").isEmpty(), is(false));
		assertThat(document.select("div.meta-information > table > tbody > tr").size(), is(8));
	}
}
