package japicmp.util;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

import java.util.ArrayList;
import java.util.List;

public class CtFieldBuilder {
	public static final String DEFAULT_FIELD_NAME = "field";
	private CtClass type = CtClass.intType;
	private String name = DEFAULT_FIELD_NAME;
	private int modifier = Modifier.PUBLIC;
	private List<String> annotations = new ArrayList<>();

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
		for (String annotation : annotations) {
			ClassFile classFile = ctClass.getClassFile();
			ConstPool constPool = classFile.getConstPool();
			AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
			Annotation annot = new Annotation(annotation, constPool);
			attr.setAnnotation(annot);
			ctField.getFieldInfo().addAttribute(attr);
		}
		return ctField;
	}

	public static CtFieldBuilder create() {
		return new CtFieldBuilder();
	}

	public CtFieldBuilder withAnnotation(String annotation) {
		this.annotations.add(annotation);
		return this;
	}
}
