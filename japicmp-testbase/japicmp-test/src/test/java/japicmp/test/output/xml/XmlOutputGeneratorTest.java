package japicmp.test.output.xml;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import japicmp.config.ImmutableOptions;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.test.output.OutputTestHelper;
import org.junit.Test;

public class XmlOutputGeneratorTest {

	@Test
	public void testHtmlOutput() {
		// GIVEN
		OutputTestHelper.Config config = OutputTestHelper.newTestConfig();
		XmlOutputGenerator generator = new XmlOutputGenerator();
		String xmlOutputFileName = "target/diff.xml";
		String htmlOutputFileName = "target/diff.html";
		ImmutableOptions options = config.options() //
				.withXmlOutputFileName(xmlOutputFileName) //
				.withHtmlOutputFileName(htmlOutputFileName) //
				.build();

		// WHEN
		generator.generate("/old/Path", "/new/Path", config.classes(), options.copyToOptions());

		// THEN
		// TODO
		if (false) {
			assertEquals(toString("diff.html"), toString(new File(htmlOutputFileName)));
			assertEquals(toString("diff.xml"), toString(new File(xmlOutputFileName)));
		}
	}

	private String toString(String resourceName) {
		try {
			File file = new File(Resources.getResource(resourceName).toURI());
			return toString(file);
		} catch (URISyntaxException e) {
			throw new IllegalStateException(e);
		}
	}

	private String toString(File file) {
		try {
			return Files.toString(file, Charsets.UTF_8);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
