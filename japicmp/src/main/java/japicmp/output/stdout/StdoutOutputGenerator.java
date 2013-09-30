package japicmp.output.stdout;

import japicmp.config.Options;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import japicmp.model.JApiParameter;
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
            processMethods(sb, jApiClass);
        }
        return sb.toString();
    }

    private void processMethods(StringBuilder sb, JApiClass jApiClass) {
        List<JApiMethod> methods = jApiClass.getMethods();
        for (JApiMethod jApiMethod : methods) {
            if (jApiMethod.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                appendMethod(sb, "===", jApiMethod);
            } else if (jApiMethod.getChangeStatus() == JApiChangeStatus.NEW) {
                appendMethod(sb, "+++", jApiMethod);
            } else if (jApiMethod.getChangeStatus() == JApiChangeStatus.REMOVED) {
                appendMethod(sb, "---", jApiMethod);
            } else if (jApiMethod.getChangeStatus() == JApiChangeStatus.MODIFIED) {
                appendMethod(sb, "***", jApiMethod);
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

    private void appendMethod(StringBuilder sb, String signs, JApiMethod jApiMethod) {
        sb.append("\t" + signs + " " + jApiMethod.getChangeStatus() + " METHOD " + jApiMethod.getName() + "(");
        int paramCount = 0;
        for (JApiParameter jApiParameter : jApiMethod.getParameters()) {
            if (paramCount > 0) {
                sb.append(",");
            }
            sb.append(jApiParameter.getType());
            paramCount++;
        }
        sb.append(")\n");
    }

    private void appendClass(StringBuilder sb, String signs, JApiClass jApiClass) {
        sb.append(signs + " " + jApiClass.getChangeStatus() + " " + jApiClass.getType() + " " + jApiClass.getFullyQualifiedName() + "\n");
    }
}
