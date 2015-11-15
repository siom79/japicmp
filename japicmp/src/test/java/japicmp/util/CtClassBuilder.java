package japicmp.util;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;

import java.util.ArrayList;
import java.util.List;

public class CtClassBuilder {
	public static final String DEFAULT_CLASS_NAME = "japicmp.Test";
	private String name = DEFAULT_CLASS_NAME;
	private int modifier = Modifier.PUBLIC;
	private List<String> annotations = new ArrayList<>();

	public CtClassBuilder name(String name) {
		this.name = name;
		return this;
	}

	public CtClassBuilder syntheticModifier() {
		this.modifier = this.modifier | ModifierHelper.ACC_SYNTHETIC;
		return this;
	}

	public CtClassBuilder withAnnotation(String annotation) {
		this.annotations.add(annotation);
		return this;
	}

	public CtClass addToClassPool(ClassPool classPool) {
		CtClass ctClass = classPool.makeClass(name);
		ctClass.setModifiers(this.modifier);
		for (String annotation : annotations) {
			ClassFile classFile = ctClass.getClassFile();
			ConstPool constPool = classFile.getConstPool();
			AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
			Annotation annot = new Annotation(annotation, constPool);
			attr.setAnnotation(annot);
			ctClass.getClassFile2().addAttribute(attr);
		}
		return ctClass;
	}

	public static CtClassBuilder create() {
		return new CtClassBuilder();
	}
}
