package japicmp.model;

import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.util.*;
import javassist.CtField;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class JApiField implements JApiHasChangeStatus, JApiHasModifiers, JApiHasAccessModifier, JApiHasStaticModifier,
	JApiHasFinalModifier, JApiHasTransientModifier, JApiHasVolatileModifier, JApiCompatibility, JApiHasAnnotations, JApiCanBeSynthetic,
	JApiHasGenericTypes {
	private final JApiChangeStatus changeStatus;
	private final JApiClass jApiClass;
	private final Optional<CtField> oldFieldOptional;
	private final Optional<CtField> newFieldOptional;
	private final List<JApiAnnotation> annotations = new LinkedList<>();
	private final JApiModifier<AccessModifier> accessModifier;
	private final JApiModifier<StaticModifier> staticModifier;
	private final JApiModifier<FinalModifier> finalModifier;
	private final JApiModifier<TransientModifier> transientModifier;
	private final JApiModifier<VolatileModifier> volatileModifier;
	private final JApiModifier<SyntheticModifier> syntheticModifier;
	private final JApiAttribute<SyntheticAttribute> syntheticAttribute;
	private final List<JApiCompatibilityChange> compatibilityChanges = new ArrayList<>();
	private final List<JApiGenericType> oldGenericTypes = new ArrayList<>();
	private final List<JApiGenericType> newGenericTypes = new ArrayList<>();
	private final JApiType type;

	public JApiField(JApiClass jApiClass, JApiChangeStatus changeStatus, Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional, JarArchiveComparatorOptions options) {
		this.jApiClass = jApiClass;
		this.oldFieldOptional = oldFieldOptional;
		this.newFieldOptional = newFieldOptional;
		computeAnnotationChanges(this.annotations, oldFieldOptional, newFieldOptional, options);
		this.accessModifier = extractAccessModifier(oldFieldOptional, newFieldOptional);
		this.staticModifier = extractStaticModifier(oldFieldOptional, newFieldOptional);
		this.finalModifier = extractFinalModifier(oldFieldOptional, newFieldOptional);
		this.transientModifier = extractTransientModifier(oldFieldOptional, newFieldOptional);
		this.volatileModifier = extractVolatileModifier(oldFieldOptional, newFieldOptional);
		this.syntheticModifier = extractSyntheticModifier(oldFieldOptional, newFieldOptional);
		this.syntheticAttribute = extractSyntheticAttribute(oldFieldOptional, newFieldOptional);
		this.type = extractType(oldFieldOptional, newFieldOptional);
		this.changeStatus = evaluateChangeStatus(changeStatus);
	}

	private void computeAnnotationChanges(List<JApiAnnotation> annotations, Optional<CtField> oldBehavior, Optional<CtField> newBehavior, JarArchiveComparatorOptions options) {
		AnnotationHelper.computeAnnotationChanges(annotations, oldBehavior, newBehavior, options, new AnnotationHelper.AnnotationsAttributeCallback<CtField>() {
			@Override
			public AnnotationsAttribute getAnnotationsAttribute(CtField field) {
				return (AnnotationsAttribute) field.getFieldInfo().getAttribute(AnnotationsAttribute.visibleTag);
			}
		});
	}

	private JApiType extractType(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		JApiType jApiType = new JApiType(Optional.<String>empty(), Optional.<String>empty(), JApiChangeStatus.UNCHANGED);
		if (oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
			CtField oldField = oldFieldOptional.get();
			CtField newField = newFieldOptional.get();
			String oldType = signatureToType(oldField.getSignature());
			String newType = signatureToType(newField.getSignature());
			if (oldType.equals(newType)) {
				jApiType = new JApiType(Optional.of(oldType), Optional.of(newType), JApiChangeStatus.UNCHANGED);
			} else {
				jApiType = new JApiType(Optional.of(oldType), Optional.of(newType), JApiChangeStatus.MODIFIED);
			}
		} else {
			if (oldFieldOptional.isPresent()) {
				CtField oldField = oldFieldOptional.get();
				String oldType = signatureToType(oldField.getSignature());
				jApiType = new JApiType(Optional.of(oldType), Optional.<String>empty(), JApiChangeStatus.REMOVED);
			} else if (newFieldOptional.isPresent()) {
				CtField newField = newFieldOptional.get();
				String newType = signatureToType(newField.getSignature());
				jApiType = new JApiType(Optional.<String>empty(), Optional.of(newType), JApiChangeStatus.NEW);
			}
		}
		return jApiType;
	}

	private String signatureToType(String signature) {
		SignatureParser methodDescriptorParser = new SignatureParser();
		List<SignatureParser.ParsedParameter> types = methodDescriptorParser.parseTypes(signature);
		if (types.size() > 0) {
			return types.get(0).getType();
		}
		return "n.a.";
	}

	private JApiChangeStatus evaluateChangeStatus(JApiChangeStatus changeStatus) {
		if (changeStatus == JApiChangeStatus.UNCHANGED) {
			if (this.accessModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.staticModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.finalModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.transientModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.volatileModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.syntheticAttribute.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.type.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
		}
		return changeStatus;
	}

	private JApiAttribute<SyntheticAttribute> extractSyntheticAttribute(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		JApiAttribute<SyntheticAttribute> jApiAttribute = new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
		if (oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
			CtField oldField = oldFieldOptional.get();
			CtField newField = newFieldOptional.get();
			byte[] attributeOldField = oldField.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
			byte[] attributeNewField = newField.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
			if (attributeOldField != null && attributeNewField != null) {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
			} else if (attributeOldField != null) {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
			} else if (attributeNewField != null) {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
			} else {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
			}
		} else {
			if (oldFieldOptional.isPresent()) {
				CtField ctField = oldFieldOptional.get();
				byte[] attribute = ctField.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
				if (attribute != null) {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.<SyntheticAttribute>empty());
				} else {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.<SyntheticAttribute>empty());
				}
			}
			if (newFieldOptional.isPresent()) {
				CtField ctField = newFieldOptional.get();
				byte[] attribute = ctField.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
				if (attribute != null) {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>empty(), Optional.of(SyntheticAttribute.SYNTHETIC));
				} else {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>empty(), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
				}
			}
		}
		return jApiAttribute;
	}

	private JApiModifier<StaticModifier> extractStaticModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		return ModifierHelper.extractModifierFromField(oldFieldOptional, newFieldOptional, new ModifierHelper.ExtractModifierFromFieldCallback<StaticModifier>() {
			@Override
			public StaticModifier getModifierForOld(CtField oldField) {
				return Modifier.isStatic(oldField.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			}

			@Override
			public StaticModifier getModifierForNew(CtField newField) {
				return Modifier.isStatic(newField.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			}
		});
	}

	private JApiModifier<FinalModifier> extractFinalModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		return ModifierHelper.extractModifierFromField(oldFieldOptional, newFieldOptional, new ModifierHelper.ExtractModifierFromFieldCallback<FinalModifier>() {
			@Override
			public FinalModifier getModifierForOld(CtField oldField) {
				return Modifier.isFinal(oldField.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			}

			@Override
			public FinalModifier getModifierForNew(CtField newField) {
				return Modifier.isFinal(newField.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			}
		});
	}

	private JApiModifier<AccessModifier> extractAccessModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		return ModifierHelper.extractModifierFromField(oldFieldOptional, newFieldOptional, new ModifierHelper.ExtractModifierFromFieldCallback<AccessModifier>() {
			@Override
			public AccessModifier getModifierForOld(CtField oldField) {
				return ModifierHelper.translateToModifierLevel(oldField.getModifiers());
			}

			@Override
			public AccessModifier getModifierForNew(CtField newField) {
				return ModifierHelper.translateToModifierLevel(newField.getModifiers());
			}
		});
	}

	private JApiModifier<AbstractModifier> extractAbstractModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		return ModifierHelper.extractModifierFromField(oldFieldOptional, newFieldOptional, new ModifierHelper.ExtractModifierFromFieldCallback<AbstractModifier>() {
			@Override
			public AbstractModifier getModifierForOld(CtField oldField) {
				return Modifier.isAbstract(oldField.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			}

			@Override
			public AbstractModifier getModifierForNew(CtField newField) {
				return Modifier.isAbstract(newField.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			}
		});
	}

	private JApiModifier<TransientModifier> extractTransientModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		return ModifierHelper.extractModifierFromField(oldFieldOptional, newFieldOptional, new ModifierHelper.ExtractModifierFromFieldCallback<TransientModifier>() {
			@Override
			public TransientModifier getModifierForOld(CtField oldField) {
				return Modifier.isTransient(oldField.getModifiers()) ? TransientModifier.TRANSIENT : TransientModifier.NON_TRANSIENT;
			}

			@Override
			public TransientModifier getModifierForNew(CtField newField) {
				return Modifier.isTransient(newField.getModifiers()) ? TransientModifier.TRANSIENT : TransientModifier.NON_TRANSIENT;
			}
		});
	}

	private JApiModifier<VolatileModifier> extractVolatileModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		return ModifierHelper.extractModifierFromField(oldFieldOptional, newFieldOptional, new ModifierHelper.ExtractModifierFromFieldCallback<VolatileModifier>() {
			@Override
			public VolatileModifier getModifierForOld(CtField oldField) {
				return Modifier.isVolatile(oldField.getModifiers()) ? VolatileModifier.VOLATILE : VolatileModifier.NON_VOLATILE;
			}

			@Override
			public VolatileModifier getModifierForNew(CtField newField) {
				return Modifier.isVolatile(newField.getModifiers()) ? VolatileModifier.VOLATILE : VolatileModifier.NON_VOLATILE;
			}
		});
	}

	private JApiModifier<SyntheticModifier> extractSyntheticModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		return ModifierHelper.extractModifierFromField(oldFieldOptional, newFieldOptional, new ModifierHelper.ExtractModifierFromFieldCallback<SyntheticModifier>() {
			@Override
			public SyntheticModifier getModifierForOld(CtField oldField) {
				return ModifierHelper.isSynthetic(oldField.getModifiers()) ? SyntheticModifier.SYNTHETIC : SyntheticModifier.NON_SYNTHETIC;
			}

			@Override
			public SyntheticModifier getModifierForNew(CtField newField) {
				return ModifierHelper.isSynthetic(newField.getModifiers()) ? SyntheticModifier.SYNTHETIC : SyntheticModifier.NON_SYNTHETIC;
			}
		});
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute(name = "name")
	public String getName() {
		return oldFieldOptional.map(CtField::getName)
			.orElseGet(() -> newFieldOptional.map(CtField::getName).orElse("n.a."));
	}

	@XmlTransient
	public Optional<CtField> getOldFieldOptional() {
		return oldFieldOptional;
	}

	@XmlTransient
	public Optional<CtField> getNewFieldOptional() {
		return newFieldOptional;
	}

	@XmlElementWrapper(name = "modifiers")
	@XmlElement(name = "modifier")
	public List<? extends JApiModifier<? extends Enum<? extends Enum<?>>>> getModifiers() {
		return Arrays.asList(this.accessModifier, this.staticModifier, this.finalModifier, this.transientModifier, this.volatileModifier, this.syntheticModifier);
	}

	@XmlTransient
	public JApiModifier<StaticModifier> getStaticModifier() {
		return staticModifier;
	}

	@XmlTransient
	public JApiModifier<FinalModifier> getFinalModifier() {
		return finalModifier;
	}

	@XmlTransient
	public JApiModifier<TransientModifier> getTransientModifier() {
		return transientModifier;
	}

	@XmlTransient
	public JApiModifier<VolatileModifier> getVolatileModifier() {
		return volatileModifier;
	}

	@XmlTransient
	public JApiModifier<AccessModifier> getAccessModifier() {
		return accessModifier;
	}

	@XmlElementWrapper(name = "attributes")
	@XmlElement(name = "attribute")
	public List<JApiAttribute<? extends Enum<?>>> getAttributes() {
		List<JApiAttribute<? extends Enum<?>>> list = new ArrayList<>();
		list.add(this.syntheticAttribute);
		return list;
	}

	@XmlTransient
	public JApiModifier<SyntheticModifier> getSyntheticModifier() {
		return this.syntheticModifier;
	}

	@XmlTransient
	public JApiAttribute<SyntheticAttribute> getSyntheticAttribute() {
		return syntheticAttribute;
	}

	@XmlElement(name = "type")
	public JApiType getType() {
		return type;
	}

	@Override
	@XmlAttribute
	public boolean isBinaryCompatible() {
		boolean binaryCompatible = true;
		for (JApiCompatibilityChange compatibilityChange : compatibilityChanges) {
			if (!compatibilityChange.isBinaryCompatible()) {
				binaryCompatible = false;
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
			}
		}
		return sourceCompatible;
	}

	@XmlElementWrapper(name = "compatibilityChanges")
	@XmlElement(name = "compatibilityChange")
	public List<JApiCompatibilityChange> getCompatibilityChanges() {
		return compatibilityChanges;
	}

	@XmlElementWrapper(name = "annotations")
	@XmlElement(name = "annotation")
	public List<JApiAnnotation> getAnnotations() {
		return annotations;
	}

	@XmlTransient
	public JApiClass getjApiClass() {
		return jApiClass;
	}

	public String toString()
	{
		return "JApiField [changeStatus="
			+ changeStatus
			+ ", jApiClass="
			+ jApiClass
			+ ", oldFieldOptional="
			+ oldFieldOptional
			+ ", newFieldOptional="
			+ newFieldOptional
			+ ", annotations="
			+ annotations
			+ ", accessModifier="
			+ accessModifier
			+ ", staticModifier="
			+ staticModifier
			+ ", finalModifier="
			+ finalModifier
			+ ", transientModifier="
			+ transientModifier
			+ ", volatileModifier="
			+ volatileModifier
			+ ", syntheticModifier="
			+ syntheticModifier
			+ ", syntheticAttribute="
			+ syntheticAttribute
			+ ", compatibilityChanges="
			+ compatibilityChanges
			+ ", type="
			+ type
			+ "]";
	}

	@XmlElementWrapper(name = "oldGenericTypes")
	@XmlElement(name = "oldGenericType")
	public List<JApiGenericType> getOldGenericTypes() {
		return oldGenericTypes;
	}

	@XmlElementWrapper(name = "newGenericTypes")
	@XmlElement(name = "newGenericType")
	public List<JApiGenericType> getNewGenericTypes() {
		return newGenericTypes;
	}
}
