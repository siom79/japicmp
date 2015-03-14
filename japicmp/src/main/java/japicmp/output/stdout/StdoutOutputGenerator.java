package japicmp.output.stdout;

import com.google.common.base.Optional;
import japicmp.config.Options;
import japicmp.model.*;
import japicmp.model.JApiAnnotationElementValue.Type;
import japicmp.output.OutputFilter;
import japicmp.output.OutputGenerator;
import javassist.bytecode.annotation.MemberValue;

import java.io.File;
import java.util.List;

public class StdoutOutputGenerator extends OutputGenerator<String> {
	private final File oldArchive;
	private final File newArchive;

	public StdoutOutputGenerator(Options options, List<JApiClass> jApiClasses, File oldArchive, File newArchive) {
        super(options, jApiClasses);
		this.oldArchive = oldArchive;
		this.newArchive = newArchive;
	}

	@Override
	public String generate() {
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
        sb.append("\t").append(signs).append(" ").append(jApiBehavior.getChangeStatus()).append(" ").append(classMemberType).append(" ")
				.append(accessModifierAsString(jApiBehavior)).append(abstractModifierAsString(jApiBehavior)).append(staticModifierAsString(jApiBehavior))
				.append(finalModifierAsString(jApiBehavior)).append(returnType(jApiBehavior)).append(jApiBehavior.getName()).append("(");
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
    	if(jApiBehavior instanceof JApiMethod) {
    		JApiMethod method = (JApiMethod)jApiBehavior;
			JApiReturnType jApiReturnType = method.getReturnType();
			if (jApiReturnType.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
				returnTypeAsString = jApiReturnType.getNewReturnType() + " ";
			} else if(jApiReturnType.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				returnTypeAsString = jApiReturnType.getNewReturnType() + " (<-" + jApiReturnType.getOldReturnType() + ") ";
			} else if(jApiReturnType.getChangeStatus() == JApiChangeStatus.NEW) {
				returnTypeAsString = jApiReturnType.getNewReturnType() + " ";
			} else {
				returnTypeAsString = jApiReturnType.getOldReturnType() + " ";
			}
    	}
    	return returnTypeAsString;
    }

    private void appendAnnotation(StringBuilder sb, String signs, JApiAnnotation jApiAnnotation, int numberOfTabs) {
        sb.append(tabs(numberOfTabs) + signs + " " + jApiAnnotation.getChangeStatus() + " ANNOTATION: " + jApiAnnotation.getFullyQualifiedName() + "\n");
    }

