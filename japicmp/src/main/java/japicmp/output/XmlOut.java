package japicmp.output;

import java.util.List;

import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.xml.XmlOutputGenerator;

public class XmlOut extends OutputGenerator {
	public XmlOut(Options options, List<JApiClass> jApiClasses) {
		super(options, jApiClasses);
	}

	@Override
	public void generate() {
		XmlOutputGenerator xmlGenerator = new XmlOutputGenerator();
		xmlGenerator.generate(options.getOldArchive().getAbsolutePath(),
				options.getNewArchive().getAbsolutePath(), jApiClasses, options);
	}
}
