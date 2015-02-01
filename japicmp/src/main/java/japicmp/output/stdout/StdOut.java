package japicmp.output.stdout;

import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.OutputGenerator;

import java.util.List;

public class StdOut extends OutputGenerator {

	public StdOut(Options options, List<JApiClass> jApiClasses) {
		super(options, jApiClasses);
	}

	@Override
	public void generate() {
		StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options);
		String output = stdoutOutputGenerator //
				.generate(options.getOldArchive(), options.getNewArchive(), jApiClasses);
		System.out.println(output);
	}
}
