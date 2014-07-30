package japicmp.output.stdout;

import japicmp.config.Options;
import japicmp.model.*;
import japicmp.output.OutputTransformer;

import java.io.File;
import java.util.List;

public class StdoutOutputGenerator {

	public String generate(File oldArchive, File newArchive, List<JApiClass> jApiClasses, Options options) {
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
            if (jApiConstructor.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                appendMethod(sb, "===", jApiConstructor, "CONSTRUCTOR");
            } else if (jApiConstructor.getChangeStatus() == JApiChangeStatus.NEW) {
                appendMethod(sb, "+++", jApiConstructor, "CONSTRUCTOR");
            } else if (jApiConstructor.getChangeStatus() == JApiChangeStatus.REMOVED) {
                appendMethod(sb, "---", jApiConstructor, "CONSTRUCTOR");
            } else if (jApiConstructor.getChangeStatus() == JApiChangeStatus.MODIFIED) {
                appendMethod(sb, "***", jApiConstructor, "CONSTRUCTOR");
            }
        }
    }

	private void processMethods(StringBuilder sb, JApiClass jApiClass) {
		List<JApiMethod> methods = jApiClass.getMethods();
		for (JApiMethod jApiMethod : methods) {
			if (jApiMethod.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
				appendMethod(sb, "===", jApiMethod, "METHOD");
			} else if (jApiMethod.getChangeStatus() == JApiChangeStatus.NEW) {
				appendMethod(sb, "+++", jApiMethod, "METHOD");
			} else if (jApiMethod.getChangeStatus() == JApiChangeStatus.REMOVED) {
				appendMethod(sb, "---", jApiMethod, "METHOD");
			} else if (jApiMethod.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				appendMethod(sb, "***", jApiMethod, "METHOD");
			}
		}
	}

	private void processClass(StringBuilder sb, JApiClass jApiClass) {
		if (jApiClass.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
			appendClass(sb, "===", jApiClass);
		} else if (jApiClass.getChangeStatus() == JApiChangeStatus.NEW) {
			appendClass(sb, "+++", jApiClass);
		} else if (jApiClass.getChangeStatus() == JApiChangeStatus.REMOVED) {
			appendClass(sb, "---", jApiClass);
		} else if (jApiClass.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			appendClass(sb, "***", jApiClass);
		}
	}

	private void appendMethod(StringBuilder sb, String signs, JApiBehavior jApiMethod, String classMemberType) {
		sb.append("\t" + signs + " " + jApiMethod.getChangeStatus() + " "+classMemberType+" " + jApiMethod.getName() + "(");
		int paramCount = 0;
		for (JApiParameter jApiParameter : jApiMethod.getParameters()) {
			if (paramCount > 0) {
				sb.append(", ");
			}
			sb.append(jApiParameter.getType());
			paramCount++;
		}
		sb.append(")");
		if (jApiMethod.getChangeStatus() == JApiChangeStatus.MODIFIED) {
            appendModifierChanges(sb, jApiMethod);
		}
		sb.append("\n");
	}

    private void appendModifierChanges(StringBuilder sb, JApiHasModifier jApiHasModifier) {
        if (jApiHasModifier.getAccessModifierOld() != jApiHasModifier.getAccessModifierNew()) {
            sb.append(" " + jApiHasModifier.getAccessModifierOld() + "->" + jApiHasModifier.getAccessModifierNew());
        }
        if(!jApiHasModifier.getFinalModifierOldOptional().isPresent() && jApiHasModifier.getFinalModifierNewOptional().isPresent() && jApiHasModifier.getFinalModifierNewOptional().get() == true) {
            sb.append(" +++final");
        } else if(jApiHasModifier.getFinalModifierOldOptional().isPresent() && jApiHasModifier.getFinalModifierOldOptional().get() == true && !jApiHasModifier.getFinalModifierNewOptional().isPresent()) {
            sb.append(" ---final");
        } else if(jApiHasModifier.getFinalModifierOldOptional().isPresent() && jApiHasModifier.getFinalModifierNewOptional().isPresent()) {
            if(jApiHasModifier.getFinalModifierOldOptional().get() == true && jApiHasModifier.getFinalModifierNewOptional().get() == false) {
                sb.append(" ---final");
            } else if(jApiHasModifier.getFinalModifierOldOptional().get() == false && jApiHasModifier.getFinalModifierNewOptional().get() == true) {
                sb.append(" +++final");
            }
        }
        if(!jApiHasModifier.getStaticModifierOldOptional().isPresent() && jApiHasModifier.getStaticModifierNewOptional().isPresent() && jApiHasModifier.getStaticModifierNewOptional().get() == true) {
            sb.append(" +++static");
        } else if(jApiHasModifier.getStaticModifierOldOptional().isPresent() && jApiHasModifier.getStaticModifierOldOptional().get() == true && !jApiHasModifier.getStaticModifierNewOptional().isPresent()) {
            sb.append(" ---static");
        } else if(jApiHasModifier.getStaticModifierOldOptional().isPresent() && jApiHasModifier.getStaticModifierNewOptional().isPresent()) {
            if(jApiHasModifier.getStaticModifierOldOptional().get() == true && jApiHasModifier.getStaticModifierNewOptional().get() == false) {
                sb.append(" ---static");
            } else if(jApiHasModifier.getStaticModifierOldOptional().get() == false && jApiHasModifier.getStaticModifierNewOptional().get() == true) {
                sb.append(" +++static");
            }
        }
    }

    private void appendClass(StringBuilder sb, String signs, JApiClass jApiClass) {
		sb.append(signs + " " + jApiClass.getChangeStatus() + " " + jApiClass.getType() + " " + jApiClass.getFullyQualifiedName());
        if (jApiClass.getChangeStatus() == JApiChangeStatus.MODIFIED) {
            appendModifierChanges(sb, jApiClass);
        }
        sb.append("\n");
	}
}
