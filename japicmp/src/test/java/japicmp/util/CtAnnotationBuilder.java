package japicmp.util;

import javassist.ClassPool;
import javassist.CtClass;

public class CtAnnotationBuilder {
	private String name = "japicmp.Test";

	public CtAnnotationBuilder name(String name) {
		this.name = name;
		return this;
	}

	public CtClass addToClassPool(ClassPool classPool) {
		return classPool.makeAnnotation(this.name);
	}

	public static CtAnnotationBuilder create() {
		return new CtAnnotationBuilder();
	}
}
