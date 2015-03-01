package japicmp.model;

import japicmp.exception.JApiCmpException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.io.Serializable;
import java.util.List;

public class JavaObjectSerializationCompatibility {

	public void evaluate(List<JApiClass> classes, ClassPool classPool) {
		for (JApiClass jApiClass : classes) {
			if (implementsSerializable(jApiClass, classPool)) {

			}
		}
	}

	private boolean implementsSerializable(JApiClass jApiClass, ClassPool classPool) {
		for (JApiImplementedInterface jApiImplementedInterface : jApiClass.getInterfaces()) {
			if (jApiImplementedInterface.getFullyQualifiedName().equals(Serializable.class.getCanonicalName())) {
				return true;
			}
		}
		JApiSuperclass jApiClassSuperclass = jApiClass.getSuperclass();
		if (jApiClassSuperclass.getOldSuperclass().isPresent()) {
			String oldSuperclass = jApiClassSuperclass.getOldSuperclass().get();
			try {
				CtClass ctClass = classPool.get(oldSuperclass);
			} catch (NotFoundException e) {
				throw new JApiCmpException(JApiCmpException.Reason.ClassLoading, "Could not load class '" + oldSuperclass + "': " + e.getMessage(), e);
			}
		}
		return false;
	}
}
