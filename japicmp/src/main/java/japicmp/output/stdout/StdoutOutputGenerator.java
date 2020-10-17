package japicmp.output.stdout;

import japicmp.cli.CliParser;
import japicmp.config.Options;
import japicmp.model.*;
import japicmp.model.JApiAnnotationElementValue.Type;
import japicmp.output.OutputFilter;
import japicmp.output.OutputGenerator;
import japicmp.util.Optional;
import javassist.bytecode.annotation.MemberValue;

import java.util.List;

public class StdoutOutputGenerator extends OutputGenerator<String> {
	static final String NO_CHANGES = "No changes.";
	static final String WARNING = "WARNING";

	public StdoutOutputGenerator(Options options, List<JApiClass> jApiClasses) {
		super(options, jApiClasses);
	}

	@Override
	public String generate() {
		OutputFilter outputFilter = new OutputFilter(options);
		outputFilter.filter(jApiClasses);
		StringBuilder sb = new StringBuilder();
		sb.append(options.getDifferenceDescription()).append('\n');
		if (options.getIgnoreMissingClasses().isIgnoreAllMissingClasses()) {
			sb.append(WARNING).append(": You are using the option '").append(CliParser.IGNORE_MISSING_CLASSES)
				.append("', i.e. superclasses and interfaces that could not be found on the classpath are ignored.")
				.append(" Hence changes caused by these superclasses and interfaces are not reflected in the output.\n");
		} else if (options.getIgnoreMissingClasses().getIgnoreMissingClassRegularExpression().size() > 0) {
			sb.append(WARNING).append(": You have ignored certain classes, i.e. superclasses and interfaces that could not ")
				.append("be found on the classpath are ignored. Hence changes caused by these superclasses and interfaces are not reflected in the output.\n");
		}
		if (jApiClasses.size() > 0) {
			for (JApiClass jApiClass : jApiClasses) {
				processClass(sb, jApiClass);
				processConstructors(sb, jApiClass);
				processMethods(sb, jApiClass);
				processAnnotations(sb, jApiClass, 1);
			}
		} else {
			sb.append(NO_CHANGES);
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
			processExceptions(sb, jApiConstructor, 2);
		}
	}

	private void processMethods(StringBuilder sb, JApiClass jApiClass) {
		List<JApiMethod> methods = jApiClass.getMethods();
		for (JApiMethod jApiMethod : methods) {
			appendMethod(sb, signs(jApiMethod), jApiMethod, "METHOD:");
			processAnnotations(sb, jApiMethod, 2);
			processExceptions(sb, jApiMethod, 2);
		}
	}

	private void processExceptions(StringBuilder sb, JApiBehavior jApiBehavior, int indent) {
		for (JApiException exception : jApiBehavior.getExceptions()) {
			appendException(sb, signs(exception), exception, indent);
		}
	}

	private void appendException(StringBuilder sb, String signs, JApiException jApiException, int indent) {
		sb.append(tabs(indent)).append(signs).append(" ").append(jApiException.getChangeStatus()).append(" EXCEPTION: ").append(jApiException.getName()).append("\n");
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
		boolean sourceCompatible = true;
		if (hasChangeStatus instanceof JApiCompatibility) {
			JApiCompatibility jApiCompatibility = (JApiCompatibility) hasChangeStatus;
			binaryCompatible = jApiCompatibility.isBinaryCompatible();
			sourceCompatible = jApiCompatibility.isSourceCompatible();
		}
		if (binaryCompatible) {
			if (sourceCompatible) {
				retVal += " ";
			} else {
				retVal += "*";
			}
		} else {
			retVal += "!";
		}
		return retVal;
	}

	private void appendMethod(StringBuilder sb, String signs, JApiBehavior jApiBehavior, String classMemberType) {
		sb.append("\t").append(signs).append(" ").append(jApiBehavior.getChangeStatus()).append(" ").append(classMemberType).append(" ")
			.append(accessModifierAsString(jApiBehavior)).append(abstractModifierAsString(jApiBehavior)).append(staticModifierAsString(jApiBehavior))
			.append(finalModifierAsString(jApiBehavior)).append(syntheticModifierAsString(jApiBehavior)).append(bridgeModifierAsString(jApiBehavior))
			.append(returnType(jApiBehavior)).append(jApiBehavior.getName()).append("(");
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
		sb.append(String.format("%s%s %s ANNOTATION: %s\n", tabs(numberOfTabs), signs, jApiAnnotation.getChangeStatus(), jApiAnnotation.getFullyQualifiedName()));
	}

