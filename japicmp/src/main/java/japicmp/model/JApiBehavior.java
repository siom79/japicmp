package japicmp.model;

import com.google.common.base.Optional;
import japicmp.util.AnnotationHelper;
import japicmp.util.Constants;
import japicmp.util.ModifierHelper;
import javassist.CtBehavior;
import javassist.CtConstructor;
import javassist.CtMethod;
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

public class JApiBehavior implements JApiHasModifiers, JApiHasChangeStatus, JApiHasAccessModifier, JApiHasStaticModifier, JApiHasFinalModifier, JApiHasAbstractModifier, JApiBinaryCompatibility, JApiHasAnnotations {
    private final String name;
    private final List<JApiParameter> parameters = new LinkedList<>();
    private final List<JApiAnnotation> annotations = new LinkedList<>();
    private final JApiModifier<AccessModifier> accessModifier;
    private final JApiModifier<FinalModifier> finalModifier;
    private final JApiModifier<StaticModifier> staticModifier;
    private final JApiModifier<AbstractModifier> abstractModifier;
    private final JApiAttribute<SyntheticAttribute> syntheticAttribute;
    protected JApiChangeStatus changeStatus;
    private boolean binaryCompatible = true;

    public JApiBehavior(String name, Optional<? extends CtBehavior> oldBehavior, Optional<? extends CtBehavior> newBehavior, JApiChangeStatus changeStatus) {
        this.name = name;
        computeAnnotationChanges(annotations, oldBehavior, newBehavior);
        this.accessModifier = extractAccessModifier(oldBehavior, newBehavior);
        this.finalModifier = extractFinalModifier(oldBehavior, newBehavior);
        this.staticModifier = extractStaticModifier(oldBehavior, newBehavior);
        this.abstractModifier = extractAbstractModifier(oldBehavior, newBehavior);
        this.syntheticAttribute = extractSyntheticAttribute(oldBehavior, newBehavior);
        this.changeStatus = evaluateChangeStatus(changeStatus);
    }

    @SuppressWarnings("unchecked")
	private void computeAnnotationChanges(List<JApiAnnotation> annotations, Optional<? extends CtBehavior> oldBehavior, Optional<? extends CtBehavior> newBehavior) {
        if (oldBehavior.isPresent()) {
            CtBehavior ctBehavior = oldBehavior.get();
            if (ctBehavior instanceof CtMethod) {
                computeAnnotationChangesMethod(annotations, (Optional<CtMethod>) oldBehavior, (Optional<CtMethod>) newBehavior);
            } else if (ctBehavior instanceof CtConstructor) {
                computeAnnotationChangesConstructor(annotations, (Optional<CtConstructor>) oldBehavior, (Optional<CtConstructor>) newBehavior);
            }
        } else if (newBehavior.isPresent()) {
            CtBehavior ctBehavior = newBehavior.get();
            if (ctBehavior instanceof CtMethod) {
                computeAnnotationChangesMethod(annotations, (Optional<CtMethod>) oldBehavior, (Optional<CtMethod>) newBehavior);
            } else if (ctBehavior instanceof CtConstructor) {
                computeAnnotationChangesConstructor(annotations, (Optional<CtConstructor>) oldBehavior, (Optional<CtConstructor>) newBehavior);
            }
        }
    }

    private void computeAnnotationChangesMethod(List<JApiAnnotation> annotations, Optional<CtMethod> oldBehavior, Optional<CtMethod> newBehavior) {
        AnnotationHelper.computeAnnotationChanges(annotations, oldBehavior, newBehavior, new AnnotationHelper.AnnotationsAttributeCallback<CtMethod>() {
            @Override
            public AnnotationsAttribute getAnnotationsAttribute(CtMethod method) {
                return (AnnotationsAttribute) method.getMethodInfo().getAttribute(AnnotationsAttribute.visibleTag);
            }
        });
    }

