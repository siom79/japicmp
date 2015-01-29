package japicmp.model;

import com.google.common.base.Optional;
import japicmp.util.AnnotationHelper;
import japicmp.util.Constants;
import japicmp.util.ModifierHelper;
import japicmp.util.MethodDescriptorParser;
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

public class JApiField implements JApiHasChangeStatus, JApiHasModifiers, JApiHasAccessModifier, JApiHasStaticModifier, JApiHasFinalModifier, JApiBinaryCompatibility, JApiHasAnnotations {
    private final JApiChangeStatus changeStatus;
    private final Optional<CtField> oldFieldOptional;
    private final Optional<CtField> newFieldOptional;
    private final List<JApiAnnotation> annotations = new LinkedList<>();
    private final JApiModifier<AccessModifier> accessModifier;
    private final JApiModifier<StaticModifier> staticModifier;
    private final JApiModifier<FinalModifier> finalModifier;
    private final JApiAttribute<SyntheticAttribute> syntheticAttribute;
    private final JApiType type;
    private boolean binaryCompatible = true;

    public JApiField(JApiChangeStatus changeStatus, Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
        this.oldFieldOptional = oldFieldOptional;
        this.newFieldOptional = newFieldOptional;
        computeAnnotationChanges(this.annotations, oldFieldOptional, newFieldOptional);
        this.accessModifier = extractAccessModifier(oldFieldOptional, newFieldOptional);
        this.staticModifier = extractStaticModifier(oldFieldOptional, newFieldOptional);
        this.finalModifier = extractFinalModifier(oldFieldOptional, newFieldOptional);
        this.syntheticAttribute = extractSyntheticAttribute(oldFieldOptional, newFieldOptional);
        this.type = extractType(oldFieldOptional, newFieldOptional);
        this.changeStatus = evaluateChangeStatus(changeStatus);
    }

    private void computeAnnotationChanges(List<JApiAnnotation> annotations, Optional<CtField> oldBehavior, Optional<CtField> newBehavior) {
        AnnotationHelper.computeAnnotationChanges(annotations, oldBehavior, newBehavior, new AnnotationHelper.AnnotationsAttributeCallback<CtField>() {
            @Override
            public AnnotationsAttribute getAnnotationsAttribute(CtField field) {
                return (AnnotationsAttribute) field.getFieldInfo().getAttribute(AnnotationsAttribute.visibleTag);
            }
        });
    }

    private JApiType extractType(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
        if (oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
            CtField oldField = oldFieldOptional.get();
            CtField newField = newFieldOptional.get();
            String oldType = signatureToType(oldField.getSignature());
            String newType = signatureToType(newField.getSignature());
            if (oldType.equals(newType)) {
                return new JApiType(Optional.of(oldType), Optional.of(newType), JApiChangeStatus.UNCHANGED);
            } else {
                return new JApiType(Optional.of(oldType), Optional.of(newType), JApiChangeStatus.MODIFIED);
            }
        } else {
            if (oldFieldOptional.isPresent()) {
                CtField oldField = oldFieldOptional.get();
                String oldType = signatureToType(oldField.getSignature());
                return new JApiType(Optional.of(oldType), Optional.<String>absent(), JApiChangeStatus.REMOVED);
            } else if (newFieldOptional.isPresent()) {
                CtField newField = newFieldOptional.get();
                String newType = signatureToType(newField.getSignature());
                return new JApiType(Optional.<String>absent(), Optional.of(newType), JApiChangeStatus.NEW);
            }
        }
        return new JApiType(Optional.<String>absent(), Optional.<String>absent(), JApiChangeStatus.UNCHANGED);
    }

    private String signatureToType(String signature) {
        MethodDescriptorParser methodDescriptorParser = new MethodDescriptorParser();
        List<String> types = methodDescriptorParser.parseTypes(signature);
        if (types.size() > 0) {
            return types.get(0);
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
            if (this.syntheticAttribute.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
                changeStatus = JApiChangeStatus.MODIFIED;
            }
            if (this.type.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
                changeStatus = JApiChangeStatus.MODIFIED;
            }
            for (JApiAnnotation jApiAnnotation : annotations) {
                if (jApiAnnotation.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
                    changeStatus = JApiChangeStatus.MODIFIED;
                }
            }
        }
        return changeStatus;
    }

