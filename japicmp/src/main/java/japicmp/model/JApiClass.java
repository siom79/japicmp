package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.exception.JApiCmpException;
import japicmp.util.AnnotationHelper;
import japicmp.util.Constants;
import japicmp.util.MethodDescriptorParser;
import japicmp.util.ModifierHelper;
import japicmp.util.SignatureParser;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JApiClass implements JApiHasModifiers, JApiHasChangeStatus, JApiHasAccessModifier, JApiHasStaticModifier, JApiHasFinalModifier, JApiHasAbstractModifier, JApiBinaryCompatibility, JApiHasAnnotations, JApiJavaObjectSerializationCompatibility, JApiCanBeSynthetic {
	private final JarArchiveComparator jarArchiveComparator;
	private final String fullyQualifiedName;
	private final JApiClassType classType;
	private final JarArchiveComparatorOptions options;
	private final Optional<CtClass> oldClass;
	private final Optional<CtClass> newClass;
	private final JApiChangeStatus changeStatus;
	private final JApiSuperclass superclass;
	private final List<JApiImplementedInterface> interfaces = new LinkedList<>();
	private final List<JApiField> fields = new LinkedList<>();
	private final List<JApiConstructor> constructors = new LinkedList<>();
	private final List<JApiMethod> methods = new LinkedList<>();
	private final List<JApiAnnotation> annotations = new LinkedList<>();
	private final JApiModifier<AccessModifier> accessModifier;
	private final JApiModifier<FinalModifier> finalModifier;
	private final JApiModifier<StaticModifier> staticModifier;
	private final JApiModifier<AbstractModifier> abstractModifier;
	private final JApiModifier<SyntheticModifier> syntheticModifier;
	private final JApiAttribute<SyntheticAttribute> syntheticAttribute;
	private final JApiSerialVersionUid jApiSerialVersionUid;
	private boolean binaryCompatible = true;
	private JApiJavaObjectSerializationChangeStatus jApiJavaObjectSerializationChangeStatus = JApiJavaObjectSerializationChangeStatus.NOT_SERIALIZABLE;
	private boolean changeCausedByClassElement = false;

	public JApiClass(JarArchiveComparator jarArchiveComparator, String fullyQualifiedName, Optional<CtClass> oldClass, Optional<CtClass> newClass, JApiChangeStatus changeStatus, JApiClassType classType, JarArchiveComparatorOptions options) {
		this.jarArchiveComparator = jarArchiveComparator;
		this.fullyQualifiedName = fullyQualifiedName;
		this.newClass = newClass;
		this.oldClass = oldClass;
		this.classType = classType;
		this.options = options;
		this.superclass = extractSuperclass(oldClass, newClass);
		computeMethodChanges(oldClass, newClass);
		computeInterfaceChanges(this.interfaces, oldClass, newClass);
		computeFieldChanges(this.fields, oldClass, newClass);
		computeAnnotationChanges(this.annotations, oldClass, newClass);
		this.accessModifier = extractAccessModifier(oldClass, newClass);
		this.finalModifier = extractFinalModifier(oldClass, newClass);
		this.staticModifier = extractStaticModifier(oldClass, newClass);
		this.abstractModifier = extractAbstractModifier(oldClass, newClass);
		this.syntheticModifier = extractSyntheticModifier(oldClass, newClass);
		this.syntheticAttribute = extractSyntheticAttribute(oldClass, newClass);
		this.jApiSerialVersionUid = JavaObjectSerializationCompatibility.extractSerialVersionUid(options, jarArchiveComparator, oldClass, newClass);
		this.changeStatus = evaluateChangeStatus(changeStatus);
	}

	private void computeAnnotationChanges(List<JApiAnnotation> annotations, Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		AnnotationHelper.computeAnnotationChanges(annotations, oldClassOptional, newClassOptional, options, new AnnotationHelper.AnnotationsAttributeCallback<CtClass>() {
			@Override
			public AnnotationsAttribute getAnnotationsAttribute(CtClass ctClass) {
				return (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
			}
		});
	}

	private JApiAttribute<SyntheticAttribute> extractSyntheticAttribute(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		JApiAttribute<SyntheticAttribute> jApiAttribute = new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			byte[] attributeOldClass = oldClass.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
			byte[] attributeNewClass = newClass.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
			if (attributeOldClass != null && attributeNewClass != null) {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
			} else if (attributeOldClass != null) {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
			} else if (attributeNewClass != null) {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
			} else {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
			}
		} else {
			if (oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				byte[] attribute = ctClass.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
				if (attribute != null) {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.<SyntheticAttribute>absent());
				} else {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.<SyntheticAttribute>absent());
				}
			}
			if (newClassOptional.isPresent()) {
				CtClass ctClass = newClassOptional.get();
				byte[] attribute = ctClass.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
				if (attribute != null) {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>absent(), Optional.of(SyntheticAttribute.SYNTHETIC));
				} else {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>absent(), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
				}
			}
		}
		return jApiAttribute;
	}

	private void computeFieldChanges(List<JApiField> fields, Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			Map<String, CtField> oldFieldsMap = buildFieldMap(oldClass);
			Map<String, CtField> newFieldsMap = buildFieldMap(newClass);
			for (CtField oldField : oldFieldsMap.values()) {
				String oldFieldName = oldField.getName();
				CtField newField = newFieldsMap.get(oldFieldName);
				if (newField != null) {
					JApiField jApiField = new JApiField(JApiChangeStatus.UNCHANGED, Optional.of(oldField), Optional.of(newField), options);
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				} else {
					JApiField jApiField = new JApiField(JApiChangeStatus.REMOVED, Optional.of(oldField), Optional.<CtField>absent(), options);
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
			for (CtField newField : newFieldsMap.values()) {
				CtField oldField = oldFieldsMap.get(newField.getName());
				if (oldField == null) {
					JApiField jApiField = new JApiField(JApiChangeStatus.NEW, Optional.<CtField>absent(), Optional.of(newField), options);
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
		} else {
			if (oldClassOptional.isPresent()) {
				Map<String, CtField> fieldMap = buildFieldMap(oldClassOptional.get());
				for (CtField field : fieldMap.values()) {
					JApiField jApiField = new JApiField(JApiChangeStatus.REMOVED, Optional.of(field), Optional.<CtField>absent(), options);
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
			if (newClassOptional.isPresent()) {
				Map<String, CtField> fieldMap = buildFieldMap(newClassOptional.get());
				for (CtField field : fieldMap.values()) {
					JApiField jApiField = new JApiField(JApiChangeStatus.NEW, Optional.<CtField>absent(), Optional.of(field), options);
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
		}
	}

	private boolean includeField(JApiField jApiField) {
		return ModifierHelper.matchesModifierLevel(jApiField, options.getAccessModifier()) && ModifierHelper.includeSynthetic(jApiField, options);
	}

	private Map<String, CtField> buildFieldMap(CtClass ctClass) {
		Map<String, CtField> fieldMap = new HashMap<>();
		CtField[] declaredFields = ctClass.getDeclaredFields();
		for (CtField field : declaredFields) {
			if (options.getFilters().includeField(field)) {
				String name = field.getName();
				fieldMap.put(name, field);
			}
		}
		return fieldMap;
	}

	private JApiSuperclass extractSuperclass(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		JApiSuperclass retVal = new JApiSuperclass(Optional.<CtClass>absent(), Optional.<CtClass>absent(), JApiChangeStatus.UNCHANGED, jarArchiveComparator);
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			Optional<CtClass> superclassOldOptional = getSuperclass(oldClass);
			Optional<CtClass> superclassNewOptional = getSuperclass(newClass);
			if (superclassOldOptional.isPresent() && superclassNewOptional.isPresent()) {
				String nameOld = superclassOldOptional.get().getName();
				String nameNew = superclassNewOptional.get().getName();
				retVal = new JApiSuperclass(superclassOldOptional, superclassNewOptional, nameOld.equals(nameNew) ? JApiChangeStatus.UNCHANGED : JApiChangeStatus.MODIFIED, jarArchiveComparator);
			} else if (superclassOldOptional.isPresent()) {
				retVal = new JApiSuperclass(superclassOldOptional, superclassNewOptional, JApiChangeStatus.REMOVED, jarArchiveComparator);
			} else if (superclassNewOptional.isPresent()) {
				retVal = new JApiSuperclass(superclassOldOptional, superclassNewOptional, JApiChangeStatus.NEW, jarArchiveComparator);
			} else {
				retVal = new JApiSuperclass(superclassOldOptional, superclassNewOptional, JApiChangeStatus.UNCHANGED, jarArchiveComparator);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				Optional<CtClass> superclassOldOptional = getSuperclass(oldClassOptional.get());
				if (superclassOldOptional.isPresent()) {
					retVal = new JApiSuperclass(superclassOldOptional, Optional.<CtClass>absent(), JApiChangeStatus.REMOVED, jarArchiveComparator);
				} else {
					retVal = new JApiSuperclass(Optional.<CtClass>absent(), Optional.<CtClass>absent(), JApiChangeStatus.UNCHANGED, jarArchiveComparator);
				}
			} else if (newClassOptional.isPresent()) {
				Optional<CtClass> superclassNewOptional = getSuperclass(newClassOptional.get());
				if (superclassNewOptional.isPresent()) {
					retVal = new JApiSuperclass(Optional.<CtClass>absent(), superclassNewOptional, JApiChangeStatus.NEW, jarArchiveComparator);
				} else {
					retVal = new JApiSuperclass(Optional.<CtClass>absent(), Optional.<CtClass>absent(), JApiChangeStatus.UNCHANGED, jarArchiveComparator);
				}
			}
		}
		return retVal;
	}

	private Optional<CtClass> getSuperclass(CtClass ctClass) {
		try {
			CtClass superClass = ctClass.getSuperclass();
			return Optional.fromNullable(superClass);
		} catch (NotFoundException e) {
			if (options.isIgnoreMissingClasses()) {
				return Optional.absent();
			} else {
				throw JApiCmpException.forClassLoading(e, e.getMessage(), jarArchiveComparator);
			}
		}
	}

	private void computeInterfaceChanges(List<JApiImplementedInterface> interfacesArg, Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			Map<String, CtClass> interfaceMapOldClass = buildInterfaceMap(oldClass);
			Map<String, CtClass> interfaceMapNewClass = buildInterfaceMap(newClass);
			for (CtClass oldInterface : interfaceMapOldClass.values()) {
				CtClass ctClassFound = interfaceMapNewClass.get(oldInterface.getName());
				if (ctClassFound != null) {
					JApiImplementedInterface jApiClass = new JApiImplementedInterface(oldInterface.getName(), JApiChangeStatus.UNCHANGED);
					interfacesArg.add(jApiClass);
				} else {
					JApiImplementedInterface jApiClass = new JApiImplementedInterface(oldInterface.getName(), JApiChangeStatus.REMOVED);
					interfacesArg.add(jApiClass);
				}
			}
			for (CtClass newInterface : interfaceMapNewClass.values()) {
				CtClass ctClassFound = interfaceMapOldClass.get(newInterface.getName());
				if (ctClassFound == null) {
					JApiImplementedInterface jApiClass = new JApiImplementedInterface(newInterface.getName(), JApiChangeStatus.NEW);
					interfacesArg.add(jApiClass);
				}
			}
		} else {
			if (oldClassOptional.isPresent()) {
				Map<String, CtClass> interfaceMap = buildInterfaceMap(oldClassOptional.get());
				for (CtClass ctClass : interfaceMap.values()) {
					JApiImplementedInterface jApiClass = new JApiImplementedInterface(ctClass.getName(), JApiChangeStatus.REMOVED);
					interfacesArg.add(jApiClass);
				}
			} else if (newClassOptional.isPresent()) {
				Map<String, CtClass> interfaceMap = buildInterfaceMap(newClassOptional.get());
				for (CtClass ctClass : interfaceMap.values()) {
					JApiImplementedInterface jApiClass = new JApiImplementedInterface(ctClass.getName(), JApiChangeStatus.NEW);
					interfacesArg.add(jApiClass);
				}
			}
		}
	}

	private Map<String, CtClass> buildInterfaceMap(CtClass ctClass) {
		Map<String, CtClass> map = new HashMap<>();
		try {
			CtClass[] interfaces = ctClass.getInterfaces();
			for (CtClass ctInterface : interfaces) {
				map.put(ctInterface.getName(), ctInterface);
			}
		} catch (NotFoundException e) {
			if (!options.isIgnoreMissingClasses()) {
				throw JApiCmpException.forClassLoading(e, "Class not found: " + e.getMessage(), jarArchiveComparator);
			}
		}
		return map;
	}

	private void computeMethodChanges(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		Map<String, List<CtMethod>> oldMethodsMap = createMethodMap(oldClassOptional);
		Map<String, List<CtMethod>> newMethodsMap = createMethodMap(newClassOptional);
		sortMethodsIntoLists(oldMethodsMap, newMethodsMap);
		Map<String, CtConstructor> oldConstructorsMap = createConstructorMap(oldClassOptional);
		Map<String, CtConstructor> newConstructorsMap = createConstructorMap(newClassOptional);
		sortConstructorsIntoLists(oldConstructorsMap, newConstructorsMap);
	}

	private void sortMethodsIntoLists(Map<String, List<CtMethod>> oldMethodsMap, Map<String, List<CtMethod>> newMethodsMap) {
		MethodDescriptorParser methodDescriptorParser = new MethodDescriptorParser();
		for (String methodName : oldMethodsMap.keySet()) {
			List<CtMethod> oldMethodsWithSameName = oldMethodsMap.get(methodName);
			Iterator<CtMethod> oldMethodsWithSameNameIterator = oldMethodsWithSameName.iterator();
			while (oldMethodsWithSameNameIterator.hasNext()) {
				CtMethod oldMethod = oldMethodsWithSameNameIterator.next();
				methodDescriptorParser.parse(oldMethod.getSignature());
				List<CtMethod> newMethodsWithSameName = newMethodsMap.get(methodName);
				if (newMethodsWithSameName == null) {
					JApiMethod jApiMethod = new JApiMethod(oldMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(oldMethod), Optional.<CtMethod>absent(), options);
					addParametersToMethod(methodDescriptorParser, jApiMethod);
					if (includeMethod(jApiMethod)) {
						methods.add(jApiMethod);
					}
				} else {
					Optional<CtMethod> matchingMethodOptional = findMatchingMethod(oldMethod, newMethodsWithSameName);
					if (matchingMethodOptional.isPresent()) {
						CtMethod matchingMethod = matchingMethodOptional.get();
						JApiMethod jApiMethod = new JApiMethod(oldMethod.getName(), JApiChangeStatus.UNCHANGED, Optional.of(oldMethod), Optional.of(matchingMethod), options);
						addParametersToMethod(methodDescriptorParser, jApiMethod);
						if (includeMethod(jApiMethod)) {
							methods.add(jApiMethod);
						}
						oldMethodsWithSameNameIterator.remove();
						newMethodsWithSameName.remove(matchingMethod);
					} else {
						JApiMethod jApiMethod = new JApiMethod(oldMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(oldMethod), Optional.<CtMethod>absent(), options);
						addParametersToMethod(methodDescriptorParser, jApiMethod);
						if (includeMethod(jApiMethod)) {
							methods.add(jApiMethod);
						}
					}
				}
			}
		}
		for (String methodName : newMethodsMap.keySet()) {
			List<CtMethod> ctMethods = newMethodsMap.get(methodName);
			for (CtMethod ctMethod : ctMethods) {
				methodDescriptorParser.parse(ctMethod.getSignature());
				List<CtMethod> methodsWithSameName = oldMethodsMap.get(ctMethod.getName());
				if (methodsWithSameName == null) {
					JApiMethod jApiMethod = new JApiMethod(ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtMethod>absent(), Optional.of(ctMethod), options);
					addParametersToMethod(methodDescriptorParser, jApiMethod);
					if (includeMethod(jApiMethod)) {
						methods.add(jApiMethod);
					}
				} else {
					Optional<CtMethod> matchingMethodOptional = findMatchingMethod(ctMethod, methodsWithSameName);
					if (matchingMethodOptional.isPresent()) {
						CtMethod matchingMethod = matchingMethodOptional.get();
						JApiMethod jApiMethod = new JApiMethod(ctMethod.getName(), JApiChangeStatus.UNCHANGED, Optional.of(ctMethod), Optional.of(matchingMethod), options);
						addParametersToMethod(methodDescriptorParser, jApiMethod);
						if (includeMethod(jApiMethod)) {
							methods.add(jApiMethod);
						}
					} else {
						JApiMethod jApiMethod = new JApiMethod(ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtMethod>absent(), Optional.of(ctMethod), options);
						addParametersToMethod(methodDescriptorParser, jApiMethod);
						if (includeMethod(jApiMethod)) {
							methods.add(jApiMethod);
						}
					}
				}
			}
		}
	}

	private Optional<CtMethod> findMatchingMethod(CtMethod method, List<CtMethod> candidates) {
		Optional<CtMethod> found = Optional.absent();
		SignatureParser methodSignatureParser = new SignatureParser();
		methodSignatureParser.parse(method.getSignature());
		List<CtMethod> methodsWithSameParameters = new ArrayList<>();
		for (CtMethod candidate : candidates) {
			boolean parameterListsEqual = true;
			List<String> probeParameters = methodSignatureParser.getParameters();
			SignatureParser candidateSignatureParser = new SignatureParser();
			candidateSignatureParser.parse(candidate.getSignature());
			List<String> candidateParameters = candidateSignatureParser.getParameters();
			if (probeParameters.size() != candidateParameters.size()) {
				parameterListsEqual = false;
			}
			if (parameterListsEqual) {
				for (int i = 0; i < probeParameters.size(); i++) {
					String probeParameter = probeParameters.get(i);
					String candidateParameter = candidateParameters.get(i);
					if (!probeParameter.equals(candidateParameter)) {
						parameterListsEqual = false;
					}
				}
			}
			if (parameterListsEqual) {
				methodsWithSameParameters.add(candidate);
			}
		}
		if (methodsWithSameParameters.size() == 1) {
			found = Optional.of(methodsWithSameParameters.get(0));
		} else if (methodsWithSameParameters.size() > 1) {
			CtMethod methodWithSameReturnType = null;
			String probeReturnType = methodSignatureParser.getReturnType();
			for (CtMethod candidate : methodsWithSameParameters) {
				SignatureParser candidateSignatureParser = new SignatureParser();
				candidateSignatureParser.parse(candidate.getSignature());
				String candidateReturnType = candidateSignatureParser.getReturnType();
				if (probeReturnType.equals(candidateReturnType)) {
					methodWithSameReturnType = candidate;
				}
			}
			if (methodWithSameReturnType != null) {
				found = Optional.of(methodWithSameReturnType);
			} else {
				found = Optional.of(methodsWithSameParameters.get(0));
			}
		}
		return found;
	}

	private boolean includeMethod(JApiMethod jApiMethod) {
		return ModifierHelper.matchesModifierLevel(jApiMethod, options.getAccessModifier()) && ModifierHelper.includeSynthetic(jApiMethod, options);
	}

	private void sortConstructorsIntoLists(Map<String, CtConstructor> oldConstructorsMap, Map<String, CtConstructor> newConstructorsMap) {
		MethodDescriptorParser methodDescriptorParser = new MethodDescriptorParser();
		for (CtConstructor ctMethod : oldConstructorsMap.values()) {
			String longName = ctMethod.getLongName();
			methodDescriptorParser.parse(ctMethod.getSignature());
			CtConstructor foundMethod = newConstructorsMap.get(longName);
			if (foundMethod == null) {
				JApiConstructor jApiConstructor = new JApiConstructor(ctMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(ctMethod), Optional.<CtConstructor>absent(), options);
				addParametersToMethod(methodDescriptorParser, jApiConstructor);
				if (includeConstructor(jApiConstructor)) {
					constructors.add(jApiConstructor);
				}
			} else {
				JApiConstructor jApiConstructor = new JApiConstructor(ctMethod.getName(), JApiChangeStatus.UNCHANGED, Optional.of(ctMethod), Optional.of(foundMethod), options);
				addParametersToMethod(methodDescriptorParser, jApiConstructor);
				if (includeConstructor(jApiConstructor)) {
					constructors.add(jApiConstructor);
				}
			}
		}
		for (CtConstructor ctMethod : newConstructorsMap.values()) {
			String longName = ctMethod.getLongName();
			methodDescriptorParser.parse(ctMethod.getSignature());
			CtConstructor foundMethod = oldConstructorsMap.get(longName);
			if (foundMethod == null) {
				JApiConstructor jApiConstructor = new JApiConstructor(ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtConstructor>absent(), Optional.of(ctMethod), options);
				addParametersToMethod(methodDescriptorParser, jApiConstructor);
				if (includeConstructor(jApiConstructor)) {
					constructors.add(jApiConstructor);
				}
			}
		}
	}

	private boolean includeConstructor(JApiConstructor jApiConstructor) {
		return ModifierHelper.matchesModifierLevel(jApiConstructor, options.getAccessModifier()) && ModifierHelper.includeSynthetic(jApiConstructor, options);
	}

	private void addParametersToMethod(MethodDescriptorParser methodDescriptorParser, JApiBehavior jApiMethod) {
		for (String param : methodDescriptorParser.getParameters()) {
			jApiMethod.addParameter(new JApiParameter(param));
		}
	}

	private Map<String, List<CtMethod>> createMethodMap(Optional<CtClass> ctClassOptional) {
		Map<String, List<CtMethod>> methods = new HashMap<>();
		if (ctClassOptional.isPresent()) {
			CtClass ctClass = ctClassOptional.get();
			for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
				if (options.getFilters().includeBehavior(ctMethod)) {
					String name = ctMethod.getName();
					List<CtMethod> ctMethods = methods.get(name);
					if (ctMethods == null) {
						ctMethods = new ArrayList<>();
						methods.put(name, ctMethods);
					}
					ctMethods.add(ctMethod);
				}
			}
		}
		return methods;
	}

	private Map<String, CtConstructor> createConstructorMap(Optional<CtClass> ctClass) {
		Map<String, CtConstructor> methods = new HashMap<>();
		if (ctClass.isPresent()) {
			for (CtConstructor ctConstructor : ctClass.get().getDeclaredConstructors()) {
				if (options.getFilters().includeBehavior(ctConstructor)) {
					methods.put(ctConstructor.getLongName(), ctConstructor);
				}
			}
		}
		return methods;
	}

	private JApiChangeStatus evaluateChangeStatus(JApiChangeStatus changeStatus) {
		if (changeStatus == JApiChangeStatus.UNCHANGED) {
			if (staticModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (finalModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (accessModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (abstractModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.syntheticAttribute.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			for (JApiImplementedInterface implementedInterface : interfaces) {
				if (implementedInterface.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					changeStatus = JApiChangeStatus.MODIFIED;
				}
			}
			if (superclass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			for (JApiField field : fields) {
				if (field.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					changeStatus = JApiChangeStatus.MODIFIED;
					changeCausedByClassElement = true;
				}
			}
			for (JApiMethod method : methods) {
				if (method.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					changeStatus = JApiChangeStatus.MODIFIED;
					changeCausedByClassElement = true;
				}
			}
			for (JApiConstructor constructor : constructors) {
				if (constructor.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					changeStatus = JApiChangeStatus.MODIFIED;
					changeCausedByClassElement = true;
				}
			}
		}
		return changeStatus;
	}

	private JApiModifier<StaticModifier> extractStaticModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		return ModifierHelper.extractModifierFromClass(oldClassOptional, newClassOptional, new ModifierHelper.ExtractModifierFromClassCallback<StaticModifier>() {
			@Override
			public StaticModifier getModifierForOld(CtClass oldClass) {
				return Modifier.isStatic(oldClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			}

			@Override
			public StaticModifier getModifierForNew(CtClass newClass) {
				return Modifier.isStatic(newClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			}
		});
	}

	private JApiModifier<FinalModifier> extractFinalModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		return ModifierHelper.extractModifierFromClass(oldClassOptional, newClassOptional, new ModifierHelper.ExtractModifierFromClassCallback<FinalModifier>() {
			@Override
			public FinalModifier getModifierForOld(CtClass oldClass) {
				return Modifier.isFinal(oldClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			}

			@Override
			public FinalModifier getModifierForNew(CtClass newClass) {
				return Modifier.isFinal(newClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			}
		});
	}

	private JApiModifier<AccessModifier> extractAccessModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		return ModifierHelper.extractModifierFromClass(oldClassOptional, newClassOptional, new ModifierHelper.ExtractModifierFromClassCallback<AccessModifier>() {
			@Override
			public AccessModifier getModifierForOld(CtClass oldClass) {
				return ModifierHelper.translateToModifierLevel(oldClass.getModifiers());
			}

			@Override
			public AccessModifier getModifierForNew(CtClass newClass) {
				return ModifierHelper.translateToModifierLevel(newClass.getModifiers());
			}
		});
	}

	private JApiModifier<AbstractModifier> extractAbstractModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		return ModifierHelper.extractModifierFromClass(oldClassOptional, newClassOptional, new ModifierHelper.ExtractModifierFromClassCallback<AbstractModifier>() {
			@Override
			public AbstractModifier getModifierForOld(CtClass oldClass) {
				return Modifier.isAbstract(oldClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			}

			@Override
			public AbstractModifier getModifierForNew(CtClass newClass) {
				return Modifier.isAbstract(newClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			}
		});
	}

	private JApiModifier<SyntheticModifier> extractSyntheticModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		return ModifierHelper.extractModifierFromClass(oldClassOptional, newClassOptional, new ModifierHelper.ExtractModifierFromClassCallback<SyntheticModifier>() {
			@Override
			public SyntheticModifier getModifierForOld(CtClass oldClass) {
				return ModifierHelper.isSynthetic(oldClass.getModifiers()) ? SyntheticModifier.SYNTHETIC : SyntheticModifier.NON_SYNTHETIC;
			}

			@Override
			public SyntheticModifier getModifierForNew(CtClass newClass) {
				return ModifierHelper.isSynthetic(newClass.getModifiers()) ? SyntheticModifier.SYNTHETIC : SyntheticModifier.NON_SYNTHETIC;
			}
		});
	}

	@XmlAttribute
	@Override
	public JApiJavaObjectSerializationChangeStatus getJavaObjectSerializationCompatible() {
		return jApiJavaObjectSerializationChangeStatus;
	}

	@XmlAttribute
	public String getJavaObjectSerializationCompatibleAsString() {
		return jApiJavaObjectSerializationChangeStatus.getDescription();
	}

	@XmlElement
	@Override
	public JApiSerialVersionUid getSerialVersionUid() {
		return this.jApiSerialVersionUid;
	}

	void setJavaObjectSerializationCompatible(JApiJavaObjectSerializationChangeStatus jApiJavaObjectSerializationChangeStatus) {
		this.jApiJavaObjectSerializationChangeStatus = jApiJavaObjectSerializationChangeStatus;
	}

	@XmlAttribute
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	@XmlTransient
	public Optional<CtClass> getNewClass() {
		return newClass;
	}

	@XmlTransient
	public Optional<CtClass> getOldClass() {
		return oldClass;
	}

	@XmlElementWrapper(name = "modifiers")
	@XmlElement(name = "modifier")
	public List<? extends JApiModifier<? extends Enum<? extends Enum<?>>>> getModifiers() {
		return Arrays.asList(this.finalModifier, this.staticModifier, this.accessModifier, this.abstractModifier, this.syntheticModifier);
	}

	@XmlElement(name = "superclass")
	public JApiSuperclass getSuperclass() {
		return superclass;
	}

	@XmlElementWrapper(name = "interfaces")
	@XmlElement(name = "interface")
	public List<JApiImplementedInterface> getInterfaces() {
		return interfaces;
	}

	@XmlElementWrapper(name = "constructors")
	@XmlElement(name = "constructor")
	public List<JApiConstructor> getConstructors() {
		return constructors;
	}

	@XmlElementWrapper(name = "methods")
	@XmlElement(name = "method")
	public List<JApiMethod> getMethods() {
		return methods;
	}

	@XmlElementWrapper(name = "fields")
	@XmlElement(name = "field")
	public List<JApiField> getFields() {
		return fields;
	}

	@XmlElement(name = "classType")
	public JApiClassType getClassType() {
		return classType;
	}

	@XmlTransient
	public JApiModifier<FinalModifier> getFinalModifier() {
		return this.finalModifier;
	}

	@XmlTransient
	public JApiModifier<StaticModifier> getStaticModifier() {
		return staticModifier;
	}

	@XmlTransient
	public JApiModifier<AccessModifier> getAccessModifier() {
		return this.accessModifier;
	}

	@XmlTransient
	public JApiModifier<AbstractModifier> getAbstractModifier() {
		return this.abstractModifier;
	}

	@XmlTransient
	public JApiModifier<SyntheticModifier> getSyntheticModifier() {
		return this.syntheticModifier;
	}

	@XmlTransient
	public JApiAttribute<SyntheticAttribute> getSyntheticAttribute() {
		return syntheticAttribute;
	}

	@XmlElementWrapper(name = "attributes")
	@XmlElement(name = "attribute")
	public List<JApiAttribute<? extends Enum<?>>> getAttributes() {
		List<JApiAttribute<? extends Enum<?>>> list = new ArrayList<>();
		list.add(this.syntheticAttribute);
		return list;
	}

	@Override
	@XmlAttribute
	public boolean isBinaryCompatible() {
		return this.binaryCompatible;
	}

	void setBinaryCompatible(boolean binaryCompatible) {
		this.binaryCompatible = binaryCompatible;
	}

	@XmlElementWrapper(name = "annotations")
	@XmlElement(name = "annotation")
	public List<JApiAnnotation> getAnnotations() {
		return annotations;
	}

	@XmlTransient
	public boolean isChangeCausedByClassElement() {
		return changeCausedByClassElement;
	}
}
