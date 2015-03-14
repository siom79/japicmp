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
			computeChangeStatus(jApiClass);
		}
	}

	private void computeChangeStatus(JApiClass jApiClass) {
		JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.NOT_SERIALIZABLE;
		Optional<Long> serialVersionUidOldOptional = Optional.absent();
		long serialVersionUidOldDefault = -1;
		Optional<Long> serialVersionUidNewOptional = Optional.absent();
		long serialVersionUidNewDefault = -1;
		boolean serializableOld = false;
		boolean serializableNew = false;
		Optional<CtClass> oldClassOptional = jApiClass.getOldClass();
		if (oldClassOptional.isPresent()) {
			CtClass ctClass = oldClassOptional.get();
			SerialVersionUidResult serialVersionUidResult = new SerialVersionUidResult(ctClass).invoke();
			serializableOld = serialVersionUidResult.isSerializable();
			serialVersionUidOldOptional = serialVersionUidResult.getSerialVersionUid();
			serialVersionUidOldDefault = serialVersionUidResult.getSerialVersionUidDefault();
		}
		Optional<CtClass> newClassOptional = jApiClass.getNewClass();
		if (newClassOptional.isPresent()) {
			CtClass ctClass = newClassOptional.get();
			SerialVersionUidResult serialVersionUidResult = new SerialVersionUidResult(ctClass).invoke();
			serializableNew = serialVersionUidResult.isSerializable();
			serialVersionUidNewOptional = serialVersionUidResult.getSerialVersionUid();
			serialVersionUidNewDefault = serialVersionUidResult.getSerialVersionUidDefault();
		}
		JApiSerialVersionUid jApiSerialVersionUid = new JApiSerialVersionUid(serializableOld, serializableNew, serialVersionUidOldDefault, serialVersionUidNewDefault, serialVersionUidOldOptional, serialVersionUidNewOptional);
		jApiClass.setSerialVersionUid(jApiSerialVersionUid);
		if (serializableOld || serializableNew) {
			if (serialVersionUidOldOptional.isPresent() && serialVersionUidNewOptional.isPresent()) {
				Long suidOld = serialVersionUidOldOptional.get();
				Long suidNew = serialVersionUidNewOptional.get();
				if (suidOld.equals(suidNew)) {
					state = checkChanges(jApiClass, serialVersionUidOldDefault, serialVersionUidNewDefault);
				} else {
					state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
				}
			} else if(serialVersionUidOldOptional.isPresent()) {
				Long suidOld = serialVersionUidOldOptional.get();
				if (suidOld.equals(serialVersionUidNewDefault)) {
					state = checkChanges(jApiClass, serialVersionUidOldDefault, serialVersionUidNewDefault);
				} else {
					state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
				}
			} else if(serialVersionUidNewOptional.isPresent()) {
				Long suidNew = serialVersionUidNewOptional.get();
				if (suidNew.equals(serialVersionUidOldDefault)) {
					state = checkChanges(jApiClass, serialVersionUidOldDefault, serialVersionUidNewDefault);
				} else {
					state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
				}
			}
		}
		jApiClass.setJavaObjectSerializationCompatible(state);
	}

	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChanges(JApiClass jApiClass, long serialVersionUidOldDefault, long serialVersionUidNewDefault) {
		JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state;
		if (serialVersionUidOldDefault == serialVersionUidNewDefault) {
			state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
		} else {
			JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChanges = checkChanges(jApiClass);
			if (checkChanges == JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_BUT_SUID_EQUAL;
			} else {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
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
		state = checkChangesForFields(jApiClass, state);
		state = checkChangesForInterfaces(jApiClass, state);
		state = checkChangesForClassType(jApiClass, state);
		return state;
	}

	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChangesForClassType(JApiClass jApiClass, JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state) {
		JApiClassType classType = jApiClass.getClassType();
		if (classType.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			JApiClassType.ClassType oldClassType = classType.getOldTypeOptional().get();
			JApiClassType.ClassType newClassType = classType.getNewTypeOptional().get();
			if (oldClassType != newClassType) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
			}
		}
		return state;
	}

	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChangesForInterfaces(JApiClass jApiClass, JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state) {
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
		return state;
	}

	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChangesForFields(JApiClass jApiClass, JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state) {
		for (JApiField field : jApiClass.getFields()) {
			if (field.getChangeStatus() == JApiChangeStatus.REMOVED) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
			}
			if (field.getStaticModifier().getChangeStatus() == JApiChangeStatus.NEW && field.getStaticModifier().getNewModifier().get() == StaticModifier.STATIC) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
			}
			if (field.getTransientModifier().getChangeStatus() == JApiChangeStatus.NEW && field.getTransientModifier().getNewModifier().get() == TransientModifier.TRANSIENT) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
			}
			if (field.getType().getChangeStatus() == JApiChangeStatus.MODIFIED) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE;
			}
		}
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
		private Optional<Long> serialVersionUid = Optional.absent();
		private long serialVersionUidDefault = -1;
		private boolean serializable;
		private CtClass ctClass;

		public SerialVersionUidResult(CtClass ctClass) {
			this.ctClass = ctClass;
		}

		public Optional<Long> getSerialVersionUid() {
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
						serialVersionUid = Optional.of((Long)constantValue);
					}
				} catch (NotFoundException e) {
					try {
						SerialVersionUID.setSerialVersionUID(ctClass);
						CtField declaredField = ctClass.getDeclaredField(SERIAL_VERSION_UID);
						Object constantValue = declaredField.getConstantValue();
						if (constantValue instanceof Long) {
							serialVersionUid = Optional.of((Long)constantValue);
							serialVersionUidDefault = (Long)constantValue;
						}
						ctClass.removeField(declaredField);
					} catch (Exception ignored) {}
				}
				if (serialVersionUidDefault == -1) {
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
