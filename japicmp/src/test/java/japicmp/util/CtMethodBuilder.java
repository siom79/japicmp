package japicmp.util;

import javassist.*;

public class CtMethodBuilder extends CtBehaviorBuilder {
	public static final String DEFAULT_METHOD_NAME = "method";
    protected String body = "return null;";
    private String name = DEFAULT_METHOD_NAME;
    private CtClass returnType;

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

    public CtMethodBuilder syntheticModifier() {
		this.modifier = this.modifier | ModifierHelper.ACC_SYNTHETIC;
		return this;
	}

    public CtMethodBuilder parameters(CtClass[] parameters) {
        return (CtMethodBuilder) super.parameters(parameters);
    }

    public CtMethodBuilder parameter(CtClass parameter) {
        return (CtMethodBuilder) super.parameter(parameter);
    }

    public CtMethodBuilder exceptions(CtClass[] exceptions) {
        return (CtMethodBuilder) super.exceptions(exceptions);
    }

    public CtMethodBuilder body(String body) {
        this.body = body;
        return this;
    }

    public CtMethodBuilder publicAccess() {
        return (CtMethodBuilder) super.publicAccess();
    }

    public CtMethodBuilder privateAccess() {
        return (CtMethodBuilder) super.privateAccess();
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
