package japicmp.test;

public class DefaultMethod {
    
    public interface IDefault {
    
    }
    
    public class CDefault implements IDefault {
    
    }
    
    public interface IAbstract {
    
    }
    
    public abstract class CAbstract implements IAbstract {
    
    }

	public static class DefaultInSubInterface {
		public interface IInterface {}

		public interface IInterfaceWithDefault extends IInterface {}

		public static abstract class CClass implements IInterfaceWithDefault {}
	}

	public static class UnrelatedDefaultInSubInterface {
		public interface IInterface {}

		public interface IInterfaceWithDefault extends IInterface {}

		public static abstract class CClass implements IInterfaceWithDefault {}
	}

	public static class DefaultInParentInterface {
		public interface IInterfaceWithDefault {}

		public interface IInterface extends IInterfaceWithDefault {}

		public static abstract class CClass implements IInterface {}
	}

	public static class DefaultInSubInterfaceAddedSuperclass {
		public interface IInterface {
			void foo();
		}

		public interface IInterfaceWithDefault extends IInterface {
			@Override
			default void foo() {}
		}

		public static abstract class CClass implements IInterfaceWithDefault {}
	}
}
