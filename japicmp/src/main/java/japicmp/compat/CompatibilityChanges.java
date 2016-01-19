package japicmp.compat;

import com.google.common.base.Optional;
import japicmp.model.*;

import java.util.*;

import static japicmp.util.ModifierHelper.hasModifierLevelDecreased;
import static japicmp.util.ModifierHelper.isNotPrivate;

public class CompatibilityChanges {

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
	}

	private void checkIfFieldsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		for (final JApiField field : jApiClass.getFields()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(field) && field.getChangeStatus() == JApiChangeStatus.REMOVED) {
				addCompatibilityChange(field, JApiCompatibilityChange.FIELD_REMOVED);
			}
			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(field)) {
				addCompatibilityChange(field, JApiCompatibilityChange.FIELD_LESS_ACCESSIBLE);
			}
			// section 13.4.8 of "Java Language Specification" SE7
			if (isNotPrivate(field) && field.getChangeStatus() == JApiChangeStatus.NEW) {
				ArrayList<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback() {
					@Override
					public int callback(JApiClass superclass, Map<String, JApiClass> classMap) {
						int changedIncompatible = 0;
						for (JApiField superclassField : superclass.getFields()) {
							if (superclassField.getName().equals(field.getName()) && fieldTypeMatches(superclassField, field)) {
								boolean superclassFieldIsStatic = false;
								boolean subclassFieldIsStatic = false;
								boolean accessModifierSubclassLess = false;
								if (field.getStaticModifier().getNewModifier().isPresent() && field.getStaticModifier().getNewModifier().get() == StaticModifier.STATIC) {
									subclassFieldIsStatic = true;
								}
								if (superclassField.getStaticModifier().getNewModifier().isPresent() && superclassField.getStaticModifier().getNewModifier().get() == StaticModifier.STATIC) {
									superclassFieldIsStatic = true;
								}
								if (field.getAccessModifier().getNewModifier().isPresent() && superclassField.getAccessModifier().getNewModifier().isPresent()) {
									if (field.getAccessModifier().getNewModifier().get().getLevel() < superclassField.getAccessModifier().getNewModifier().get().getLevel()) {
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
		}
	}

	private interface OnSuperclassCallback {
		int callback(JApiClass superclass, Map<String, JApiClass> classMap);
	}

	private interface OnImplementedInterfaceCallback {
		int callback(JApiClass implementedInterface, Map<String, JApiClass> classMap);
	}

	private void forAllSuperclasses(JApiClass jApiClass, Map<String, JApiClass> classMap, List<Integer> returnValues, OnSuperclassCallback onSuperclassCallback) {
		JApiSuperclass superclass = jApiClass.getSuperclass();
		if (superclass.getNewSuperclassName().isPresent()) {
			JApiClass foundClass = classMap.get(superclass.getNewSuperclassName().get());
			if (foundClass != null) {
				int returnValue = onSuperclassCallback.callback(foundClass, classMap);
				returnValues.add(returnValue);
				forAllSuperclasses(foundClass, classMap, returnValues, onSuperclassCallback);
			} else {
				Optional<JApiClass> superclassJApiClassOptional = superclass.getJApiClass();
				if (superclassJApiClassOptional.isPresent()) {
					JApiClass superclassJApiClass = superclassJApiClassOptional.get();
					evaluate(Arrays.asList(superclassJApiClass));
					int returnValue = onSuperclassCallback.callback(superclassJApiClass, classMap);
					returnValues.add(returnValue);
					forAllSuperclasses(superclassJApiClass, classMap, returnValues, onSuperclassCallback);
				}
			}
		}
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
		}
	}

	private void checkIfMethodsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		for (final JApiMethod method : jApiClass.getMethods()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getChangeStatus() == JApiChangeStatus.REMOVED) {
				final List<Integer> returnValues = new ArrayList<>();
				forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback() {
					@Override
					public int callback(JApiClass superclass, Map<String, JApiClass> classMap) {
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
				forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback() {
					@Override
					public int callback(JApiClass superclass, Map<String, JApiClass> classMap) {
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
			if (isInterface(jApiClass)) {
				if (method.getChangeStatus() == JApiChangeStatus.NEW) {
					addCompatibilityChange(method, JApiCompatibilityChange.METHOD_ADDED_TO_INTERFACE);
				}
			}
		}
	}

	private boolean isInterface(JApiClass jApiClass) {
		return jApiClass.getClassType().getNewTypeOptional().isPresent() && jApiClass.getClassType().getNewTypeOptional().get() == JApiClassType.ClassType.INTERFACE;
	}

	private void checkIfMethodHasBeenPulledUp(JApiClass jApiClass, Map<String, JApiClass> classMap, final JApiMethod method, List<Integer> returnValues) {
		JApiClassType classType = jApiClass.getClassType();
		Optional<JApiClassType.ClassType> newTypeOptional = classType.getNewTypeOptional();
		Optional<JApiClassType.ClassType> oldTypeOptional = classType.getOldTypeOptional();
		if (newTypeOptional.isPresent() && oldTypeOptional.isPresent()) {
			JApiClassType.ClassType newType = newTypeOptional.get();
			JApiClassType.ClassType oldType = oldTypeOptional.get();
			if (newType == JApiClassType.ClassType.INTERFACE && oldType == JApiClassType.ClassType.INTERFACE) {
				forAllImplementedInterfaces(jApiClass, classMap, returnValues, new OnImplementedInterfaceCallback() {
					@Override
					public int callback(JApiClass implementedInterface, Map<String, JApiClass> classMap) {
						for (JApiMethod superMethod : implementedInterface.getMethods()) {
							if (superMethod.getName().equals(method.getName()) && superMethod.hasSameParameter(method) && superMethod.hasSameReturnType(method)) {
								return 1;
							}
						}
						return 0;
					}
				});
			}
		}
	}

	private void forAllImplementedInterfaces(JApiClass jApiClass, Map<String, JApiClass> classMap, List<Integer> returnValues, OnImplementedInterfaceCallback onImplementedInterfaceCallback) {
		List<JApiImplementedInterface> interfaces = jApiClass.getInterfaces();
		for (JApiImplementedInterface implementedInterface : interfaces) {
			String fullyQualifiedName = implementedInterface.getFullyQualifiedName();
			JApiClass foundClass = classMap.get(fullyQualifiedName);
			if (foundClass != null) {
				int returnValue = onImplementedInterfaceCallback.callback(foundClass, classMap);
				returnValues.add(returnValue);
				forAllImplementedInterfaces(foundClass, classMap, returnValues, onImplementedInterfaceCallback);
			} else {
				//TODO
			}
		}
	}

	private void checkIfSuperclassesOrInterfacesChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		final JApiSuperclass superclass = jApiClass.getSuperclass();
		// section 13.4.4 of "Java Language Specification" SE7
		if (superclass.getChangeStatus() == JApiChangeStatus.REMOVED) {
			addCompatibilityChange(superclass, JApiCompatibilityChange.CLASS_REMOVED);
			addCompatibilityChange(jApiClass, JApiCompatibilityChange.SUPERCLASS_REMOVED);
		} else if (superclass.getChangeStatus() == JApiChangeStatus.UNCHANGED || superclass.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			List<Integer> returnValues = new ArrayList<>();
			forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback() {
				@Override
				public int callback(JApiClass superclass, Map<String, JApiClass> classMap) {
					int binaryCompatible = 0;
					checkIfMethodsHaveChangedIncompatible(superclass, classMap);
					checkIfConstructorsHaveChangedIncompatible(superclass, classMap);
					checkIfFieldsHaveChangedIncompatible(superclass, classMap);
					if (!superclass.isBinaryCompatible()) {
						binaryCompatible = 1;
					}
					return binaryCompatible;
				}
			});
			for (Integer returnValue : returnValues) {
				if (returnValue == 1) {
					addCompatibilityChange(jApiClass, JApiCompatibilityChange.SUPERCLASS_MODIFIED_INCOMPATIBLE);
				}
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
						addCompatibilityChange(jApiClass, JApiCompatibilityChange.SUPERCLASS_REMOVED);
					} else if (superClassChangedFromObject) {
						addCompatibilityChange(jApiClass, JApiCompatibilityChange.SUPERCLASS_ADDED);
					} else {
						addCompatibilityChange(jApiClass, JApiCompatibilityChange.SUPERCLASS_CHANGED);
					}
				}
			} else {
				if (superclass.getOldSuperclassName().isPresent() && !superclass.getNewSuperclassName().isPresent()) {
					addCompatibilityChange(jApiClass, JApiCompatibilityChange.SUPERCLASS_REMOVED);
				} else if (!superclass.getOldSuperclassName().isPresent() && superclass.getNewSuperclassName().isPresent()) {
					addCompatibilityChange(jApiClass, JApiCompatibilityChange.SUPERCLASS_ADDED);
				}
			}
		}
		// section 13.4.4 of "Java Language Specification" SE7
		for (JApiImplementedInterface implementedInterface : jApiClass.getInterfaces()) {
			if (implementedInterface.getChangeStatus() == JApiChangeStatus.REMOVED) {
				addCompatibilityChange(implementedInterface, JApiCompatibilityChange.CLASS_REMOVED);
			} else if (implementedInterface.getChangeStatus() == JApiChangeStatus.MODIFIED || implementedInterface.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
				JApiClass foundClass = classMap.get(implementedInterface.getFullyQualifiedName());
				if (foundClass != null) {
					implementedInterface.setJApiClass(foundClass);
					checkIfMethodsHaveChangedIncompatible(foundClass, classMap);
					checkIfFieldsHaveChangedIncompatible(foundClass, classMap);
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
