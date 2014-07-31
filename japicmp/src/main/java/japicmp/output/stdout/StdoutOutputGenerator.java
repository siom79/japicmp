package japicmp.output.stdout;

import japicmp.config.Options;
import japicmp.model.*;
import japicmp.output.OutputTransformer;

import java.io.File;
import java.util.List;

import com.google.common.base.Optional;

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
		switch(changeStatus) {
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
		sb.append("\t" + signs + " " + jApiMethod.getChangeStatus() + " "+classMemberType+" " + jApiMethod.getName() + "(");
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
        if (jApiHasModifier.getAccessModifierOld() != jApiHasModifier.getAccessModifierNew()) {
            sb.append(tabs(numberOfTabs) + jApiHasModifier.getAccessModifierOld() + "->" + jApiHasModifier.getAccessModifierNew() + "\n");
        }
        if(!jApiHasModifier.getFinalModifierOldOptional().isPresent() && jApiHasModifier.getFinalModifierNewOptional().isPresent() && jApiHasModifier.getFinalModifierNewOptional().get() == true) {
            sb.append(tabs(numberOfTabs) + "+++ " + JApiChangeStatus.NEW + " MODIFIER final\n");
        } else if(jApiHasModifier.getFinalModifierOldOptional().isPresent() && jApiHasModifier.getFinalModifierOldOptional().get() == true && !jApiHasModifier.getFinalModifierNewOptional().isPresent()) {
            sb.append(tabs(numberOfTabs) + "--- " + JApiChangeStatus.REMOVED + " MODIFIER final\n");
        } else if(jApiHasModifier.getFinalModifierOldOptional().isPresent() && jApiHasModifier.getFinalModifierNewOptional().isPresent()) {
            if(jApiHasModifier.getFinalModifierOldOptional().get() == true && jApiHasModifier.getFinalModifierNewOptional().get() == false) {
                sb.append(tabs(numberOfTabs) + "--- " + JApiChangeStatus.REMOVED + " MODIFIER final\n");
            } else if(jApiHasModifier.getFinalModifierOldOptional().get() == false && jApiHasModifier.getFinalModifierNewOptional().get() == true) {
                sb.append(tabs(numberOfTabs) + "+++ " + JApiChangeStatus.NEW + " MODIFIER final\n");
            }
        }
        if(!jApiHasModifier.getStaticModifierOldOptional().isPresent() && jApiHasModifier.getStaticModifierNewOptional().isPresent() && jApiHasModifier.getStaticModifierNewOptional().get() == true) {
            sb.append(tabs(numberOfTabs) + "+++ " + JApiChangeStatus.NEW + " MODIFIER static\n");
        } else if(jApiHasModifier.getStaticModifierOldOptional().isPresent() && jApiHasModifier.getStaticModifierOldOptional().get() == true && !jApiHasModifier.getStaticModifierNewOptional().isPresent()) {
            sb.append(tabs(numberOfTabs) + "--- " + JApiChangeStatus.REMOVED + " MODIFIER static\n");
        } else if(jApiHasModifier.getStaticModifierOldOptional().isPresent() && jApiHasModifier.getStaticModifierNewOptional().isPresent()) {
            if(jApiHasModifier.getStaticModifierOldOptional().get() == true && jApiHasModifier.getStaticModifierNewOptional().get() == false) {
                sb.append(tabs(numberOfTabs) + "--- " + JApiChangeStatus.REMOVED + " MODIFIER static\n");
            } else if(jApiHasModifier.getStaticModifierOldOptional().get() == false && jApiHasModifier.getStaticModifierNewOptional().get() == true) {
                sb.append(tabs(numberOfTabs) + "+++ " + JApiChangeStatus.NEW + " MODIFIER static\n");
            }
        }
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
	}

	private void processSuperclassChanges(StringBuilder sb, JApiClass jApiClass) {
		Optional<JApiSuperclass> superclassOptional = jApiClass.getSuperclassOptional();
		if(superclassOptional.isPresent()) {
			JApiSuperclass jApiSuperclass = superclassOptional.get();
			sb.append(tabs(1) + signs(jApiSuperclass.getChangeStatus()) + " " + jApiSuperclass.getChangeStatus() + " EXTENDS " + superclassChangeAsString(jApiSuperclass) + "\n");
		}
		
	}

	private String superclassChangeAsString(JApiSuperclass jApiSuperclass) {
		if(jApiSuperclass.getOldSuperclass().isPresent() && jApiSuperclass.getNewSuperclass().isPresent()) {
			return jApiSuperclass.getNewSuperclass().get() + " (<- " + jApiSuperclass.getOldSuperclass().get() + ")";
		} else if(jApiSuperclass.getOldSuperclass().isPresent() && !jApiSuperclass.getNewSuperclass().isPresent()) {
			return jApiSuperclass.getOldSuperclass().get();
		} else if(!jApiSuperclass.getOldSuperclass().isPresent() && jApiSuperclass.getNewSuperclass().isPresent()) {
			return jApiSuperclass.getNewSuperclass().get();
		}
		return "n.a.";
	}

	private void processInterfaceChanges(StringBuilder sb, JApiClass jApiClass) {
		List<JApiImplementedInterface> interfaces = jApiClass.getInterfaces();
		for(JApiImplementedInterface implementedInterface : interfaces) {
			sb.append(tabs(1) + signs(implementedInterface.getChangeStatus()) + " " + implementedInterface.getChangeStatus() + " IMPLEMENTS " + implementedInterface.getFullyQualifiedName() + "\n");
		}
	}
}
