package japicmp.test.semver.finalpublicmethod;

public class ClassWithFinalPublicMethodInSuperClass {

	public static class SuperClass {
		public final void foo() {}
	}

	public static class SubClass extends SuperClass {	}
}
