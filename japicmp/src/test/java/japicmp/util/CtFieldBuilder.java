package japicmp.util;

import javassist.*;

public class CtFieldBuilder {
	public static final String DEFAULT_FIELD_NAME = "field";
	private CtClass type = CtClass.intType;
	private String name = DEFAULT_FIELD_NAME;
	private int modifier = Modifier.PUBLIC;

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

	public CtField addToClass(CtClass ctClass) throws CannotCompileException {
		CtField ctField = new CtField(this.type, this.name, ctClass);
		ctField.setModifiers(this.modifier);
		ctClass.addField(ctField);
		return ctField;
	}

	public static CtFieldBuilder create() {
		return new CtFieldBuilder();
	}
}
