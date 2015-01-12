package japicmp.output;

import java.util.List;

import japicmp.config.Options;
import japicmp.model.JApiClass;

public class SemverOut extends OutputGenerator {

	public SemverOut(Options options, List<JApiClass> jApiClasses) {
		super(options, jApiClasses);
	}

	@Override
	public void generate() {
		System.err.println("E: semver diff output is not implemented"); // TODO
	}
}
