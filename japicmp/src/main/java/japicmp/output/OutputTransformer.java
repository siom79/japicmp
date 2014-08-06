package japicmp.output;

import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiConstructor;
import japicmp.model.JApiField;
import japicmp.model.JApiImplementedInterface;
import japicmp.model.JApiMethod;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class OutputTransformer {

    private OutputTransformer() {

    }

    public static void removeUnchanged(List<JApiClass> jApiClasses) {
        Iterator<JApiClass> itClasses = jApiClasses.iterator();
        while(itClasses.hasNext()) {
        	JApiClass jApiClass = itClasses.next();
        	if(jApiClass.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
        		itClasses.remove();
        	} else {
        		Iterator<JApiMethod> itMethods = jApiClass.getMethods().iterator();
        		while(itMethods.hasNext()) {
        			JApiMethod jApiMethod = itMethods.next();
        			if(jApiMethod.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
        				itMethods.remove();
        			}
        		}
        		Iterator<JApiConstructor> itConstructors = jApiClass.getConstructors().iterator();
        		while(itConstructors.hasNext()) {
        			JApiConstructor jApiConstructor = itConstructors.next();
        			if(jApiConstructor.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
        				itConstructors.remove();
        			}
        		}
        		Iterator<JApiImplementedInterface> itInterfaces = jApiClass.getInterfaces().iterator();
        		while(itInterfaces.hasNext()) {
        			JApiImplementedInterface jApiImplementedInterface = itInterfaces.next();
        			if(jApiImplementedInterface.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
        				itInterfaces.remove();
        			}
        		}
        		Iterator<JApiField> itFields = jApiClass.getFields().iterator();
        		while(itFields.hasNext()) {
        			JApiField jApiField = itFields.next();
        			if(jApiField.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
        				itFields.remove();
        			}
        		}
        	}
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
