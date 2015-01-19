package japicmp.output.xml;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import japicmp.config.ImmutableOptions;
import japicmp.model.JApiClass;
import japicmp.output.OutputGenerator;

public class XmlOut extends OutputGenerator {
	public XmlOut(ImmutableOptions options, ImmutableList<JApiClass> jApiClasses) {
		super(options, jApiClasses);
	}

	@Override
	public Optional<String> generate(ImmutableList<JApiClass> notUsed) {
		XmlOutputGenerator xmlGenerator = new XmlOutputGenerator();
		xmlGenerator.generate(jApiClasses, options);
		return Optional.absent();
	}
}