    private void appendAnnotationElement(StringBuilder sb, String signs, JApiAnnotationElement jApiAnnotationElement, int numberOfTabs) {
        sb.append(tabs(numberOfTabs) + signs + " " + jApiAnnotationElement.getChangeStatus() + " ELEMENT: " + jApiAnnotationElement.getName() + "=");
        Optional<MemberValue> oldValue = jApiAnnotationElement.getOldValue();
        Optional<MemberValue> newValue = jApiAnnotationElement.getNewValue();
        if (oldValue.isPresent() && newValue.isPresent()) {
            if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                sb.append(elementValueList2String(jApiAnnotationElement.getNewElementValues()));
            } else if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.REMOVED) {
                sb.append(elementValueList2String(jApiAnnotationElement.getOldElementValues()) + " (-)");
            } else if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.NEW) {
                sb.append(elementValueList2String(jApiAnnotationElement.getNewElementValues()) + " (+)");
            } else if (jApiAnnotationElement.getChangeStatus() == JApiChangeStatus.MODIFIED) {
                sb.append(elementValueList2String(jApiAnnotationElement.getNewElementValues()) + " (<- " + elementValueList2String(jApiAnnotationElement.getOldElementValues()) + ")");
            }
        } else if (!oldValue.isPresent() && newValue.isPresent()) {
            sb.append(elementValueList2String(jApiAnnotationElement.getNewElementValues()) + " (+)");
        } else if (oldValue.isPresent() && !newValue.isPresent()) {
            sb.append(elementValueList2String(jApiAnnotationElement.getOldElementValues()) + " (-)");
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
				sb.append(value.getName().get() + "=");
			}
			if (value.getType() != Type.Array && value.getType() != Type.Annotation) {
				if (value.getType() == Type.Enum) {
					sb.append(value.getFullyQualifiedName() + "." + value.getValueString());
				} else {
					sb.append(value.getValueString());
				}
			} else {
				if (value.getType() == Type.Array) {
					sb.append("{" + elementValueList2String(value.getValues()) + "}");
				} else if (value.getType() == Type.Annotation) {
					sb.append("@" + value.getFullyQualifiedName() + "(" + elementValueList2String(value.getValues()) + ")");
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
        sb.append(signs + " " + jApiClass.getChangeStatus() + " " + processClassType(jApiClass) + ": " + accessModifierAsString(jApiClass) + abstractModifierAsString(jApiClass)
                + staticModifierAsString(jApiClass) + finalModifierAsString(jApiClass) + jApiClass.getFullyQualifiedName() + "\n");
        processInterfaceChanges(sb, jApiClass);
        processSuperclassChanges(sb, jApiClass);
        processFieldChanges(sb, jApiClass);
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
		String returnValue;
		JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus status = jApiClass.getJavaObjectSerializationCompatible();
		switch (status) {
			case SERIALIZABLE_COMPATIBLE:
				returnValue = " (serialization compatible)";
				break;
			case SERIALIZABLE_INCOMPATIBLE:
				returnValue = " (serialization incompatible)";
				break;
			case SERIALIZABLE_INCOMPATIBLE_BUT_SUID_EQUAL:
				returnValue = " (serialization incompatible but serialVersionUID equal)";
				break;
			default:
				returnValue = "";
		}
		return returnValue;
	}

    private void processFieldChanges(StringBuilder sb, JApiClass jApiClass) {
        List<JApiField> fields = jApiClass.getFields();
        for (JApiField field : fields) {
            sb.append(tabs(1) + signs(field) + " " + field.getChangeStatus() + " FIELD: " + accessModifierAsString(field) + staticModifierAsString(field)
                    + finalModifierAsString(field) + fieldTypeChangeAsString(field) + field.getName() + "\n");
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
        } else if (accessModifier.getOldModifier().isPresent() && !accessModifier.getNewModifier().isPresent()) {
            if (accessModifier.getOldModifier().get() != notPrintValue) {
                return accessModifier.getOldModifier().get() + "(-) ";
            }
        } else if (!accessModifier.getOldModifier().isPresent() && accessModifier.getNewModifier().isPresent()) {
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
        } else if (type.getOldTypeOptional().isPresent() && !type.getNewTypeOptional().isPresent()) {
            return type.getOldTypeOptional().get() + " ";
        } else if (!type.getOldTypeOptional().isPresent() && type.getNewTypeOptional().isPresent()) {
            return type.getNewTypeOptional().get() + " ";
        }
        return "n.a.";
    }

    private void processSuperclassChanges(StringBuilder sb, JApiClass jApiClass) {
        JApiSuperclass jApiSuperclass = jApiClass.getSuperclass();
        if (options.isOutputOnlyModifications() && jApiSuperclass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
            sb.append(tabs(1) + signs(jApiSuperclass) + " " + jApiSuperclass.getChangeStatus() + " SUPERCLASS: " + superclassChangeAsString(jApiSuperclass) + "\n");
        }
    }

    private String superclassChangeAsString(JApiSuperclass jApiSuperclass) {
        if (jApiSuperclass.getOldSuperclass().isPresent() && jApiSuperclass.getNewSuperclass().isPresent()) {
            return jApiSuperclass.getNewSuperclass().get() + " (<- " + jApiSuperclass.getOldSuperclass().get() + ")";
        } else if (jApiSuperclass.getOldSuperclass().isPresent() && !jApiSuperclass.getNewSuperclass().isPresent()) {
            return jApiSuperclass.getOldSuperclass().get();
        } else if (!jApiSuperclass.getOldSuperclass().isPresent() && jApiSuperclass.getNewSuperclass().isPresent()) {
            return jApiSuperclass.getNewSuperclass().get();
        }
        return "n.a.";
    }

    private void processInterfaceChanges(StringBuilder sb, JApiClass jApiClass) {
        List<JApiImplementedInterface> interfaces = jApiClass.getInterfaces();
        for (JApiImplementedInterface implementedInterface : interfaces) {
            sb.append(tabs(1) + signs(implementedInterface) + " " + implementedInterface.getChangeStatus() + " INTERFACE: " + implementedInterface.getFullyQualifiedName() + "\n");
        }
    }
}
