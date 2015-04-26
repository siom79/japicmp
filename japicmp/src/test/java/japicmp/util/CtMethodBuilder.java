package japicmp.util;

import javassist.*;

public class CtMethodBuilder {
	public static final String DEFAULT_METHOD_NAME = "method";
	private String name = DEFAULT_METHOD_NAME;
	private int modifier = Modifier.PUBLIC;
	private CtClass returnType;
	private CtClass[] parameters = new CtClass[]{};
	private CtClass[] exceptions = new CtClass[]{};
	private String body = "return null;";
	private CtClass declaringClass;

	public CtMethodBuilder name(String name) {
		this.name = name;
		return this;
	}

	public CtMethodBuilder modifier(int modifier) {
		this.modifier = modifier;
		return this;
	}

	public CtMethodBuilder returnType(CtClass ctClass) {
		this.returnType = ctClass;
		return this;
	}

	public CtMethodBuilder parameters(CtClass[] parameters) {
		this.parameters = parameters;
		return this;
	}

	public CtMethodBuilder exceptions(CtClass[] exceptions) {
		this.exceptions = exceptions;
		return this;
	}

	public CtMethodBuilder body(String body) {
		this.body = body;
		return this;
	}

	public CtMethodBuilder declaringClass(CtClass ctClass) {
		this.declaringClass = ctClass;
		return this;
	}

	public CtMethodBuilder publicAccess() {
		this.modifier = this.modifier | Modifier.PUBLIC;
		return this;
	}

	public CtMethodBuilder privateAccess() {
		this.modifier = this.modifier | Modifier.PRIVATE;
		return this;
	}

	public CtMethodBuilder syntheticModifier() {
		this.modifier = this.modifier | ModifierHelper.ACC_SYNTHETIC;
		return this;
	}

	public CtMethod build(ClassPool classPool) throws CannotCompileException {
		if (this.declaringClass == null) {
			throw new IllegalStateException("Declaring class must not be null");
		}
		if (this.returnType == null) {
			this.returnType = this.declaringClass;
		}
		return CtNewMethod.make(this.modifier, this.returnType, this.name, this.parameters, this.exceptions, this.body, this.declaringClass);
	}
}
