package japicmp.util;

import javassist.*;

import java.util.Arrays;

public class CtMethodBuilder {
	public static final String DEFAULT_METHOD_NAME = "method";
	private String name = DEFAULT_METHOD_NAME;
	private int modifier = 0;
	private CtClass returnType;
	private CtClass[] parameters = new CtClass[]{};
	private CtClass[] exceptions = new CtClass[]{};
	private String body = "return null;";

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

	public CtMethodBuilder parameter(CtClass parameter) {
		if (this.parameters == null) {
			this.parameters = new CtClass[]{parameter};
		} else {
			CtClass[] newParameters = new CtClass[this.parameters.length + 1];
			System.arraycopy(this.parameters, 0, newParameters, 0, this.parameters.length);
			newParameters[this.parameters.length] = parameter;
			this.parameters = newParameters;
		}
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

	public CtMethod addToClass(CtClass declaringClass) throws CannotCompileException {
		if (this.returnType == null) {
			this.returnType = declaringClass;
		}
		CtMethod ctMethod = CtNewMethod.make(this.modifier, this.returnType, this.name, this.parameters, this.exceptions, this.body, declaringClass);
		declaringClass.addMethod(ctMethod);
		return ctMethod;
	}

	public static CtMethodBuilder create() {
		CtMethodBuilder ctMethodBuilder = new CtMethodBuilder();
		return ctMethodBuilder;
	}
}
