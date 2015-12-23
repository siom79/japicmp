package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.util.AnnotationHelper;
import japicmp.util.Constants;
import japicmp.util.ModifierHelper;
import japicmp.util.OptionalHelper;
import javassist.CtBehavior;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.bytecode.AnnotationsAttribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class JApiBehavior implements JApiHasModifiers, JApiHasChangeStatus, JApiHasAccessModifier, JApiHasStaticModifier, JApiHasFinalModifier, JApiHasAbstractModifier, JApiBinaryCompatibility, JApiHasAnnotations, JApiHasBridgeModifier, JApiCanBeSynthetic, JApiHasLineNumber {
	private final String name;
	private final List<JApiParameter> parameters = new LinkedList<>();
	private final List<JApiAnnotation> annotations = new LinkedList<>();
	private final JApiModifier<AccessModifier> accessModifier;
	private final JApiModifier<FinalModifier> finalModifier;
	private final JApiModifier<StaticModifier> staticModifier;
	private final JApiModifier<AbstractModifier> abstractModifier;
	private final JApiModifier<BridgeModifier> bridgeModifier;
	private final JApiModifier<SyntheticModifier> syntheticModifier;
	private final JApiAttribute<SyntheticAttribute> syntheticAttribute;
	protected JApiChangeStatus changeStatus;
	private boolean binaryCompatible = true;
	private final Optional<Integer> oldLineNumber;
	private final Optional<Integer> newLineNumber;

	public JApiBehavior(String name, Optional<? extends CtBehavior> oldBehavior, Optional<? extends CtBehavior> newBehavior, JApiChangeStatus changeStatus, JarArchiveComparatorOptions options) {
		this.name = name;
		computeAnnotationChanges(annotations, oldBehavior, newBehavior, options);
		this.accessModifier = extractAccessModifier(oldBehavior, newBehavior);
		this.finalModifier = extractFinalModifier(oldBehavior, newBehavior);
		this.staticModifier = extractStaticModifier(oldBehavior, newBehavior);
		this.abstractModifier = extractAbstractModifier(oldBehavior, newBehavior);
		this.bridgeModifier = extractBridgeModifier(oldBehavior, newBehavior);
		this.syntheticModifier = extractSyntheticModifier(oldBehavior, newBehavior);
		this.syntheticAttribute = extractSyntheticAttribute(oldBehavior, newBehavior);
		this.changeStatus = evaluateChangeStatus(changeStatus);
		this.oldLineNumber = getLineNumber(oldBehavior);
		this.newLineNumber = getLineNumber(newBehavior);
	}

	private Optional<Integer> getLineNumber(Optional<? extends CtBehavior> methodOptional) {
		Optional<Integer> lineNumberOptional = Optional.absent();
		if (methodOptional.isPresent()) {
			CtBehavior ctMethod = methodOptional.get();
			int lineNumber = ctMethod.getMethodInfo().getLineNumber(0);
			if (lineNumber >= 0) {
				lineNumberOptional = Optional.of(lineNumber);
			}
		}
		return lineNumberOptional;
	}

	@SuppressWarnings("unchecked")
	private void computeAnnotationChanges(List<JApiAnnotation> annotations, Optional<? extends CtBehavior> oldBehavior, Optional<? extends CtBehavior> newBehavior, JarArchiveComparatorOptions options) {
		if (oldBehavior.isPresent()) {
			CtBehavior ctBehavior = oldBehavior.get();
			if (ctBehavior instanceof CtMethod) {
				computeAnnotationChangesMethod(annotations, (Optional<CtMethod>) oldBehavior, (Optional<CtMethod>) newBehavior, options);
			} else if (ctBehavior instanceof CtConstructor) {
				computeAnnotationChangesConstructor(annotations, (Optional<CtConstructor>) oldBehavior, (Optional<CtConstructor>) newBehavior, options);
			}
		} else if (newBehavior.isPresent()) {
			CtBehavior ctBehavior = newBehavior.get();
			if (ctBehavior instanceof CtMethod) {
				computeAnnotationChangesMethod(annotations, (Optional<CtMethod>) oldBehavior, (Optional<CtMethod>) newBehavior, options);
			} else if (ctBehavior instanceof CtConstructor) {
				computeAnnotationChangesConstructor(annotations, (Optional<CtConstructor>) oldBehavior, (Optional<CtConstructor>) newBehavior, options);
			}
		}
	}

	private void computeAnnotationChangesMethod(List<JApiAnnotation> annotations, Optional<CtMethod> oldBehavior, Optional<CtMethod> newBehavior, JarArchiveComparatorOptions options) {
		AnnotationHelper.computeAnnotationChanges(annotations, oldBehavior, newBehavior, options, new AnnotationHelper.AnnotationsAttributeCallback<CtMethod>() {
			@Override
			public AnnotationsAttribute getAnnotationsAttribute(CtMethod method) {
				return (AnnotationsAttribute) method.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
			}
		});
	}

	private void computeAnnotationChangesConstructor(List<JApiAnnotation> annotations, Optional<CtConstructor> oldBehavior, Optional<CtConstructor> newBehavior, JarArchiveComparatorOptions options) {
		AnnotationHelper.computeAnnotationChanges(annotations, oldBehavior, newBehavior, options, new AnnotationHelper.AnnotationsAttributeCallback<CtConstructor>() {
			@Override
			public AnnotationsAttribute getAnnotationsAttribute(CtConstructor method) {
				return (AnnotationsAttribute) method.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
			}
		});
	}

	private JApiChangeStatus evaluateChangeStatus(JApiChangeStatus changeStatus) {
		if (changeStatus == JApiChangeStatus.UNCHANGED) {
			if (this.staticModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.finalModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.accessModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.abstractModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if (this.syntheticAttribute.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
		}
		return changeStatus;
	}

	protected JApiAttribute<SyntheticAttribute> extractSyntheticAttribute(Optional<? extends CtBehavior> oldBehaviorOptional, Optional<? extends CtBehavior> newBehaviorOptional) {
		JApiAttribute<SyntheticAttribute> jApiAttribute = new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
		if (oldBehaviorOptional.isPresent() && newBehaviorOptional.isPresent()) {
			CtBehavior oldBehavior = oldBehaviorOptional.get();
			CtBehavior newBehavior = newBehaviorOptional.get();
			byte[] attributeOldBehavior = oldBehavior.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
			byte[] attributeNewBehavior = newBehavior.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
			if (attributeOldBehavior != null && attributeNewBehavior != null) {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
			} else if (attributeOldBehavior != null) {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
			} else if (attributeNewBehavior != null) {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
			} else {
				jApiAttribute = new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
			}
		} else {
			if (oldBehaviorOptional.isPresent()) {
				CtBehavior ctBehavior = oldBehaviorOptional.get();
				byte[] attribute = ctBehavior.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
				if (attribute != null) {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.<SyntheticAttribute>absent());
				} else {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.<SyntheticAttribute>absent());
				}
			}
			if (newBehaviorOptional.isPresent()) {
				CtBehavior ctBehavior = newBehaviorOptional.get();
				byte[] attribute = ctBehavior.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
				if (attribute != null) {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>absent(), Optional.of(SyntheticAttribute.SYNTHETIC));
				} else {
					jApiAttribute = new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>absent(), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
				}
			}
		}
		return jApiAttribute;
	}

	public boolean hasSameParameter(JApiMethod method) {
		boolean haveSameParameter = true;
		List<JApiParameter> parameters1 = getParameters();
		List<JApiParameter> parameters2 = method.getParameters();
		if (parameters1.size() != parameters2.size()) {
			haveSameParameter = false;
		}
		if (haveSameParameter) {
			for (int i = 0; i < parameters1.size(); i++) {
				if (!parameters1.get(i).getType().equals(parameters2.get(i).getType())) {
					haveSameParameter = false;
				}
			}
		}
		return haveSameParameter;
	}

	private JApiModifier<StaticModifier> extractStaticModifier(Optional<? extends CtBehavior> oldBehaviorOptional, Optional<? extends CtBehavior> newBehaviorOptional) {
		return ModifierHelper.extractModifierFromBehavior(oldBehaviorOptional, newBehaviorOptional, new ModifierHelper.ExtractModifierFromBehaviorCallback<StaticModifier>() {
			@Override
			public StaticModifier getModifierForOld(CtBehavior oldBehavior) {
				return Modifier.isStatic(oldBehavior.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			}

			@Override
			public StaticModifier getModifierForNew(CtBehavior newBehavior) {
				return Modifier.isStatic(newBehavior.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			}
		});
	}

	private JApiModifier<FinalModifier> extractFinalModifier(Optional<? extends CtBehavior> oldBehaviorOptional, Optional<? extends CtBehavior> newBehaviorOptional) {
		return ModifierHelper.extractModifierFromBehavior(oldBehaviorOptional, newBehaviorOptional, new ModifierHelper.ExtractModifierFromBehaviorCallback<FinalModifier>() {
			@Override
			public FinalModifier getModifierForOld(CtBehavior oldBehavior) {
				return Modifier.isFinal(oldBehavior.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			}

			@Override
			public FinalModifier getModifierForNew(CtBehavior newBehavior) {
				return Modifier.isFinal(newBehavior.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			}
		});
	}

	private JApiModifier<AccessModifier> extractAccessModifier(Optional<? extends CtBehavior> oldBehaviorOptional, Optional<? extends CtBehavior> newBehaviorOptional) {
		return ModifierHelper.extractModifierFromBehavior(oldBehaviorOptional, newBehaviorOptional, new ModifierHelper.ExtractModifierFromBehaviorCallback<AccessModifier>() {
			@Override
			public AccessModifier getModifierForOld(CtBehavior oldBehavior) {
				return ModifierHelper.translateToModifierLevel(oldBehavior.getModifiers());
			}

			@Override
			public AccessModifier getModifierForNew(CtBehavior newBehavior) {
				return ModifierHelper.translateToModifierLevel(newBehavior.getModifiers());
			}
		});
	}

	private JApiModifier<AbstractModifier> extractAbstractModifier(Optional<? extends CtBehavior> oldBehaviorOptional, Optional<? extends CtBehavior> newBehaviorOptional) {
		return ModifierHelper.extractModifierFromBehavior(oldBehaviorOptional, newBehaviorOptional, new ModifierHelper.ExtractModifierFromBehaviorCallback<AbstractModifier>() {
			@Override
			public AbstractModifier getModifierForOld(CtBehavior oldBehavior) {
				return Modifier.isAbstract(oldBehavior.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			}

			@Override
			public AbstractModifier getModifierForNew(CtBehavior newBehavior) {
				return Modifier.isAbstract(newBehavior.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			}
		});
	}

	private JApiModifier<BridgeModifier> extractBridgeModifier(Optional<? extends CtBehavior> oldBehaviorOptional, Optional<? extends CtBehavior> newBehaviorOptional) {
		return ModifierHelper.extractModifierFromBehavior(oldBehaviorOptional, newBehaviorOptional, new ModifierHelper.ExtractModifierFromBehaviorCallback<BridgeModifier>() {
			@Override
			public BridgeModifier getModifierForOld(CtBehavior oldBehavior) {
				return ModifierHelper.isBridge(oldBehavior.getModifiers()) ? BridgeModifier.BRIDGE : BridgeModifier.NON_BRIDGE;
			}

			@Override
			public BridgeModifier getModifierForNew(CtBehavior newBehavior) {
				return ModifierHelper.isBridge(newBehavior.getModifiers()) ? BridgeModifier.BRIDGE : BridgeModifier.NON_BRIDGE;
			}
		});
	}

	private JApiModifier<SyntheticModifier> extractSyntheticModifier(Optional<? extends CtBehavior> oldBehaviorOptional, Optional<? extends CtBehavior> newBehaviorOptional) {
		return ModifierHelper.extractModifierFromBehavior(oldBehaviorOptional, newBehaviorOptional, new ModifierHelper.ExtractModifierFromBehaviorCallback<SyntheticModifier>() {
			@Override
			public SyntheticModifier getModifierForOld(CtBehavior oldBehavior) {
				return ModifierHelper.isSynthetic(oldBehavior.getModifiers()) ? SyntheticModifier.SYNTHETIC : SyntheticModifier.NON_SYNTHETIC;
			}

			@Override
			public SyntheticModifier getModifierForNew(CtBehavior newBehavior) {
				return ModifierHelper.isSynthetic(newBehavior.getModifiers()) ? SyntheticModifier.SYNTHETIC : SyntheticModifier.NON_SYNTHETIC;
			}
		});
	}

	@XmlElementWrapper(name = "modifiers")
	@XmlElement(name = "modifier")
	public List<? extends JApiModifier<? extends Enum<? extends Enum<?>>>> getModifiers() {
		return Arrays.asList(this.finalModifier, this.staticModifier, this.accessModifier, this.abstractModifier, this.bridgeModifier, this.syntheticModifier);
	}

	@XmlAttribute
	public String getName() {
		return name;
	}

	@XmlAttribute
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlElementWrapper(name = "parameters")
	@XmlElement(name = "parameter")
	public List<JApiParameter> getParameters() {
		return parameters;
	}

	public void addParameter(JApiParameter jApiParameter) {
		parameters.add(jApiParameter);
	}

	@XmlTransient
	public JApiModifier<AccessModifier> getAccessModifier() {
		return accessModifier;
	}

	@XmlTransient
	public JApiModifier<FinalModifier> getFinalModifier() {
		return finalModifier;
	}

	@XmlTransient
	public JApiModifier<StaticModifier> getStaticModifier() {
		return staticModifier;
	}

	public JApiModifier<AbstractModifier> getAbstractModifier() {
		return this.abstractModifier;
	}

	@XmlElementWrapper(name = "attributes")
	@XmlElement(name = "attribute")
	public List<JApiAttribute<? extends Enum<?>>> getAttributes() {
		List<JApiAttribute<? extends Enum<?>>> list = new ArrayList<>();
		list.add(this.syntheticAttribute);
		return list;
	}

	@XmlTransient
	public JApiModifier<BridgeModifier> getBridgeModifier() {
		return this.bridgeModifier;
	}

	@XmlTransient
	public JApiModifier<SyntheticModifier> getSyntheticModifier() {
		return this.syntheticModifier;
	}

	@XmlTransient
	public JApiAttribute<SyntheticAttribute> getSyntheticAttribute() {
		return syntheticAttribute;
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

	@Override
	public Optional<Integer> getOldLineNumber() {
		return this.oldLineNumber;
	}

	@Override
	public Optional<Integer> geNewLineNumber() {
		return this.newLineNumber;
	}

	@XmlAttribute(name = "oldLineNumber")
	public String getOldLineNumberAsString() {
		return OptionalHelper.optionalToString(this.oldLineNumber);
	}

	@XmlAttribute(name = "newLineNumber")
	public String getNewLineNumberAsString() {
		return OptionalHelper.optionalToString(this.newLineNumber);
	}
}
