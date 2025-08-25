package japicmp.model;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.exception.JApiCmpException;
import japicmp.util.*;
import java.util.stream.Stream;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ClassFile;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.*;

import static japicmp.util.ModifierHelper.isSynthetic;

public class JApiClass implements JApiHasModifiers, JApiHasChangeStatus, JApiHasAccessModifier, JApiHasStaticModifier, JApiHasFinalModifier, JApiHasAbstractModifier,
	JApiCompatibility, JApiHasAnnotations, JApiJavaObjectSerializationCompatibility, JApiCanBeSynthetic, JApiHasGenericTemplates {
	private final JarArchiveComparator jarArchiveComparator;
	private final String fullyQualifiedName;
	private final JApiClassType classType;
	private final JarArchiveComparatorOptions options;
	private final Optional<CtClass> oldClass;
	private final Optional<CtClass> newClass;
	private final JApiChangeStatus changeStatus;
	private final JApiSuperclass superclass;
	private final List<JApiImplementedInterface> interfaces = new ArrayList<>();
	private final List<JApiField> fields = new ArrayList<>();
	private final List<JApiConstructor> constructors = new ArrayList<>();
	private final List<JApiMethod> methods = new ArrayList<>();
	private final List<JApiAnnotation> annotations = new ArrayList<>();
	private final JApiModifier<AccessModifier> accessModifier;
	private final JApiModifier<FinalModifier> finalModifier;
	private final JApiModifier<StaticModifier> staticModifier;
	private final JApiModifier<AbstractModifier> abstractModifier;
	private final JApiModifier<SyntheticModifier> syntheticModifier;
	private final JApiAttribute<SyntheticAttribute> syntheticAttribute;
	private final List<JApiCompatibilityChange> compatibilityChanges = new ArrayList<>();
	private final JApiSerialVersionUid jApiSerialVersionUid;
	private final JApiClassFileFormatVersion classFileFormatVersion;
	private boolean changeCausedByClassElement = false;
	private JApiJavaObjectSerializationChangeStatus jApiJavaObjectSerializationChangeStatus = JApiJavaObjectSerializationChangeStatus.NOT_SERIALIZABLE;
	private List<JApiGenericTemplate> genericTemplates = new ArrayList<>();

	public JApiClass(JarArchiveComparator jarArchiveComparator, String fullyQualifiedName, Optional<CtClass> oldClass,
					 Optional<CtClass> newClass, JApiChangeStatus changeStatus, JApiClassType classType) {
		this.jarArchiveComparator = jarArchiveComparator;
		this.options = this.jarArchiveComparator.getJarArchiveComparatorOptions();
		this.fullyQualifiedName = fullyQualifiedName;
		this.newClass = newClass;
		this.oldClass = oldClass;
		this.classType = classType;
		this.superclass = extractSuperclass(oldClass, newClass);
		computeGenericTemplateChanges(oldClass, newClass);
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

	private void computeGenericTemplateChanges(Optional<CtClass> oldClass, Optional<CtClass> newClass) {
		this.genericTemplates = GenericTemplateHelper.computeGenericTemplateChanges(new GenericTemplateHelper.SignatureParserCallback() {
			@Override
			public boolean isOldAndNewPresent() {
				return oldClass.isPresent() && newClass.isPresent();
			}

			@Override
			public boolean isOldPresent() {
				return oldClass.isPresent();
			}

			@Override
			public boolean isNewPresent() {
				return newClass.isPresent();
			}

			@Override
			public SignatureParser oldSignatureParser() {
				SignatureParser signatureParser = new SignatureParser();
				signatureParser.parseTemplatesOfClass(oldClass.get());
				return signatureParser;
			}

			@Override
			public SignatureParser newSignatureParser() {
				SignatureParser signatureParser = new SignatureParser();
				signatureParser.parseTemplatesOfClass(newClass.get());
				return signatureParser;
			}
		});
	}

	private JApiClassFileFormatVersion extractClassFileFormatVersion(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		Optional<ClassFile> oldClassFile = oldClassOptional.map(CtClass::getClassFile);
		Optional<ClassFile> newClassFile = newClassOptional.map(CtClass::getClassFile);
		int majorVersionOld = oldClassFile.map(ClassFile::getMajorVersion).orElse(-1);
		int minorVersionOld = oldClassFile.map(ClassFile::getMinorVersion).orElse(-1);
		int majorVersionNew = newClassFile.map(ClassFile::getMajorVersion).orElse(-1);
		int minorVersionNew = newClassFile.map(ClassFile::getMinorVersion).orElse(-1);
		return new JApiClassFileFormatVersion(majorVersionOld, minorVersionOld, majorVersionNew, minorVersionNew);
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
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.<SyntheticAttribute>empty());
				} else {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.<SyntheticAttribute>empty());
				}
			}
			if (newClassOptional.isPresent()) {
				CtClass ctClass = newClassOptional.get();
				byte[] attribute = ctClass.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
				if (attribute != null) {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>empty(), Optional.of(SyntheticAttribute.SYNTHETIC));
				} else {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>empty(), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
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
					enhanceGenericTypeToField(oldField, jApiField.getOldGenericTypes());
					enhanceGenericTypeToField(newField, jApiField.getNewGenericTypes());
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				} else {
					JApiField jApiField = new JApiField(this, JApiChangeStatus.REMOVED, Optional.of(oldField), Optional.<CtField>empty(), options);
					enhanceGenericTypeToField(oldField, jApiField.getOldGenericTypes());
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
			for (CtField newField : newFieldsMap.values()) {
				CtField oldField = oldFieldsMap.get(newField.getName());
				if (oldField == null) {
					JApiField jApiField = new JApiField(this, JApiChangeStatus.NEW, Optional.<CtField>empty(), Optional.of(newField), options);
					enhanceGenericTypeToField(newField, jApiField.getNewGenericTypes());
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
		} else {
			if (oldClassOptional.isPresent()) {
				Map<String, CtField> fieldMap = buildFieldMap(oldClassOptional.get());
				for (CtField field : fieldMap.values()) {
					JApiField jApiField = new JApiField(this, JApiChangeStatus.REMOVED, Optional.of(field), Optional.<CtField>empty(), options);
					enhanceGenericTypeToField(field, jApiField.getOldGenericTypes());
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
			if (newClassOptional.isPresent()) {
				Map<String, CtField> fieldMap = buildFieldMap(newClassOptional.get());
				for (CtField field : fieldMap.values()) {
					JApiField jApiField = new JApiField(this, JApiChangeStatus.NEW, Optional.<CtField>empty(), Optional.of(field), options);
					enhanceGenericTypeToField(field, jApiField.getNewGenericTypes());
					if (includeField(jApiField)) {
						fields.add(jApiField);
					}
				}
			}
		}
	}

	private void enhanceGenericTypeToField(CtField field, List<JApiGenericType> genericTypes) {
		Optional.ofNullable(field.getGenericSignature())
			.map(new SignatureParser()::parseTypes).map(List::stream).flatMap(Stream::findFirst)
			.ifPresent(parsedParameter -> SignatureParser.copyGenericParameters(parsedParameter, genericTypes));
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
		JApiSuperclass retVal = new JApiSuperclass(this, Optional.<CtClass>empty(), Optional.<CtClass>empty(), JApiChangeStatus.UNCHANGED, jarArchiveComparator);
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
					retVal = new JApiSuperclass(this, superclassOldOptional, Optional.<CtClass>empty(), JApiChangeStatus.REMOVED, jarArchiveComparator);
				} else {
					retVal = new JApiSuperclass(this, Optional.<CtClass>empty(), Optional.<CtClass>empty(), JApiChangeStatus.UNCHANGED, jarArchiveComparator);
				}
			} else if (newClassOptional.isPresent()) {
				Optional<CtClass> superclassNewOptional = getSuperclass(newClassOptional.get());
				if (superclassNewOptional.isPresent()) {
					retVal = new JApiSuperclass(this, Optional.<CtClass>empty(), superclassNewOptional, JApiChangeStatus.NEW, jarArchiveComparator);
				} else {
					retVal = new JApiSuperclass(this, Optional.<CtClass>empty(), Optional.<CtClass>empty(), JApiChangeStatus.UNCHANGED, jarArchiveComparator);
				}
			}
		}
		return retVal;
	}

	private Optional<CtClass> getSuperclass(CtClass ctClass) {
		try {
			CtClass superClass = ctClass.getSuperclass();
			return Optional.ofNullable(superClass);
		} catch (NotFoundException e) {
			if (options.getIgnoreMissingClasses().ignoreClass(e.getMessage())) {
				return Optional.empty();
			} else {
				throw JApiCmpException.forClassLoading(e, e.getMessage(), jarArchiveComparator);
			}
		}
	}

	private void computeInterfaceChanges(List<JApiImplementedInterface> interfacesArg, Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		Map<String, CtClass> interfaceMapOldClass = oldClassOptional.map(oldClass -> buildInterfaceMap(oldClass, JarArchiveComparator.ArchiveType.OLD)).orElseGet(Collections::emptyMap);
		Map<String, CtClass> interfaceMapNewClass = newClassOptional.map(newClass -> buildInterfaceMap(newClass, JarArchiveComparator.ArchiveType.NEW)).orElseGet(Collections::emptyMap);
		for (CtClass oldInterface : interfaceMapOldClass.values()) {
			CtClass ctClassFound = interfaceMapNewClass.get(oldInterface.getName());
			JApiChangeStatus interfaceStatus = ctClassFound != null ? JApiChangeStatus.UNCHANGED : JApiChangeStatus.REMOVED;
			JApiImplementedInterface jApiClass = new JApiImplementedInterface(oldInterface, oldInterface.getName(), interfaceStatus);
			interfacesArg.add(jApiClass);
		}
		for (CtClass newInterface : interfaceMapNewClass.values()) {
			if (interfaceMapOldClass.get(newInterface.getName()) == null) {
				JApiImplementedInterface jApiClass = new JApiImplementedInterface(newInterface, newInterface.getName(), JApiChangeStatus.NEW);
				interfacesArg.add(jApiClass);
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
			getSuperclass(ctClass).ifPresent(superClass -> buildInterfaceMap(superClass, archiveType, map));
		} catch (NotFoundException e) {
			if (!options.getIgnoreMissingClasses().ignoreClass(e.getMessage())) {
				throw JApiCmpException.forClassLoading(e, "Class not found: " + e.getMessage(), jarArchiveComparator);
			}
		}
	}

	private void buildInterfaceMap(JarArchiveComparator.ArchiveType archiveType, Map<String, CtClass> map, CtClass ctInterface) throws NotFoundException {
		CtClass loadedInterface = this.jarArchiveComparator.loadClass(archiveType, ctInterface.getName()).orElse(null);
		if (loadedInterface != null) {
			for (CtClass additionalInterface : loadedInterface.getInterfaces()) {
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
		SignatureParser methodDescriptorParser = new SignatureParser();
		for (String methodName : oldMethodsMap.keySet()) {
			List<CtMethod> oldMethodsWithSameName = oldMethodsMap.get(methodName);
			Iterator<CtMethod> oldMethodsWithSameNameIterator = oldMethodsWithSameName.iterator();
			while (oldMethodsWithSameNameIterator.hasNext()) {
				CtMethod oldMethod = oldMethodsWithSameNameIterator.next();
				methodDescriptorParser.parse(oldMethod);
				List<CtMethod> newMethodsWithSameName = newMethodsMap.get(methodName);
				if (newMethodsWithSameName == null) {
					JApiMethod jApiMethod = new JApiMethod(jApiClass, oldMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(oldMethod), Optional.<CtMethod>empty(), jarArchiveComparator);
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
						JApiMethod jApiMethod = new JApiMethod(jApiClass, oldMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(oldMethod), Optional.<CtMethod>empty(), jarArchiveComparator);
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
				methodDescriptorParser.parse(ctMethod);
				List<CtMethod> methodsWithSameName = oldMethodsMap.get(ctMethod.getName());
				if (methodsWithSameName == null) {
					JApiMethod jApiMethod = new JApiMethod(jApiClass, ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtMethod>empty(), Optional.of(ctMethod), jarArchiveComparator);
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
						JApiMethod jApiMethod = new JApiMethod(jApiClass, ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtMethod>empty(), Optional.of(ctMethod), jarArchiveComparator);
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
		Optional<CtMethod> found = Optional.empty();
		SignatureParser methodSignatureParser = new SignatureParser();
		methodSignatureParser.parse(method);
		List<CtMethod> methodsWithSameParameters = new ArrayList<>();
		findMatchingMethodsWithSameParameterTypes(candidates, methodSignatureParser, methodsWithSameParameters);
		if (methodsWithSameParameters.size() == 1) {
			found = Optional.of(methodsWithSameParameters.get(0));
		} else if (methodsWithSameParameters.size() > 1) {
			CtMethod methodWithSameReturnType = null;
			SignatureParser.ParsedParameter probeReturnType = methodSignatureParser.getReturnType();
			for (CtMethod candidate : methodsWithSameParameters) {
				SignatureParser candidateSignatureParser = new SignatureParser();
				candidateSignatureParser.parse(candidate);
				SignatureParser.ParsedParameter candidateReturnType = candidateSignatureParser.getReturnType();
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

	private void findMatchingMethodsWithSameParameterTypes(List<CtMethod> candidates, SignatureParser methodSignatureParser,
														   List<CtMethod> methodsWithSameParameters) {
		for (CtMethod candidate : candidates) {
			boolean parameterListsEqual = true;
			List<SignatureParser.ParsedParameter> probeParameters = methodSignatureParser.getParameters();
			SignatureParser candidateSignatureParser = new SignatureParser();
			candidateSignatureParser.parse(candidate);
			List<SignatureParser.ParsedParameter> candidateParameters = candidateSignatureParser.getParameters();
			if (probeParameters.size() != candidateParameters.size()) {
				parameterListsEqual = false;
			}
			if (parameterListsEqual) {
				for (int i = 0; i < probeParameters.size(); i++) {
					SignatureParser.ParsedParameter probeParameter = probeParameters.get(i);
					SignatureParser.ParsedParameter candidateParameter = candidateParameters.get(i);
					if (!probeParameter.equals(candidateParameter)) {
						parameterListsEqual = false;
						break;
					}
				}
			}
			if (parameterListsEqual) {
				methodsWithSameParameters.add(candidate);
			}
		}
	}

	private boolean includeMethod(JApiMethod jApiMethod) {
		return ModifierHelper.matchesModifierLevel(jApiMethod, options.getAccessModifier());
	}

	private void sortConstructorsIntoLists(JApiClass jApiClass, Map<String, CtConstructor> oldConstructorsMap, Map<String, CtConstructor> newConstructorsMap) {
		SignatureParser methodDescriptorParser = new SignatureParser();
		for (CtConstructor ctMethod : oldConstructorsMap.values()) {
			String longName = ctMethod.getLongName();
			methodDescriptorParser.parse(ctMethod);
			CtConstructor foundMethod = newConstructorsMap.get(longName);
			JApiChangeStatus constructorStatus = foundMethod == null ? JApiChangeStatus.REMOVED : JApiChangeStatus.UNCHANGED;
			JApiConstructor jApiConstructor = new JApiConstructor(jApiClass, ctMethod.getName(), constructorStatus, Optional.of(ctMethod), Optional.ofNullable(foundMethod), jarArchiveComparator);
			addParametersToMethod(methodDescriptorParser, jApiConstructor);
			if (includeConstructor(jApiConstructor)) {
				constructors.add(jApiConstructor);
			}
		}
		for (CtConstructor ctMethod : newConstructorsMap.values()) {
			String longName = ctMethod.getLongName();
			methodDescriptorParser.parse(ctMethod);
			CtConstructor foundMethod = oldConstructorsMap.get(longName);
			if (foundMethod == null) {
				JApiConstructor jApiConstructor = new JApiConstructor(jApiClass, ctMethod.getName(), JApiChangeStatus.NEW, Optional.empty(), Optional.of(ctMethod), jarArchiveComparator);
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

	private void addParametersToMethod(SignatureParser methodDescriptorParser, JApiBehavior jApiBehavior) {
		for (SignatureParser.ParsedParameter param : methodDescriptorParser.getParameters()) {
			jApiBehavior.addParameter(new JApiParameter(param.getType(), Optional.empty()));
		}
		jApiBehavior.enhanceGenericTypeToParameters();
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
			if (syntheticAttribute.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
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
				if (field.getChangeStatus() != JApiChangeStatus.UNCHANGED && !isSynthetic(field)) {
					changeStatus = JApiChangeStatus.MODIFIED;
					changeCausedByClassElement = true;
				}
			}
			for (JApiMethod method : methods) {
				if (method.getChangeStatus() != JApiChangeStatus.UNCHANGED && !isSynthetic(method)) {
					changeStatus = JApiChangeStatus.MODIFIED;
					changeCausedByClassElement = true;
				}
			}
			for (JApiConstructor constructor : constructors) {
				if (constructor.getChangeStatus() != JApiChangeStatus.UNCHANGED && !isSynthetic(constructor)) {
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
				return isSynthetic(oldClass.getModifiers()) ? SyntheticModifier.SYNTHETIC : SyntheticModifier.NON_SYNTHETIC;
			}

			@Override
			public SyntheticModifier getModifierForNew(CtClass newClass) {
				return isSynthetic(newClass.getModifiers()) ? SyntheticModifier.SYNTHETIC : SyntheticModifier.NON_SYNTHETIC;
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

	@XmlElementWrapper(name = "genericTemplates")
	@XmlElement(name = "genericTemplate")
	public List<JApiGenericTemplate> getGenericTemplates() {
		return genericTemplates;
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
