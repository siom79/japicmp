package japicmp.util;

import javassist.*;

public class CtFieldBuilder {
	public static final String DEFAULT_FIELD_NAME = "field";
	private CtClass declaringClass;
	private CtClass type = CtClass.intType;
	private String name = DEFAULT_FIELD_NAME;
	private int modifier = Modifier.PUBLIC;

	public CtFieldBuilder declaringClass(CtClass ctClass) {
		this.declaringClass = ctClass;
		return this;
	}

	public CtFieldBuilder type(CtClass ctClass) {
		this.type = ctClass;
		return this;
	}

	public CtFieldBuilder name(String name) {
		this.name = name;
		return this;
	}

	public CtFieldBuilder syntheticModifier() {
		this.modifier = this.modifier | ModifierHelper.ACC_SYNTHETIC;
		return this;
	}

	public CtField build(ClassPool classPool) throws CannotCompileException {
		if (this.declaringClass == null) {
			throw new IllegalStateException("Declaring class must not be null");
		}
		CtField ctField = new CtField(this.type, this.name, this.declaringClass);
		ctField.setModifiers(this.modifier);
		return ctField;
	}
}
