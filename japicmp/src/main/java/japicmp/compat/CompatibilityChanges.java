package japicmp.compat;

import japicmp.model.*;
import japicmp.util.Optional;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.exception.JApiCmpException;
import japicmp.util.ClassHelper;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static japicmp.util.ModifierHelper.hasModifierLevelDecreased;
import static japicmp.util.ModifierHelper.isNotPrivate;
import static japicmp.util.ModifierHelper.isSynthetic;

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
		checkIfConstructorsHaveChangedIncompatible(jApiClass, classMap);
		checkIfFieldsHaveChangedIncompatible(jApiClass, classMap);
		if (jApiClass.getClassType().getChangeStatus() == JApiChangeStatus.MODIFIED) {
			addCompatibilityChange(jApiClass, JApiCompatibilityChange.CLASS_TYPE_CHANGED);
		}
		checkIfAnnotationDeprectedAdded(jApiClass);
		if (hasModifierLevelDecreased(jApiClass)) {
			addCompatibilityChange(jApiClass, JApiCompatibilityChange.CLASS_LESS_ACCESSIBLE);
		}
	}

	private void checkIfFieldsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		for (final JApiField field : jApiClass.getFields()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(field) && field.getChangeStatus() == JApiChangeStatus.REMOVED) {
				ArrayList<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback<Integer>() {
					@Override
					public Integer callback(JApiClass superclass, Map<String, JApiClass> classMap, JApiChangeStatus changeStatusOfSuperclass) {
						int movedToSuperclass = 0;
						for (JApiField superclassField : superclass.getFields()) {
							if (superclassField.getName().equals(field.getName()) && fieldTypeMatches(superclassField, field) && isNotPrivate(superclassField)) {
								movedToSuperclass = 1;
							}
						}
						return movedToSuperclass;
					}
				});
				boolean movedToSuperclass = false;
				for (Integer returnValue : returnValues) {
					if (returnValue == 1) {
						movedToSuperclass = true;
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
				forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback<Integer>() {
					@Override
					public Integer callback(JApiClass superclass, Map<String, JApiClass> classMap, JApiChangeStatus changeStatusOfSuperclass) {
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
					}
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
			if (isNotPrivate(field) && field.getType().hasChanged()) {
				addCompatibilityChange(field, JApiCompatibilityChange.FIELD_TYPE_CHANGED);
			}
			checkIfAnnotationDeprectedAdded(field);
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
			classType = new JApiClassType(Optional.of(ClassHelper.getType(oldClassOptional.get())), Optional.<JApiClassType.ClassType>absent(), JApiChangeStatus.REMOVED);
		} else if (!oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			classType = new JApiClassType(Optional.<JApiClassType.ClassType>absent(), Optional.of(ClassHelper.getType(newClassOptional.get())), JApiChangeStatus.NEW);
		} else {
			classType = new JApiClassType(Optional.<JApiClassType.ClassType>absent(), Optional.<JApiClassType.ClassType>absent(), JApiChangeStatus.UNCHANGED);
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

	private void checkIfConstructorsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		for (JApiConstructor constructor : jApiClass.getConstructors()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(constructor) && constructor.getChangeStatus() == JApiChangeStatus.REMOVED) {
				addCompatibilityChange(constructor, JApiCompatibilityChange.CONSTRUCTOR_REMOVED);
			}
			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(constructor)) {
				addCompatibilityChange(constructor, JApiCompatibilityChange.CONSTRUCTOR_LESS_ACCESSIBLE);
			}
			checkIfAnnotationDeprectedAdded(constructor);
		}
	}

	private void checkIfMethodsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		for (final JApiMethod method : jApiClass.getMethods()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getChangeStatus() == JApiChangeStatus.REMOVED) {
				final List<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback<Integer>() {
					@Override
					public Integer callback(JApiClass superclass, Map<String, JApiClass> classMap, JApiChangeStatus changeStatusOfSuperclass) {
						for (JApiMethod superMethod : superclass.getMethods()) {
							if (superMethod.getName().equals(method.getName()) && superMethod.hasSameParameter(method) && superMethod.hasSameReturnType(method)) {
								return 1;
							}
						}
						return 0;
					}
				});
				checkIfMethodHasBeenPulledUp(jApiClass, classMap, method, returnValues);
				boolean superclassHasSameMethod = false;
				for (Integer returnValue : returnValues) {
					if (returnValue == 1) {
						superclassHasSameMethod = true;
					}
				}
				if (!superclassHasSameMethod) {
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_REMOVED);
				}
			}
			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(method)) {
				addCompatibilityChange(method, JApiCompatibilityChange.METHOD_LESS_ACCESSIBLE);
			}
			// section 13.4.12 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getChangeStatus() == JApiChangeStatus.NEW) {
				List<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback<Integer>() {
					@Override
					public Integer callback(JApiClass superclass, Map<String, JApiClass> classMap, JApiChangeStatus changeStatusOfSuperclass) {
						for (JApiMethod superMethod : superclass.getMethods()) {
							if (superMethod.getName().equals(method.getName()) && superMethod.hasSameParameter(method) && superMethod.hasSameReturnType(method)) {
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
					}
				});
				for (Integer returnValue : returnValues) {
					if (returnValue == 1) {
						addCompatibilityChange(method, JApiCompatibilityChange.METHOD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS);
					} else if (returnValue == 2) {
						addCompatibilityChange(method, JApiCompatibilityChange.METHOD_IS_STATIC_AND_OVERRIDES_NOT_STATIC);
					}
				}
			}
			// section 13.4.15 of "Java Language Specification" SE7 (Method Result Type)
			if (method.getReturnType().getChangeStatus() == JApiChangeStatus.MODIFIED) {
				addCompatibilityChange(method, JApiCompatibilityChange.METHOD_RETURN_TYPE_CHANGED);
			}
			// section 13.4.16 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getAbstractModifier().hasChangedFromTo(AbstractModifier.NON_ABSTRACT, AbstractModifier.ABSTRACT)) {
				addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NOW_ABSTRACT);
			}
			// section 13.4.17 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getFinalModifier().hasChangedFromTo(FinalModifier.NON_FINAL, FinalModifier.FINAL)) {
				if (!(method.getStaticModifier().getOldModifier().isPresent() && method.getStaticModifier().getOldModifier().get() == StaticModifier.STATIC)) {
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NOW_FINAL);
				}
			}
			// section 13.4.18 of "Java Language Specification" SE7
			if (isNotPrivate(method)) {
				if (method.getStaticModifier().hasChangedFromTo(StaticModifier.NON_STATIC, StaticModifier.STATIC)) {
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NOW_STATIC);
				}
				if (method.getStaticModifier().hasChangedFromTo(StaticModifier.STATIC, StaticModifier.NON_STATIC)) {
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NO_LONGER_STATIC);
				}
			}
			checkAbstractMethod(jApiClass, classMap, method);
			checkIfExceptionIsNowChecked(method);
			checkIfAnnotationDeprectedAdded(method);
		}
	}

	private void checkIfAnnotationDeprectedAdded(JApiHasAnnotations jApiHasAnnotations) {
		for (JApiAnnotation annotation : jApiHasAnnotations.getAnnotations()) {
			if (annotation.getChangeStatus() == JApiChangeStatus.NEW || annotation.getChangeStatus() == JApiChangeStatus.MODIFIED) {
				if (annotation.getFullyQualifiedName().equals(Deprecated.class.getName())) {
					addCompatibilityChange((JApiCompatibility) jApiHasAnnotations, JApiCompatibilityChange.ANNOTATION_DEPRECATED_ADDED);
				}
			}
		}
	}

	private void checkAbstractMethod(JApiClass jApiClass, Map<String, JApiClass> classMap, JApiMethod method) {
		if (isInterface(jApiClass)) {
			if (jApiClass.getChangeStatus() != JApiChangeStatus.NEW) {
				if (method.getChangeStatus() == JApiChangeStatus.NEW && !isSynthetic(method)) {
					List<JApiMethod> implementedMethods = getImplementedMethods(jApiClass, classMap, method);
					if (implementedMethods.size() == 0) {
						// new default method in interface
						if (method.getAbstractModifier().hasChangedTo(AbstractModifier.NON_ABSTRACT)) {
							addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NEW_DEFAULT);
						} else {
							addCompatibilityChange(method, JApiCompatibilityChange.METHOD_ADDED_TO_INTERFACE);
						}
					} else {
						boolean allNew = true;
						for (JApiMethod jApiMethod : implementedMethods) {
							if (jApiMethod.getChangeStatus() != JApiChangeStatus.NEW) {
								allNew = false;
							}
						}
						if (allNew) {
							addCompatibilityChange(method, JApiCompatibilityChange.METHOD_ADDED_TO_INTERFACE);
						}
					}
				} else if (method.getChangeStatus() == JApiChangeStatus.MODIFIED || method.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
					JApiModifier<AbstractModifier> abstractModifier = method.getAbstractModifier();
					// method changed from abstract to default
					if (abstractModifier.getOldModifier().isPresent() && abstractModifier.getOldModifier().get() == AbstractModifier.ABSTRACT &&
						abstractModifier.getNewModifier().isPresent() && abstractModifier.getNewModifier().get() == AbstractModifier.NON_ABSTRACT) {
						addCompatibilityChange(method, JApiCompatibilityChange.METHOD_ABSTRACT_NOW_DEFAULT);
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
						List<JApiMethod> implementedMethods = getImplementedMethods(jApiClass, classMap, method);
						if (implementedMethods.size() > 0) {
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
		forAllSuperclasses(jApiClass, classMap, jApiMethods, new OnSuperclassCallback<JApiMethod>() {
			@Override
			public JApiMethod callback(JApiClass superclass, Map<String, JApiClass> classMap, JApiChangeStatus changeStatusOfSuperclass) {
				for (JApiMethod jApiMethod : superclass.getMethods()) {
					if (isAbstract(jApiMethod)) {
						if (jApiMethod.getName().equals(method.getName()) && jApiMethod.hasSameSignature(method)) {
							return jApiMethod;
						}
					}
				}
				return null;
			}
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

	private List<JApiMethod> getImplementedMethods(final JApiClass jApiClass, final Map<String, JApiClass> classMap, final JApiMethod method) {
		ArrayList<JApiMethod> jApiMethods = new ArrayList<>();
		forAllImplementedInterfaces(jApiClass, classMap, jApiMethods, new OnImplementedInterfaceCallback<JApiMethod>() {
			@Override
			public JApiMethod callback(JApiClass implementedInterface, Map<String, JApiClass> classMap) {
				for (JApiMethod jApiMethod : implementedInterface.getMethods()) {
					if (isAbstract(jApiMethod)) {
						if (jApiMethod.getName().equals(method.getName()) && jApiMethod.hasSameSignature(method)) {
							return jApiMethod;
						}
					}
				}
				return null;
			}
		});
		return removeNullValues(jApiMethods);
	}

	private boolean isAbstract(JApiHasAbstractModifier jApiHasAbstractModifier) {
		boolean isAbstract = false;
		if (jApiHasAbstractModifier.getAbstractModifier().hasChangedTo(AbstractModifier.ABSTRACT)) {
			isAbstract = true;
		}
		return isAbstract;
	}

	private void checkIfExceptionIsNowChecked(JApiMethod method) {
		for (JApiException exception : method.getExceptions()) {
			if (exception.getChangeStatus() == JApiChangeStatus.NEW && exception.isCheckedException() && method.getChangeStatus() != JApiChangeStatus.NEW) {
				addCompatibilityChange(method, JApiCompatibilityChange.METHOD_NOW_THROWS_CHECKED_EXCEPTION);
			}
		}
	}

	private boolean isInterface(JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().isPresent() && jApiClass.getClassType().getNewTypeOptional().get() == JApiClassType.ClassType.INTERFACE;
	}

	private void checkIfMethodHasBeenPulledUp(JApiClass jApiClass, Map<String, JApiClass> classMap, final JApiMethod method, List<Integer> returnValues) {
		forAllImplementedInterfaces(jApiClass, classMap, returnValues, new OnImplementedInterfaceCallback<Integer>() {
			@Override
			public Integer callback(JApiClass implementedInterface, Map<String, JApiClass> classMap) {
				for (JApiMethod superMethod : implementedInterface.getMethods()) {
					if (superMethod.getName().equals(method.getName()) && superMethod.hasSameParameter(method) && superMethod.hasSameReturnType(method)) {
						return 1;
					}
				}
				return 0;
			}
		});
	}

	private <T> void forAllImplementedInterfaces(JApiClass jApiClass, Map<String, JApiClass> classMap, List<T> returnValues, OnImplementedInterfaceCallback<T> onImplementedInterfaceCallback) {
		List<JApiImplementedInterface> interfaces = jApiClass.getInterfaces();
		for (JApiImplementedInterface implementedInterface : interfaces) {
			String fullyQualifiedName = implementedInterface.getFullyQualifiedName();
			JApiClass foundClass = classMap.get(fullyQualifiedName);
			if (foundClass != null) {
				T returnValue = onImplementedInterfaceCallback.callback(foundClass, classMap);
				returnValues.add(returnValue);
				forAllImplementedInterfaces(foundClass, classMap, returnValues, onImplementedInterfaceCallback);
			} else {
				//TODO
			}
		}
	}

	/**
	 * Is a method implemented in a super class
	 * @param jApiMethod the method
	 * @return <code>true</code> if it is implemented in a super class
	 */
	private boolean isImplemented(JApiMethod jApiMethod) {
        JApiClass aClass = jApiMethod.getjApiClass();
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
			forAllSuperclasses(jApiClass, classMap, new ArrayList<Integer>(), new OnSuperclassCallback<Integer>() {
				@Override
				public Integer callback(JApiClass superclass, Map<String, JApiClass> classMap, JApiChangeStatus changeStatusOfSuperclass) {
					for (JApiMethod jApiMethod : superclass.getMethods()) {
						if (!isAbstract(jApiMethod) && jApiMethod.getChangeStatus() != JApiChangeStatus.REMOVED && isNotPrivate(jApiMethod)) {
							implementedMethods.add(jApiMethod);
						}
					}
					for (JApiField jApiField : superclass.getFields()) {
						if (jApiField.getChangeStatus() != JApiChangeStatus.REMOVED && isNotPrivate(jApiField)) {
							fields.add(jApiField);
						}
					}
					for (JApiMethod jApiMethod : superclass.getMethods()) {
						if (jApiMethod.getChangeStatus() == JApiChangeStatus.REMOVED && !isImplemented(jApiMethod)) {
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
					for (JApiField jApiField : superclass.getFields()) {
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
				}
			});
			if (removedAndNotOverriddenMethods.size() > 0) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.METHOD_REMOVED_IN_SUPERCLASS);
			}
			if (removedAndNotOverriddenFields.size() > 0) {
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
						forAllSuperclasses(jApiClass, classMap, ancestors, new OnSuperclassCallback<JApiSuperclass>() {
							@Override
							public JApiSuperclass callback(JApiClass clazz, Map<String, JApiClass> classMap, JApiChangeStatus changeStatusOfSuperclass) {
								JApiSuperclass ancestor = clazz.getSuperclass();
								if (ancestor.getNewSuperclassName().isPresent() && ancestor.getNewSuperclassName().get().equals(superclass.getOldSuperclassName().get())) {
									matchingAncestors.add(ancestor);
								}
								return ancestor;
							}
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
					if (interfaceClass.getMethods().size() > 0) { //no marker interface
						boolean allInterfaceMethodsImplemented = true;
						for (JApiMethod interfaceMethod : interfaceClass.getMethods()) {
							boolean interfaceMethodImplemented = false;
							if (isSynthetic(interfaceMethod)) {
								continue;
							}
							for (JApiMethod classMethod : jApiClass.getMethods()) {
								if (classMethod.getName().equals(interfaceMethod.getName()) && classMethod.hasSameSignature(interfaceMethod)) {
									interfaceMethodImplemented = true;
									break;
								}
							}
							if (!interfaceMethodImplemented) {
								allInterfaceMethodsImplemented = false;
								break;
							}
						}
						if (!allInterfaceMethodsImplemented) {
							addCompatibilityChange(jApiClass, JApiCompatibilityChange.INTERFACE_ADDED);
						}
					}
				}
			}
		}
		checkIfClassNowCheckedException(jApiClass);
		checkIfAbstractMethodAddedInSuperclass(jApiClass, classMap);
		checkIfAbstractMethodAdded(jApiClass, classMap);
	}

	private boolean hasSameType(JApiField field, JApiField otherField) {
		boolean hasSameNewType = false;
		if (field.getType().getNewTypeOptional().isPresent() && otherField.getType().getNewTypeOptional().isPresent()) {
			hasSameNewType = field.getType().getNewTypeOptional().get().equals(otherField.getType().getNewTypeOptional().get());
		} else if (field.getType().getOldTypeOptional().isPresent() && otherField.getType().getNewTypeOptional().isPresent()) {
			hasSameNewType = field.getType().getOldTypeOptional().get().equals(otherField.getType().getNewTypeOptional().get());
		} else if (field.getType().getOldTypeOptional().isPresent() && otherField.getType().getOldTypeOptional().isPresent()) {
			hasSameNewType = field.getType().getOldTypeOptional().get().equals(otherField.getType().getOldTypeOptional().get());
		}
		return hasSameNewType;
	}

	private void checkIfAbstractMethodAdded(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		if (jApiClass.getChangeStatus() != JApiChangeStatus.NEW && !isAbstract(jApiClass)) {
			//TODO compute hull of all methods and check if there are any abstract methods not implemented
		}
	}

	private void checkIfAbstractMethodAddedInSuperclass(final JApiClass jApiClass, Map<String, JApiClass> classMap) {
		if (jApiClass.getChangeStatus() != JApiChangeStatus.NEW) {
			final List<JApiMethod> abstractMethods = new ArrayList<>();
			final List<JApiMethod> implementedMethods = new ArrayList<>();
			final List<JApiImplementedInterface> implementedInterfaces = new ArrayList<>();
			for (JApiMethod jApiMethod : jApiClass.getMethods()) {
				if (!isAbstract(jApiMethod)) {
					implementedMethods.add(jApiMethod);
				}
			}
			forAllSuperclasses(jApiClass, classMap, new ArrayList<Integer>(), new OnSuperclassCallback<Integer>() {
				@Override
				public Integer callback(JApiClass superclass, Map<String, JApiClass> classMap, JApiChangeStatus changeStatusOfSuperclass) {
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
									abstractMethods.add(jApiMethod);
								}
							}
						}
					}
					implementedInterfaces.addAll(superclass.getInterfaces());
					return 0;
				}
			});
			if (abstractMethods.size() > 0) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.METHOD_ABSTRACT_ADDED_IN_SUPERCLASS);
			}
			abstractMethods.clear();
			for (JApiImplementedInterface jApiImplementedInterface : implementedInterfaces) {
				String fullyQualifiedName = jApiImplementedInterface.getFullyQualifiedName();
				JApiClass foundClass = classMap.get(fullyQualifiedName);
				if (foundClass == null) {
					foundClass = loadClass(fullyQualifiedName, EnumSet.allOf(Classpath.class));
				}
				for (JApiMethod method : foundClass.getMethods()) {
					boolean isImplemented = false;
					for (JApiMethod implementedMethod : implementedMethods) {
						if (method.getName().equals(implementedMethod.getName()) && method.hasSameSignature(implementedMethod)) {
							isImplemented = true;
							break;
						}
					}
					if (!isImplemented) {
						if (method.getChangeStatus() == JApiChangeStatus.NEW || jApiImplementedInterface.getChangeStatus() == JApiChangeStatus.NEW) {
							abstractMethods.add(method);
						}
					}
				}
			}
			if (abstractMethods.size() > 0) {
				addCompatibilityChange(jApiClass, JApiCompatibilityChange.METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE);
			}
		}
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
