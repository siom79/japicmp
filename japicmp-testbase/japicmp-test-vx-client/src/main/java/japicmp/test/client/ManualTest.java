package japicmp.test.client;

import japicmp.test.Interfaces;

public class ManualTest {

	public static void main(String[] args) {
		new Interfaces.ClassAbstractMethodToStatic().doSomething();
		new Interfaces.ClassDefaultMethodToStatic().doSomething();
	}
}
