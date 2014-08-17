package japicmp.test.util;

import japicmp.model.*;

import java.io.File;
import java.util.List;

public class Helper {

    public static File getArchive(String filename) {
        return new File("target" + File.separator + filename);
    }

    public static JApiClass getJApiClass(List<JApiClass> jApiClasses, String fqn) {
        for (JApiClass jApiClass : jApiClasses) {
            if (jApiClass.getFullyQualifiedName().equals(fqn)) {
                return jApiClass;
            }
        }
        throw new IllegalArgumentException("No class found with name " + fqn + ".");
    }

    public static JApiMethod getJApiMethod(List<JApiMethod> jApiMethods, String name) {
        for(JApiMethod jApiMethod : jApiMethods) {
            if(jApiMethod.getName().equals(name)) {
                return jApiMethod;
            }
        }
        throw new IllegalArgumentException("No method found with name " + name + ".");
    }
    
    public static JApiField getJApiField(List<JApiField> jApiFields, String name) {
    	for(JApiField jApiField : jApiFields) {
    		if(jApiField.getName().equals(name)) {
    			return jApiField;
    		}
    	}
    	throw new IllegalArgumentException("No field found with name " + name + ".");
    }
    
    public static JApiImplementedInterface getJApiImplementedInterface(List<JApiImplementedInterface> jApiImplementedInterfaces, String name) {
    	for(JApiImplementedInterface jApiImplementedInterface : jApiImplementedInterfaces) {
    		if(jApiImplementedInterface.getFullyQualifiedName().equals(name)) {
    			return jApiImplementedInterface;
    		}
    	}
    	throw new IllegalArgumentException("No interface found with name " + name + ".");
    }
    
    public static JApiAnnotation getJApiAnnotation(List<JApiAnnotation> annotations, String name) {
        for(JApiAnnotation annotation : annotations) {
            if(annotation.getFullyQualifiedName().equals(name)) {
                return annotation;
            }
        }
        throw new IllegalArgumentException("No annotation found with name " + name + ".");
    }

    public static JApiAnnotationElement getJApiAnnotationElement(List<JApiAnnotationElement> annotationElements, String name) {
        for(JApiAnnotationElement annotationElement : annotationElements) {
            if(annotationElement.getName().equals(name)) {
                return annotationElement;
            }
        }
        throw new IllegalArgumentException("No annotation element found with name " + name + ".");
    }
}
