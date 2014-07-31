package japicmp.test;

public class Interfaces {
	
	public interface TestInterface {
		
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
}
