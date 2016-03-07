package japicmp.test;

import java.io.Serializable;

public class Interfaces {

	public interface TestInterface {

	}

	public interface SecondTestInterface {

	}

	public static class InterfaceToNoInterfaceClass {

	}

	public static class NoInterfaceToInterfaceClass implements TestInterface {

	}

	public static class NoInterfaceRemainsNoInterfaceClass {

	}

	public static class InterfaceRemainsInterfaceClass implements TestInterface {

	}

	public static class InterfaceChangesClass implements SecondTestInterface {

	}

	public interface InterfaceLosesMethod {

	}

	public class ClassWithInterfaceLosesMethod implements InterfaceLosesMethod {

		public void method() {

		}
	}

	public class SuperclassLosesMethod {

	}

	public class SubclassWithSuperclassLosesMethod extends SuperclassLosesMethod {

	}

	public class NewClassWithNewInterface implements TestInterface {

	}

	public static class NoInterfaceToSerializableInterface implements Serializable {

	}

	public interface MethodPulledToSuperInterfaceBase {
		void methodPulledUp();
	}

	public interface MethodPulledToSuperInterfaceChild extends MethodPulledToSuperInterfaceBase {

	}

	public interface InterfaceWithFields {
		String UNCHANGED = "UNCHANGED";
		String ADDED = "ADDED";
	}

	public interface InterfaceAddMethod {
		void methodAdded();
	}

	public static class ClassImplementsComparable implements Comparable<ClassImplementsComparable> {

		@Override
		public int compareTo(ClassImplementsComparable o) {
			return 0;
		}
	}
}
