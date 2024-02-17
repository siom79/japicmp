package japicmp.compat;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.exception.JApiCmpException;
import japicmp.model.*;
import japicmp.util.ClassHelper;
import japicmp.util.Optional;
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
			addCompatibilityChange(jApiClass, JApiCompatibilityChange.CLASS_REMOVED);
		} else if (jApiClass.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			// section 13.4.1 of "Java Language Specification" SE7
			if (jApiClass.getAbstractModifier().hasChangedFromTo(AbstractModifier.NON_ABSTRACT, AbstractModifier.ABSTRACT)) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.CLASS_NOW_ABSTRACT);
			}
			// section 13.4.2 of "Java Language Specification" SE7
			if (jApiClass.getFinalModifier().hasChangedFromTo(FinalModifier.NON_FINAL, FinalModifier.FINAL)) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.CLASS_NOW_FINAL);
			}
			// section 13.4.3 of "Java Language Specification" SE7
			if (jApiClass.getAccessModifier().hasChangedFrom(AccessModifier.PUBLIC)) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.CLASS_NO_LONGER_PUBLIC);
			}
		}
		// section 13.4.4 of "Java Language Specification" SE7
		checkIfSuperclassesOrInterfacesChangedIncompatible(jApiClass, classMap);
		checkIfMethodsHaveChangedIncompatible(jApiClass, classMap);
		checkIfConstructorsHaveChangedIncompatible(jApiClass);
		checkIfFieldsHaveChangedIncompatible(jApiClass, classMap);
		if (jApiClass.getClassType().getChangeStatus() == JApiChangeStatus.MODIFIED) {
			addCompatibilityChange(jApiClass, JApiCompatibilityChange.CLASS_TYPE_CHANGED);
		}
		checkIfAnnotationDeprecatedAdded(jApiClass);
		if (hasModifierLevelDecreased(jApiClass)) {
			addCompatibilityChange(jApiClass, JApiCompatibilityChange.CLASS_LESS_ACCESSIBLE);
		}
		if (jApiClass.getChangeStatus().isNotNewOrRemoved()) {
			checkIfGenericTemplatesHaveChanged(jApiClass);
		}
	}

	private void checkIfGenericTemplatesHaveChanged(JApiHasGenericTemplates jApiHasGenericTemplates) {
		for (JApiGenericTemplate jApiGenericTemplate : jApiHasGenericTemplates.getGenericTemplates()) {
			if (jApiGenericTemplate.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				((JApiCompatibility)jApiHasGenericTemplates).getCompatibilityChanges().add(JApiCompatibilityChange.CLASS_GENERIC_TEMPLATE_CHANGED);
				break;
			}
		}
		for (JApiGenericTemplate jApiGenericTemplate : jApiHasGenericTemplates.getGenericTemplates()) {
			JApiChangeStatus changeStatus = jApiGenericTemplate.getChangeStatus();
			if (changeStatus == JApiChangeStatus.MODIFIED || changeStatus == JApiChangeStatus.UNCHANGED) {
				if (!SignatureParser.equalGenericTypes(jApiGenericTemplate.getOldGenericTypes(), jApiGenericTemplate.getNewGenericTypes())) {
					jApiGenericTemplate.getCompatibilityChanges().add(JApiCompatibilityChange.CLASS_GENERIC_TEMPLATE_GENERICS_CHANGED);
					((JApiCompatibility)jApiHasGenericTemplates).getCompatibilityChanges().add(JApiCompatibilityChange.CLASS_GENERIC_TEMPLATE_GENERICS_CHANGED);
					break;
				}
			}
		}
	}

	private void checkIfFieldsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		for (final JApiField field : jApiClass.getFields()) {
			// section 8.3.1.4 of "Java Language Specification" SE7
			if (field.getVolatileModifier().hasChangedFromTo(VolatileModifier.NON_VOLATILE, VolatileModifier.VOLATILE)) {
				addCompatibilityChange(field, JApiCompatibilityChange.FIELD_NOW_VOLATILE);
			}
			if (field.getVolatileModifier().hasChangedFromTo(VolatileModifier.VOLATILE, VolatileModifier.NON_VOLATILE)) {
				addCompatibilityChange(field, JApiCompatibilityChange.FIELD_NO_LONGER_VOLATILE);
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
					addCompatibilityChange(field, JApiCompatibilityChange.FIELD_REMOVED);
				}
			}
			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(field)) {
				addCompatibilityChange(field, JApiCompatibilityChange.FIELD_LESS_ACCESSIBLE);
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
						addCompatibilityChange(field, JApiCompatibilityChange.FIELD_STATIC_AND_OVERRIDES_STATIC);
					} else if (returnValue == 2) {
						addCompatibilityChange(field, JApiCompatibilityChange.FIELD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS);
					}
				}
			}
			// section 13.4.9 of "Java Language Specification" SE7
			if (isNotPrivate(field) && field.getFinalModifier().hasChangedFromTo(FinalModifier.NON_FINAL, FinalModifier.FINAL)) {
				addCompatibilityChange(field, JApiCompatibilityChange.FIELD_NOW_FINAL);
			}
			// section 13.4.10 of "Java Language Specification" SE7
			if (isNotPrivate(field)) {
				if (field.getStaticModifier().hasChangedFromTo(StaticModifier.NON_STATIC, StaticModifier.STATIC)) {
					addCompatibilityChange(field, JApiCompatibilityChange.FIELD_NOW_STATIC);
				}
				if (field.getStaticModifier().hasChangedFromTo(StaticModifier.STATIC, StaticModifier.NON_STATIC)) {
					addCompatibilityChange(field, JApiCompatibilityChange.FIELD_NO_LONGER_STATIC);
				}
			}
			// section 13.4.11 of "Java Language Specification" SE7
			if (field.getTransientModifier().hasChangedFromTo(TransientModifier.NON_TRANSIENT, TransientModifier.TRANSIENT)) {
				addCompatibilityChange(field, JApiCompatibilityChange.FIELD_NOW_TRANSIENT);
			}
			if (field.getTransientModifier().hasChangedFromTo(TransientModifier.TRANSIENT, TransientModifier.NON_TRANSIENT)) {
				addCompatibilityChange(field, JApiCompatibilityChange.FIELD_NO_LONGER_TRANSIENT);
			}
			if (isNotPrivate(field) && field.getType().hasChanged()) {
				addCompatibilityChange(field, JApiCompatibilityChange.FIELD_TYPE_CHANGED);
			}
			checkIfAnnotationDeprecatedAdded(field);
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
			JApiClass foundClass = classMap.get(newSuperclassName);
			if (foundClass == null) {
				Optional<JApiClass> superclassJApiClassOptional = superclass.getJApiClass();
				if (superclassJApiClassOptional.isPresent()) {
					foundClass = superclassJApiClassOptional.get();
				} else {
					foundClass = loadClass(newSuperclassName, EnumSet.of(Classpath.NEW_CLASSPATH));
					evaluate(Collections.singletonList(foundClass));
				}
				classMap.put(foundClass.getFullyQualifiedName(), foundClass);
			}
			T returnValue = onSuperclassCallback.callback(foundClass, classMap, superclass.getChangeStatus());
			returnValues.add(returnValue);
			forAllSuperclasses(foundClass, classMap, returnValues, onSuperclassCallback);
		}
	}

	private enum Classpath {
		OLD_CLASSPATH,
		NEW_CLASSPATH
	}

	private JApiClass loadClass(String newSuperclassName, EnumSet<Classpath> classpaths) {
		JApiClass foundClass;
		Optional<CtClass> oldClassOptional = Optional.absent();
		Optional<CtClass> newClassOptional = Optional.absent();
		JarArchiveComparatorOptions.ClassPathMode classPathMode = this.jarArchiveComparator.getJarArchiveComparatorOptions().getClassPathMode();
		if (classPathMode == JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH) {
			ClassPool classPool = this.jarArchiveComparator.getCommonClassPool();
			try {
				oldClassOptional = Optional.of(classPool.get(newSuperclassName));
			} catch (NotFoundException ignored) {}
			try {
				newClassOptional = Optional.of(classPool.get(newSuperclassName));
			} catch (NotFoundException ignored) {}
		} else {
			if (classpaths.contains(Classpath.OLD_CLASSPATH)) {
				ClassPool oldClassPool = this.jarArchiveComparator.getOldClassPool();
				try {
					oldClassOptional = Optional.of(oldClassPool.get(newSuperclassName));
				} catch (NotFoundException ignored) {}
			}
			if (classpaths.contains(Classpath.NEW_CLASSPATH)) {
				ClassPool newClassPool = this.jarArchiveComparator.getNewClassPool();
				try {
					newClassOptional = Optional.of(newClassPool.get(newSuperclassName));
				} catch (NotFoundException ignored) {}
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
			classType = new JApiClassType(Optional.of(ClassHelper.getType(oldClassOptional.get())), Optional.absent(), JApiChangeStatus.REMOVED);
		} else if (!oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			classType = new JApiClassType(Optional.absent(), Optional.of(ClassHelper.getType(newClassOptional.get())), JApiChangeStatus.NEW);
		} else {
			classType = new JApiClassType(Optional.absent(), Optional.absent(), JApiChangeStatus.UNCHANGED);
		}
		foundClass = new JApiClass(this.jarArchiveComparator, newSuperclassName, oldClassOptional, newClassOptional, changeStatus, classType);
		return foundClass;
	}

	private boolean fieldTypeMatches(JApiField field1, JApiField field2) {
		boolean matches = true;
		JApiType type1 = field1.getType();
		JApiType type2 = field2.getType();
		if (type1.getNewTypeOptional().isPresent() && type2.getNewTypeOptional().isPresent()) {
			if (!type1.getNewTypeOptional().get().equals(type2.getNewTypeOptional().get())) {
				matches = false;
			}
		}
		return matches;
	}

	private void checkIfConstructorsHaveChangedIncompatible(JApiClass jApiClass) {
		for (JApiConstructor constructor : jApiClass.getConstructors()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(constructor) && constructor.getChangeStatus() == JApiChangeStatus.REMOVED) {
				addCompatibilityChange(constructor, JApiCompatibilityChange.CONSTRUCTOR_REMOVED);
			}
			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(constructor)) {
				if (isNotFinal(jApiClass)  || constructor.getAccessModifier().hasChangedFrom(AccessModifier.PUBLIC)) {
					addCompatibilityChange(constructor, JApiCompatibilityChange.CONSTRUCTOR_LESS_ACCESSIBLE);
				}
			}
			checkIfExceptionIsNowChecked(constructor);
			checkIfAnnotationDeprecatedAdded(constructor);
			checkIfVarargsChanged(constructor);
			checkIfParametersGenericsChanged(constructor);
			if (jApiClass.getChangeStatus().isNotNewOrRemoved()) {
				checkIfGenericTemplatesHaveChanged(constructor);
			}
		}
	}

	private static boolean isNotFinal(JApiClass jApiClass) {
		return !(jApiClass.getFinalModifier().getNewModifier().isPresent() && jApiClass.getFinalModifier().getNewModifier().get() == FinalModifier.FINAL);
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
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_REMOVED);
				}
			}

			if (!isInterface(jApiClass) && method.getChangeStatus() == JApiChangeStatus.NEW && method.getAccessModifier().getNewModifier().get().getLevel() == AccessModifier.PUBLIC.getLevel()) {
				addCompatibilityChange(method, JApiCompatibilityChange.METHOD_ADDED_TO_PUBLIC_CLASS);
			}

			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(method)) {
				addCompatibilityChange(method, JApiCompatibilityChange.METHOD_LESS_ACCESSIBLE);
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
                            if (superMethod.getStaticModifier().getNewModifier().isPresent() && method.getStaticModifier().getNewModifier().isPresent()) {
                                if (superMethod.getStaticModifier().getNewModifier().get() == StaticModifier.NON_STATIC
                                    && method.getStaticModifier().getNewModifier().get() == StaticModifier.STATIC) {
                                    return 2;
                                }
                            }
                        }
                    }
                    return 0;
                });
				for (Integer returnValue : returnValues) {
					if (returnValue == 1) {
						addCompatibilityChange(method, JApiCompatibilityChange.METHOD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS);
					} else if (returnValue == 2) {
						addCompatibilityChange(method, JApiCompatibilityChange.METHOD_IS_STATIC_AND_OVERRIDES_NOT_STATIC);
					}
				}
			}
			checkIfReturnTypeChanged(method);
			// section 13.4.16 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getAbstractModifier().hasChangedFromTo(AbstractModifier.NON_ABSTRACT, AbstractModifier.ABSTRACT)) {
				addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NOW_ABSTRACT);
			}
			// section 13.4.17 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getFinalModifier().hasChangedFromTo(FinalModifier.NON_FINAL, FinalModifier.FINAL)) {
				if ((jApiClass.getFinalModifier().getOldModifier().isPresent() && jApiClass.getFinalModifier().getOldModifier().get() != FinalModifier.FINAL) &&
					!(method.getStaticModifier().getOldModifier().isPresent() && method.getStaticModifier().getOldModifier().get() == StaticModifier.STATIC)) {
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NOW_FINAL);
				}
			}
			if (isNotPrivate(method) && method.getChangeStatus() == JApiChangeStatus.REMOVED) {
				List<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, (superclass, classMap1, changeStatusOfSuperclass) -> {
					for (JApiMethod superMethod : superclass.getMethods()) {
						if (areMatching(superMethod, method)) {
							if (method.getFinalModifier().getOldModifier().isPresent()
									&& method.getFinalModifier().getOldModifier().get() == FinalModifier.NON_FINAL
									&& superMethod.getFinalModifier().getNewModifier().isPresent()
									&& superMethod.getFinalModifier().getNewModifier().get() == FinalModifier.FINAL) {
								addCompatibilityChange(superMethod, JApiCompatibilityChange.METHOD_NOW_FINAL);
								return 1;
							}
						}
					}
					return 0;
				});
				if (returnValues.stream().anyMatch(value -> value == 1)) {
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NOW_FINAL);
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_MOVED_TO_SUPERCLASS);
				}
			}
			// section 13.4.18 of "Java Language Specification" SE7
			if (isNotPrivate(method) && !isInterface(method.getjApiClass())) {
				if (method.getStaticModifier().hasChangedFromTo(StaticModifier.NON_STATIC, StaticModifier.STATIC)) {
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NOW_STATIC);
				}
				if (method.getStaticModifier().hasChangedFromTo(StaticModifier.STATIC, StaticModifier.NON_STATIC)) {
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NO_LONGER_STATIC);
				}
			}
			checkAbstractMethod(jApiClass, classMap, method);
			checkIfExceptionIsNowChecked(method);
			checkIfAnnotationDeprecatedAdded(method);
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
			addCompatibilityChange(method, JApiCompatibilityChange.METHOD_RETURN_TYPE_CHANGED);
		}
		if (method.getChangeStatus() == JApiChangeStatus.MODIFIED ||
				method.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
			if (!SignatureParser.equalGenericTypes(method.getReturnType().getOldGenericTypes(), method.getReturnType().getNewGenericTypes())) {
				addCompatibilityChange(method.getReturnType(), JApiCompatibilityChange.METHOD_RETURN_TYPE_GENERICS_CHANGED);
			}
		}
	}

	private void checkIfParametersGenericsChanged(JApiBehavior jApiBehavior) {
		if (jApiBehavior.getChangeStatus() == JApiChangeStatus.MODIFIED ||
				jApiBehavior.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
			for (JApiParameter jApiParameter : jApiBehavior.getParameters()) {
				if (!SignatureParser.equalGenericTypes(jApiParameter.getOldGenericTypes(), jApiParameter.getNewGenericTypes())) {
					jApiParameter.getCompatibilityChanges().add(JApiCompatibilityChange.METHOD_PARAMETER_GENERICS_CHANGED);
				}
			}
		}
	}

	private void checkIfFieldGenericsChanged(JApiField jApiField) {
		if (jApiField.getChangeStatus() == JApiChangeStatus.MODIFIED ||
				jApiField.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
			if (!SignatureParser.equalGenericTypes(jApiField.getOldGenericTypes(), jApiField.getNewGenericTypes())) {
				jApiField.getCompatibilityChanges().add(JApiCompatibilityChange.FIELD_GENERICS_CHANGED);
			}
		}
	}

	private void checkIfAnnotationDeprecatedAdded(JApiHasAnnotations jApiHasAnnotations) {
		for (JApiAnnotation annotation : jApiHasAnnotations.getAnnotations()) {
			if (annotation.getChangeStatus() == JApiChangeStatus.NEW || annotation.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				if (annotation.getFullyQualifiedName().equals(Deprecated.class.getName())) {
					addCompatibilityChange((JApiCompatibility) jApiHasAnnotations, JApiCompatibilityChange.ANNOTATION_DEPRECATED_ADDED);
				}
			}
		}
	}

	private void checkIfVarargsChanged(JApiBehavior behavior) {
		if (behavior.getVarargsModifier().hasChangedFromTo(VarargsModifier.VARARGS, VarargsModifier.NON_VARARGS)) {
			addCompatibilityChange(behavior, JApiCompatibilityChange.METHOD_NO_LONGER_VARARGS);
		}
		if (behavior.getVarargsModifier().hasChangedFromTo(VarargsModifier.NON_VARARGS, VarargsModifier.VARARGS)) {
			addCompatibilityChange(behavior, JApiCompatibilityChange.METHOD_NOW_VARARGS);
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
								addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NEW_STATIC_ADDED_TO_INTERFACE);
							} else {
								addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NEW_DEFAULT);
							}
						} else {
							addCompatibilityChange(method, JApiCompatibilityChange.METHOD_ADDED_TO_INTERFACE);
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
							addCompatibilityChange(method, JApiCompatibilityChange.METHOD_ADDED_TO_INTERFACE);
						}
					}
				} else if (method.getChangeStatus() == JApiChangeStatus.MODIFIED || method.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
					JApiModifier<AbstractModifier> abstractModifier = method.getAbstractModifier();
					if (abstractModifier.getOldModifier().isPresent() && abstractModifier.getOldModifier().get() == AbstractModifier.ABSTRACT &&
						abstractModifier.getNewModifier().isPresent() && abstractModifier.getNewModifier().get() == AbstractModifier.NON_ABSTRACT) {
						// method changed from abstract to default
						addCompatibilityChange(method, JApiCompatibilityChange.METHOD_ABSTRACT_NOW_DEFAULT);
					}
					JApiModifier<StaticModifier> staticModifier = method.getStaticModifier();
					if (staticModifier.hasChangedFromTo(StaticModifier.NON_STATIC, StaticModifier.STATIC)) {
						addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NON_STATIC_IN_INTERFACE_NOW_STATIC);
					} else if (staticModifier.hasChangedFromTo(StaticModifier.STATIC, StaticModifier.NON_STATIC)) {
						addCompatibilityChange(method, JApiCompatibilityChange.METHOD_STATIC_IN_INTERFACE_NO_LONGER_STATIC);
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
							addCompatibilityChange(method, JApiCompatibilityChange.METHOD_ABSTRACT_ADDED_TO_CLASS);
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
		if (jApiHasAbstractModifier.getAbstractModifier().getNewModifier().isPresent()) {
			AbstractModifier abstractModifier = jApiHasAbstractModifier.getAbstractModifier().getNewModifier().get();
			return abstractModifier == AbstractModifier.ABSTRACT;
		}
		return false;
	}

	private void checkIfExceptionIsNowChecked(JApiBehavior behavior) {
		for (JApiException exception : behavior.getExceptions()) {
			if (exception.getChangeStatus() == JApiChangeStatus.NEW && exception.isCheckedException() && behavior.getChangeStatus() != JApiChangeStatus.NEW) {
				addCompatibilityChange(behavior, JApiCompatibilityChange.METHOD_NOW_THROWS_CHECKED_EXCEPTION);
			}
			if (exception.getChangeStatus() == JApiChangeStatus.REMOVED && exception.isCheckedException() && behavior.getChangeStatus() != JApiChangeStatus.REMOVED) {
				addCompatibilityChange(behavior, JApiCompatibilityChange.METHOD_NO_LONGER_THROWS_CHECKED_EXCEPTION);
			}
		}
	}

	private boolean isClass(final JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().isPresent() && jApiClass.getClassType().getNewTypeOptional().get() == JApiClassType.ClassType.CLASS;
	}

	private boolean isInterface(final JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().isPresent() && jApiClass.getClassType().getNewTypeOptional().get() == JApiClassType.ClassType.INTERFACE;
	}

	private boolean isAnnotation(final JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().isPresent() && jApiClass.getClassType().getNewTypeOptional().get() == JApiClassType.ClassType.ANNOTATION;
	}

	private boolean isEnum(final JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().isPresent() && jApiClass.getClassType().getNewTypeOptional().get() == JApiClassType.ClassType.ENUM;
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
	 * @param jApiMethod the method
	 * @return <code>true</code> if it is implemented in a super class
	 */
	private boolean isImplemented(JApiMethod jApiMethod, JApiClass aClass) {
	    while(aClass != null) {
            for (JApiMethod method : aClass.getMethods()) {
                if (jApiMethod.getName().equals(method.getName()) && jApiMethod.hasSameParameter(method) &&
                    !isAbstract(method) && method.getChangeStatus() != JApiChangeStatus.REMOVED && isNotPrivate(method)) {
                    return true;
                }
            }

            if(aClass.getSuperclass() != null && aClass.getSuperclass().getJApiClass().isPresent()) {
                aClass = aClass.getSuperclass().getJApiClass().get();
            } else {
                aClass = null;
            }
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
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.METHOD_REMOVED_IN_SUPERCLASS);
			}
			if (!removedAndNotOverriddenFields.isEmpty()) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.FIELD_REMOVED_IN_SUPERCLASS);
			}
			if (superclass.getOldSuperclassName().isPresent() && superclass.getNewSuperclassName().isPresent()) {
				if (!superclass.getOldSuperclassName().get().equals(superclass.getNewSuperclassName().get())) {
					boolean superClassChangedToObject = false;
					boolean superClassChangedFromObject = false;
					if (!superclass.getOldSuperclassName().get().equals("java.lang.Object") && superclass.getNewSuperclassName().get().equals("java.lang.Object")) {
						superClassChangedToObject = true;
					}
					if (superclass.getOldSuperclassName().get().equals("java.lang.Object") && !superclass.getNewSuperclassName().get().equals("java.lang.Object")) {
						superClassChangedFromObject = true;
					}
					if (superClassChangedToObject) {
						addCompatibilityChange(superclass, JApiCompatibilityChange.SUPERCLASS_REMOVED);
					} else if (superClassChangedFromObject) {
						addCompatibilityChange(superclass, JApiCompatibilityChange.SUPERCLASS_ADDED);
					} else {
						// check if the old superclass is still an ancestor of the new superclass
						List<JApiSuperclass> ancestors = new ArrayList<>();
						final List<JApiSuperclass> matchingAncestors = new ArrayList<>();
						forAllSuperclasses(jApiClass, classMap, ancestors, (clazz, classMap12, changeStatusOfSuperclass) -> {
                            JApiSuperclass ancestor = clazz.getSuperclass();
                            if (ancestor.getNewSuperclassName().isPresent() && ancestor.getNewSuperclassName().get().equals(superclass.getOldSuperclassName().get())) {
                                matchingAncestors.add(ancestor);
                            }
                            return ancestor;
                        });
						if (matchingAncestors.isEmpty()) {
							addCompatibilityChange(superclass, JApiCompatibilityChange.SUPERCLASS_REMOVED);
						} else {
							// really, superclass(es) inserted - but the old superclass is still an ancestor
							addCompatibilityChange(superclass, JApiCompatibilityChange.SUPERCLASS_ADDED);
						}
					}
				}
			} else {
				if (superclass.getOldSuperclassName().isPresent()) {
					addCompatibilityChange(superclass, JApiCompatibilityChange.SUPERCLASS_REMOVED);
				} else if (superclass.getNewSuperclassName().isPresent()) {
					addCompatibilityChange(superclass, JApiCompatibilityChange.SUPERCLASS_ADDED);
				}
			}
		}
		// section 13.4.4 of "Java Language Specification" SE7
		for (JApiImplementedInterface implementedInterface : jApiClass.getInterfaces()) {
			if (implementedInterface.getChangeStatus() == JApiChangeStatus.REMOVED) {
				addCompatibilityChange(implementedInterface, JApiCompatibilityChange.INTERFACE_REMOVED);
			} else {
				JApiClass interfaceClass = classMap.get(implementedInterface.getFullyQualifiedName());
				if (interfaceClass == null) {
					interfaceClass = loadClass(implementedInterface.getFullyQualifiedName(), EnumSet.allOf(Classpath.class));
				}
				if (implementedInterface.getChangeStatus() == JApiChangeStatus.MODIFIED || implementedInterface.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
					implementedInterface.setJApiClass(interfaceClass);
					checkIfMethodsHaveChangedIncompatible(interfaceClass, classMap);
					checkIfFieldsHaveChangedIncompatible(interfaceClass, classMap);
				} else if (implementedInterface.getChangeStatus() == JApiChangeStatus.NEW) {
					addCompatibilityChange(jApiClass, JApiCompatibilityChange.INTERFACE_ADDED);
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
							addCompatibilityChange(jApiClass, JApiCompatibilityChange.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE);
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
                                if (jApiMethod.getAbstractModifier().getNewModifier().isPresent()) {
                                    AbstractModifier abstractModifier = jApiMethod.getAbstractModifier().getNewModifier().get();
                                    if (abstractModifier == AbstractModifier.ABSTRACT) {
                                        abstractMethods.add(jApiMethod);
                                    } else {
                                        defaultMethods.add(jApiMethod);
                                    }
                                }
                            }
                        }
                    }
                }
                implementedInterfaces.addAll(superclass.getInterfaces());
                return 0;
            });
			implementedInterfaces.addAll(jApiClass.getInterfaces());
			if (!abstractMethods.isEmpty()) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.METHOD_ABSTRACT_ADDED_IN_SUPERCLASS);
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
							if (interfaceMethod.getAbstractModifier().getNewModifier().isPresent()) {
								AbstractModifier abstractModifier = interfaceMethod.getAbstractModifier().getNewModifier().get();
								if (abstractModifier == AbstractModifier.ABSTRACT) {
									abstractMethods.add(interfaceMethod);
								} else {
									defaultMethods.add(interfaceMethod);
								}
							}
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
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE);
			}
			if (!defaultMethods.isEmpty()) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.METHOD_DEFAULT_ADDED_IN_IMPLEMENTED_INTERFACE);
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
			if (jApiClassSuperclass.getNewSuperclassName().isPresent()) {
				String fqn = jApiClassSuperclass.getNewSuperclassName().get();
				if ("java.lang.Exception".equals(fqn)) {
					addCompatibilityChange(jApiClass, JApiCompatibilityChange.CLASS_NOW_CHECKED_EXCEPTION);
				}
			}
		}
	}

	private void addCompatibilityChange(JApiCompatibility binaryCompatibility, JApiCompatibilityChange compatibilityChange) {
		List<JApiCompatibilityChange> compatibilityChanges = binaryCompatibility.getCompatibilityChanges();
		if (!compatibilityChanges.contains(compatibilityChange)) {
			compatibilityChanges.add(compatibilityChange);
		}
	}
}
