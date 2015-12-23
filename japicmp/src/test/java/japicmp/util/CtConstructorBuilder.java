package japicmp.util;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtNewConstructor;

public class CtConstructorBuilder extends CtBehaviorBuilder {
	protected String body = "System.out.println(\"a\");";

	public static CtConstructorBuilder create() {
		return new CtConstructorBuilder();
	}

	public CtConstructorBuilder modifier(int modifier) {
		this.modifier = modifier;
		return this;
	}

	public CtConstructorBuilder parameters(CtClass[] parameters) {
		return (CtConstructorBuilder) super.parameters(parameters);
	}

	public CtConstructorBuilder parameter(CtClass parameter) {
		return (CtConstructorBuilder) super.parameter(parameter);
	}

	public CtConstructorBuilder exceptions(CtClass[] exceptions) {
		return (CtConstructorBuilder) super.exceptions(exceptions);
	}

	public CtConstructorBuilder body(String body) {
		this.body = body;
		return this;
	}

	public CtConstructorBuilder publicAccess() {
		return (CtConstructorBuilder) super.publicAccess();
	}

	public CtConstructorBuilder privateAccess() {
		return (CtConstructorBuilder) super.privateAccess();
	}

	public CtConstructor addToClass(CtClass declaringClass) throws CannotCompileException {
		CtConstructor ctConstructor = CtNewConstructor.make(this.parameters, this.exceptions, this.body, declaringClass);
		declaringClass.addConstructor(ctConstructor);
		return ctConstructor;
	}
}
