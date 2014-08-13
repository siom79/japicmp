package japicmp.test;

import japicmp.test.inheritance.Kangaroo;
import japicmp.test.inheritance.Mammal;

import org.junit.Test;

public class InheritanceTest {

	@Test
	public void test() {
		Kangaroo kangaroo = new Kangaroo();
		Mammal mammal = (Mammal) kangaroo;
		mammal.lactate();
	}
}
