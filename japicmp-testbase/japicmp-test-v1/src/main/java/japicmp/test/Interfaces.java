package japicmp.test;

public class Interfaces {

	public interface TestInterface {

	}

	public interface SecondTestInterface {

	}

	public static class InterfaceToNoInterfaceClass implements TestInterface {

	}

	public static class NoInterfaceToInterfaceClass {

	}

	public static class NoInterfaceRemainsNoInterfaceClass {

	}

	public static class InterfaceRemainsInterfaceClass implements TestInterface {

	}

	public static class InterfaceChangesClass implements TestInterface {

	}

	public interface InterfaceLosesMethod {
		void method();
	}

	public class ClassWithInterfaceLosesMethod implements InterfaceLosesMethod {

		@Override
		public void method() {

		}
	}

	public class SuperclassLosesMethod {

		public void method() {

		}
	}

	public class SubclassWithSuperclassLosesMethod extends SuperclassLosesMethod {

	}

	public static class NoInterfaceToSerializableInterface {

	}

	public interface MethodPulledToSuperInterfaceBase {

	}

	public interface MethodPulledToSuperInterfaceChild {
		void methodPulledUp();
	}

	public interface InterfaceWithFields {
		String UNCHANGED = "UNCHANGED";
		String REMOVED = "REMOVED";
	}

	public interface InterfaceAddMethod {

	}

	public static class ClassImplementsComparable {

	}
}
