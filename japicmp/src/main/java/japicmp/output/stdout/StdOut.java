package japicmp.output.stdout;

import java.util.List;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import japicmp.config.ImmutableOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.OutputGenerator;

public class StdOut extends OutputGenerator {

	public StdOut(ImmutableOptions options, ImmutableList<JApiClass> jApiClasses) {
		super(options, jApiClasses);
	}

	@Override
	public Optional<String> generate(ImmutableList<JApiClass> notUsed) {
		StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options);
		String output = stdoutOutputGenerator //
				.generate(options.getOldArchive(), options.getNewArchive(), jApiClasses);
		return Optional.of(output);
	}
}