	private void appendAnnotationElement(StringBuilder sb, String signs, JApiAnnotationElement jApiAnnotationElement, int numberOfTabs) {
		sb.append(String.format("%s%s %s ELEMENT: %s=", tabs(numberOfTabs), signs, jApiAnnotationElement.getChangeStatus(), jApiAnnotationElement.getName()));
		Optional<MemberValue> oldValue = jApiAnnotationElement.getOldValue();
		Optional<MemberValue> newValue = jApiAnnotationElement.getNewValue();
		if (oldValue.isPresent() && newValue.isPresent()) {
			if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
				sb.append(elementValueList2String(jApiAnnotationElement.getNewElementValues()));
			} else if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.REMOVED) {
				sb.append(String.format("%s (-)", elementValueList2String(jApiAnnotationElement.getOldElementValues())));
			} else if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.NEW) {
				sb.append(String.format("%s (+)", elementValueList2String(jApiAnnotationElement.getNewElementValues())));
			} else if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				sb.append(String.format("%s (<- %s)", elementValueList2String(jApiAnnotationElement.getNewElementValues()), elementValueList2String(jApiAnnotationElement.getOldElementValues())));
			}
		} else if (!oldValue.isPresent() && newValue.isPresent()) {
			sb.append(String.format("%s (+)", elementValueList2String(jApiAnnotationElement.getNewElementValues())));
		} else if (oldValue.isPresent() && !newValue.isPresent()) {
			sb.append(String.format("%s (-)", elementValueList2String(jApiAnnotationElement.getOldElementValues())));
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
				} else if (value.getType() == Type.Annotation) {
					sb.append("@").append(value.getFullyQualifiedName()).append("(").append(elementValueList2String(value.getValues())).append(")");
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
		sb.append(signs).append(" ").append(jApiClass.getChangeStatus()).append(" ").append(processClassType(jApiClass)).append(": ").append(accessModifierAsString(jApiClass)).append(abstractModifierAsString(jApiClass)).append(staticModifierAsString(jApiClass)).append(finalModifierAsString(jApiClass)).append(syntheticModifierAsString(jApiClass)).append(jApiClass.getFullyQualifiedName()).append(" ").append(javaObjectSerializationStatus(jApiClass)).append("\n");
		processClassFileFormatVersionChanges(sb, jApiClass);
		processInterfaceChanges(sb, jApiClass);
		processSuperclassChanges(sb, jApiClass);
		processFieldChanges(sb, jApiClass);
	}

	private void processClassFileFormatVersionChanges(StringBuilder sb, JApiClass jApiClass) {
		JApiClassFileFormatVersion classFileFormatVersion = jApiClass.getClassFileFormatVersion();
		sb.append(tabs(1))
			.append(signs(classFileFormatVersion))
			.append(" CLASS FILE FORMAT VERSION: ");
		if (classFileFormatVersion.getMajorVersionNew() != -1 && classFileFormatVersion.getMinorVersionNew() != -1) {
			sb.append(classFileFormatVersion.getMajorVersionNew()).append(".").append(classFileFormatVersion.getMinorVersionNew());
		} else {
			sb.append("n.a.");
		}
		sb.append(" <- ");
		if (classFileFormatVersion.getMajorVersionOld() != -1 && classFileFormatVersion.getMinorVersionOld() != -1) {
			sb.append(classFileFormatVersion.getMajorVersionOld()).append(".").append(classFileFormatVersion.getMinorVersionOld());
		} else {
			sb.append("n.a.");
		}
		sb.append("\n");
	}

	private String processClassType(JApiClass jApiClass) {
		JApiClassType classType = jApiClass.getClassType();
		switch (classType.getChangeStatus()) {
			case NEW:
				return classType.getNewType();
			case REMOVED:
				return classType.getOldType();
			case MODIFIED:
				return classType.getNewType() + " (<- " + classType.getOldType() + ") ";
			case UNCHANGED:
				return classType.getOldType();
		}
		return "n.a.";
	}

	private String javaObjectSerializationStatus(JApiClass jApiClass) {
		return " (" + jApiClass.getJavaObjectSerializationCompatible().getDescription() + ")";
	}

	private void processFieldChanges(StringBuilder sb, JApiClass jApiClass) {
		List<JApiField> jApiFields = jApiClass.getFields();
		for (JApiField jApiField : jApiFields) {
			sb.append(tabs(1)).append(signs(jApiField)).append(" ").append(jApiField.getChangeStatus()).append(" FIELD: ")
				.append(accessModifierAsString(jApiField)).append(staticModifierAsString(jApiField))
				.append(finalModifierAsString(jApiField)).append(syntheticModifierAsString(jApiField))
				.append(fieldTypeChangeAsString(jApiField)).append(jApiField.getName()).append("\n");
			processAnnotations(sb, jApiField, 2);
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

	private String syntheticModifierAsString(JApiHasSyntheticModifier modifier) {
		JApiModifier<SyntheticModifier> syntheticModifier = modifier.getSyntheticModifier();
		return modifierAsString(syntheticModifier, SyntheticModifier.NON_SYNTHETIC);
	}

	private String bridgeModifierAsString(JApiHasBridgeModifier modifier) {
		JApiModifier<BridgeModifier> bridgeModifier = modifier.getBridgeModifier();
		return modifierAsString(bridgeModifier, BridgeModifier.NON_BRIDGE);
	}

	private <T> String modifierAsString(JApiModifier<T> modifier, T notPrintValue) {
		if (modifier.getOldModifier().isPresent() && modifier.getNewModifier().isPresent()) {
			if (modifier.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				return modifier.getNewModifier().get() + " (<- " + modifier.getOldModifier().get() + ") ";
			} else if (modifier.getChangeStatus() == JApiChangeStatus.NEW) {
				if (modifier.getNewModifier().get() != notPrintValue) {
					return modifier.getNewModifier().get() + "(+) ";
				}
			} else if (modifier.getChangeStatus() == JApiChangeStatus.REMOVED) {
				if (modifier.getOldModifier().get() != notPrintValue) {
					return modifier.getOldModifier().get() + "(-) ";
				}
			} else {
				if (modifier.getNewModifier().get() != notPrintValue) {
					return modifier.getNewModifier().get() + " ";
				}
			}
		} else if (modifier.getOldModifier().isPresent()) {
			if (modifier.getOldModifier().get() != notPrintValue) {
				return modifier.getOldModifier().get() + "(-) ";
			}
		} else if (modifier.getNewModifier().isPresent()) {
			if (modifier.getNewModifier().get() != notPrintValue) {
				return modifier.getNewModifier().get() + "(+) ";
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
		} else if (type.getOldTypeOptional().isPresent() && !type.getNewTypeOptional().isPresent()) {
			return type.getOldTypeOptional().get() + " ";
		} else if (!type.getOldTypeOptional().isPresent() && type.getNewTypeOptional().isPresent()) {
			return type.getNewTypeOptional().get() + " ";
		}
		return "n.a.";
	}

	private void processSuperclassChanges(StringBuilder sb, JApiClass jApiClass) {
		JApiSuperclass jApiSuperclass = jApiClass.getSuperclass();
		if (!options.isOutputOnlyModifications() || jApiSuperclass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
			sb.append(tabs(1)).append(signs(jApiSuperclass)).append(" ").append(jApiSuperclass.getChangeStatus()).append(" SUPERCLASS: ").append(superclassChangeAsString(jApiSuperclass)).append("\n");
		}
	}

	private String superclassChangeAsString(JApiSuperclass jApiSuperclass) {
		if (jApiSuperclass.getOldSuperclassName().isPresent() && jApiSuperclass.getNewSuperclassName().isPresent()) {
			return jApiSuperclass.getNewSuperclassName().get() + " (<- " + jApiSuperclass.getOldSuperclassName().get() + ")";
		} else if (jApiSuperclass.getOldSuperclassName().isPresent() && !jApiSuperclass.getNewSuperclassName().isPresent()) {
			return jApiSuperclass.getOldSuperclassName().get();
		} else if (!jApiSuperclass.getOldSuperclassName().isPresent() && jApiSuperclass.getNewSuperclassName().isPresent()) {
			return jApiSuperclass.getNewSuperclassName().get();
		}
		return "n.a.";
	}

	private void processInterfaceChanges(StringBuilder sb, JApiClass jApiClass) {
		List<JApiImplementedInterface> interfaces = jApiClass.getInterfaces();
		for (JApiImplementedInterface implementedInterface : interfaces) {
			sb.append(tabs(1)).append(signs(implementedInterface)).append(" ").append(implementedInterface.getChangeStatus()).append(" INTERFACE: ").append(implementedInterface.getFullyQualifiedName()).append("\n");
		}
	}
}
