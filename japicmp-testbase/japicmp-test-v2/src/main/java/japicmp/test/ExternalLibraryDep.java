package japicmp.test;

import org.apache.commons.math3.analysis.TrivariateFunction;
import org.apache.commons.math3.stat.inference.TTest;

public class ExternalLibraryDep {

	public static class NoSuperclassToExtendsTestCase extends TTest implements TrivariateFunction {
		@Override
		public double value(double v, double v1, double v2) {
			return 0;
		}
	}
}
