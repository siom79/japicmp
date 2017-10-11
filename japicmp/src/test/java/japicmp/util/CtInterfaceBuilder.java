package japicmp.util;

import com.google.common.base.Optional;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;

public class CtInterfaceBuilder {
	private String name = "japicmp.Test";
	private Optional<CtClass> superInterfaceOptional = Optional.absent();

	public CtInterfaceBuilder name(String name) {
		this.name = name;
		return this;
	}

	public CtClass addToClassPool(ClassPool classPool) throws CannotCompileException {
		CtClass ctClass;
		if (superInterfaceOptional.isPresent()) {
			ctClass = classPool.makeInterface(this.name, superInterfaceOptional.get());
		} else {
			ctClass = classPool.makeInterface(this.name);
		}
		return ctClass;
	}

	public static CtInterfaceBuilder create() {
		return new CtInterfaceBuilder();
	}

	public CtInterfaceBuilder withSuperInterface(CtClass superInterface) {
		this.superInterfaceOptional = Optional.fromNullable(superInterface);
		return this;
	}
}
