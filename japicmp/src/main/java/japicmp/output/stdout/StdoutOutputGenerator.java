package japicmp.output.stdout;

import java.io.File;
import java.util.List;

import com.google.common.base.Optional;
import japicmp.config.Options;
import japicmp.model.AbstractModifier;
import japicmp.model.AccessModifier;
import japicmp.model.FinalModifier;
import japicmp.model.JApiAnnotation;
import japicmp.model.JApiAnnotationElement;
import japicmp.model.JApiAnnotationElementValue;
import japicmp.model.JApiAnnotationElementValue.Type;
import japicmp.model.JApiBehavior;
import japicmp.model.JApiBinaryCompatibility;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiConstructor;
import japicmp.model.JApiField;
import japicmp.model.JApiHasAbstractModifier;
import japicmp.model.JApiHasAccessModifier;
import japicmp.model.JApiHasAnnotations;
import japicmp.model.JApiHasChangeStatus;
import japicmp.model.JApiHasFinalModifier;
import japicmp.model.JApiHasStaticModifier;
import japicmp.model.JApiImplementedInterface;
import japicmp.model.JApiMethod;
import japicmp.model.JApiModifier;
import japicmp.model.JApiParameter;
import japicmp.model.JApiReturnType;
import japicmp.model.JApiSuperclass;
import japicmp.model.JApiType;
import japicmp.model.StaticModifier;
import japicmp.output.OutputFilter;
import javassist.bytecode.annotation.MemberValue;

public class StdoutOutputGenerator {
	private final Options options;

	public StdoutOutputGenerator(Options options) {
		this.options = options;
	}

