package japicmp.compat;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.exception.JApiCmpException;
import japicmp.model.*;
import japicmp.util.ClassHelper;
import japicmp.util.SignatureParser;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.*;

import static japicmp.util.ModifierHelper.*;

public class CompatibilityChanges {
	private final JarArchiveComparator jarArchiveComparator;

	public CompatibilityChanges(JarArchiveComparator jarArchiveComparator) {
		this.jarArchiveComparator = jarArchiveComparator;
	}

	public void evaluate(List<JApiClass> classes) {
		Map<String, JApiClass> classMap = buildClassMap(classes);
		for (JApiClass clazz : classes) {
			evaluateBinaryCompatibility(clazz, classMap);
		}
	}

	private Map<String, JApiClass> buildClassMap(List<JApiClass> classes) {
		Map<String, JApiClass> classMap = new HashMap<>();
		for (JApiClass clazz : classes) {
			classMap.put(clazz.getFullyQualifiedName(), clazz);
		}
		return classMap;
	}

	private void evaluateBinaryCompatibility(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		if (jApiClass.getAccessModifier().hasChangedTo(AccessModifier.PUBLIC)) {
			return; // class appears as "new" public class
		}
		if (jApiClass.getChangeStatus() == JApiChangeStatus.REMOVED) {
			addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.CLASS_REMOVED);
		} else if (jApiClass.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			// section 13.4.1 of "Java Language Specification" SE7
			if (jApiClass.getAbstractModifier().hasChangedFromTo(AbstractModifier.NON_ABSTRACT, AbstractModifier.ABSTRACT)) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.CLASS_NOW_ABSTRACT);
			}
			// section 13.4.2 of "Java Language Specification" SE7
			if (jApiClass.getFinalModifier().hasChangedFromTo(FinalModifier.NON_FINAL, FinalModifier.FINAL)) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.CLASS_NOW_FINAL);
			}
			// section 13.4.3 of "Java Language Specification" SE7
			if (jApiClass.getAccessModifier().hasChangedFrom(AccessModifier.PUBLIC)) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.CLASS_NO_LONGER_PUBLIC);
			}
		}
		// section 13.4.4 of "Java Language Specification" SE7
		checkIfSuperclassesOrInterfacesChangedIncompatible(jApiClass, classMap);
		checkIfMethodsHaveChangedIncompatible(jApiClass, classMap);
		checkIfConstructorsHaveChangedIncompatible(jApiClass, classMap);
		checkIfFieldsHaveChangedIncompatible(jApiClass, classMap);
		if (jApiClass.getClassType().getChangeStatus() == JApiChangeStatus.MODIFIED) {
			addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.CLASS_TYPE_CHANGED);
		}
		checkIfAnnotationsChanged(jApiClass, classMap);
		if (hasModifierLevelDecreased(jApiClass)) {
			addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.CLASS_LESS_ACCESSIBLE);
		}
		if (jApiClass.getChangeStatus().isNotNewOrRemoved()) {
			checkIfGenericTemplatesHaveChanged(jApiClass);
		}
	}

	private void checkIfGenericTemplatesHaveChanged(JApiHasGenericTemplates jApiHasGenericTemplates) {
		for (JApiGenericTemplate jApiGenericTemplate : jApiHasGenericTemplates.getGenericTemplates()) {
			if (jApiGenericTemplate.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				addCompatibilityChange(jApiHasGenericTemplates, JApiCompatibilityChangeType.CLASS_GENERIC_TEMPLATE_CHANGED);
				break;
			}
		}
		for (JApiGenericTemplate jApiGenericTemplate : jApiHasGenericTemplates.getGenericTemplates()) {
			JApiChangeStatus changeStatus = jApiGenericTemplate.getChangeStatus();
			if (changeStatus == JApiChangeStatus.MODIFIED || changeStatus == JApiChangeStatus.UNCHANGED) {
				if (!SignatureParser.equalGenericTypes(jApiGenericTemplate.getOldGenericTypes(), jApiGenericTemplate.getNewGenericTypes())) {
					addCompatibilityChange(jApiGenericTemplate, JApiCompatibilityChangeType.CLASS_GENERIC_TEMPLATE_GENERICS_CHANGED);
					addCompatibilityChange(jApiHasGenericTemplates, JApiCompatibilityChangeType.CLASS_GENERIC_TEMPLATE_GENERICS_CHANGED);
					break;
				}
			}
		}
	}

	private void checkIfFieldsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		for (final JApiField field : jApiClass.getFields()) {
			// section 8.3.1.4 of "Java Language Specification" SE7
			if (field.getVolatileModifier().hasChangedFromTo(VolatileModifier.NON_VOLATILE, VolatileModifier.VOLATILE)) {
				addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_NOW_VOLATILE);
			}
			if (field.getVolatileModifier().hasChangedFromTo(VolatileModifier.VOLATILE, VolatileModifier.NON_VOLATILE)) {
				addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_NO_LONGER_VOLATILE);
			}
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(field) && field.getChangeStatus() == JApiChangeStatus.REMOVED) {
				ArrayList<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, (superclass, classMap1, changeStatusOfSuperclass) -> {
					int movedToSuperclass = 0;
					for (JApiField superclassField : superclass.getFields()) {
						if (superclassField.getName().equals(field.getName()) && fieldTypeMatches(superclassField, field) && isNotPrivate(superclassField)) {
							movedToSuperclass = 1;
						}
					}
					return movedToSuperclass;
				});
				boolean movedToSuperclass = false;
				for (Integer returnValue : returnValues) {
					if (returnValue == 1) {
						movedToSuperclass = true;
						break;
					}
				}
				if (!movedToSuperclass) {
					addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_REMOVED);
				}
			}
			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(field)) {
				addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_LESS_ACCESSIBLE);
			}
			// section 13.4.8 of "Java Language Specification" SE7
			if (isNotPrivate(field) && field.getChangeStatus() == JApiChangeStatus.NEW) {
				ArrayList<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, (superclass, classMap12, changeStatusOfSuperclass) -> {
					int changedIncompatible = 0;
					for (JApiField superclassField : superclass.getFields()) {
						if (superclassField.getName().equals(field.getName()) && fieldTypeMatches(superclassField, field)) {
							boolean superclassFieldIsStatic = false;
							boolean subclassFieldIsStatic = false;
							boolean accessModifierSubclassLess = false;
							if (field.getStaticModifier().getNewModifier().isPresent() && field.getStaticModifier().getNewModifier().get() == StaticModifier.STATIC) {
								subclassFieldIsStatic = true;
							}
							if (superclassField.getStaticModifier().getNewModifier().isPresent() && superclassField.getStaticModifier().getNewModifier().get() == StaticModifier.STATIC && superclassField.getChangeStatus() != JApiChangeStatus.NEW) {
								superclassFieldIsStatic = true;
							}
							if (field.getAccessModifier().getNewModifier().isPresent() && superclassField.getAccessModifier().getNewModifier().isPresent()) {
								if (field.getAccessModifier().getNewModifier().get().getLevel() < superclassField.getAccessModifier().getNewModifier().get().getLevel() && superclassField.getChangeStatus() != JApiChangeStatus.NEW) {
									accessModifierSubclassLess = true;
								}
							}
							if (superclassFieldIsStatic) {
								if (subclassFieldIsStatic) {
									changedIncompatible = 1;
								}
							}
							if (accessModifierSubclassLess) {
								changedIncompatible = 2;
							}
						}
					}
					return changedIncompatible;
				});
				for (int returnValue : returnValues) {
					if (returnValue == 1) {
						addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_STATIC_AND_OVERRIDES_STATIC);
					} else if (returnValue == 2) {
						addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS);
					}
				}
			}
			// section 13.4.9 of "Java Language Specification" SE7
			if (isNotPrivate(field) && field.getFinalModifier().hasChangedFromTo(FinalModifier.NON_FINAL, FinalModifier.FINAL)) {
				addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_NOW_FINAL);
			}
			// section 13.4.10 of "Java Language Specification" SE7
			if (isNotPrivate(field)) {
				if (field.getStaticModifier().hasChangedFromTo(StaticModifier.NON_STATIC, StaticModifier.STATIC)) {
					addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_NOW_STATIC);
				}
				if (field.getStaticModifier().hasChangedFromTo(StaticModifier.STATIC, StaticModifier.NON_STATIC)) {
					addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_NO_LONGER_STATIC);
				}
			}
			// section 13.4.11 of "Java Language Specification" SE7
			if (field.getTransientModifier().hasChangedFromTo(TransientModifier.NON_TRANSIENT, TransientModifier.TRANSIENT)) {
				addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_NOW_TRANSIENT);
			}
			if (field.getTransientModifier().hasChangedFromTo(TransientModifier.TRANSIENT, TransientModifier.NON_TRANSIENT)) {
				addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_NO_LONGER_TRANSIENT);
			}
			if (isNotPrivate(field) && field.getType().hasChanged()) {
				addCompatibilityChange(field, JApiCompatibilityChangeType.FIELD_TYPE_CHANGED);
			}
			checkIfAnnotationsChanged(field, classMap);
			checkIfFieldGenericsChanged(field);
		}
	}

	private interface OnSuperclassCallback<T> {
		T callback(JApiClass superclass, Map<String, JApiClass> classMap, JApiChangeStatus changeStatusOfSuperclass);
	}

	private interface OnImplementedInterfaceCallback<T> {
		T callback(JApiClass implementedInterface, Map<String, JApiClass> classMap);
	}

	private <T> void forAllSuperclasses(JApiClass jApiClass, Map<String, JApiClass> classMap, List<T> returnValues, OnSuperclassCallback<T> onSuperclassCallback) {
		JApiSuperclass superclass = jApiClass.getSuperclass();
		if (superclass.getNewSuperclassName().isPresent()) {
			String newSuperclassName = superclass.getNewSuperclassName().get();
			superclass.getJApiClass().ifPresent(x -> classMap.putIfAbsent(newSuperclassName, x));
			JApiClass foundClass = classMap.computeIfAbsent(newSuperclassName, this::loadAndEvaluate);
			superclass.setJApiClass(foundClass);
			T returnValue = onSuperclassCallback.callback(foundClass, classMap, superclass.getChangeStatus());
			returnValues.add(returnValue);
			forAllSuperclasses(foundClass, classMap, returnValues, onSuperclassCallback);
		}
	}

	private JApiClass loadAndEvaluate(String className) {
		JApiClass loaded = loadClass(className, EnumSet.of(Classpath.NEW_CLASSPATH));
		evaluate(Collections.singletonList(loaded));
		return loaded;
	}

	private enum Classpath {
		OLD_CLASSPATH,
		NEW_CLASSPATH
	}

	private JApiClass loadClass(String newSuperclassName, EnumSet<Classpath> classpaths) {
		JApiClass foundClass;
		Optional<CtClass> oldClassOptional = Optional.empty();
		Optional<CtClass> newClassOptional = Optional.empty();
		JarArchiveComparatorOptions.ClassPathMode classPathMode = this.jarArchiveComparator.getJarArchiveComparatorOptions().getClassPathMode();
		if (classPathMode == JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH) {
			ClassPool classPool = this.jarArchiveComparator.getCommonClassPool();
			try {
				oldClassOptional = Optional.of(classPool.get(newSuperclassName));
			} catch (NotFoundException ignored) {
			}
			try {
				newClassOptional = Optional.of(classPool.get(newSuperclassName));
			} catch (NotFoundException ignored) {
			}
		} else {
			if (classpaths.contains(Classpath.OLD_CLASSPATH)) {
				ClassPool oldClassPool = this.jarArchiveComparator.getOldClassPool();
				try {
					oldClassOptional = Optional.of(oldClassPool.get(newSuperclassName));
				} catch (NotFoundException ignored) {
				}
			}
			if (classpaths.contains(Classpath.NEW_CLASSPATH)) {
				ClassPool newClassPool = this.jarArchiveComparator.getNewClassPool();
				try {
					newClassOptional = Optional.of(newClassPool.get(newSuperclassName));
				} catch (NotFoundException ignored) {
				}
			}
		}
		if (!oldClassOptional.isPresent() && !newClassOptional.isPresent()) {
			if (!this.jarArchiveComparator.getJarArchiveComparatorOptions().getIgnoreMissingClasses().ignoreClass(newSuperclassName)) {
				throw JApiCmpException.forClassLoading(newSuperclassName, this.jarArchiveComparator);
			}
		}
		JApiClassType classType;
		JApiChangeStatus changeStatus = JApiChangeStatus.UNCHANGED;
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			classType = new JApiClassType(Optional.of(ClassHelper.getType(oldClassOptional.get())), Optional.of(ClassHelper.getType(newClassOptional.get())), JApiChangeStatus.UNCHANGED);
		} else if (oldClassOptional.isPresent() && !newClassOptional.isPresent()) {
			classType = new JApiClassType(Optional.of(ClassHelper.getType(oldClassOptional.get())), Optional.empty(), JApiChangeStatus.REMOVED);
		} else if (!oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			classType = new JApiClassType(Optional.empty(), Optional.of(ClassHelper.getType(newClassOptional.get())), JApiChangeStatus.NEW);
		} else {
			classType = new JApiClassType(Optional.empty(), Optional.empty(), JApiChangeStatus.UNCHANGED);
		}
		foundClass = new JApiClass(this.jarArchiveComparator, newSuperclassName, oldClassOptional, newClassOptional, changeStatus, classType);
		return foundClass;
	}

	private boolean fieldTypeMatches(JApiField field1, JApiField field2) {
		Optional<String> newType1 = field1.getType().getNewTypeOptional();
		Optional<String> newType2 = field2.getType().getNewTypeOptional();
		if (newType1.isPresent() && newType2.isPresent()) {
			return newType1.equals(newType2);
		}
		return true;
	}

	private void checkIfConstructorsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		for (JApiConstructor constructor : jApiClass.getConstructors()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(constructor) && constructor.getChangeStatus() == JApiChangeStatus.REMOVED) {
				addCompatibilityChange(constructor, JApiCompatibilityChangeType.CONSTRUCTOR_REMOVED);
			}
			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(constructor)) {
				if (isNotFinal(jApiClass) || constructor.getAccessModifier().hasChangedFrom(AccessModifier.PUBLIC)) {
					addCompatibilityChange(constructor, JApiCompatibilityChangeType.CONSTRUCTOR_LESS_ACCESSIBLE);
				}
			}
			checkIfExceptionIsNowChecked(constructor);
			checkIfAnnotationsChanged(constructor, classMap);
			checkIfVarargsChanged(constructor);
			checkIfParametersGenericsChanged(constructor);
			if (jApiClass.getChangeStatus().isNotNewOrRemoved()) {
				checkIfGenericTemplatesHaveChanged(constructor);
			}
		}
	}

	private static boolean isNotFinal(JApiClass jApiClass) {
		return !jApiClass.getFinalModifier().getNewModifier().map(FinalModifier.FINAL::equals).orElse(false);
	}

	private void checkIfMethodsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		for (final JApiMethod method : jApiClass.getMethods()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getChangeStatus() == JApiChangeStatus.REMOVED) {
				final List<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, (superclass, classMap12, changeStatusOfSuperclass) -> {
					for (JApiMethod superMethod : superclass.getMethods()) {
						if (superMethod.getName().equals(method.getName()) && superMethod.hasSameSignature(method)) {
							return 1;
						}
					}
					return 0;
				});
				checkIfMethodHasBeenPulledUp(jApiClass, classMap, method, returnValues);
				boolean superclassHasSameMethod = false;
				for (Integer returnValue : returnValues) {
					if (returnValue == 1) {
						superclassHasSameMethod = true;
						break;
					}
				}
				if (!superclassHasSameMethod) {
					addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_REMOVED);
				}
			}

			if (!isInterface(jApiClass) && method.getChangeStatus() == JApiChangeStatus.NEW && method.getAccessModifier().getNewModifier().get().getLevel() == AccessModifier.PUBLIC.getLevel()) {
				addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_ADDED_TO_PUBLIC_CLASS);
			}

			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(method)) {
				addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_LESS_ACCESSIBLE);
			}
			// section 13.4.12 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getChangeStatus() == JApiChangeStatus.NEW) {
				List<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, (superclass, classMap13, changeStatusOfSuperclass) -> {
					for (JApiMethod superMethod : superclass.getMethods()) {
						if (superMethod.getName().equals(method.getName()) && superMethod.hasSameSignature(method)) {
							if (superMethod.getAccessModifier().getNewModifier().isPresent() && method.getAccessModifier().getNewModifier().isPresent()) {
								if (superMethod.getAccessModifier().getNewModifier().get().getLevel() > method.getAccessModifier().getNewModifier().get().getLevel()) {
									return 1;
								}
							}
							if (superMethod.getStaticModifier().getNewModifier().map(StaticModifier.NON_STATIC::equals).orElse(false) &&
								method.getStaticModifier().getNewModifier().map(StaticModifier.STATIC::equals).orElse(false)) {
								return 2;
							}
						}
					}
					return 0;
				});
				for (Integer returnValue : returnValues) {
					if (returnValue == 1) {
						addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS);
					} else if (returnValue == 2) {
						addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_IS_STATIC_AND_OVERRIDES_NOT_STATIC);
					}
				}
			}
			checkIfReturnTypeChanged(method);
			// section 13.4.16 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getAbstractModifier().hasChangedFromTo(AbstractModifier.NON_ABSTRACT, AbstractModifier.ABSTRACT)) {
				addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_NOW_ABSTRACT);
			}
			// section 13.4.17 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getFinalModifier().hasChangedFromTo(FinalModifier.NON_FINAL, FinalModifier.FINAL)) {
				if (jApiClass.getFinalModifier().getOldModifier().map(x -> x != FinalModifier.FINAL).orElse(false) &&
					!method.getStaticModifier().getOldModifier().map(StaticModifier.STATIC::equals).orElse(false)) {
					addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_NOW_FINAL);
				}
			}
			if (isNotPrivate(method) && method.getChangeStatus() == JApiChangeStatus.REMOVED) {
				List<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, (superclass, classMap1, changeStatusOfSuperclass) -> {
					for (JApiMethod superMethod : superclass.getMethods()) {
						if (areMatching(superMethod, method)) {
							if (method.getFinalModifier().getOldModifier().map(FinalModifier.NON_FINAL::equals).orElse(false)
								&& superMethod.getFinalModifier().getNewModifier().map(FinalModifier.FINAL::equals).orElse(false)) {
								addCompatibilityChange(superMethod, JApiCompatibilityChangeType.METHOD_NOW_FINAL);
								return 1;
							}
						}
					}
					return 0;
				});
				if (returnValues.stream().anyMatch(value -> value == 1)) {
					addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_NOW_FINAL);
					addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_MOVED_TO_SUPERCLASS);
				}
			}
			// section 13.4.18 of "Java Language Specification" SE7
			if (isNotPrivate(method) && !isInterface(method.getjApiClass())) {
				if (method.getStaticModifier().hasChangedFromTo(StaticModifier.NON_STATIC, StaticModifier.STATIC)) {
					addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_NOW_STATIC);
				}
				if (method.getStaticModifier().hasChangedFromTo(StaticModifier.STATIC, StaticModifier.NON_STATIC)) {
					addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_NO_LONGER_STATIC);
				}
			}
			checkAbstractMethod(jApiClass, classMap, method);
			checkIfExceptionIsNowChecked(method);
			checkIfAnnotationsChanged(method, classMap);
			checkIfVarargsChanged(method);
			checkIfParametersGenericsChanged(method);
			if (method.getChangeStatus().isNotNewOrRemoved()) {
				checkIfGenericTemplatesHaveChanged(method);
			}
		}
	}

	private void checkIfReturnTypeChanged(JApiMethod method) {
		// section 13.4.15 of "Java Language Specification" SE7 (Method Result Type)
		if (method.getReturnType().getChangeStatus() == JApiChangeStatus.MODIFIED) {
			JApiCompatibilityChange change = addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_RETURN_TYPE_CHANGED);
			if (method.getAccessModifier().hasChangedToMoreVisible()) {
				change.setSourceCompatible(true);
				change.setBinaryCompatible(true);
			}
		}
		if (method.getChangeStatus() == JApiChangeStatus.MODIFIED ||
			method.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
			if (!SignatureParser.equalGenericTypes(method.getReturnType().getOldGenericTypes(), method.getReturnType().getNewGenericTypes())) {
				addCompatibilityChange(method.getReturnType(), JApiCompatibilityChangeType.METHOD_RETURN_TYPE_GENERICS_CHANGED);
			}
		}
	}

	private void checkIfParametersGenericsChanged(JApiBehavior jApiBehavior) {
		if (jApiBehavior.getChangeStatus() == JApiChangeStatus.MODIFIED ||
			jApiBehavior.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
			for (JApiParameter jApiParameter : jApiBehavior.getParameters()) {
				if (!SignatureParser.equalGenericTypes(jApiParameter.getOldGenericTypes(), jApiParameter.getNewGenericTypes())) {
					addCompatibilityChange(jApiParameter, JApiCompatibilityChangeType.METHOD_PARAMETER_GENERICS_CHANGED);
				}
			}
		}
	}

	private void checkIfFieldGenericsChanged(JApiField jApiField) {
		if (jApiField.getChangeStatus() == JApiChangeStatus.MODIFIED ||
			jApiField.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
			if (!SignatureParser.equalGenericTypes(jApiField.getOldGenericTypes(), jApiField.getNewGenericTypes())) {
				addCompatibilityChange(jApiField, JApiCompatibilityChangeType.FIELD_GENERICS_CHANGED);
			}
		}
	}

	private void checkIfAnnotationsChanged(JApiHasAnnotations jApiHasAnnotations, Map<String, JApiClass> classMap) {
		boolean isNoAnnotations = this.jarArchiveComparator.getJarArchiveComparatorOptions().isNoAnnotations();
		if (!isNoAnnotations) {
			for (JApiAnnotation annotation : jApiHasAnnotations.getAnnotations()) {
				final JApiChangeStatus status = annotation.getChangeStatus();
				if (status == JApiChangeStatus.REMOVED) {
					addCompatibilityChange(jApiHasAnnotations, JApiCompatibilityChangeType.ANNOTATION_REMOVED);
				} else {
					boolean isDeprecated = annotation.getFullyQualifiedName().equals(Deprecated.class.getName());
					if (isDeprecated && status == JApiChangeStatus.NEW) {
						addCompatibilityChange(jApiHasAnnotations, JApiCompatibilityChangeType.ANNOTATION_DEPRECATED_ADDED);
					} else {
						JApiClass annotationClass = classMap.computeIfAbsent(annotation.getFullyQualifiedName(), fqn -> loadClass(fqn, EnumSet.allOf(Classpath.class)));
						annotation.setJApiClass(annotationClass);
						if (status == JApiChangeStatus.NEW) {
							addCompatibilityChange(jApiHasAnnotations, JApiCompatibilityChangeType.ANNOTATION_ADDED);
						} else {
							for (JApiAnnotationElement annotationElement : annotation.getElements()) {
								if (annotationElement.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
									addCompatibilityChange(jApiHasAnnotations, JApiCompatibilityChangeType.ANNOTATION_MODIFIED);
								}
							}
						}
					}
				}
			}
		}
	}

	private void checkIfVarargsChanged(JApiBehavior behavior) {
		if (behavior.getVarargsModifier().hasChangedFromTo(VarargsModifier.VARARGS, VarargsModifier.NON_VARARGS)) {
			addCompatibilityChange(behavior, JApiCompatibilityChangeType.METHOD_NO_LONGER_VARARGS);
		}
		if (behavior.getVarargsModifier().hasChangedFromTo(VarargsModifier.NON_VARARGS, VarargsModifier.VARARGS)) {
			addCompatibilityChange(behavior, JApiCompatibilityChangeType.METHOD_NOW_VARARGS);
		}
	}

	private void checkAbstractMethod(JApiClass jApiClass, Map<String, JApiClass> classMap, JApiMethod method) {
		if (isInterface(jApiClass)) {
			if (jApiClass.getChangeStatus() != JApiChangeStatus.NEW) {
				if (method.getChangeStatus() == JApiChangeStatus.NEW && !isSynthetic(method)) {
					List<JApiMethod> methodsWithSameSignature = getMethodsInImplementedInterfacesWithSameSignature(jApiClass, classMap, method);
					if (methodsWithSameSignature.isEmpty()) {
						// new default method in interface
						if (method.getAbstractModifier().getNewModifier().get() == AbstractModifier.NON_ABSTRACT) {
							if (method.getStaticModifier().getNewModifier().get() == StaticModifier.STATIC) {
								addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_NEW_STATIC_ADDED_TO_INTERFACE);
							} else {
								addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_NEW_DEFAULT);
							}
						} else {
							addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_ADDED_TO_INTERFACE);
						}
					} else {
						boolean allNew = true;
						for (JApiMethod jApiMethod : methodsWithSameSignature) {
							if (jApiMethod.getChangeStatus() != JApiChangeStatus.NEW) {
								allNew = false;
								break;
							}
						}
						if (allNew) {
							addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_ADDED_TO_INTERFACE);
						}
					}
				} else if (method.getChangeStatus() == JApiChangeStatus.MODIFIED || method.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
					JApiModifier<AbstractModifier> abstractModifier = method.getAbstractModifier();
					if (abstractModifier.getOldModifier().map(AbstractModifier.ABSTRACT::equals).orElse(false) &&
						abstractModifier.getNewModifier().map(AbstractModifier.NON_ABSTRACT::equals).orElse(false)) {
						// method changed from abstract to default
						addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_ABSTRACT_NOW_DEFAULT);
					}
					JApiModifier<StaticModifier> staticModifier = method.getStaticModifier();
					if (staticModifier.hasChangedFromTo(StaticModifier.NON_STATIC, StaticModifier.STATIC)) {
						addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_NON_STATIC_IN_INTERFACE_NOW_STATIC);
					} else if (staticModifier.hasChangedFromTo(StaticModifier.STATIC, StaticModifier.NON_STATIC)) {
						addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_STATIC_IN_INTERFACE_NO_LONGER_STATIC);
					}
				}
			}
		} else {
			if (isAbstract(method)) {
				if (jApiClass.getChangeStatus() != JApiChangeStatus.NEW) {
					if (method.getChangeStatus() == JApiChangeStatus.NEW && !isSynthetic(method)) {
						List<JApiMethod> overriddenMethods = getOverriddenMethods(jApiClass, classMap, method);
						boolean overridesAbstract = false;
						for (JApiMethod jApiMethod : overriddenMethods) {
							if (isAbstract(jApiMethod) && jApiMethod.getChangeStatus() != JApiChangeStatus.NEW) {
								overridesAbstract = true;
							}
						}
						List<JApiMethod> implementedMethods = getMethodsInImplementedInterfacesWithSameSignature(jApiClass, classMap, method);
						if (!implementedMethods.isEmpty()) {
							overridesAbstract = true;
						}
						if (!overridesAbstract) {
							addCompatibilityChange(method, JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_TO_CLASS);
						}
					}
				}
			}
		}
	}

	private List<JApiMethod> getOverriddenMethods(final JApiClass jApiClass, final Map<String, JApiClass> classMap, final JApiMethod method) {
		ArrayList<JApiMethod> jApiMethods = new ArrayList<>();
		forAllSuperclasses(jApiClass, classMap, jApiMethods, (superclass, classMap1, changeStatusOfSuperclass) -> {
			for (JApiMethod jApiMethod : superclass.getMethods()) {
				if (isAbstract(jApiMethod)) {
					if (jApiMethod.getName().equals(method.getName()) && jApiMethod.hasSameSignature(method)) {
						return jApiMethod;
					}
				}
			}
			return null;
		});
		return removeNullValues(jApiMethods);
	}

	private List<JApiMethod> removeNullValues(ArrayList<JApiMethod> jApiMethods) {
		ArrayList<JApiMethod> returnValues = new ArrayList<>();
		for (JApiMethod jApiMethod : jApiMethods) {
			if (jApiMethod != null) {
				returnValues.add(jApiMethod);
			}
		}
		return returnValues;
	}

	private List<JApiMethod> getMethodsInImplementedInterfacesWithSameSignature(final JApiClass jApiClass, final Map<String, JApiClass> classMap, final JApiMethod method) {
		ArrayList<JApiMethod> jApiMethods = new ArrayList<>();
		forAllImplementedInterfaces(jApiClass, classMap, jApiMethods, new ArrayList<>(), (implementedInterface, classMap1) -> {
			for (JApiMethod jApiMethod : implementedInterface.getMethods()) {
				if (jApiMethod.getName().equals(method.getName()) && jApiMethod.hasSameSignature(method)) {
					return jApiMethod;
				}
			}
			return null;
		});
		return removeNullValues(jApiMethods);
	}

	private boolean isAbstract(JApiHasAbstractModifier jApiHasAbstractModifier) {
		return jApiHasAbstractModifier.getAbstractModifier().getNewModifier().map(AbstractModifier.ABSTRACT::equals).orElse(false);
	}

	private void checkIfExceptionIsNowChecked(JApiBehavior behavior) {
		for (JApiException exception : behavior.getExceptions()) {
			if (exception.getChangeStatus() == JApiChangeStatus.NEW && exception.isCheckedException() && behavior.getChangeStatus() != JApiChangeStatus.NEW) {
				addCompatibilityChange(behavior, JApiCompatibilityChangeType.METHOD_NOW_THROWS_CHECKED_EXCEPTION);
			}
			if (exception.getChangeStatus() == JApiChangeStatus.REMOVED && exception.isCheckedException() && behavior.getChangeStatus() != JApiChangeStatus.REMOVED) {
				addCompatibilityChange(behavior, JApiCompatibilityChangeType.METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION);
			}
		}
	}

	private boolean isClass(final JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().map(JApiClassType.ClassType.CLASS::equals).orElse(false);
	}

	private boolean isInterface(final JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().map(JApiClassType.ClassType.INTERFACE::equals).orElse(false);
	}

	private boolean isAnnotation(final JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().map(JApiClassType.ClassType.ANNOTATION::equals).orElse(false);
	}

	private boolean isEnum(final JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().map(JApiClassType.ClassType.ENUM::equals).orElse(false);
	}

	private void checkIfMethodHasBeenPulledUp(JApiClass jApiClass, Map<String, JApiClass> classMap, final JApiMethod method, List<Integer> returnValues) {
		forAllImplementedInterfaces(jApiClass, classMap, returnValues, new ArrayList<>(), (implementedInterface, classMap1) -> {
			for (JApiMethod superMethod : implementedInterface.getMethods()) {
				if (superMethod.getName().equals(method.getName()) && superMethod.hasSameSignature(method)) {
					return 1;
				}
			}
			return 0;
		});
	}

	private <T> void forAllImplementedInterfaces(JApiClass jApiClass, Map<String, JApiClass> classMap, List<T> returnValues, List<JApiImplementedInterface> visited,
												 OnImplementedInterfaceCallback<T> onImplementedInterfaceCallback) {
		List<JApiImplementedInterface> interfaces = jApiClass.getInterfaces();
		for (JApiImplementedInterface implementedInterface : interfaces) {
			String fullyQualifiedName = implementedInterface.getFullyQualifiedName();
			JApiClass foundClass = classMap.get(fullyQualifiedName);
			if (foundClass != null && visited.stream().noneMatch(v -> v.getFullyQualifiedName().equals(implementedInterface.getFullyQualifiedName()))) {
				T returnValue = onImplementedInterfaceCallback.callback(foundClass, classMap);
				returnValues.add(returnValue);
				visited.add(implementedInterface);
				forAllImplementedInterfaces(foundClass, classMap, returnValues, visited, onImplementedInterfaceCallback);
			}
		}
	}

	/**
	 * Is a method implemented in a super class
	 *
	 * @param jApiMethod the method
	 * @return <code>true</code> if it is implemented in a super class
	 */
	private boolean isImplemented(JApiMethod jApiMethod, JApiClass aClass) {
		while (aClass != null) {
			for (JApiMethod method : aClass.getMethods()) {
				if (jApiMethod.getName().equals(method.getName()) && jApiMethod.hasSameParameter(method) &&
					!isAbstract(method) && method.getChangeStatus() != JApiChangeStatus.REMOVED && isNotPrivate(method)) {
					return true;
				}
			}

			aClass = Optional.ofNullable(aClass.getSuperclass()).flatMap(JApiSuperclass::getJApiClass).orElse(null);
		}
		return false;
	}

	private void checkIfSuperclassesOrInterfacesChangedIncompatible(final JApiClass jApiClass, Map<String, JApiClass> classMap) {
		final JApiSuperclass superclass = jApiClass.getSuperclass();
		// section 13.4.4 of "Java Language Specification" SE7
		if (superclass.getChangeStatus() == JApiChangeStatus.UNCHANGED
			|| superclass.getChangeStatus() == JApiChangeStatus.MODIFIED
			|| superclass.getChangeStatus() == JApiChangeStatus.REMOVED) {
			final List<JApiMethod> implementedMethods = new ArrayList<>();
			final List<JApiMethod> removedAndNotOverriddenMethods = new ArrayList<>();
			final List<JApiField> fields = new ArrayList<>();
			final List<JApiField> removedAndNotOverriddenFields = new ArrayList<>();
			for (JApiMethod jApiMethod : jApiClass.getMethods()) {
				if (!isAbstract(jApiMethod) && jApiMethod.getChangeStatus() != JApiChangeStatus.REMOVED && isNotPrivate(jApiMethod)) {
					implementedMethods.add(jApiMethod);
				}
			}
			for (JApiField jApiField : jApiClass.getFields()) {
				if (jApiField.getChangeStatus() != JApiChangeStatus.REMOVED && isNotPrivate(jApiField)) {
					fields.add(jApiField);
				}
			}
			forAllSuperclasses(jApiClass, classMap, new ArrayList<>(), (superclass1, classMap1, changeStatusOfSuperclass) -> {
				for (JApiMethod jApiMethod : superclass1.getMethods()) {
					if (!isAbstract(jApiMethod) && jApiMethod.getChangeStatus() != JApiChangeStatus.REMOVED && isNotPrivate(jApiMethod)) {
						implementedMethods.add(jApiMethod);
					}
					removedAndNotOverriddenMethods.removeIf(removedAndNotOverriddenMethod ->
						jApiMethod.getName().equals(removedAndNotOverriddenMethod.getName()) &&
							jApiMethod.hasSameSignature(removedAndNotOverriddenMethod)
					);
				}
				for (JApiField jApiField : superclass1.getFields()) {
					if (jApiField.getChangeStatus() != JApiChangeStatus.REMOVED && isNotPrivate(jApiField)) {
						fields.add(jApiField);
					}
					removedAndNotOverriddenFields.removeIf(removedAndNotOverriddenField ->
						jApiField.getName().equals(removedAndNotOverriddenField.getName()) &&
							hasSameType(jApiField, removedAndNotOverriddenField)
					);
				}
				for (JApiMethod jApiMethod : superclass1.getMethods()) {
					if (jApiMethod.getChangeStatus() == JApiChangeStatus.REMOVED && !isImplemented(jApiMethod, jApiMethod.getjApiClass())) {
						boolean implemented = false;
						for (JApiMethod implementedMethod : implementedMethods) {
							if (jApiMethod.getName().equals(implementedMethod.getName()) && jApiMethod.hasSameSignature(implementedMethod)) {
								implemented = true;
								break;
							}
						}
						if (!implemented) {
							removedAndNotOverriddenMethods.add(jApiMethod);
						}
					}
				}
				for (JApiField jApiField : superclass1.getFields()) {
					if (jApiField.getChangeStatus() == JApiChangeStatus.REMOVED) {
						boolean overridden = false;
						for (JApiField field : fields) {
							if (field.getName().equals(jApiField.getName()) && hasSameType(jApiField, field)) {
								overridden = true;
							}
						}
						if (!overridden) {
							removedAndNotOverriddenFields.add(jApiField);
						}
					}
				}
				return 0;
			});
			if (!removedAndNotOverriddenMethods.isEmpty()) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.METHOD_REMOVED_IN_SUPERCLASS);
			}
			if (!removedAndNotOverriddenFields.isEmpty()) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.FIELD_REMOVED_IN_SUPERCLASS);
			}
			if (superclass.getOldSuperclassName().isPresent() && superclass.getNewSuperclassName().isPresent()) {
				if (!superclass.getOldSuperclassName().equals(superclass.getNewSuperclassName())) {
					String oldSuperclassName = superclass.getOldSuperclassName().get();
					String newSuperclassName = superclass.getNewSuperclassName().get();
					boolean superClassChangedToObject = !oldSuperclassName.equals("java.lang.Object") && newSuperclassName.equals("java.lang.Object");
					boolean superClassChangedFromObject = oldSuperclassName.equals("java.lang.Object") && !newSuperclassName.equals("java.lang.Object");
					if (superClassChangedToObject) {
						addCompatibilityChange(superclass, JApiCompatibilityChangeType.SUPERCLASS_REMOVED);
					} else if (superClassChangedFromObject) {
						addCompatibilityChange(superclass, JApiCompatibilityChangeType.SUPERCLASS_ADDED);
					} else {
						// check if the old superclass is still an ancestor of the new superclass
						List<JApiSuperclass> ancestors = new ArrayList<>();
						final List<JApiSuperclass> matchingAncestors = new ArrayList<>();
						forAllSuperclasses(jApiClass, classMap, ancestors, (clazz, classMap12, changeStatusOfSuperclass) -> {
							JApiSuperclass ancestor = clazz.getSuperclass();
							if (ancestor.getNewSuperclassName().filter(oldSuperclassName::equals).isPresent()) {
								matchingAncestors.add(ancestor);
							}
							return ancestor;
						});
						if (matchingAncestors.isEmpty()) {
							addCompatibilityChange(superclass, JApiCompatibilityChangeType.SUPERCLASS_REMOVED);
						} else {
							// really, superclass(es) inserted - but the old superclass is still an ancestor
							addCompatibilityChange(superclass, JApiCompatibilityChangeType.SUPERCLASS_ADDED);
						}
					}
				}
			} else {
				if (superclass.getOldSuperclassName().isPresent()) {
					addCompatibilityChange(superclass, JApiCompatibilityChangeType.SUPERCLASS_REMOVED);
				} else if (superclass.getNewSuperclassName().isPresent()) {
					addCompatibilityChange(superclass, JApiCompatibilityChangeType.SUPERCLASS_ADDED);
				}
			}
		}
		// section 13.4.4 of "Java Language Specification" SE7
		for (JApiImplementedInterface implementedInterface : jApiClass.getInterfaces()) {
			if (implementedInterface.getChangeStatus() == JApiChangeStatus.REMOVED) {
				addCompatibilityChange(implementedInterface, JApiCompatibilityChangeType.INTERFACE_REMOVED);
			} else {
				JApiClass interfaceClass = classMap.get(implementedInterface.getFullyQualifiedName());
				if (interfaceClass == null) {
					interfaceClass = loadClass(implementedInterface.getFullyQualifiedName(), EnumSet.allOf(Classpath.class));
				}
				implementedInterface.setJApiClass(interfaceClass);
				if (implementedInterface.getChangeStatus() == JApiChangeStatus.MODIFIED || implementedInterface.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
					checkIfMethodsHaveChangedIncompatible(interfaceClass, classMap);
					checkIfFieldsHaveChangedIncompatible(interfaceClass, classMap);
				} else if (implementedInterface.getChangeStatus() == JApiChangeStatus.NEW) {
					addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.INTERFACE_ADDED);
					// no marker interface, no abstract class, no interface and annotation
					if (!interfaceClass.getMethods().isEmpty() && !isAbstract(jApiClass) && !isInterface(jApiClass) && !isAnnotation(jApiClass) && !isEnum(jApiClass)) {
						boolean allInterfaceMethodsImplemented = true;
						for (JApiMethod interfaceMethod : interfaceClass.getMethods()) {
							boolean interfaceMethodImplemented = false;
							if (isSynthetic(interfaceMethod)) {
								continue;
							}
							if (!isAbstract(interfaceMethod)) { //default implementation
								continue;
							}
							if (isImplemented(interfaceMethod, jApiClass)) {
								interfaceMethodImplemented = true;
								break;
							}
							if (!interfaceMethodImplemented) {
								allInterfaceMethodsImplemented = false;
								break;
							}
						}
						if (!allInterfaceMethodsImplemented) {
							addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE);
						}
					}
				}
			}
		}
		checkIfClassNowCheckedException(jApiClass);
		checkIfAbstractMethodAddedInSuperclass(jApiClass, classMap);
		checkIfAbstractMethodAdded(jApiClass);
	}

	private boolean hasSameType(JApiField field, JApiField otherField) {
		boolean hasSameNewType = false;
		if (field.getType().getNewTypeOptional().isPresent() && otherField.getType().getNewTypeOptional().isPresent()) {
			hasSameNewType = field.getType().getNewTypeOptional().get().equals(otherField.getType().getNewTypeOptional().get());
		} else if (field.getType().getOldTypeOptional().isPresent() && otherField.getType().getNewTypeOptional().isPresent()) {
			hasSameNewType = field.getType().getOldTypeOptional().get().equals(otherField.getType().getNewTypeOptional().get());
		} else if (field.getType().getOldTypeOptional().isPresent() && otherField.getType().getOldTypeOptional().isPresent()) {
			hasSameNewType = field.getType().getOldTypeOptional().get().equals(otherField.getType().getOldTypeOptional().get());
		} else if (field.getType().getNewTypeOptional().isPresent() && otherField.getType().getOldTypeOptional().isPresent()) {
			hasSameNewType = field.getType().getNewTypeOptional().get().equals(otherField.getType().getOldTypeOptional().get());
		}
		return hasSameNewType;
	}

	private void checkIfAbstractMethodAdded(JApiClass jApiClass) {
		if (jApiClass.getChangeStatus() != JApiChangeStatus.NEW && !isAbstract(jApiClass)) {
			//TODO compute hull of all methods and check if there are any abstract methods not implemented
		}
	}

	private void checkIfAbstractMethodAddedInSuperclass(final JApiClass jApiClass, Map<String, JApiClass> classMap) {
		if (jApiClass.getChangeStatus() != JApiChangeStatus.NEW && isClass(jApiClass)) {
			final List<JApiMethod> abstractMethods = new ArrayList<>();
			final List<JApiMethod> defaultMethods = new ArrayList<>();
			final List<JApiMethod> implementedMethods = new ArrayList<>();
			final List<JApiImplementedInterface> implementedInterfaces = new ArrayList<>();
			for (JApiMethod jApiMethod : jApiClass.getMethods()) {
				if (!isAbstract(jApiMethod)) {
					implementedMethods.add(jApiMethod);
				}
			}
			forAllSuperclasses(jApiClass, classMap, new ArrayList<>(), (superclass, classMap1, changeStatusOfSuperclass) -> {
				for (JApiMethod jApiMethod : superclass.getMethods()) {
					if (!isAbstract(jApiMethod)) {
						implementedMethods.add(jApiMethod);
					}
				}
				for (JApiMethod jApiMethod : superclass.getMethods()) {
					if (isAbstract(jApiMethod)) {
						boolean isImplemented = false;
						for (JApiMethod implementedMethod : implementedMethods) {
							if (jApiMethod.getName().equals(implementedMethod.getName()) && jApiMethod.hasSameSignature(implementedMethod)) {
								isImplemented = true;
								break;
							}
						}
						if (!isImplemented) {
							if (jApiMethod.getChangeStatus() == JApiChangeStatus.NEW || changeStatusOfSuperclass == JApiChangeStatus.NEW || changeStatusOfSuperclass == JApiChangeStatus.MODIFIED) {
								jApiMethod.getAbstractModifier().getNewModifier()
									.map(x -> x == AbstractModifier.ABSTRACT ? abstractMethods : defaultMethods)
									.ifPresent(methods -> methods.add(jApiMethod));
							}
						}
					}
				}
				implementedInterfaces.addAll(superclass.getInterfaces());
				return 0;
			});
			implementedInterfaces.addAll(jApiClass.getInterfaces());
			if (!abstractMethods.isEmpty()) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_SUPERCLASS);
			}
			abstractMethods.clear();
			for (JApiImplementedInterface jApiImplementedInterface : implementedInterfaces) {
				JApiClass interfaceClass = getJApiClass(jApiImplementedInterface, classMap);
				for (JApiMethod interfaceMethod : interfaceClass.getMethods()) {
					boolean isImplemented = false;
					for (JApiMethod implementedMethod : implementedMethods) {
						if (areMatching(interfaceMethod, implementedMethod)) {
							isImplemented = true;
							break;
						}
					}
					if (!isImplemented) {
						if (interfaceMethod.getChangeStatus() == JApiChangeStatus.NEW || jApiImplementedInterface.getChangeStatus() == JApiChangeStatus.NEW) {
							interfaceMethod.getAbstractModifier().getNewModifier()
								.map(x -> x == AbstractModifier.ABSTRACT ? abstractMethods : defaultMethods)
								.ifPresent(methods -> methods.add(interfaceMethod));
						}
					}
				}
			}
			final List<JApiMethod> abstractMethodsWithDefaultInInterface = new ArrayList<>();
			for (JApiMethod abstractMethod : abstractMethods) {
				for (JApiImplementedInterface implementedInterface : implementedInterfaces) {
					final JApiClass interfaceClass = getJApiClass(implementedInterface, classMap);
					for (JApiMethod defaultMethodCandidate : interfaceClass.getMethods()) {
						if (!isAbstract(defaultMethodCandidate)
							&& areMatching(abstractMethod, defaultMethodCandidate)) {
							// we have a default implementation for this method
							// double-check that we extend interface that the method comes from
							for (JApiImplementedInterface extendedInterface : interfaceClass.getInterfaces()) {
								JApiClass extendedInterfaceClass = getJApiClass(extendedInterface, classMap);

								if (abstractMethod.getjApiClass().equals(extendedInterfaceClass)) {
									abstractMethodsWithDefaultInInterface.add(abstractMethod);
								}
							}
						}
					}
				}
			}
			abstractMethods.removeAll(abstractMethodsWithDefaultInInterface);

			if (!abstractMethods.isEmpty()) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE);
			}
			if (!defaultMethods.isEmpty()) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE);
			}
		}
	}

	private static boolean areMatching(JApiMethod left, JApiMethod right) {
		return left.getName().equals(right.getName())
			&& left.hasSameSignature(right);
	}

	private JApiClass getJApiClass(JApiImplementedInterface implementedInterface, Map<String, JApiClass> classMap) {
		String fullyQualifiedName = implementedInterface.getFullyQualifiedName();
		JApiClass interfaceClass = classMap.get(fullyQualifiedName);
		if (interfaceClass == null) {
			interfaceClass = loadClass(fullyQualifiedName, EnumSet.allOf(Classpath.class));
		}
		return interfaceClass;
	}

	private void checkIfClassNowCheckedException(JApiClass jApiClass) {
		JApiSuperclass jApiClassSuperclass = jApiClass.getSuperclass();
		if (jApiClassSuperclass.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			jApiClassSuperclass.getNewSuperclassName().filter("java.lang.Exception"::equals)
				.ifPresent(fqn -> addCompatibilityChange(jApiClass, JApiCompatibilityChangeType.CLASS_NOW_CHECKED_EXCEPTION));
		}
	}

	private JApiCompatibilityChange addCompatibilityChange(JApiCompatibility binaryCompatibility, JApiCompatibilityChangeType changeType) {
		JApiCompatibilityChange change = new JApiCompatibilityChange(changeType);
		List<JApiCompatibilityChange> compatibilityChanges = binaryCompatibility.getCompatibilityChanges();
		java.util.Optional<JApiCompatibilityChange> first = compatibilityChanges.stream()
			.filter(cc -> cc.equals(change))
			.findFirst();
		if (!first.isPresent()) {
			compatibilityChanges.add(change);
			return change;
		}
		return first.get();
	}
}
