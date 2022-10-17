package japicmp.test;

public class DefaultMethod {
    
    public interface IDefault {
        default void foo() {
            System.out.println("foo()");
        }
    }
    
    public class CDefault implements IDefault {
    
    }
    
    public interface IAbstract {
        void foo();
    }
    
    public abstract class CAbstract implements IAbstract {
    
    }

	public static class DefaultInSubInterface {
		public interface IInterface {
			void foo();
		}

		public interface IInterfaceWithDefault extends IInterface {
			@Override
			default void foo() {}
		}

		public abstract static class CClass implements IInterfaceWithDefault {}
	}

	public static class UnrelatedDefaultInSubInterface {
		public interface IInterface {
			void foo();
		}

		public interface IInterfaceWithDefault extends IInterface {
			default void bar() {}
		}

		public static abstract class CClass implements IInterfaceWithDefault {}
	}

	public static class DefaultInParentInterface {
		public interface IInterfaceWithDefault {
			default void foo() {}
		}

		public interface IInterface extends IInterfaceWithDefault {
			@Override
			void foo();
		}

		public abstract static class CClass implements IInterface {}
	}

	public static class DefaultInSubInterfaceAddedSuperclass {
		public interface IInterface {
			void foo();
		}

		public interface IInterfaceWithDefault extends IInterface {
			@Override
			default void foo() {}
		}

		public static class CAddedSuperClass implements IInterfaceWithDefault {}

		public abstract static class CClass extends CAddedSuperClass {}
	}
}
