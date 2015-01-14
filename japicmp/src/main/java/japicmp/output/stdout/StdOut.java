package japicmp.output.stdout;

import java.util.List;

import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.OutputGenerator;
import japicmp.output.stdout.StdoutOutputGenerator;

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
