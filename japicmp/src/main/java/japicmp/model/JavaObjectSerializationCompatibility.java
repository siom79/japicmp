package japicmp.model;

import japicmp.util.Optional;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.exception.JApiCmpException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.SerialVersionUID;

import java.io.Externalizable;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaObjectSerializationCompatibility {
	private static final Logger LOGGER = Logger.getLogger(JavaObjectSerializationCompatibility.class.getName());
	private static final String SERIAL_VERSION_UID = "serialVersionUID";

	public static JApiSerialVersionUid extractSerialVersionUid(JarArchiveComparatorOptions options, JarArchiveComparator jarArchiveComparator, Optional<CtClass> oldClass, Optional<CtClass> newClass) {
		SerialVersionUidResult resultOld = computeSerialVersionUid(options, oldClass, jarArchiveComparator);
		SerialVersionUidResult resultNew = computeSerialVersionUid(options, newClass, jarArchiveComparator);
		return new JApiSerialVersionUid(resultOld.serializable, resultNew.serializable, resultOld.serialVersionUidDefault,
			resultNew.serialVersionUidDefault, resultOld.serialVersionUid, resultNew.serialVersionUid);
	}

	public void evaluate(List<JApiClass> jApiClasses) {
		for (JApiClass jApiClass : jApiClasses) {
			computeChangeStatus(jApiClass);
		}
	}

	private static class SerialVersionUidResult {
		boolean serializable = false;
		Optional<Long> serialVersionUid = Optional.absent();
		Optional<Long> serialVersionUidDefault = Optional.absent();
	}

	private static SerialVersionUidResult computeSerialVersionUid(JarArchiveComparatorOptions options, Optional<CtClass> ctClassOptional, JarArchiveComparator jarArchiveComparator) {
		SerialVersionUidResult result = new SerialVersionUidResult();
		if (ctClassOptional.isPresent()) {
			CtClass ctClass = ctClassOptional.get();
			if (isCtClassSerializable(options, ctClass, jarArchiveComparator)) {
				result.serializable = true;
				try {
					CtField declaredField = ctClass.getDeclaredField(SERIAL_VERSION_UID);
					Object constantValue = declaredField.getConstantValue();
					if (constantValue instanceof Long) {
						result.serialVersionUid = Optional.of((Long) constantValue);
					}
				} catch (Exception e) {
					LOGGER.log(Level.FINE, "Failed to get serialVersionUid from class " + ctClass.getName() + ": " + e.getLocalizedMessage(), e);
					try {
						SerialVersionUID.setSerialVersionUID(ctClass);
						CtField declaredField = ctClass.getDeclaredField(SERIAL_VERSION_UID);
						Object constantValue = declaredField.getConstantValue();
						if (constantValue instanceof Long) {
							result.serialVersionUidDefault = Optional.of((Long) constantValue);
						}
						ctClass.removeField(declaredField);
					} catch (Exception ex) {
						LOGGER.log(Level.FINE, "Failed to compute default serialVersionUid for class " + ctClass.getName() + ": " + ex.getLocalizedMessage(), ex);
					}
				}
				if (!result.serialVersionUidDefault.isPresent()) {
					try {
						CtField declaredFieldOriginal = ctClass.getDeclaredField(SERIAL_VERSION_UID);
						ctClass.removeField(declaredFieldOriginal);
						SerialVersionUID.setSerialVersionUID(ctClass);
						CtField declaredField = ctClass.getDeclaredField(SERIAL_VERSION_UID);
						Object constantValue = declaredField.getConstantValue();
						if (constantValue instanceof Long) {
							result.serialVersionUidDefault = Optional.of((Long) constantValue);
						}
						ctClass.removeField(declaredField);
						ctClass.addField(declaredFieldOriginal);
					} catch (Exception ex) {
						LOGGER.log(Level.FINE, "Failed to compute default serialVersionUid for class " + ctClass.getName() + ": " + ex.getLocalizedMessage(), ex);
					}
				}
			}
		}
		return result;
	}

	private static boolean isCtClassSerializable(JarArchiveComparatorOptions options, CtClass clazz, JarArchiveComparator jarArchiveComparator) {
		ClassPool pool = clazz.getClassPool();
		try {
			return clazz.subtypeOf(pool.get("java.io.Serializable"));
		} catch (NotFoundException e) {
			if (options.getIgnoreMissingClasses().ignoreClass(e.getMessage())) {
				return false;
			} else {
				throw JApiCmpException.forClassLoading(e, clazz.getName(), jarArchiveComparator);
			}
		}
	}

	private void computeChangeStatus(JApiClass jApiClass) {
		JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.NOT_SERIALIZABLE;
		JApiSerialVersionUid jApiSerialVersionUid = jApiClass.getSerialVersionUid();
		if (jApiSerialVersionUid.isSerializableOld() || jApiSerialVersionUid.isSerializableNew()) {
			state = checkChanges(jApiClass);
			if (!state.isIncompatible()) {
				if (jApiSerialVersionUid.getSerialVersionUidInClassOld().isPresent() && jApiSerialVersionUid.getSerialVersionUidInClassNew().isPresent()) {
					Long suidOld = jApiSerialVersionUid.getSerialVersionUidInClassOld().get();
					Long suidNew = jApiSerialVersionUid.getSerialVersionUidInClassNew().get();
					if (suidOld.equals(suidNew)) {
						state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
					} else {
						state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SERIALVERSIONUID_MODIFIED;
					}
				} else if (jApiSerialVersionUid.getSerialVersionUidInClassOld().isPresent()) {
					Long suidOld = jApiSerialVersionUid.getSerialVersionUidInClassOld().get();
					if (jApiClass.getChangeStatus() != JApiChangeStatus.REMOVED) {
						if (jApiSerialVersionUid.isSerializableNew()) {
							if (jApiSerialVersionUid.getSerialVersionUidDefaultNew().isPresent()) {
								Long suidNewDefault = jApiSerialVersionUid.getSerialVersionUidDefaultNew().get();
								if (suidOld.equals(suidNewDefault)) {
									state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
								} else {
									state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SERIALVERSIONUID_REMOVED_AND_NOT_MATCHES_NEW_DEFAULT;
								}
							} else {
								state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CLASS_REMOVED;
							}
						} else {
							state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SERIALIZABLE_REMOVED;
						}
					} else {
						state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CLASS_REMOVED;
					}
				} else if (jApiSerialVersionUid.getSerialVersionUidInClassNew().isPresent()) {
					if (jApiClass.getChangeStatus() != JApiChangeStatus.NEW) {
						if (jApiSerialVersionUid.isSerializableOld()) {
							Long suidNew = jApiSerialVersionUid.getSerialVersionUidInClassNew().get();
							if (jApiSerialVersionUid.getSerialVersionUidDefaultOld().isPresent()) {
								Long suidOldDefault = jApiSerialVersionUid.getSerialVersionUidDefaultOld().get();
								if (suidNew.equals(suidOldDefault)) {
									state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
								} else {
									state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SERIALVERSIONUID_REMOVED_AND_NOT_MATCHES_NEW_DEFAULT;
								}
							} else {
								state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
							}
						}
					} else {
						state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
					}
				} else {
					if (jApiClass.getChangeStatus() == JApiChangeStatus.NEW) {
						state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
					} else if (jApiClass.getChangeStatus() == JApiChangeStatus.REMOVED) {
						state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CLASS_REMOVED;
					} else {
						if (!isEnum(jApiClass)) { //default serialVersionUID is ignored for enums (section 1.12 "Serialization of Enum Constants")
							if (jApiSerialVersionUid.getSerialVersionUidDefaultOld().isPresent() && jApiSerialVersionUid.getSerialVersionUidDefaultNew().isPresent()) {
								Long defaultOld = jApiSerialVersionUid.getSerialVersionUidDefaultOld().get();
								Long defaultNew = jApiSerialVersionUid.getSerialVersionUidDefaultNew().get();
								if (defaultOld.equals(defaultNew)) {
									state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
								} else {
									state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_DEFAULT_SERIALVERSIONUID_CHANGED;
								}
							} else if (jApiSerialVersionUid.getSerialVersionUidDefaultOld().isPresent()) {
								state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SERIALIZABLE_REMOVED;
							} else {
								state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
							}
						}
					}
				}
			}
		}
		jApiClass.setJavaObjectSerializationCompatible(state);
	}

	private boolean isEnum(JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().isPresent() && jApiClass.getClassType().getNewTypeOptional().get() == JApiClassType.ClassType.ENUM;
	}

	/**
	 * Checks compatibility of changes according to http://docs.oracle.com/javase/7/docs/platform/serialization/spec/version.html#5172.
	 *
	 * @param jApiClass the class to check
	 * @return either SERIALIZABLE_INCOMPATIBLE or SERIALIZABLE_COMPATIBLE
	 */
	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChanges(JApiClass jApiClass) {
		JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE;
		if (jApiClass.getChangeStatus() == JApiChangeStatus.REMOVED) {
			return JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CLASS_REMOVED;
		}
		state = checkChangesForClassType(jApiClass, state);
		if (state != JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE) {
			return state;
		}
		state = checkChangesForSuperclass(jApiClass, state);
		if (state != JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE) {
			return state;
		}
		state = checkChangesForFields(jApiClass, state);
		if (state != JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE) {
			return state;
		}
		state = checkChangesForInterfaces(jApiClass, state);
		return state;
	}

	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChangesForSuperclass(JApiClass jApiClass, JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state) {
		JApiSuperclass jApiClassSuperclass = jApiClass.getSuperclass();
		if (jApiClassSuperclass.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SUPERCLASS_MODIFIED;
		}
		return state;
	}

	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChangesForClassType(JApiClass jApiClass, JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state) {
		JApiClassType classType = jApiClass.getClassType();
		if (classType.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			JApiClassType.ClassType oldClassType = classType.getOldTypeOptional().get();
			JApiClassType.ClassType newClassType = classType.getNewTypeOptional().get();
			if (oldClassType != newClassType) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CLASS_TYPE_MODIFIED;
			}
		}
		return state;
	}

	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChangesForInterfaces(JApiClass jApiClass, JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state) {
		boolean serializableAdded = false;
		boolean serializableRemoved = false;
		boolean serializableUnchanged = false;
		boolean externalizableAdded = false;
		boolean externalizableRemoved = false;
		for (JApiImplementedInterface implementedInterface : jApiClass.getInterfaces()) {
			if (Serializable.class.getCanonicalName().equals(implementedInterface.getFullyQualifiedName())) {
				if (implementedInterface.getChangeStatus() == JApiChangeStatus.NEW) {
					serializableAdded = true;
				} else if (implementedInterface.getChangeStatus() == JApiChangeStatus.REMOVED) {
					serializableRemoved = true;
				} else if (implementedInterface.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
					serializableUnchanged = true;
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
		if (serializableRemoved) {
			state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SERIALIZABLE_REMOVED;
		}
		if (externalizableRemoved) {
			state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_EXTERNALIZABLE_REMOVED;
		}
		if ((serializableRemoved || serializableUnchanged || serializableAdded) && externalizableAdded) {
			state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CHANGED_FROM_SERIALIZABLE_TO_EXTERNALIZABLE;
		}
		if ((serializableUnchanged || serializableAdded) && externalizableRemoved) {
			state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_CHANGED_FROM_EXTERNALIZABLE_TO_SERIALIZABLE;
		}
		return state;
	}

	private JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus checkChangesForFields(JApiClass jApiClass, JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus state) {
		for (JApiField field : jApiClass.getFields()) {
			if (field.getChangeStatus() == JApiChangeStatus.REMOVED) {
				if (!"serialVersionUID".equals(field.getName())) {
					state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_REMOVED;
				}
			}
			JApiModifier<StaticModifier> staticModifier = field.getStaticModifier();
			if (staticModifier.getOldModifier().isPresent() && staticModifier.getNewModifier().isPresent()) {
				if (staticModifier.getOldModifier().get() == StaticModifier.NON_STATIC && staticModifier.getNewModifier().get() == StaticModifier.STATIC) {
					state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_CHANGED_FROM_NONSTATIC_TO_STATIC;
				}
			}
			JApiModifier<TransientModifier> transientModifier = field.getTransientModifier();
			if (transientModifier.getOldModifier().isPresent() && transientModifier.getNewModifier().isPresent()) {
				if (transientModifier.getOldModifier().get() == TransientModifier.NON_TRANSIENT && transientModifier.getNewModifier().get() == TransientModifier.TRANSIENT) {
					state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_CHANGED_FROM_NONTRANSIENT_TO_TRANSIENT;
				}
			}
			if (field.getType().getChangeStatus() == JApiChangeStatus.MODIFIED) {
				state = JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_TYPE_MODIFIED;
			}
		}
		return state;
	}
}