	public String generate(File oldArchive, File newArchive, List<JApiClass> jApiClasses) {
		OutputFilter outputFilter = new OutputFilter(options);
		outputFilter.filter(jApiClasses);
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("Comparing %s with %s:%n", oldArchive.getAbsolutePath(), newArchive.getAbsolutePath()));
		for (JApiClass jApiClass : jApiClasses) {
			processClass(sb, jApiClass);
			processConstructors(sb, jApiClass);
			processMethods(sb, jApiClass);
			processAnnotations(sb, jApiClass, 1);
		}
		return sb.toString();
	}

	private void processAnnotations(StringBuilder sb, JApiHasAnnotations jApiClass, int numberofTabs) {
		List<JApiAnnotation> annotations = jApiClass.getAnnotations();
		for (JApiAnnotation jApiAnnotation : annotations) {
			appendAnnotation(sb, signs(jApiAnnotation), jApiAnnotation, numberofTabs);
			List<JApiAnnotationElement> elements = jApiAnnotation.getElements();
			for (JApiAnnotationElement jApiAnnotationElement : elements) {
				appendAnnotationElement(sb, signs(jApiAnnotationElement), jApiAnnotationElement, numberofTabs + 1);
			}
		}
	}

	private void processConstructors(StringBuilder sb, JApiClass jApiClass) {
		List<JApiConstructor> constructors = jApiClass.getConstructors();
		for (JApiConstructor jApiConstructor : constructors) {
			appendMethod(sb, signs(jApiConstructor), jApiConstructor, "CONSTRUCTOR:");
			processAnnotations(sb, jApiConstructor, 2);
		}
	}

	private void processMethods(StringBuilder sb, JApiClass jApiClass) {
		List<JApiMethod> methods = jApiClass.getMethods();
		for (JApiMethod jApiMethod : methods) {
			appendMethod(sb, signs(jApiMethod), jApiMethod, "METHOD:");
			processAnnotations(sb, jApiMethod, 2);
		}
	}

	private void processClass(StringBuilder sb, JApiClass jApiClass) {
		appendClass(sb, signs(jApiClass), jApiClass);
	}

	private String signs(JApiHasChangeStatus hasChangeStatus) {
		JApiChangeStatus changeStatus = hasChangeStatus.getChangeStatus();
		String retVal = "???";
		switch (changeStatus) {
			case UNCHANGED:
				retVal = "===";
				break;
			case NEW:
				retVal = "+++";
				break;
			case REMOVED:
				retVal = "---";
				break;
			case MODIFIED:
				retVal = "***";
				break;
		}
		boolean binaryCompatible = true;
		if (hasChangeStatus instanceof JApiBinaryCompatibility) {
			JApiBinaryCompatibility binaryCompatibility = (JApiBinaryCompatibility) hasChangeStatus;
			binaryCompatible = binaryCompatibility.isBinaryCompatible();
		}
		if (binaryCompatible) {
			retVal += " ";
		} else {
			retVal += "!";
		}
		return retVal;
	}

	private void appendMethod(StringBuilder sb, String signs, JApiBehavior jApiBehavior, String classMemberType) {
		sb.append("\t").append(signs).append(" ") //
				.append(jApiBehavior.getChangeStatus()).append(" ") //
				.append(classMemberType).append(" ") //
				.append(accessModifierAsString(jApiBehavior)) //
				.append(abstractModifierAsString(jApiBehavior)) //
				.append(staticModifierAsString(jApiBehavior)) //
				.append(finalModifierAsString(jApiBehavior)) //
				.append(returnType(jApiBehavior)) //
				.append(jApiBehavior.getName()).append("(");
		int paramCount = 0;
		for (JApiParameter jApiParameter : jApiBehavior.getParameters()) {
			if (paramCount > 0) {
				sb.append(", ");
			}
			sb.append(jApiParameter.getType());
			paramCount++;
		}
		sb.append(")\n");
	}

	private String returnType(JApiBehavior jApiBehavior) {
		String returnTypeAsString = "";
		if (jApiBehavior instanceof JApiMethod) {
			JApiMethod method = (JApiMethod) jApiBehavior;
			JApiReturnType jApiReturnType = method.getReturnType();
			if (jApiReturnType.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
				returnTypeAsString = jApiReturnType.getNewReturnType() + " ";
			} else if (jApiReturnType.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				returnTypeAsString = jApiReturnType.getNewReturnType() + " (<-" + jApiReturnType.getOldReturnType() + ") ";
			} else if (jApiReturnType.getChangeStatus() == JApiChangeStatus.NEW) {
				returnTypeAsString = jApiReturnType.getNewReturnType() + " ";
			} else {
				returnTypeAsString = jApiReturnType.getOldReturnType() + " ";
			}
		}
		return returnTypeAsString;
	}

	private void appendAnnotation(StringBuilder sb, String signs, JApiAnnotation jApiAnnotation, int numberOfTabs) {
		sb.append(tabs(numberOfTabs)).append(signs).append(" ") //
				.append(jApiAnnotation.getChangeStatus()).append(" ANNOTATION: ") //
				.append(jApiAnnotation.getFullyQualifiedName()).append("\n");
	}

	private void appendAnnotationElement(StringBuilder sb, String signs, JApiAnnotationElement jApiAnnotationElement, int numberOfTabs) {
		sb.append(tabs(numberOfTabs)).append(signs).append(" ") //
				.append(jApiAnnotationElement.getChangeStatus()).append(" ELEMENT: ") //
				.append(jApiAnnotationElement.getName()).append("=");
		Optional<MemberValue> oldValue = jApiAnnotationElement.getOldValue();
		Optional<MemberValue> newValue = jApiAnnotationElement.getNewValue();
		if (oldValue.isPresent() && newValue.isPresent()) {
			if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
				sb.append(elementValueList2String(jApiAnnotationElement.getNewElementValues()));
			} else if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.REMOVED) {
				sb.append(elementValueList2String(jApiAnnotationElement.getOldElementValues())).append(" (-)");
			} else if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.NEW) {
				sb.append(elementValueList2String(jApiAnnotationElement.getNewElementValues())).append(" (+)");
			} else if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				sb.append(elementValueList2String(jApiAnnotationElement.getNewElementValues())) //
						.append(" (<- ") //
						.append(elementValueList2String(jApiAnnotationElement.getOldElementValues())) //
						.append(")");
			}
		} else if (!oldValue.isPresent() && newValue.isPresent()) {
			sb.append(elementValueList2String(jApiAnnotationElement.getNewElementValues())).append(" (+)");
		} else if (oldValue.isPresent()) {
			sb.append(elementValueList2String(jApiAnnotationElement.getOldElementValues())).append(" (-)");
		} else {
			sb.append(" n.a.");
		}
		sb.append("\n");
	}

	private String elementValueList2String(List<JApiAnnotationElementValue> values) {
		StringBuilder sb = new StringBuilder();
		for (JApiAnnotationElementValue value : values) {
			if (sb.length() > 0) {
				sb.append(",");
			}
			if (value.getName().isPresent()) {
				sb.append(value.getName().get()).append("=");
			}
			if (value.getType() != Type.Array && value.getType() != Type.Annotation) {
				if (value.getType() == Type.Enum) {
					sb.append(value.getFullyQualifiedName()).append(".").append(value.getValueString());
				} else {
					sb.append(value.getValueString());
				}
			} else {
				if (value.getType() == Type.Array) {
					sb.append("{").append(elementValueList2String(value.getValues())).append("}");
				} else {
					sb.append("@").append(value.getFullyQualifiedName()) //
							.append("(").append(elementValueList2String(value.getValues())).append(")");
				}
			}
		}
		return sb.toString();
	}

	private String tabs(int numberOfTabs) {
		if (numberOfTabs <= 0) {
			return "";
		} else if (numberOfTabs == 1) {
			return "\t";
		} else if (numberOfTabs == 2) {
			return "\t\t";
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < numberOfTabs; i++) {
				sb.append("\t");
			}
			return sb.toString();
		}
	}

	private void appendClass(StringBuilder sb, String signs, JApiClass jApiClass) {
		sb.append(signs).append(" ") //
				.append(jApiClass.getChangeStatus()).append(" ") //
				.append(jApiClass.getType()).append(": ") //
				.append(accessModifierAsString(jApiClass)) //
				.append(abstractModifierAsString(jApiClass)) //
				.append(staticModifierAsString(jApiClass)) //
				.append(finalModifierAsString(jApiClass)) //
				.append(jApiClass.getFullyQualifiedName()) //
				.append("\n");
		processInterfaceChanges(sb, jApiClass);
		processSuperclassChanges(sb, jApiClass);
		processFieldChanges(sb, jApiClass);
	}

	private void processFieldChanges(StringBuilder sb, JApiClass jApiClass) {
		List<JApiField> fields = jApiClass.getFields();
		for (JApiField field : fields) {
			sb.append(tabs(1)) //
					.append(signs(field)).append(" ").append(field.getChangeStatus()) //
					.append(" FIELD: ") //
					.append(accessModifierAsString(field)) //
					.append(staticModifierAsString(field)) //
					.append(finalModifierAsString(field)) //
					.append(fieldTypeChangeAsString(field)) //
					.append(field.getName()) //
					.append("\n");
			processAnnotations(sb, field, 2);
		}
	}

	private String abstractModifierAsString(JApiHasAbstractModifier hasAbstractModifier) {
		JApiModifier<AbstractModifier> modifier = hasAbstractModifier.getAbstractModifier();
		return modifierAsString(modifier, AbstractModifier.NON_ABSTRACT);
	}

	private String finalModifierAsString(JApiHasFinalModifier hasFinalModifier) {
		JApiModifier<FinalModifier> modifier = hasFinalModifier.getFinalModifier();
		return modifierAsString(modifier, FinalModifier.NON_FINAL);
	}

	private String staticModifierAsString(JApiHasStaticModifier hasStaticModifier) {
		JApiModifier<StaticModifier> modifier = hasStaticModifier.getStaticModifier();
		return modifierAsString(modifier, StaticModifier.NON_STATIC);
	}

	private String accessModifierAsString(JApiHasAccessModifier modifier) {
		JApiModifier<AccessModifier> accessModifier = modifier.getAccessModifier();
		return modifierAsString(accessModifier, AccessModifier.PACKAGE_PROTECTED);
	}

	private <T> String modifierAsString(JApiModifier<T> accessModifier, T notPrintValue) {
		if (accessModifier.getOldModifier().isPresent() && accessModifier.getNewModifier().isPresent()) {
			if (accessModifier.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				return accessModifier.getNewModifier().get() + " (<- " + accessModifier.getOldModifier().get() + ") ";
			} else if (accessModifier.getChangeStatus() == JApiChangeStatus.NEW) {
				if (accessModifier.getNewModifier().get() != notPrintValue) {
					return accessModifier.getNewModifier().get() + "(+) ";
				}
			} else if (accessModifier.getChangeStatus() == JApiChangeStatus.REMOVED) {
				if (accessModifier.getOldModifier().get() != notPrintValue) {
					return accessModifier.getOldModifier().get() + "(-) ";
				}
			} else {
				if (accessModifier.getNewModifier().get() != notPrintValue) {
					return accessModifier.getNewModifier().get() + " ";
				}
			}
		} else if (accessModifier.getOldModifier().isPresent()) {
			if (accessModifier.getOldModifier().get() != notPrintValue) {
				return accessModifier.getOldModifier().get() + "(-) ";
			}
		} else if (accessModifier.getNewModifier().isPresent()) {
			if (accessModifier.getNewModifier().get() != notPrintValue) {
				return accessModifier.getNewModifier().get() + "(+) ";
			}
		}
		return "";
	}

	private String fieldTypeChangeAsString(JApiField field) {
		JApiType type = field.getType();
		if (type.getOldTypeOptional().isPresent() && type.getNewTypeOptional().isPresent()) {
			if (type.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				return type.getNewTypeOptional().get() + " (<- " + type.getOldTypeOptional().get() + ") ";
			} else if (type.getChangeStatus() == JApiChangeStatus.NEW) {
				return type.getNewTypeOptional().get() + "(+) ";
			} else if (type.getChangeStatus() == JApiChangeStatus.REMOVED) {
				return type.getOldTypeOptional().get() + "(-) ";
			} else {
				return type.getNewTypeOptional().get() + " ";
			}
		} else if (type.getOldTypeOptional().isPresent()) {
			return type.getOldTypeOptional().get() + " ";
		} else if (type.getNewTypeOptional().isPresent()) {
			return type.getNewTypeOptional().get() + " ";
		}
		return "n.a.";
	}

	private void processSuperclassChanges(StringBuilder sb, JApiClass jApiClass) {
		JApiSuperclass jApiSuperclass = jApiClass.getSuperclass();
		if (options.isOutputOnlyModifications() && jApiSuperclass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
			sb.append(tabs(1)).append(signs(jApiSuperclass)).append(" ") //
					.append(jApiSuperclass.getChangeStatus()) //
					.append(" SUPERCLASS: ") //
					.append(superclassChangeAsString(jApiSuperclass)) //
					.append("\n");
		}
	}

	private String superclassChangeAsString(JApiSuperclass jApiSuperclass) {
		if (jApiSuperclass.getOldSuperclass().isPresent() && jApiSuperclass.getNewSuperclass().isPresent()) {
			return jApiSuperclass.getNewSuperclass().get() + " (<- " + jApiSuperclass.getOldSuperclass().get() + ")";
		} else if (jApiSuperclass.getOldSuperclass().isPresent()) {
			return jApiSuperclass.getOldSuperclass().get();
		} else if (jApiSuperclass.getNewSuperclass().isPresent()) {
			return jApiSuperclass.getNewSuperclass().get();
		}
		return "n.a.";
	}

	private void processInterfaceChanges(StringBuilder sb, JApiClass jApiClass) {
		List<JApiImplementedInterface> interfaces = jApiClass.getInterfaces();
		for (JApiImplementedInterface implementedInterface : interfaces) {
			sb.append(tabs(1)).append(signs(implementedInterface)).append(" ") //
					.append(implementedInterface.getChangeStatus()) //
					.append(" INTERFACE: ") //
					.append(implementedInterface.getFullyQualifiedName()) //
					.append("\n");
		}
	}
}
