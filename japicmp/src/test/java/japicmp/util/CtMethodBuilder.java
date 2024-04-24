package japicmp.util;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.SignatureAttribute;
import javassist.bytecode.annotation.Annotation;

import java.util.HashMap;
import java.util.Map;

public class CtMethodBuilder extends CtBehaviorBuilder {
	private static final String DEFAULT_METHOD_NAME = "method";
	private String body = "return null;";
	private String name = DEFAULT_METHOD_NAME;
	private CtClass returnType;
	private final Map<String, CtElement[]> annotations = new HashMap<>();

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

	public CtMethodBuilder protectedAccess() {
		return (CtMethodBuilder) super.protectedAccess();
	}

	public CtMethodBuilder staticAccess() {
		return (CtMethodBuilder) super.staticAccess();
	}

	public CtMethodBuilder abstractMethod() {
		return (CtMethodBuilder) super.abstractMethod();
	}

	public CtMethodBuilder finalMethod() {
		return (CtMethodBuilder) super.finalMethod();
	}

	public CtMethodBuilder withAnnotation(String annotation, CtElement... elements) {
		this.annotations.put(annotation, elements);
		return this;
	}

	public CtMethodBuilder signature(String signature) {
		super.signature(signature);
		return this;
	}

	public CtMethod addToClass(CtClass declaringClass) throws CannotCompileException {
		if (this.returnType == null) {
			this.returnType = CtClass.voidType;
		}
		CtMethod ctMethod = CtNewMethod.make(this.modifier, this.returnType, this.name, this.parameters, this.exceptions, this.body, declaringClass);
		ctMethod.setModifiers(this.modifier);
		declaringClass.addMethod(ctMethod);
		for (String annotation : annotations.keySet()) {
            ConstPool constPool = declaringClass.getClassFile().getConstPool();
			AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
			Annotation annot = new Annotation(annotation, constPool);
			for (CtElement element : annotations.get(annotation)) {
				annot.addMemberValue(element.name, element.value.apply(constPool));
			}
			attr.setAnnotation(annot);
			ctMethod.getMethodInfo().addAttribute(attr);
		}
		if (signature != null) {
			SignatureAttribute signatureAttribute = new SignatureAttribute(declaringClass.getClassFile().getConstPool(), signature);
			ctMethod.getMethodInfo().addAttribute(signatureAttribute);
		}
		return ctMethod;
	}

	public static CtMethodBuilder create() {
		return new CtMethodBuilder();
	}
}