    private void computeAnnotationChangesConstructor(List<JApiAnnotation> annotations, Optional<CtConstructor> oldBehavior, Optional<CtConstructor> newBehavior) {
        AnnotationHelper.computeAnnotationChanges(annotations, oldBehavior, newBehavior, new AnnotationHelper.AnnotationsAttributeCallback<CtConstructor>() {
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
        if (oldBehaviorOptional.isPresent() && newBehaviorOptional.isPresent()) {
            CtBehavior oldBehavior = oldBehaviorOptional.get();
            CtBehavior newBehavior = newBehaviorOptional.get();
            byte[] attributeOldBehavior = oldBehavior.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
            byte[] attributeNewBehavior = newBehavior.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
            if (attributeOldBehavior != null && attributeNewBehavior != null) {
                return new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
            } else if (attributeOldBehavior != null) {
                return new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
            } else if (attributeNewBehavior != null) {
                return new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
            } else {
                return new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
            }
        } else {
            if (oldBehaviorOptional.isPresent()) {
                CtBehavior ctBehavior = oldBehaviorOptional.get();
                byte[] attribute = ctBehavior.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
                if (attribute != null) {
                    return new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.<SyntheticAttribute>absent());
                } else {
                    return new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.<SyntheticAttribute>absent());
                }
            }
            if (newBehaviorOptional.isPresent()) {
                CtBehavior ctBehavior = newBehaviorOptional.get();
                byte[] attribute = ctBehavior.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
                if (attribute != null) {
                    return new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>absent(), Optional.of(SyntheticAttribute.SYNTHETIC));
                } else {
                    return new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>absent(), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
                }
            }
        }
        return new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
    }

    protected JApiModifier<StaticModifier> extractStaticModifier(Optional<? extends CtBehavior> oldClassOptional, Optional<? extends CtBehavior> newClassOptional) {
        if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
            CtBehavior oldClass = oldClassOptional.get();
            CtBehavior newClass = newClassOptional.get();
            StaticModifier oldClassFinalModifier = Modifier.isStatic(oldClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
            StaticModifier newClassFinalModifier = Modifier.isStatic(newClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
            if (oldClassFinalModifier != newClassFinalModifier) {
                return new JApiModifier<StaticModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.MODIFIED);
            } else {
                return new JApiModifier<StaticModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.UNCHANGED);
            }
        } else {
            if (oldClassOptional.isPresent()) {
                CtBehavior ctClass = oldClassOptional.get();
                StaticModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
                return new JApiModifier<StaticModifier>(Optional.of(finalModifier), Optional.<StaticModifier>absent(), JApiChangeStatus.REMOVED);
            } else {
                CtBehavior ctClass = newClassOptional.get();
                StaticModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
                return new JApiModifier<StaticModifier>(Optional.<StaticModifier>absent(), Optional.of(finalModifier), JApiChangeStatus.NEW);
            }
        }
    }

    protected JApiModifier<FinalModifier> extractFinalModifier(Optional<? extends CtBehavior> oldClassOptional, Optional<? extends CtBehavior> newClassOptional) {
        if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
            CtBehavior oldClass = oldClassOptional.get();
            CtBehavior newClass = newClassOptional.get();
            FinalModifier oldClassFinalModifier = Modifier.isFinal(oldClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
            FinalModifier newClassFinalModifier = Modifier.isFinal(newClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
            if (oldClassFinalModifier != newClassFinalModifier) {
                return new JApiModifier<FinalModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.MODIFIED);
            } else {
                return new JApiModifier<FinalModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.UNCHANGED);
            }
        } else {
            if (oldClassOptional.isPresent()) {
                CtBehavior ctClass = oldClassOptional.get();
                FinalModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
                return new JApiModifier<FinalModifier>(Optional.of(finalModifier), Optional.<FinalModifier>absent(), JApiChangeStatus.REMOVED);
            } else {
                CtBehavior ctClass = newClassOptional.get();
                FinalModifier finalModifier = Modifier.isFinal(ctClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
                return new JApiModifier<FinalModifier>(Optional.<FinalModifier>absent(), Optional.of(finalModifier), JApiChangeStatus.NEW);
            }
        }
    }

    protected JApiModifier<AccessModifier> extractAccessModifier(Optional<? extends CtBehavior> oldClassOptional, Optional<? extends CtBehavior> newClassOptional) {
        if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
            CtBehavior oldClass = oldClassOptional.get();
            CtBehavior newClass = newClassOptional.get();
            AccessModifier oldClassAccessModifier = ModifierHelper.translateToModifierLevel(oldClass.getModifiers());
            AccessModifier newClassAccessModifier = ModifierHelper.translateToModifierLevel(newClass.getModifiers());
            if (oldClassAccessModifier != newClassAccessModifier) {
                return new JApiModifier<AccessModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.MODIFIED);
            } else {
                return new JApiModifier<AccessModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.UNCHANGED);
            }
        } else {
            if (oldClassOptional.isPresent()) {
                CtBehavior ctClass = oldClassOptional.get();
                AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctClass.getModifiers());
                return new JApiModifier<AccessModifier>(Optional.of(accessModifier), Optional.<AccessModifier>absent(), JApiChangeStatus.REMOVED);
            } else {
                CtBehavior ctClass = newClassOptional.get();
                AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctClass.getModifiers());
                return new JApiModifier<AccessModifier>(Optional.<AccessModifier>absent(), Optional.of(accessModifier), JApiChangeStatus.NEW);
            }
        }
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

    private JApiModifier<AbstractModifier> extractAbstractModifier(Optional<? extends CtBehavior> oldClassOptional, Optional<? extends CtBehavior> newClassOptional) {
        if (oldClassOptional.isPresent() && newClassOptional.isPresent()) {
            CtBehavior oldClass = oldClassOptional.get();
            CtBehavior newClass = newClassOptional.get();
            AbstractModifier oldClassAccessModifier = Modifier.isAbstract(oldClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
            AbstractModifier newClassAccessModifier = Modifier.isAbstract(newClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
            if (oldClassAccessModifier != newClassAccessModifier) {
                return new JApiModifier<AbstractModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.MODIFIED);
            } else {
                return new JApiModifier<AbstractModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.UNCHANGED);
            }
        } else {
            if (oldClassOptional.isPresent()) {
                CtBehavior ctClass = oldClassOptional.get();
                AbstractModifier abstractModifier = Modifier.isAbstract(ctClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
                return new JApiModifier<AbstractModifier>(Optional.of(abstractModifier), Optional.<AbstractModifier>absent(), JApiChangeStatus.REMOVED);
            } else {
                CtBehavior ctClass = newClassOptional.get();
                AbstractModifier abstractModifier = Modifier.isAbstract(ctClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
                return new JApiModifier<AbstractModifier>(Optional.<AbstractModifier>absent(), Optional.of(abstractModifier), JApiChangeStatus.NEW);
            }
        }
    }

    @XmlElementWrapper(name = "modifiers")
    @XmlElement(name = "modifier")
    public List<JApiModifier<? extends Enum<?>>> getModifiers() {
        return Arrays.asList(this.finalModifier, this.staticModifier, this.accessModifier, this.abstractModifier);
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
}
