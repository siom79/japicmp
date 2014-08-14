package japicmp.output.stdout;

import japicmp.config.Options;
import japicmp.model.*;
import japicmp.output.OutputFilter;

import java.io.File;
import java.util.List;

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
		}
		return sb.toString();
	}

	private void processConstructors(StringBuilder sb, JApiClass jApiClass) {
		List<JApiConstructor> constructors = jApiClass.getConstructors();
		for (JApiConstructor jApiConstructor : constructors) {
			appendMethod(sb, signs(jApiConstructor), jApiConstructor, "CONSTRUCTOR:");
		}
	}

	private void processMethods(StringBuilder sb, JApiClass jApiClass) {
		List<JApiMethod> methods = jApiClass.getMethods();
		for (JApiMethod jApiMethod : methods) {
			appendMethod(sb, signs(jApiMethod), jApiMethod, "METHOD:");
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
		if(binaryCompatible) {
			retVal += " ";
		} else {
			retVal += "!";
		}
		return retVal;
	}

	private void appendMethod(StringBuilder sb, String signs, JApiBehavior jApiMethod, String classMemberType) {
		sb.append("\t" + signs + " " + jApiMethod.getChangeStatus() + " " + classMemberType + " " + accessModifierAsString(jApiMethod) + abstractModifierAsString(jApiMethod)
				+ staticModifierAsString(jApiMethod) + finalModifierAsString(jApiMethod) + jApiMethod.getName() + "(");
		int paramCount = 0;
		for (JApiParameter jApiParameter : jApiMethod.getParameters()) {
			if (paramCount > 0) {
				sb.append(", ");
			}
			sb.append(jApiParameter.getType());
			paramCount++;
		}
		sb.append(")\n");
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
		sb.append(signs + " " + jApiClass.getChangeStatus() + " " + jApiClass.getType() + ": " + accessModifierAsString(jApiClass) + abstractModifierAsString(jApiClass)
				+ staticModifierAsString(jApiClass) + finalModifierAsString(jApiClass) + jApiClass.getFullyQualifiedName() + "\n");
		processInterfaceChanges(sb, jApiClass);
		processSuperclassChanges(sb, jApiClass);
		processFieldChanges(sb, jApiClass);
	}

	private void processFieldChanges(StringBuilder sb, JApiClass jApiClass) {
		List<JApiField> fields = jApiClass.getFields();
		for (JApiField field : fields) {
			sb.append(tabs(1) + signs(field) + " " + field.getChangeStatus() + " FIELD: " + accessModifierAsString(field) + staticModifierAsString(field)
					+ finalModifierAsString(field) + fieldTypeChangeAsString(field) + field.getName() + "\n");
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
