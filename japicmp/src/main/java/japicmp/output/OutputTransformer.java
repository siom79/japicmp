package japicmp.output;

import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class OutputTransformer {

    private OutputTransformer() {

    }

    public static void removeUnchanged(List<JApiClass> jApiClasses) {
        List<JApiClass> classesToRemove = new LinkedList<JApiClass>();
        for (JApiClass jApiClass : jApiClasses) {
            if (jApiClass.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                classesToRemove.add(jApiClass);
            } else {
                List<JApiMethod> methodsToRemove = new LinkedList<JApiMethod>();
                List<JApiMethod> methods = jApiClass.getMethods();
                for (JApiMethod jApiMethod : methods) {
                    if (jApiMethod.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
                        methodsToRemove.add(jApiMethod);
                    }
                }
                for (JApiMethod jApiMethod : methodsToRemove) {
                    methods.remove(jApiMethod);
                }
            }
        }
        for (JApiClass jApiClass : classesToRemove) {
            jApiClasses.remove(jApiClass);
        }
    }

    public static void sortClassesAndMethods(List<JApiClass> jApiClasses) {
        Collections.sort(jApiClasses, new Comparator<JApiClass>() {
            public int compare(JApiClass o1, JApiClass o2) {
                return o1.getFullyQualifiedName().compareToIgnoreCase(o2.getFullyQualifiedName());
            }
        });
        for(JApiClass jApiClass : jApiClasses) {
            Collections.sort(jApiClass.getMethods(), new Comparator<JApiMethod>() {
                public int compare(JApiMethod o1, JApiMethod o2) {
                    return o1.getName().compareToIgnoreCase(o2.getName());
                }
            });
        }
    }
}
