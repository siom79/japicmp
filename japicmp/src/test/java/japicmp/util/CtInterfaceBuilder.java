package japicmp.util;

import javassist.ClassPool;
import javassist.CtClass;

public class CtInterfaceBuilder {
	private String name = "japicmp.Test";

	public CtInterfaceBuilder name(String name) {
		this.name = name;
		return this;
	}

	public CtClass addToClassPool(ClassPool classPool) {
		CtClass ctClass = classPool.makeInterface(this.name);
		return ctClass;
	}

	public static CtInterfaceBuilder create() {
		return new CtInterfaceBuilder();
	}
}
