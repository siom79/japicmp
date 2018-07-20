package japicmp.model;

import japicmp.util.Optional;
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
import javassist.bytecode.ClassFile;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class JApiClass implements JApiHasModifiers, JApiHasChangeStatus, JApiHasAccessModifier, JApiHasStaticModifier, JApiHasFinalModifier, JApiHasAbstractModifier,
	JApiCompatibility, JApiHasAnnotations, JApiJavaObjectSerializationCompatibility, JApiCanBeSynthetic {
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
	private final List<JApiCompatibilityChange> compatibilityChanges = new LinkedList<>();
	private final JApiSerialVersionUid jApiSerialVersionUid;
	private final JApiClassFileFormatVersion classFileFormatVersion;
	private boolean changeCausedByClassElement = false;
	private JApiJavaObjectSerializationChangeStatus jApiJavaObjectSerializationChangeStatus = JApiJavaObjectSerializationChangeStatus.NOT_SERIALIZABLE;

	public JApiClass(JarArchiveComparator jarArchiveComparator, String fullyQualifiedName, Optional<CtClass> oldClass,
					 Optional<CtClass> newClass, JApiChangeStatus changeStatus, JApiClassType classType) {
		this.jarArchiveComparator = jarArchiveComparator;
		this.options = this.jarArchiveComparator.getJarArchiveComparatorOptions();
		this.fullyQualifiedName = fullyQualifiedName;
		this.newClass = newClass;
		this.oldClass = oldClass;
		this.classType = classType;
		this.superclass = extractSuperclass(oldClass, newClass);
		computeMethodChanges(this, oldClass, newClass);
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
		this.classFileFormatVersion = extractClassFileFormatVersion(oldClass, newClass);
		this.changeStatus = evaluateChangeStatus(changeStatus);
	}

	private JApiClassFileFormatVersion extractClassFileFormatVersion(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			ClassFile classFileOld = oldClass.getClassFile();
			ClassFile classFileNew = newClass.getClassFile();
			return new JApiClassFileFormatVersion(classFileOld.getMajorVersion(), classFileOld.getMinorVersion(), classFileNew.getMajorVersion(), classFileNew.getMinorVersion());
		} else {
			if (oldClassOptional.isPresent()) {
				CtClass oldClass = oldClassOptional.get();
				ClassFile classFileOld = oldClass.getClassFile();
				return new JApiClassFileFormatVersion(classFileOld.getMajorVersion(), classFileOld.getMinorVersion(), -1, -1);
			}
			if (newClassOptional.isPresent()) {
				CtClass newClass = newClassOptional.get();
				ClassFile classFileNew = newClass.getClassFile();
				return new JApiClassFileFormatVersion(-1, -1, classFileNew.getMajorVersion(), classFileNew.getMinorVersion());
			}
			return new JApiClassFileFormatVersion(-1, -1, -1, -1);
		}
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
					JApiField jApiField = new JApiField(this, JApiChangeStatus.UNCHANGED, Optional.of(oldField), Optional.of(newField), options);
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				} else {
					JApiField jApiField = new JApiField(this, JApiChangeStatus.REMOVED, Optional.of(oldField), Optional.<CtField>absent(), options);
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
			for (CtField newField : newFieldsMap.values()) {
				CtField oldField = oldFieldsMap.get(newField.getName());
				if (oldField == null) {
					JApiField jApiField = new JApiField(this, JApiChangeStatus.NEW, Optional.<CtField>absent(), Optional.of(newField), options);
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
		} else {
			if (oldClassOptional.isPresent()) {
				Map<String, CtField> fieldMap = buildFieldMap(oldClassOptional.get());
				for (CtField field : fieldMap.values()) {
					JApiField jApiField = new JApiField(this, JApiChangeStatus.REMOVED, Optional.of(field), Optional.<CtField>absent(), options);
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
			if (newClassOptional.isPresent()) {
				Map<String, CtField> fieldMap = buildFieldMap(newClassOptional.get());
				for (CtField field : fieldMap.values()) {
					JApiField jApiField = new JApiField(this, JApiChangeStatus.NEW, Optional.<CtField>absent(), Optional.of(field), options);
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
		}
	}

	private boolean includeField(JApiField jApiField) {
		return ModifierHelper.matchesModifierLevel(jApiField, options.getAccessModifier());
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
		JApiSuperclass retVal = new JApiSuperclass(this, Optional.<CtClass>absent(), Optional.<CtClass>absent(), JApiChangeStatus.UNCHANGED, jarArchiveComparator);
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			Optional<CtClass> superclassOldOptional = getSuperclass(oldClass);
			Optional<CtClass> superclassNewOptional = getSuperclass(newClass);
			if (superclassOldOptional.isPresent() && superclassNewOptional.isPresent()) {
				String nameOld = superclassOldOptional.get().getName();
				String nameNew = superclassNewOptional.get().getName();
				retVal = new JApiSuperclass(this, superclassOldOptional, superclassNewOptional, nameOld.equals(nameNew) ? JApiChangeStatus.UNCHANGED : JApiChangeStatus.MODIFIED, jarArchiveComparator);
			} else if (superclassOldOptional.isPresent()) {
				retVal = new JApiSuperclass(this, superclassOldOptional, superclassNewOptional, JApiChangeStatus.REMOVED, jarArchiveComparator);
			} else if (superclassNewOptional.isPresent()) {
				retVal = new JApiSuperclass(this, superclassOldOptional, superclassNewOptional, JApiChangeStatus.NEW, jarArchiveComparator);
			} else {
				retVal = new JApiSuperclass(this, superclassOldOptional, superclassNewOptional, JApiChangeStatus.UNCHANGED, jarArchiveComparator);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				Optional<CtClass> superclassOldOptional = getSuperclass(oldClassOptional.get());
				if (superclassOldOptional.isPresent()) {
					retVal = new JApiSuperclass(this, superclassOldOptional, Optional.<CtClass>absent(), JApiChangeStatus.REMOVED, jarArchiveComparator);
				} else {
					retVal = new JApiSuperclass(this, Optional.<CtClass>absent(), Optional.<CtClass>absent(), JApiChangeStatus.UNCHANGED, jarArchiveComparator);
				}
			} else if (newClassOptional.isPresent()) {
				Optional<CtClass> superclassNewOptional = getSuperclass(newClassOptional.get());
				if (superclassNewOptional.isPresent()) {
					retVal = new JApiSuperclass(this, Optional.<CtClass>absent(), superclassNewOptional, JApiChangeStatus.NEW, jarArchiveComparator);
				} else {
					retVal = new JApiSuperclass(this, Optional.<CtClass>absent(), Optional.<CtClass>absent(), JApiChangeStatus.UNCHANGED, jarArchiveComparator);
				}
			}
		}
		retVal.setJApiClass(this);
		return retVal;
	}

	private Optional<CtClass> getSuperclass(CtClass ctClass) {
		try {
			CtClass superClass = ctClass.getSuperclass();
			return Optional.fromNullable(superClass);
		} catch (NotFoundException e) {
			if (options.getIgnoreMissingClasses().ignoreClass(e.getMessage())) {
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
			Map<String, CtClass> interfaceMapOldClass = buildInterfaceMap(oldClass, JarArchiveComparator.ArchiveType.OLD);
			Map<String, CtClass> interfaceMapNewClass = buildInterfaceMap(newClass, JarArchiveComparator.ArchiveType.NEW);
			for (CtClass oldInterface : interfaceMapOldClass.values()) {
				CtClass ctClassFound = interfaceMapNewClass.get(oldInterface.getName());
				if (ctClassFound != null) {
					JApiImplementedInterface jApiClass = new JApiImplementedInterface(oldInterface, oldInterface.getName(), JApiChangeStatus.UNCHANGED);
					interfacesArg.add(jApiClass);
				} else {
					JApiImplementedInterface jApiClass = new JApiImplementedInterface(oldInterface, oldInterface.getName(), JApiChangeStatus.REMOVED);
					interfacesArg.add(jApiClass);
				}
			}
			for (CtClass newInterface : interfaceMapNewClass.values()) {
				CtClass ctClassFound = interfaceMapOldClass.get(newInterface.getName());
				if (ctClassFound == null) {
					JApiImplementedInterface jApiClass = new JApiImplementedInterface(newInterface, newInterface.getName(), JApiChangeStatus.NEW);
					interfacesArg.add(jApiClass);
				}
			}
		} else {
			if (oldClassOptional.isPresent()) {
				Map<String, CtClass> interfaceMap = buildInterfaceMap(oldClassOptional.get(), JarArchiveComparator.ArchiveType.OLD);
				for (CtClass ctClass : interfaceMap.values()) {
					JApiImplementedInterface jApiClass = new JApiImplementedInterface(ctClass, ctClass.getName(), JApiChangeStatus.REMOVED);
					interfacesArg.add(jApiClass);
				}
			} else if (newClassOptional.isPresent()) {
				Map<String, CtClass> interfaceMap = buildInterfaceMap(newClassOptional.get(), JarArchiveComparator.ArchiveType.NEW);
				for (CtClass ctClass : interfaceMap.values()) {
					JApiImplementedInterface jApiClass = new JApiImplementedInterface(ctClass, ctClass.getName(), JApiChangeStatus.NEW);
					interfacesArg.add(jApiClass);
				}
			}
		}
	}

	private Map<String, CtClass> buildInterfaceMap(CtClass ctClass, JarArchiveComparator.ArchiveType archiveType) {
		Map<String, CtClass> map = new HashMap<>();
		buildInterfaceMap(ctClass, archiveType, map);
		return map;
	}

	private void buildInterfaceMap(CtClass ctClass, JarArchiveComparator.ArchiveType archiveType, Map<String, CtClass> map) {
		try {
			CtClass[] interfaces = ctClass.getInterfaces();
			for (CtClass ctInterface : interfaces) {
				map.put(ctInterface.getName(), ctInterface);
				buildInterfaceMap(archiveType, map, ctInterface);
			}
			Optional<CtClass> superClassOptional = getSuperclass(ctClass);
			if (superClassOptional.isPresent()) {
				buildInterfaceMap(superClassOptional.get(), archiveType, map);
			}
		} catch (NotFoundException e) {
			if (!options.getIgnoreMissingClasses().ignoreClass(e.getMessage())) {
				throw JApiCmpException.forClassLoading(e, "Class not found: " + e.getMessage(), jarArchiveComparator);
			}
		}
	}

	private void buildInterfaceMap(JarArchiveComparator.ArchiveType archiveType, Map<String, CtClass> map, CtClass ctInterface) throws NotFoundException {
		Optional<CtClass> loadedInterfaceOptional = this.jarArchiveComparator.loadClass(archiveType, ctInterface.getName());
		if (loadedInterfaceOptional.isPresent()) {
			CtClass loadedInterface = loadedInterfaceOptional.get();
			CtClass[] loadedInterfaceInterfaces = loadedInterface.getInterfaces();
			for (CtClass additionalInterface : loadedInterfaceInterfaces) {
				map.put(additionalInterface.getName(), additionalInterface);
				buildInterfaceMap(archiveType, map, additionalInterface);
			}
		}
	}

	private void computeMethodChanges(JApiClass jApiClass, Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		Map<String, List<CtMethod>> oldMethodsMap = createMethodMap(oldClassOptional);
		Map<String, List<CtMethod>> newMethodsMap = createMethodMap(newClassOptional);
		sortMethodsIntoLists(jApiClass, oldMethodsMap, newMethodsMap);
		Map<String, CtConstructor> oldConstructorsMap = createConstructorMap(oldClassOptional);
		Map<String, CtConstructor> newConstructorsMap = createConstructorMap(newClassOptional);
		sortConstructorsIntoLists(jApiClass, oldConstructorsMap, newConstructorsMap);
	}

	private void sortMethodsIntoLists(JApiClass jApiClass, Map<String, List<CtMethod>> oldMethodsMap, Map<String, List<CtMethod>> newMethodsMap) {
		MethodDescriptorParser methodDescriptorParser = new MethodDescriptorParser();
		for (String methodName : oldMethodsMap.keySet()) {
			List<CtMethod> oldMethodsWithSameName = oldMethodsMap.get(methodName);
			Iterator<CtMethod> oldMethodsWithSameNameIterator = oldMethodsWithSameName.iterator();
			while (oldMethodsWithSameNameIterator.hasNext()) {
				CtMethod oldMethod = oldMethodsWithSameNameIterator.next();
				methodDescriptorParser.parse(oldMethod.getSignature());
				List<CtMethod> newMethodsWithSameName = newMethodsMap.get(methodName);
				if (newMethodsWithSameName == null) {
					JApiMethod jApiMethod = new JApiMethod(jApiClass, oldMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(oldMethod), Optional.<CtMethod>absent(), jarArchiveComparator);
					addParametersToMethod(methodDescriptorParser, jApiMethod);
					if (includeMethod(jApiMethod)) {
						methods.add(jApiMethod);
					}
				} else {
					Optional<CtMethod> matchingMethodOptional = findMatchingMethod(oldMethod, newMethodsWithSameName);
					if (matchingMethodOptional.isPresent()) {
						CtMethod matchingMethod = matchingMethodOptional.get();
						JApiMethod jApiMethod = new JApiMethod(jApiClass, oldMethod.getName(), JApiChangeStatus.UNCHANGED, Optional.of(oldMethod), Optional.of(matchingMethod), jarArchiveComparator);
						addParametersToMethod(methodDescriptorParser, jApiMethod);
						if (includeMethod(jApiMethod)) {
							methods.add(jApiMethod);
						}
						oldMethodsWithSameNameIterator.remove();
						newMethodsWithSameName.remove(matchingMethod);
					} else {
						JApiMethod jApiMethod = new JApiMethod(jApiClass, oldMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(oldMethod), Optional.<CtMethod>absent(), jarArchiveComparator);
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
					JApiMethod jApiMethod = new JApiMethod(jApiClass, ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtMethod>absent(), Optional.of(ctMethod), jarArchiveComparator);
					addParametersToMethod(methodDescriptorParser, jApiMethod);
					if (includeMethod(jApiMethod)) {
						methods.add(jApiMethod);
					}
				} else {
					Optional<CtMethod> matchingMethodOptional = findMatchingMethod(ctMethod, methodsWithSameName);
					if (matchingMethodOptional.isPresent()) {
						CtMethod matchingMethod = matchingMethodOptional.get();
						JApiMethod jApiMethod = new JApiMethod(jApiClass, ctMethod.getName(), JApiChangeStatus.UNCHANGED, Optional.of(ctMethod), Optional.of(matchingMethod), jarArchiveComparator);
						addParametersToMethod(methodDescriptorParser, jApiMethod);
						if (includeMethod(jApiMethod)) {
							methods.add(jApiMethod);
						}
					} else {
						JApiMethod jApiMethod = new JApiMethod(jApiClass, ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtMethod>absent(), Optional.of(ctMethod), jarArchiveComparator);
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
		return ModifierHelper.matchesModifierLevel(jApiMethod, options.getAccessModifier());
	}

	private void sortConstructorsIntoLists(JApiClass jApiClass, Map<String, CtConstructor> oldConstructorsMap, Map<String, CtConstructor> newConstructorsMap) {
		MethodDescriptorParser methodDescriptorParser = new MethodDescriptorParser();
		for (CtConstructor ctMethod : oldConstructorsMap.values()) {
			String longName = ctMethod.getLongName();
			methodDescriptorParser.parse(ctMethod.getSignature());
			CtConstructor foundMethod = newConstructorsMap.get(longName);
			if (foundMethod == null) {
				JApiConstructor jApiConstructor = new JApiConstructor(jApiClass, ctMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(ctMethod), Optional.<CtConstructor>absent(), jarArchiveComparator);
				addParametersToMethod(methodDescriptorParser, jApiConstructor);
				if (includeConstructor(jApiConstructor)) {
					constructors.add(jApiConstructor);
				}
			} else {
				JApiConstructor jApiConstructor = new JApiConstructor(jApiClass, ctMethod.getName(), JApiChangeStatus.UNCHANGED, Optional.of(ctMethod), Optional.of(foundMethod), jarArchiveComparator);
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
				JApiConstructor jApiConstructor = new JApiConstructor(jApiClass, ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtConstructor>absent(), Optional.of(ctMethod), jarArchiveComparator);
				addParametersToMethod(methodDescriptorParser, jApiConstructor);
				if (includeConstructor(jApiConstructor)) {
					constructors.add(jApiConstructor);
				}
			}
		}
	}

	private boolean includeConstructor(JApiConstructor jApiConstructor) {
		return ModifierHelper.matchesModifierLevel(jApiConstructor, options.getAccessModifier());
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
		boolean binaryCompatible = true;
		for (JApiCompatibilityChange compatibilityChange : compatibilityChanges) {
			if (!compatibilityChange.isBinaryCompatible()) {
				binaryCompatible = false;
				break;
			}
		}
		if (binaryCompatible) {
			for (JApiField field : fields) {
				if (!field.isBinaryCompatible()) {
					binaryCompatible = false;
					break;
				}
			}
		}
		if (binaryCompatible) {
			for (JApiMethod method : methods) {
				if (!method.isBinaryCompatible()) {
					binaryCompatible = false;
					break;
				}
			}
		}
		if (binaryCompatible) {
			for (JApiConstructor constructor : constructors) {
				if (!constructor.isBinaryCompatible()) {
					binaryCompatible = false;
					break;
				}
			}
		}
		if (binaryCompatible) {
			if (!superclass.isBinaryCompatible()) {
				binaryCompatible = false;
			}
		}
		if (binaryCompatible) {
			for (JApiImplementedInterface anInterface : interfaces) {
				// don't use JApiImplementedInterface.isBinaryCompatible(), since that checks the corresponding source
				// without checking if this class still provides the equivalent methods from some other source
				for (JApiCompatibilityChange change : anInterface.getCompatibilityChanges()) {
					if (!change.isBinaryCompatible()) {
						binaryCompatible = false;
						break;
					}
				}
				if (!binaryCompatible) {
					break;
				}
			}
		}
		return binaryCompatible;
	}

	@Override
	@XmlAttribute
	public boolean isSourceCompatible() {
		boolean sourceCompatible = true;
		for (JApiCompatibilityChange compatibilityChange : compatibilityChanges) {
			if (!compatibilityChange.isSourceCompatible()) {
				sourceCompatible = false;
				break;
			}
		}
		if (sourceCompatible) {
			for (JApiField field : fields) {
				if (!field.isSourceCompatible()) {
					sourceCompatible = false;
					break;
				}
			}
		}
		if (sourceCompatible) {
			for (JApiMethod method : methods) {
				if (!method.isSourceCompatible()) {
					sourceCompatible = false;
					break;
				}
			}
		}
		if (sourceCompatible) {
			for (JApiConstructor constructor : constructors) {
				if (!constructor.isSourceCompatible()) {
					sourceCompatible = false;
					break;
				}
			}
		}
		if (sourceCompatible) {
			if (!superclass.isSourceCompatible()) {
				sourceCompatible = false;
			}
		}
		if (sourceCompatible) {
			for (JApiImplementedInterface anInterface : interfaces) {
				// don't use JApiImplementedInterface.isSourceCompatible(), since that checks the corresponding source
				// without checking if this class still provides the equivalent methods from some other source
				for (JApiCompatibilityChange change : anInterface.getCompatibilityChanges()) {
					if (!change.isSourceCompatible()) {
						sourceCompatible = false;
						break;
					}
				}
				if (!sourceCompatible) {
					break;
				}
			}
		}
		return sourceCompatible;
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

	@XmlElementWrapper(name = "compatibilityChanges")
	@XmlElement(name = "compatibilityChange")
	public List<JApiCompatibilityChange> getCompatibilityChanges() {
		return this.compatibilityChanges;
	}

	@XmlElement(name = "classFileFormatVersion")
	public JApiClassFileFormatVersion getClassFileFormatVersion() {
		return classFileFormatVersion;
	}

	public String toString()
	{
		return "JApiClass [fullyQualifiedName="
			+ fullyQualifiedName
			+ ", changeStatus="
			+ changeStatus
			+ ", compatibilityChanges="
			+ compatibilityChanges
			+ "]";
	}



}
