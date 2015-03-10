package japicmp.model;

import com.google.common.base.Optional;
import japicmp.exception.JApiCmpException;
import japicmp.util.ModifierHelper;
import javassist.*;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.List;

public class JavaObjectSerializationCompatibility {
	public static final String SERIAL_VERSION_UID = "serialVersionUID";

	public void evaluate(List<JApiClass> classes) {
		for (JApiClass jApiClass : classes) {
			JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus changeStatus = computeChangeStatus(jApiClass);
			jApiClass.setJavaObjectSerializationCompatible(changeStatus);
		}
	}

	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus computeChangeStatus(JApiClass jApiClass) {
		JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.NOT_SERIALIZABLE;
		long serialVersionUidOld = -1L;
		long serialVersionUidOldDefault = -1L;
		long serialVersionUidNew = -1L;
		long serialVersionUidNewDefault = -1L;
		boolean serializable = false;
		Optional<CtClass> oldClassOptional = jApiClass.getOldClass();
		if (oldClassOptional.isPresent()) {
			CtClass ctClass = oldClassOptional.get();
			SerialVersionUidResult serialVersionUidResult = new SerialVersionUidResult(serialVersionUidOld, serialVersionUidOldDefault, ctClass).invoke();
			serializable = serialVersionUidResult.isSerializable();
			serialVersionUidOld = serialVersionUidResult.getSerialVersionUid();
			serialVersionUidOldDefault = serialVersionUidResult.getSerialVersionUidDefault();
		}
		Optional<CtClass> newClassOptional = jApiClass.getNewClass();
		if (newClassOptional.isPresent()) {
			CtClass ctClass = newClassOptional.get();
			SerialVersionUidResult serialVersionUidResult = new SerialVersionUidResult(serialVersionUidNew, serialVersionUidNewDefault, ctClass).invoke();
			serializable = serialVersionUidResult.isSerializable();
			serialVersionUidNew = serialVersionUidResult.getSerialVersionUid();
			serialVersionUidNewDefault = serialVersionUidResult.getSerialVersionUidDefault();
		}
		if (serializable) {
			if (serialVersionUidOld == serialVersionUidNew) {
				if (serialVersionUidOldDefault != serialVersionUidNewDefault) {
					JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChanges = checkChanges(jApiClass);
					if (checkChanges == JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE) {
						state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_BUT_SUID_EQUAL;
					} else {
						state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
					}
				} else {
					state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
				}
			} else {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
			}
		}
		return state;
	}

	/**
	 * Checks compatibility of changes according to http://docs.oracle.com/javase/7/docs/platform/serialization/spec/version.html#5172.
	 * @param jApiClass the class to check
	 * @return either SERIALIZABLE_INCOMPATIBLE or SERIALIZABLE_COMPATIBLE
	 */
	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChanges(JApiClass jApiClass) {
		JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
		for (JApiField field : jApiClass.getFields()) {
			if (field.getChangeStatus() == JApiChangeStatus.REMOVED) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
			}
			if (field.getStaticModifier().getChangeStatus() == JApiChangeStatus.NEW) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
			}
			if (field.getTransientModifier().getChangeStatus() == JApiChangeStatus.NEW) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
			}
			if (field.getType().getChangeStatus() == JApiChangeStatus.MODIFIED) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
			}
		}
		boolean serializableAdded = false;
		boolean serializableRemoved = false;
		boolean externalizableAdded = false;
		boolean externalizableRemoved = false;
		for (JApiImplementedInterface implementedInterface : jApiClass.getInterfaces()) {
			if (Serializable.class.getCanonicalName().equals(implementedInterface.getFullyQualifiedName())) {
				if (implementedInterface.getChangeStatus() == JApiChangeStatus.NEW) {
					serializableAdded = true;
				} else if (implementedInterface.getChangeStatus() == JApiChangeStatus.REMOVED) {
					serializableRemoved = true;
				}
			}
			if (Externalizable.class.getCanonicalName().equals(implementedInterface.getFullyQualifiedName())) {
				if (implementedInterface.getChangeStatus() == JApiChangeStatus.NEW) {
					externalizableAdded = true;
				} else if (implementedInterface.getChangeStatus() == JApiChangeStatus.REMOVED) {
					externalizableRemoved = true;
				}
			}
		}
		if (serializableRemoved && externalizableAdded) {
			state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
		}
		if (serializableAdded && externalizableRemoved) {
			state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
		}
		if (serializableRemoved) {
			state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
		}
		if (externalizableRemoved) {
			state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
		}
		//TODO: type of class changes
		return state;
	}

	private boolean isCtClassSerializable(CtClass clazz) {
		ClassPool pool = clazz.getClassPool();
		try {
			return clazz.subtypeOf(pool.get("java.io.Serializable"));
		} catch (NotFoundException e) {
			throw new JApiCmpException(JApiCmpException.Reason.ClassLoading, "Failed to determine whether the class '" + clazz.getName() + "' is serializable: " + e.getMessage(), e);
		}
	}

	private class SerialVersionUidResult {
		private long serialVersionUid;
		private long serialVersionUidDefault;
		private boolean serializable;
		private CtClass ctClass;

		public SerialVersionUidResult(long serialVersionUid, long serialVersionUidDefault, CtClass ctClass) {
			this.serialVersionUid = serialVersionUid;
			this.serialVersionUidDefault = serialVersionUidDefault;
			this.ctClass = ctClass;
		}

		public long getSerialVersionUid() {
			return serialVersionUid;
		}

		public long getSerialVersionUidDefault() {
			return serialVersionUidDefault;
		}

		public boolean isSerializable() {
			return serializable;
		}

		public SerialVersionUidResult invoke() {
			if (isCtClassSerializable(ctClass)) {
				serializable = true;
				try {
					CtField declaredField = ctClass.getDeclaredField(SERIAL_VERSION_UID);
					Object constantValue = declaredField.getConstantValue();
					if (constantValue instanceof Long) {
						serialVersionUid = (Long)constantValue;
					}
				} catch (NotFoundException e) {
					try {
						SerialVersionUID.setSerialVersionUID(ctClass);
						CtField declaredField = ctClass.getDeclaredField(SERIAL_VERSION_UID);
						Object constantValue = declaredField.getConstantValue();
						if (constantValue instanceof Long) {
							serialVersionUid = (Long)constantValue;
							serialVersionUidDefault = serialVersionUid;
						}
						ctClass.removeField(declaredField);
					} catch (Exception ignored) {}
				}
				if (serialVersionUidDefault == -1L) {
					try {
						CtField declaredFieldOriginal = ctClass.getDeclaredField(SERIAL_VERSION_UID);
						ctClass.removeField(declaredFieldOriginal);
						SerialVersionUID.setSerialVersionUID(ctClass);
						CtField declaredField = ctClass.getDeclaredField(SERIAL_VERSION_UID);
						Object constantValue = declaredField.getConstantValue();
						if (constantValue instanceof Long) {
							serialVersionUidDefault = (Long)constantValue;
						}
						ctClass.removeField(declaredField);
						ctClass.addField(declaredFieldOriginal);
					} catch (Exception ignored) {}
				}
			}
			return this;
		}
	}
}
