package japicmp.model;

import com.google.common.base.Optional;
import japicmp.exception.JApiCmpException;
import javassist.*;

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
					state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_BUT_SUID_EQUAL;
				} else {
					state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
				}
			} else {
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
