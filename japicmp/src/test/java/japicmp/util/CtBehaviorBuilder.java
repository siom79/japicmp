package japicmp.util;

import javassist.CtClass;
import javassist.Modifier;
import javassist.bytecode.AccessFlag;

public abstract class CtBehaviorBuilder {
	protected int modifier = 0;
	protected CtClass[] parameters = new CtClass[]{};
	protected CtClass[] exceptions = new CtClass[]{};
	protected String signature = null;

	public CtBehaviorBuilder parameters(CtClass[] parameters) {
		this.parameters = parameters;
		return this;
	}

	public CtBehaviorBuilder parameter(CtClass parameter) {
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

	public CtBehaviorBuilder exceptions(CtClass[] exceptions) {
		this.exceptions = exceptions;
		return this;
	}

	public CtBehaviorBuilder publicAccess() {
		this.modifier = AccessFlag.setPublic(this.modifier);
		return this;
	}

	public CtBehaviorBuilder privateAccess() {
		this.modifier = AccessFlag.setPrivate(this.modifier);
		return this;
	}

	public CtBehaviorBuilder protectedAccess() {
		this.modifier = AccessFlag.setProtected(this.modifier);
		return this;
	}

	public CtBehaviorBuilder packageProtectedAccess() {
		this.modifier = AccessFlag.setPackage(this.modifier);
		return this;
	}

	public CtBehaviorBuilder staticAccess() {
		this.modifier = this.modifier | Modifier.STATIC;
		return this;
	}

	public CtBehaviorBuilder abstractMethod() {
		this.modifier = this.modifier | Modifier.ABSTRACT;
		return this;
	}

	public CtBehaviorBuilder finalMethod() {
		this.modifier = this.modifier | Modifier.FINAL;
		return this;
	}

	public CtBehaviorBuilder signature(String signature) {
		this.signature = signature;
		return this;
	}
}
