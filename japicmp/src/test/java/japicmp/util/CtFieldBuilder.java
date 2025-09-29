package japicmp.util;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.bytecode.AccessFlag;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

import java.util.HashMap;
import java.util.Map;

public class CtFieldBuilder {
	public static final String DEFAULT_FIELD_NAME = "field";
	private CtClass type = CtClass.intType;
	private String name = DEFAULT_FIELD_NAME;
	private int modifier = Modifier.PUBLIC;
	private final Map<String, CtElement[]> annotations = new HashMap<>();
	private Object constantValue = null;

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
		if (constantValue != null) {
			if (constantValue instanceof Boolean) {
				ctClass.addField(ctField, CtField.Initializer.constant((Boolean) constantValue));
			} else if (constantValue instanceof Integer) {
				ctClass.addField(ctField, CtField.Initializer.constant((Integer) constantValue));
			} else if (constantValue instanceof Long) {
				ctClass.addField(ctField, CtField.Initializer.constant((Long) constantValue));
			} else if (constantValue instanceof String) {
				ctClass.addField(ctField, CtField.Initializer.constant((String) constantValue));
			} else {
				throw new IllegalArgumentException("Provided constant value for field is of unsupported type: " + constantValue.getClass().getName());
			}
		} else {
			ctClass.addField(ctField);
		}
		for (String annotation : annotations.keySet()) {
			ClassFile classFile = ctClass.getClassFile();
			ConstPool constPool = classFile.getConstPool();
			AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
			Annotation annot = new Annotation(annotation, constPool);
			for (CtElement element : annotations.get(annotation)) {
				annot.addMemberValue(element.name, element.value.apply(constPool));
			}
			attr.setAnnotation(annot);
			ctField.getFieldInfo().addAttribute(attr);
		}
		return ctField;
	}

	public static CtFieldBuilder create() {
		return new CtFieldBuilder();
	}

	public CtFieldBuilder withAnnotation(String annotation, CtElement... elements) {
		this.annotations.put(annotation, elements);
		return this;
	}

	public CtFieldBuilder staticAccess() {
		this.modifier = this.modifier | Modifier.STATIC;
		return this;
	}

	public CtFieldBuilder privateAccess() {
		this.modifier = AccessFlag.setPrivate(this.modifier);
		return this;
	}

	public CtFieldBuilder packageProtectedAccess() {
		this.modifier = AccessFlag.setPackage(this.modifier);
		return this;
	}

	public CtFieldBuilder protectedAccess() {
		this.modifier = AccessFlag.setProtected(this.modifier);
		return this;
	}

	public CtFieldBuilder transientAccess() {
		this.modifier = this.modifier | Modifier.TRANSIENT;
		return this;
	}

	public CtFieldBuilder volatileAccess() {
		this.modifier = this.modifier | Modifier.VOLATILE;
		return this;
	}

	public CtFieldBuilder finalAccess() {
		this.modifier = this.modifier | Modifier.FINAL;
		return this;
	}

	public CtFieldBuilder withConstantValue(Object value) {
		this.constantValue = value;
		return this;
	}
}
