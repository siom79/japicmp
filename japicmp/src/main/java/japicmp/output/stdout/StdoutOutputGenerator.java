package japicmp.output.stdout;

import japicmp.config.Options;
import japicmp.model.*;
import japicmp.output.OutputTransformer;

import java.io.File;
import java.util.List;

public class StdoutOutputGenerator {
	private final Options options;

	public StdoutOutputGenerator(Options options) {
		this.options = options;
	}

	public String generate(File oldArchive, File newArchive, List<JApiClass> jApiClasses) {
		if (options.isOutputOnlyModifications()) {
			OutputTransformer.removeUnchanged(jApiClasses);
		}
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
			appendMethod(sb, signs(jApiConstructor.getChangeStatus()), jApiConstructor, "CONSTRUCTOR");
		}
	}

	private void processMethods(StringBuilder sb, JApiClass jApiClass) {
		List<JApiMethod> methods = jApiClass.getMethods();
		for (JApiMethod jApiMethod : methods) {
			appendMethod(sb, signs(jApiMethod.getChangeStatus()), jApiMethod, "METHOD");
		}
	}

	private void processClass(StringBuilder sb, JApiClass jApiClass) {
		appendClass(sb, signs(jApiClass.getChangeStatus()), jApiClass);
	}

	private String signs(JApiChangeStatus changeStatus) {
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
		return retVal;
	}

	private void appendMethod(StringBuilder sb, String signs, JApiBehavior jApiMethod, String classMemberType) {
		sb.append("\t" + signs + " " + jApiMethod.getChangeStatus() + " " + classMemberType + " " + jApiMethod.getName() + "(");
		int paramCount = 0;
		for (JApiParameter jApiParameter : jApiMethod.getParameters()) {
			if (paramCount > 0) {
				sb.append(", ");
			}
			sb.append(jApiParameter.getType());
			paramCount++;
		}
		sb.append(")\n");
		processModifierChanges(sb, jApiMethod, 2);
	}

	private void processModifierChanges(StringBuilder sb, JApiHasModifier jApiHasModifier, int numberOfTabs) {
		List<JApiModifier<? extends Enum<?>>> modifiers = jApiHasModifier.getModifiers();
		for(JApiModifier<? extends Enum<?>> modifier : modifiers) {
			if ((options.isOutputOnlyModifications() && modifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) || !options.isOutputOnlyModifications()) {
				sb.append(tabs(numberOfTabs) + signs(modifier.getChangeStatus()) + " " + modifier.getChangeStatus() + " MODIFIER " + 
						modifierChangeAsString(modifier, modifier.getChangeStatus()) + "\n");
			}
		}
	}

	private <T> String modifierChangeAsString(JApiModifier<T> jApiModifier, JApiChangeStatus changeStatus) {
		if (jApiModifier.getOldModifier().isPresent() && jApiModifier.getNewModifier().isPresent()) {
			if(changeStatus == JApiChangeStatus.MODIFIED) {
				return jApiModifier.getNewModifier().get() + " (<- " + jApiModifier.getOldModifier().get() + ")";
			} else {
				return jApiModifier.getNewModifier().get().toString();
			}
		} else if (jApiModifier.getOldModifier().isPresent() && !jApiModifier.getNewModifier().isPresent()) {
			return jApiModifier.getOldModifier().get().toString();
		} else if (!jApiModifier.getOldModifier().isPresent() && jApiModifier.getNewModifier().isPresent()) {
			return jApiModifier.getNewModifier().get().toString();
		}
		return "n.a.";
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
		sb.append(signs + " " + jApiClass.getChangeStatus() + " " + jApiClass.getType() + " " + jApiClass.getFullyQualifiedName() + "\n");
		processModifierChanges(sb, jApiClass, 1);
		processInterfaceChanges(sb, jApiClass);
		processSuperclassChanges(sb, jApiClass);
		processFieldChanges(sb, jApiClass);
	}

	private void processFieldChanges(StringBuilder sb, JApiClass jApiClass) {
		List<JApiField> fields = jApiClass.getFields();
		for(JApiField field : fields) {
			sb.append(tabs(1) + signs(field.getChangeStatus()) + " " + field.getChangeStatus() + " FIELD " + fieldTypeChangeAsString(field) + " " + field.getName() + "\n");
			processModifierChanges(sb, field, 2);
		}
	}

    private String fieldTypeChangeAsString(JApiField field) {
        JApiType type = field.getType();
        if(type.getOldTypeOptional().isPresent() && type.getNewTypeOptional().isPresent()) {
            if(type.getChangeStatus() == JApiChangeStatus.MODIFIED) {
                return type.getOldTypeOptional().get() + " (<- " + type.getNewTypeOptional().get() + ")";
            } else if(type.getChangeStatus() == JApiChangeStatus.NEW) {
                return type.getNewTypeOptional().get();
            } else if(type.getChangeStatus() == JApiChangeStatus.REMOVED) {
                return type.getOldTypeOptional().get();
            } else {
                return type.getNewTypeOptional().get();
            }
        } else if(type.getOldTypeOptional().isPresent() && !type.getNewTypeOptional().isPresent()) {
            return type.getOldTypeOptional().get();
        } else if(!type.getOldTypeOptional().isPresent() && type.getNewTypeOptional().isPresent()) {
            return type.getNewTypeOptional().get();
        }
        return "n.a.";
    }

	private void processSuperclassChanges(StringBuilder sb, JApiClass jApiClass) {
		JApiSuperclass jApiSuperclass = jApiClass.getSuperclass();
		if (options.isOutputOnlyModifications() && jApiSuperclass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
			sb.append(tabs(1) + signs(jApiSuperclass.getChangeStatus()) + " " + jApiSuperclass.getChangeStatus() + " SUPERCLASS " + superclassChangeAsString(jApiSuperclass) + "\n");
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
			sb.append(tabs(1) + signs(implementedInterface.getChangeStatus()) + " " + implementedInterface.getChangeStatus() + " INTERFACE "
					+ implementedInterface.getFullyQualifiedName() + "\n");
		}
	}
}
