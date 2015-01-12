package japicmp.output;

import java.util.List;

import japicmp.config.Options;
import japicmp.model.JApiClass;

public abstract class OutputGenerator {

	protected final Options options;
	protected final List<JApiClass> jApiClasses;

	public OutputGenerator(Options options, List<JApiClass> jApiClasses) {
		this.options = options;
		this.jApiClasses = jApiClasses;
	}

	public abstract void generate();
}
