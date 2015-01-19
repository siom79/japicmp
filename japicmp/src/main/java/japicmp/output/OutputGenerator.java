package japicmp.output;

import com.google.common.collect.ImmutableList;
import japicmp.config.ImmutableOptions;
import japicmp.model.JApiClass;

public abstract class OutputGenerator implements Output {

	protected final ImmutableOptions options;
	protected final ImmutableList<JApiClass> jApiClasses;

	public OutputGenerator(ImmutableOptions options, ImmutableList<JApiClass> jApiClasses) {
		this.options = options;
		this.jApiClasses = jApiClasses;
	}
}
