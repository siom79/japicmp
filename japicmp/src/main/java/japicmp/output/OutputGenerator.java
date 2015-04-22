package japicmp.output;

import japicmp.config.Options;
import japicmp.model.JApiClass;

import java.util.List;

public abstract class OutputGenerator<T> {
	protected final Options options;
	protected final List<JApiClass> jApiClasses;

	public OutputGenerator(Options options, List<JApiClass> jApiClasses) {
		this.options = options;
		this.jApiClasses = jApiClasses;
	}

	public abstract T generate();
}