    private JApiAttribute<SyntheticAttribute> extractSyntheticAttribute(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
        if (oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
            CtField oldField = oldFieldOptional.get();
            CtField newField = newFieldOptional.get();
            byte[] attributeOldField = oldField.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
            byte[] attributeNewField = newField.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
            if (attributeOldField != null && attributeNewField != null) {
                return new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
            } else if (attributeOldField != null) {
                return new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
            } else if (attributeNewField != null) {
                return new JApiAttribute<>(JApiChangeStatus.MODIFIED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
            } else {
                return new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
            }
        } else {
            if (oldFieldOptional.isPresent()) {
                CtField ctField = oldFieldOptional.get();
                byte[] attribute = ctField.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
                if (attribute != null) {
                    return new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.<SyntheticAttribute>absent());
                } else {
                    return new JApiAttribute<>(JApiChangeStatus.REMOVED, Optional.of(SyntheticAttribute.NON_SYNTHETIC), Optional.<SyntheticAttribute>absent());
                }
            }
            if (newFieldOptional.isPresent()) {
                CtField ctField = newFieldOptional.get();
                byte[] attribute = ctField.getAttribute(Constants.JAVA_CONSTPOOL_ATTRIBUTE_SYNTHETIC);
                if (attribute != null) {
                    return new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>absent(), Optional.of(SyntheticAttribute.SYNTHETIC));
                } else {
                    return new JApiAttribute<>(JApiChangeStatus.NEW, Optional.<SyntheticAttribute>absent(), Optional.of(SyntheticAttribute.NON_SYNTHETIC));
                }
            }
        }
        return new JApiAttribute<>(JApiChangeStatus.UNCHANGED, Optional.of(SyntheticAttribute.SYNTHETIC), Optional.of(SyntheticAttribute.SYNTHETIC));
    }

    private JApiModifier<AccessModifier> extractAccessModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
        if (oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
            CtField oldField = oldFieldOptional.get();
            CtField newField = newFieldOptional.get();
            AccessModifier oldFieldAccessModifier = ModifierHelper.translateToModifierLevel(oldField.getModifiers());
            AccessModifier newFieldAccessModifier = ModifierHelper.translateToModifierLevel(newField.getModifiers());
            if (oldFieldAccessModifier != newFieldAccessModifier) {
                return new JApiModifier<AccessModifier>(Optional.of(oldFieldAccessModifier), Optional.of(newFieldAccessModifier), JApiChangeStatus.MODIFIED);
            } else {
                return new JApiModifier<AccessModifier>(Optional.of(oldFieldAccessModifier), Optional.of(newFieldAccessModifier), JApiChangeStatus.UNCHANGED);
            }
        } else {
            if (oldFieldOptional.isPresent()) {
                CtField ctField = oldFieldOptional.get();
                AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctField.getModifiers());
                return new JApiModifier<AccessModifier>(Optional.of(accessModifier), Optional.<AccessModifier>absent(), JApiChangeStatus.REMOVED);
            } else {
                CtField ctField = newFieldOptional.get();
                AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctField.getModifiers());
                return new JApiModifier<AccessModifier>(Optional.<AccessModifier>absent(), Optional.of(accessModifier), JApiChangeStatus.NEW);
            }
        }
    }

    private JApiModifier<StaticModifier> extractStaticModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
        if (oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
            CtField oldField = oldFieldOptional.get();
            CtField newField = newFieldOptional.get();
            StaticModifier oldFieldFinalModifier = Modifier.isStatic(oldField.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
            StaticModifier newFieldFinalModifier = Modifier.isStatic(newField.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
            if (oldFieldFinalModifier != newFieldFinalModifier) {
                return new JApiModifier<StaticModifier>(Optional.of(oldFieldFinalModifier), Optional.of(newFieldFinalModifier), JApiChangeStatus.MODIFIED);
            } else {
                return new JApiModifier<StaticModifier>(Optional.of(oldFieldFinalModifier), Optional.of(newFieldFinalModifier), JApiChangeStatus.UNCHANGED);
            }
        } else {
            if (oldFieldOptional.isPresent()) {
                CtField ctField = oldFieldOptional.get();
                StaticModifier finalModifier = Modifier.isFinal(ctField.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
                return new JApiModifier<StaticModifier>(Optional.of(finalModifier), Optional.<StaticModifier>absent(), JApiChangeStatus.REMOVED);
            } else {
                CtField ctField = newFieldOptional.get();
                StaticModifier finalModifier = Modifier.isFinal(ctField.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
                return new JApiModifier<StaticModifier>(Optional.<StaticModifier>absent(), Optional.of(finalModifier), JApiChangeStatus.NEW);
            }
        }
    }

    private JApiModifier<FinalModifier> extractFinalModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
        if (oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
            CtField oldField = oldFieldOptional.get();
            CtField newField = newFieldOptional.get();
            FinalModifier oldFieldFinalModifier = Modifier.isFinal(oldField.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
            FinalModifier newFieldFinalModifier = Modifier.isFinal(newField.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
            if (oldFieldFinalModifier != newFieldFinalModifier) {
                return new JApiModifier<FinalModifier>(Optional.of(oldFieldFinalModifier), Optional.of(newFieldFinalModifier), JApiChangeStatus.MODIFIED);
            } else {
                return new JApiModifier<FinalModifier>(Optional.of(oldFieldFinalModifier), Optional.of(newFieldFinalModifier), JApiChangeStatus.UNCHANGED);
            }
        } else {
            if (oldFieldOptional.isPresent()) {
                CtField ctField = oldFieldOptional.get();
                FinalModifier finalModifier = Modifier.isFinal(ctField.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
                return new JApiModifier<FinalModifier>(Optional.of(finalModifier), Optional.<FinalModifier>absent(), JApiChangeStatus.REMOVED);
            } else {
                CtField ctField = newFieldOptional.get();
                FinalModifier finalModifier = Modifier.isFinal(ctField.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
                return new JApiModifier<FinalModifier>(Optional.<FinalModifier>absent(), Optional.of(finalModifier), JApiChangeStatus.NEW);
            }
        }
    }

    @XmlAttribute(name = "changeStatus")
    public JApiChangeStatus getChangeStatus() {
        return changeStatus;
    }

    @XmlAttribute(name = "name")
    public String getName() {
        String name = "n.a.";
        if (oldFieldOptional.isPresent()) {
            name = oldFieldOptional.get().getName();
        }
        if (newFieldOptional.isPresent()) {
            name = newFieldOptional.get().getName();
        }
        return name;
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
    public List<JApiModifier<? extends Enum<?>>> getModifiers() {
        return Arrays.asList(this.accessModifier, this.staticModifier, this.finalModifier);
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
