package japicmp.model;

import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.exception.JApiCmpException;
import japicmp.util.AnnotationHelper;
import japicmp.util.Constants;
import japicmp.util.ModifierHelper;
import japicmp.util.SignatureParser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;

public class JApiClass implements JApiHasModifiers, JApiHasChangeStatus, JApiHasAccessModifier, JApiHasStaticModifier, JApiHasFinalModifier, JApiHasAbstractModifier,
		JApiBinaryCompatibility, JApiHasAnnotations {
	private JarArchiveComparator jarArchiveComparator;
	private final String fullyQualifiedName;
	private final Type type;
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
	private final JApiAttribute<SyntheticAttribute> syntheticAttribute;
	private boolean binaryCompatible = true;

	public enum Type {
		ANNOTATION, INTERFACE, CLASS, ENUM
	}

	public JApiClass(JarArchiveComparator jarArchiveComparator, String fullyQualifiedName, Optional<CtClass> oldClass, Optional<CtClass> newClass, JApiChangeStatus changeStatus, Type type) {
		this.jarArchiveComparator = jarArchiveComparator;
		this.fullyQualifiedName = fullyQualifiedName;
		this.newClass = newClass;
		this.oldClass = oldClass;
		this.type = type;
		this.superclass = extractSuperclass(oldClass, newClass);
		computeMethodChanges(oldClass, newClass);
		computeInterfaceChanges(this.interfaces, oldClass, newClass);
		computeFieldChanges(this.fields, oldClass, newClass);
		computeAnnotationChanges(this.annotations, oldClass, newClass);
		this.accessModifier = extractAccessModifier(oldClass, newClass);
		this.finalModifier = extractFinalModifier(oldClass, newClass);
		this.staticModifier = extractStaticModifier(oldClass, newClass);
		this.abstractModifier = extractAbstractModifier(oldClass, newClass);
		this.syntheticAttribute = extractSyntheticAttribute(oldClass, newClass);
		this.changeStatus = evaluateChangeStatus(changeStatus);
	}

	private void computeAnnotationChanges(List<JApiAnnotation> annotations, Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		AnnotationHelper.computeAnnotationChanges(annotations, oldClassOptional, newClassOptional, new AnnotationHelper.AnnotationsAttributeCallback<CtClass>() {
			@Override
			public AnnotationsAttribute getAnnotationsAttribute(CtClass ctClass) {
				return (AnnotationsAttribute) ctClass.getClassFile().getAttribute(AnnotationsAttribute.visibleTag);
			}
		});
	}

	private JApiAttribute<SyntheticAttribute> extractSyntheticAttribute(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			byte[] attributeOldClass = oldClass.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
			byte[] attributeNewClass = newClass.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
			if (attributeOldClass != null && attributeNewClass != null) {
				return new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
			} else if (attributeOldClass != null) {
				return new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
			} else if (attributeNewClass != null) {
				return new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
			} else {
				return new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
			}
		} else {
			if (oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				byte[] attribute = ctClass.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
				if (attribute != null) {
					return new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.<SyntheticAttribute>absent());
				} else {
					return new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.<SyntheticAttribute>absent());
				}
			}
			if (newClassOptional.isPresent()) {
				CtClass ctClass = newClassOptional.get();
				byte[] attribute = ctClass.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
				if (attribute != null) {
					return new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>absent(), Optional.of(SyntheticAttribute.SYNTHETIC));
				} else {
					return new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>absent(), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
				}
			}
		}
		return new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
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
					JApiField jApiField = new JApiField(JApiChangeStatus.UNCHANGED, Optional.of(oldField), Optional.of(newField));
					fields.add(jApiField);
				} else {
					JApiField jApiField = new JApiField(JApiChangeStatus.REMOVED, Optional.of(oldField), Optional.<CtField>absent());
					fields.add(jApiField);
				}
			}
			for (CtField newField : newFieldsMap.values()) {
				CtField oldField = oldFieldsMap.get(newField.getName());
				if (oldField == null) {
					JApiField jApiField = new JApiField(JApiChangeStatus.NEW, Optional.<CtField>absent(), Optional.of(newField));
					fields.add(jApiField);
				}
			}
		} else {
			if (oldClassOptional.isPresent()) {
				Map<String, CtField> fieldMap = buildFieldMap(oldClassOptional.get());
				for (CtField field : fieldMap.values()) {
					JApiField jApiField = new JApiField(JApiChangeStatus.REMOVED, Optional.of(field), Optional.<CtField>absent());
					fields.add(jApiField);
				}
			}
			if (newClassOptional.isPresent()) {
				Map<String, CtField> fieldMap = buildFieldMap(newClassOptional.get());
				for (CtField field : fieldMap.values()) {
					JApiField jApiField = new JApiField(JApiChangeStatus.NEW, Optional.<CtField>absent(), Optional.of(field));
					fields.add(jApiField);
				}
			}
		}
	}

	private Map<String, CtField> buildFieldMap(CtClass ctClass) {
		Map<String, CtField> fieldMap = new HashMap<>();
		CtField[] declaredFields = ctClass.getDeclaredFields();
		for (CtField field : declaredFields) {
			String name = field.getName();
			fieldMap.put(name, field);
		}
		return fieldMap;
	}

	private JApiSuperclass extractSuperclass(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		JApiSuperclass retVal = null;
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			Optional<CtClass> superclassOldOptional = getSuperclass(oldClass);
			Optional<CtClass> superclassNewOptional = getSuperclass(newClass);
			if (superclassOldOptional.isPresent() && superclassNewOptional.isPresent()) {
				String nameOld = superclassOldOptional.get().getName();
				String nameNew = superclassNewOptional.get().getName();
				retVal = new JApiSuperclass(Optional.of(nameOld), Optional.of(nameNew), nameOld.equals(nameNew) ? JApiChangeStatus.UNCHANGED : JApiChangeStatus.MODIFIED);
			} else if (superclassOldOptional.isPresent() && !superclassNewOptional.isPresent()) {
				retVal = new JApiSuperclass(Optional.of(superclassOldOptional.get().getName()), Optional.<String>absent(), JApiChangeStatus.REMOVED);
			} else if (!superclassOldOptional.isPresent() && superclassNewOptional.isPresent()) {
				retVal = new JApiSuperclass(Optional.<String>absent(), Optional.of(superclassNewOptional.get().getName()), JApiChangeStatus.NEW);
			} else {
				retVal = new JApiSuperclass(Optional.<String>absent(), Optional.<String>absent(), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				Optional<CtClass> superclassOldOptional = getSuperclass(oldClassOptional.get());
				if (superclassOldOptional.isPresent()) {
					JApiSuperclass superclass = new JApiSuperclass(Optional.of(superclassOldOptional.get().getName()), Optional.<String>absent(), JApiChangeStatus.REMOVED);
					retVal = superclass;
				} else {
					retVal = new JApiSuperclass(Optional.<String>absent(), Optional.<String>absent(), JApiChangeStatus.UNCHANGED);
				}
			} else if (newClassOptional.isPresent()) {
				Optional<CtClass> superclassNewOptional = getSuperclass(newClassOptional.get());
				if (superclassNewOptional.isPresent()) {
					JApiSuperclass superclass = new JApiSuperclass(Optional.<String>absent(), Optional.of(superclassNewOptional.get().getName()), JApiChangeStatus.NEW);
					retVal = superclass;
				} else {
					retVal = new JApiSuperclass(Optional.<String>absent(), Optional.<String>absent(), JApiChangeStatus.UNCHANGED);
				}
			}
		}
		if (retVal == null) {
			retVal = new JApiSuperclass(Optional.<String>absent(), Optional.<String>absent(), JApiChangeStatus.UNCHANGED);
		}
		return retVal;
	}

	private Optional<CtClass> getSuperclass(CtClass ctClass) {
		try {
			CtClass superClass = ctClass.getSuperclass();
			return Optional.of(superClass);
		} catch (NotFoundException e) {
			throw new JApiCmpException(JApiCmpException.Reason.ClassLoading, "Could not load superclass for class '" + ctClass.getName() + "': " + e.getMessage() + ". Please make sure that all libraries have been added to the classpath (CLASSPATH=" + constructClasspath() + ").", e);
		}
	}

	private String constructClasspath() {
		return this.jarArchiveComparator.getClasspath();
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
			throw new JApiCmpException(JApiCmpException.Reason.ClassLoading, "Could not load interfaces for class '" + ctClass.getName() + "': " + e.getMessage() + ". Please make sure that all libraries have been added to the classpath (CLASSPATH=" + constructClasspath() + ").", e);
		}
		return map;
	}

	private void computeMethodChanges(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		Map<String, CtMethod> oldMethodsMap = createMethodMap(oldClassOptional);
		Map<String, CtMethod> newMethodsMap = createMethodMap(newClassOptional);
		sortMethodsIntoLists(oldMethodsMap, newMethodsMap);
		Map<String, CtConstructor> oldConstructorsMap = createConstructorMap(oldClassOptional);
		Map<String, CtConstructor> newConstructorsMap = createConstructorMap(newClassOptional);
		sortConstructorsIntoLists(oldConstructorsMap, newConstructorsMap);
	}

	private void sortMethodsIntoLists(Map<String, CtMethod> oldMethodsMap, Map<String, CtMethod> newMethodsMap) {
		SignatureParser signatureParser = new SignatureParser();
		for (CtMethod ctMethod : oldMethodsMap.values()) {
			signatureParser.parse(ctMethod.getSignature());
			CtMethod foundMethod = newMethodsMap.get(toMethodKey(ctMethod, signatureParser));
			if (foundMethod == null) {
				JApiMethod jApiMethod = new JApiMethod(ctMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(ctMethod), Optional.<CtMethod>absent(),
						signatureParser.getReturnType());
				addParametersToMethod(signatureParser, jApiMethod);
				methods.add(jApiMethod);
			} else {
				JApiMethod jApiMethod = new JApiMethod(ctMethod.getName(), JApiChangeStatus.UNCHANGED, Optional.of(ctMethod), Optional.of(foundMethod),
						signatureParser.getReturnType());
				addParametersToMethod(signatureParser, jApiMethod);
				methods.add(jApiMethod);
			}
		}
		for (CtMethod ctMethod : newMethodsMap.values()) {
			signatureParser.parse(ctMethod.getSignature());
			CtMethod foundMethod = oldMethodsMap.get(toMethodKey(ctMethod, signatureParser));
			if (foundMethod == null) {
				JApiMethod jApiMethod = new JApiMethod(ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtMethod>absent(), Optional.of(ctMethod),
						signatureParser.getReturnType());
				addParametersToMethod(signatureParser, jApiMethod);
				methods.add(jApiMethod);
			}
		}
	}

	private void sortConstructorsIntoLists(Map<String, CtConstructor> oldConstructorsMap, Map<String, CtConstructor> newConstructorsMap) {
		SignatureParser signatureParser = new SignatureParser();
		for (CtConstructor ctMethod : oldConstructorsMap.values()) {
			String longName = ctMethod.getLongName();
			signatureParser.parse(ctMethod.getSignature());
			CtConstructor foundMethod = newConstructorsMap.get(longName);
			if (foundMethod == null) {
				JApiConstructor jApiConstructor = new JApiConstructor(ctMethod.getName(), JApiChangeStatus.REMOVED, Optional.of(ctMethod), Optional.<CtConstructor>absent());
				addParametersToMethod(signatureParser, jApiConstructor);
				constructors.add(jApiConstructor);
			} else {
				JApiConstructor jApiConstructor = new JApiConstructor(ctMethod.getName(), JApiChangeStatus.UNCHANGED, Optional.of(ctMethod), Optional.of(foundMethod));
				addParametersToMethod(signatureParser, jApiConstructor);
				constructors.add(jApiConstructor);
			}
		}
		for (CtConstructor ctMethod : newConstructorsMap.values()) {
			String longName = ctMethod.getLongName();
			signatureParser.parse(ctMethod.getSignature());
			CtConstructor foundMethod = oldConstructorsMap.get(longName);
			if (foundMethod == null) {
				JApiConstructor jApiConstructor = new JApiConstructor(ctMethod.getName(), JApiChangeStatus.NEW, Optional.<CtConstructor>absent(), Optional.of(ctMethod));
				addParametersToMethod(signatureParser, jApiConstructor);
				constructors.add(jApiConstructor);
			}
		}
	}

	private void addParametersToMethod(SignatureParser signatureParser, JApiBehavior jApiMethod) {
		for (String param : signatureParser.getParameters()) {
			jApiMethod.addParameter(new JApiParameter(param));
		}
	}

	private Map<String, CtMethod> createMethodMap(Optional<CtClass> ctClassOptional) {
		Map<String, CtMethod> methods = new HashMap<String, CtMethod>();
		if (ctClassOptional.isPresent()) {
			CtClass ctClass = ctClassOptional.get();
			for (CtMethod ctMethod : ctClass.getDeclaredMethods()) {
				SignatureParser signatureParser = new SignatureParser();
				signatureParser.parse(ctMethod.getSignature());
				methods.put(toMethodKey(ctMethod, signatureParser), ctMethod);
			}
		}
		return methods;
	}

	private String toMethodKey(CtMethod ctMethod, SignatureParser signatureParser) {
		return ctMethod.getLongName() + "#" + signatureParser.getReturnType();
	}

	private Map<String, CtConstructor> createConstructorMap(Optional<CtClass> ctClass) {
		Map<String, CtConstructor> methods = new HashMap<>();
		if (ctClass.isPresent()) {
			for (CtConstructor ctConstructor : ctClass.get().getDeclaredConstructors()) {
				methods.put(ctConstructor.getLongName(), ctConstructor);
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
				}
			}
			for (JApiMethod method : methods) {
				if (method.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					changeStatus = JApiChangeStatus.MODIFIED;
				}
			}
			for (JApiConstructor constructor : constructors) {
				if (constructor.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					changeStatus = JApiChangeStatus.MODIFIED;
				}
			}
			for (JApiAnnotation annotation : annotations) {
				if (annotation.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					changeStatus = JApiChangeStatus.MODIFIED;
				}
			}
		}
		return changeStatus;
	}

	private JApiModifier<StaticModifier> extractStaticModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			StaticModifier oldClassFinalModifier = Modifier.isStatic(oldClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			StaticModifier newClassFinalModifier = Modifier.isStatic(newClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			if (oldClassFinalModifier != newClassFinalModifier) {
				return new JApiModifier<StaticModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<StaticModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				StaticModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
				return new JApiModifier<StaticModifier>(Optional.of(finalModifier), Optional.<StaticModifier>absent(), JApiChangeStatus.REMOVED);
			}
			if (newClassOptional.isPresent()) {
				CtClass ctClass = newClassOptional.get();
				StaticModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
				return new JApiModifier<StaticModifier>(Optional.<StaticModifier>absent(), Optional.of(finalModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<StaticModifier>(Optional.<StaticModifier>absent(), Optional.<StaticModifier>absent(), JApiChangeStatus.UNCHANGED);
	}

	private JApiModifier<FinalModifier> extractFinalModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			FinalModifier oldClassFinalModifier = Modifier.isFinal(oldClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			FinalModifier newClassFinalModifier = Modifier.isFinal(newClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			if (oldClassFinalModifier != newClassFinalModifier) {
				return new JApiModifier<FinalModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<FinalModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				FinalModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
				return new JApiModifier<FinalModifier>(Optional.of(finalModifier), Optional.<FinalModifier>absent(), JApiChangeStatus.REMOVED);
			}
			if (newClassOptional.isPresent()) {
				CtClass ctClass = newClassOptional.get();
				FinalModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
				return new JApiModifier<FinalModifier>(Optional.<FinalModifier>absent(), Optional.of(finalModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<FinalModifier>(Optional.<FinalModifier>absent(), Optional.<FinalModifier>absent(), JApiChangeStatus.UNCHANGED);
	}

	private JApiModifier<AccessModifier> extractAccessModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			AccessModifier oldClassAccessModifier = ModifierHelper.translateToModifierLevel(oldClass.getModifiers());
			AccessModifier newClassAccessModifier = ModifierHelper.translateToModifierLevel(newClass.getModifiers());
			if (oldClassAccessModifier != newClassAccessModifier) {
				return new JApiModifier<AccessModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<AccessModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctClass.getModifiers());
				return new JApiModifier<AccessModifier>(Optional.of(accessModifier), Optional.<AccessModifier>absent(), JApiChangeStatus.REMOVED);
			}
			if (newClassOptional.isPresent()) {
				CtClass ctClass = newClassOptional.get();
				AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctClass.getModifiers());
				return new JApiModifier<AccessModifier>(Optional.<AccessModifier>absent(), Optional.of(accessModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<AccessModifier>(Optional.<AccessModifier>absent(), Optional.<AccessModifier>absent(), JApiChangeStatus.UNCHANGED);
	}

	private JApiModifier<AbstractModifier> extractAbstractModifier(Optional<CtClass> oldClassOptional, Optional<CtClass> newClassOptional) {
		if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtClass oldClass = oldClassOptional.get();
			CtClass newClass = newClassOptional.get();
			AbstractModifier oldClassAccessModifier = Modifier.isAbstract(oldClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			AbstractModifier newClassAccessModifier = Modifier.isAbstract(newClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			if (oldClassAccessModifier != newClassAccessModifier) {
				return new JApiModifier<AbstractModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<AbstractModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if (oldClassOptional.isPresent()) {
				CtClass ctClass = oldClassOptional.get();
				AbstractModifier abstractModifier = Modifier.isAbstract(ctClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
				return new JApiModifier<AbstractModifier>(Optional.of(abstractModifier), Optional.<AbstractModifier>absent(), JApiChangeStatus.REMOVED);
			}
			if (newClassOptional.isPresent()) {
				CtClass ctClass = newClassOptional.get();
				AbstractModifier abstractModifier = Modifier.isAbstract(ctClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
				return new JApiModifier<AbstractModifier>(Optional.<AbstractModifier>absent(), Optional.of(abstractModifier), JApiChangeStatus.NEW);
			}
		}
		return new JApiModifier<AbstractModifier>(Optional.<AbstractModifier>absent(), Optional.<AbstractModifier>absent(), JApiChangeStatus.UNCHANGED);
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
	public List<JApiModifier<? extends Enum<?>>> getModifiers() {
		return Arrays.asList(this.finalModifier, this.staticModifier, this.accessModifier, this.abstractModifier);
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

	@XmlElementWrapper(name = "constructors", nillable = false)
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

	@XmlAttribute
	public Type getType() {
		return type;
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
}
