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
}
