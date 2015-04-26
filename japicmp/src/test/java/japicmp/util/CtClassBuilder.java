package japicmp.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;

public class CtClassBuilder {
	public static final String DEFAULT_CLASS_NAME = "japicmp.Test";
	private String name = DEFAULT_CLASS_NAME;
	private int modifier = Modifier.PUBLIC;

	public CtClassBuilder name(String name) {
		this.name = name;
		return this;
	}

	public CtClassBuilder syntheticModifier() {
		this.modifier = this.modifier | ModifierHelper.ACC_SYNTHETIC;
		return this;
	}

	public CtClass build(ClassPool classPool) {
		CtClass ctClass = classPool.makeClass(name);
		ctClass.setModifiers(this.modifier);
		return ctClass;
	}
}
