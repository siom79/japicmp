package japicmp.model;

import com.google.common.base.Optional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static japicmp.util.ModifierHelper.hasModifierLevelDecreased;
import static japicmp.util.ModifierHelper.isNotPrivate;

public class BinaryCompatibility {

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
		boolean binaryCompatible = true;
		if (jApiClass.getChangeStatus() == JApiChangeStatus.REMOVED) {
			binaryCompatible = false;
		} else if (jApiClass.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			// section 13.4.1 of "Java Language Specification" SE7
			if (jApiClass.getAbstractModifier().hasChangedFromTo(AbstractModifier.NON_ABSTRACT, AbstractModifier.ABSTRACT)) {
				binaryCompatible = false;
			}
			// section 13.4.2 of "Java Language Specification" SE7
			if (jApiClass.getFinalModifier().hasChangedFromTo(FinalModifier.NON_FINAL, FinalModifier.FINAL)) {
				binaryCompatible = false;
			}
			// section 13.4.3 of "Java Language Specification" SE7
			if (jApiClass.getAccessModifier().hasChangedFrom(AccessModifier.PUBLIC)) {
				binaryCompatible = false;
			}
		}
		// section 13.4.4 of "Java Language Specification" SE7
		if (superclassesOrInterfacesChangedIncompatible(jApiClass, classMap)) {
			binaryCompatible = false;
		}
		if (methodsHaveChangedIncompatible(jApiClass, classMap)) {
			binaryCompatible = false;
		}
		if (constructorsHaveChangedIncompatible(jApiClass, classMap)) {
			binaryCompatible = false;
		}
		if (fieldsHaveChangedIncompatible(jApiClass, classMap)) {
			binaryCompatible = false;
		}
		if (jApiClass.getClassType().getChangeStatus() == JApiChangeStatus.MODIFIED) {
			binaryCompatible = false;
		}
		jApiClass.setBinaryCompatible(binaryCompatible);
	}

	private boolean fieldsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		boolean changedIncompatible = false;
		for (final JApiField field : jApiClass.getFields()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(field) && field.getChangeStatus() == JApiChangeStatus.REMOVED) {
				field.setBinaryCompatible(false);
				changedIncompatible = true;
			}
			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(field)) {
				field.setBinaryCompatible(false);
				changedIncompatible = true;
			}
			// section 13.4.8 of "Java Language Specification" SE7
			if (isNotPrivate(field) && field.getChangeStatus() == JApiChangeStatus.NEW) {
				ArrayList<Boolean> returnValues = new ArrayList<Boolean>();
				forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback() {
					@Override
					public boolean callback(JApiClass superclass, Map<String, JApiClass> classMap) {
						boolean changedIncompatible = false;
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
									if (subclassFieldIsStatic || accessModifierSubclassLess) {
										field.setBinaryCompatible(false);
										changedIncompatible = true;
									}
								}
							}
						}
						return changedIncompatible;
					}
				});
				for (boolean returnValue : returnValues) {
					if (returnValue) {
						field.setBinaryCompatible(false);
						changedIncompatible = true;
					}
				}
			}
			// section 13.4.9 of "Java Language Specification" SE7
			if (isNotPrivate(field) && field.getFinalModifier().hasChangedFromTo(FinalModifier.NON_FINAL, FinalModifier.FINAL)) {
				field.setBinaryCompatible(false);
				changedIncompatible = true;
			}
			// section 13.4.10 of "Java Language Specification" SE7
			if (isNotPrivate(field) && (field.getStaticModifier().hasChangedFromTo(StaticModifier.NON_STATIC, StaticModifier.STATIC) || field.getStaticModifier().hasChangedFromTo(StaticModifier.STATIC, StaticModifier.NON_STATIC))) {
				field.setBinaryCompatible(false);
				changedIncompatible = true;
			}
			if (isNotPrivate(field) && field.getType().hasChanged()) {
				field.setBinaryCompatible(false);
				changedIncompatible = true;
			}
		}
		return changedIncompatible;
	}

	private interface OnSuperclassCallback {
		boolean callback(JApiClass superclass, Map<String, JApiClass> classMap);
	}

	private interface OnImplementedInterfaceCallback {
		boolean callback(JApiClass implementedInterface, Map<String, JApiClass> classMap);
	}

	private void forAllSuperclasses(JApiClass jApiClass, Map<String, JApiClass> classMap, List<Boolean> returnValues, OnSuperclassCallback onSuperclassCallback) {
		JApiSuperclass superclass = jApiClass.getSuperclass();
		if (superclass.getNewSuperclassName().isPresent()) {
			JApiClass foundClass = classMap.get(superclass.getNewSuperclassName().get());
			if (foundClass != null) {
				boolean returnValue = onSuperclassCallback.callback(foundClass, classMap);
				returnValues.add(returnValue);
				forAllSuperclasses(foundClass, classMap, returnValues, onSuperclassCallback);
			} else {
				Optional<JApiClass> superclassJApiClassOptional = superclass.getJApiClass();
				if (superclassJApiClassOptional.isPresent()) {
					JApiClass superclassJApiClass = superclassJApiClassOptional.get();
					boolean returnValue = onSuperclassCallback.callback(superclassJApiClass, classMap);
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

	private boolean constructorsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		boolean changedIncompatible = false;
		for (JApiConstructor constructor : jApiClass.getConstructors()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(constructor) && constructor.getChangeStatus() == JApiChangeStatus.REMOVED) {
				constructor.setBinaryCompatible(false);
				changedIncompatible = true;
			}
			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(constructor)) {
				constructor.setBinaryCompatible(false);
				changedIncompatible = true;
			}
		}
		return changedIncompatible;
	}

	private boolean methodsHaveChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		boolean changedIncompatible = false;
		for (final JApiMethod method : jApiClass.getMethods()) {
			// section 13.4.6 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getChangeStatus() == JApiChangeStatus.REMOVED) {
				final List<Boolean> returnValues = new ArrayList<Boolean>();
				forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback() {
					@Override
					public boolean callback(JApiClass superclass, Map<String, JApiClass> classMap) {
						for (JApiMethod superMethod : superclass.getMethods()) {
							if (superMethod.getName().equals(method.getName()) && superMethod.hasSameParameter(method) && superMethod.hasSameReturnType(method)) {
								return true;
							}
						}
						return false;
					}
				});
				checkIfMethodHasBeenPulledUp(jApiClass, classMap, method, returnValues);
				boolean superclassHasSameMethod = false;
				for (boolean returnValue : returnValues) {
					if (returnValue) {
						superclassHasSameMethod = true;
					}
				}
				if (!superclassHasSameMethod) {
					method.setBinaryCompatible(false);
					changedIncompatible = true;
				}
			}
			// section 13.4.7 of "Java Language Specification" SE7
			if (hasModifierLevelDecreased(method)) {
				method.setBinaryCompatible(false);
				changedIncompatible = true;
			}
			// section 13.4.12 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getChangeStatus() == JApiChangeStatus.NEW) {
				List<Boolean> returnValues = new ArrayList<Boolean>();
				forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback() {
					@Override
					public boolean callback(JApiClass superclass, Map<String, JApiClass> classMap) {
						for (JApiMethod superMethod : superclass.getMethods()) {
							if (superMethod.getName().equals(method.getName()) && superMethod.hasSameParameter(method) && superMethod.hasSameReturnType(method)) {
								if (superMethod.getAccessModifier().getNewModifier().isPresent() && method.getAccessModifier().getNewModifier().isPresent()) {
									if (superMethod.getAccessModifier().getNewModifier().get().getLevel() > method.getAccessModifier().getNewModifier().get().getLevel()) {
										return true;
									}
								}
								if (superMethod.getStaticModifier().getNewModifier().isPresent() && method.getStaticModifier().getNewModifier().isPresent()) {
									if (superMethod.getStaticModifier().getNewModifier().get() == StaticModifier.NON_STATIC && method.getStaticModifier().getNewModifier().get() == StaticModifier.STATIC) {
										return true;
									}
								}
							}
						}
						return false;
					}
				});
				for (boolean returnValue : returnValues) {
					if (returnValue) {
						method.setBinaryCompatible(false);
						changedIncompatible = true;
					}
				}
			}
			// section 13.4.15 of "Java Language Specification" SE7 (Method Result Type)
			if (method.getReturnType().getChangeStatus() == JApiChangeStatus.MODIFIED) {
				method.setBinaryCompatible(false);
				changedIncompatible = true;
			}
			// section 13.4.16 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getAbstractModifier().hasChangedFromTo(AbstractModifier.NON_ABSTRACT, AbstractModifier.ABSTRACT)) {
				method.setBinaryCompatible(false);
				changedIncompatible = true;
			}
			// section 13.4.17 of "Java Language Specification" SE7
			if (isNotPrivate(method) && method.getFinalModifier().hasChangedFromTo(FinalModifier.NON_FINAL, FinalModifier.FINAL)) {
				if (!(method.getStaticModifier().getOldModifier().isPresent() && method.getStaticModifier().getOldModifier().get() == StaticModifier.STATIC)) {
					method.setBinaryCompatible(false);
					changedIncompatible = true;
				}
			}
			// section 13.4.18 of "Java Language Specification" SE7
			if (isNotPrivate(method) && (method.getStaticModifier().hasChangedFromTo(StaticModifier.NON_STATIC, StaticModifier.STATIC) || method.getStaticModifier().hasChangedFromTo(StaticModifier.STATIC, StaticModifier.NON_STATIC))) {
				method.setBinaryCompatible(false);
				changedIncompatible = true;
			}
		}
		return changedIncompatible;
	}

	private void checkIfMethodHasBeenPulledUp(JApiClass jApiClass, Map<String, JApiClass> classMap, final JApiMethod method, List<Boolean> returnValues) {
		JApiClassType classType = jApiClass.getClassType();
		Optional<JApiClassType.ClassType> newTypeOptional = classType.getNewTypeOptional();
		Optional<JApiClassType.ClassType> oldTypeOptional = classType.getOldTypeOptional();
		if (newTypeOptional.isPresent() && oldTypeOptional.isPresent()) {
			JApiClassType.ClassType newType = newTypeOptional.get();
			JApiClassType.ClassType oldType = oldTypeOptional.get();
			if (newType == JApiClassType.ClassType.INTERFACE && oldType == JApiClassType.ClassType.INTERFACE) {
				forAllImplementedInterfaces(jApiClass, classMap, returnValues, new OnImplementedInterfaceCallback() {
					@Override
					public boolean callback(JApiClass implementedInterface, Map<String, JApiClass> classMap) {
						for (JApiMethod superMethod : implementedInterface.getMethods()) {
							if (superMethod.getName().equals(method.getName()) && superMethod.hasSameParameter(method) && superMethod.hasSameReturnType(method)) {
								return true;
							}
						}
						return false;
					}
				});
			}
		}
	}

	private void forAllImplementedInterfaces(JApiClass jApiClass, Map<String, JApiClass> classMap, List<Boolean> returnValues, OnImplementedInterfaceCallback onImplementedInterfaceCallback) {
		List<JApiImplementedInterface> interfaces = jApiClass.getInterfaces();
		for (JApiImplementedInterface implementedInterface : interfaces) {
			String fullyQualifiedName = implementedInterface.getFullyQualifiedName();
			JApiClass foundClass = classMap.get(fullyQualifiedName);
			if (foundClass != null) {
				boolean returnValue = onImplementedInterfaceCallback.callback(foundClass, classMap);
				returnValues.add(returnValue);
				forAllImplementedInterfaces(foundClass, classMap, returnValues, onImplementedInterfaceCallback);
			} else {
				//TODO
			}
		}
	}

	private boolean superclassesOrInterfacesChangedIncompatible(JApiClass jApiClass, Map<String, JApiClass> classMap) {
		boolean changedIncompatible = false;
		final JApiSuperclass superclass = jApiClass.getSuperclass();
		// section 13.4.4 of "Java Language Specification" SE7
		if (superclass.getChangeStatus() == JApiChangeStatus.REMOVED) {
			superclass.setBinaryCompatible(false);
			changedIncompatible = true;
		} else if (superclass.getChangeStatus() == JApiChangeStatus.UNCHANGED || superclass.getChangeStatus() == JApiChangeStatus.MODIFIED) {
			List<Boolean> returnValues = new ArrayList<Boolean>();
			forAllSuperclasses(jApiClass, classMap, returnValues, new OnSuperclassCallback() {
				@Override
				public boolean callback(JApiClass superclass, Map<String, JApiClass> classMap) {
					boolean changedIncompatible = false;
					if (methodsHaveChangedIncompatible(superclass, classMap)) {
						superclass.setBinaryCompatible(false);
						changedIncompatible = true;
					}
					if (constructorsHaveChangedIncompatible(superclass, classMap)) {
						superclass.setBinaryCompatible(false);
						changedIncompatible = true;
					}
					if (fieldsHaveChangedIncompatible(superclass, classMap)) {
						superclass.setBinaryCompatible(false);
						changedIncompatible = true;
					}
					return changedIncompatible;
				}
			});
			for (boolean returnValue : returnValues) {
				if (returnValue) {
					changedIncompatible = true;
				}
			}
			if (superclass.getOldSuperclassName().isPresent() && superclass.getNewSuperclassName().isPresent()) {
				if (!superclass.getOldSuperclassName().get().equals(superclass.getNewSuperclassName().get())) {
					superclass.setBinaryCompatible(false);
					changedIncompatible = true;
				}
			} else if (superclass.getOldSuperclassName().isPresent() && !superclass.getNewSuperclassName().isPresent()) {
				superclass.setBinaryCompatible(false);
				changedIncompatible = true;
			} else if (!superclass.getOldSuperclassName().isPresent() && superclass.getNewSuperclassName().isPresent()) {
				superclass.setBinaryCompatible(false);
				changedIncompatible = true;
			}
		}
		// section 13.4.4 of "Java Language Specification" SE7
		for (JApiImplementedInterface implementedInterface : jApiClass.getInterfaces()) {
			if (implementedInterface.getChangeStatus() == JApiChangeStatus.REMOVED) {
				implementedInterface.setBinaryCompatible(false);
				changedIncompatible = true;
			} else if (implementedInterface.getChangeStatus() == JApiChangeStatus.MODIFIED || implementedInterface.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
				JApiClass foundClass = classMap.get(implementedInterface.getFullyQualifiedName());
				if (foundClass != null) {
					if (methodsHaveChangedIncompatible(foundClass, classMap)) {
						implementedInterface.setBinaryCompatible(false);
						changedIncompatible = true;
					}
					if (constructorsHaveChangedIncompatible(foundClass, classMap)) {
						implementedInterface.setBinaryCompatible(false);
						changedIncompatible = true;
					}
					if (fieldsHaveChangedIncompatible(foundClass, classMap)) {
						implementedInterface.setBinaryCompatible(false);
						changedIncompatible = true;
					}
				}
				if (implementedInterface.getChangeStatus() == JApiChangeStatus.MODIFIED) {
					implementedInterface.setBinaryCompatible(false);
					changedIncompatible = true;
				}
			}
		}
		return changedIncompatible;
	}
}
