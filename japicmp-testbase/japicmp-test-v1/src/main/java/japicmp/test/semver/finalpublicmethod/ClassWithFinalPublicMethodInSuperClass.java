package japicmp.test.semver.finalpublicmethod;

public class ClassWithFinalPublicMethodInSuperClass {

	public static class SuperClass {}

	public static class SubClass extends SuperClass {
		public void foo() {}
	}
}
