package japicmp.model;

import japicmp.util.ModifierHelper;

import java.util.Arrays;
import java.util.List;

import javassist.CtField;
import javassist.Modifier;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;

public class JApiField {
	private final JApiChangeStatus changeStatus;
	private final Optional<CtField> oldFieldOptional;
	private final Optional<CtField> newFieldOptional;
	private final JApiModifier<AccessModifier> accessModifier;
	private final JApiModifier<StaticModifier> staticModifier;
	private final JApiModifier<FinalModifier> finalModifier;

	public JApiField(JApiChangeStatus changeStatus, Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		this.oldFieldOptional = oldFieldOptional;
		this.newFieldOptional = newFieldOptional;
		this.accessModifier = extractAccessModifier(oldFieldOptional, newFieldOptional);
		this.staticModifier = extractStaticModifier(oldFieldOptional, newFieldOptional);
		this.finalModifier = extractFinalModifier(oldFieldOptional, newFieldOptional);
		this.changeStatus = evaluateChangeStatus(changeStatus);
	}

    private JApiChangeStatus evaluateChangeStatus(JApiChangeStatus changeStatus) {
    	if(changeStatus == JApiChangeStatus.UNCHANGED) {
			if(this.accessModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if(this.staticModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
			if(this.finalModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
				changeStatus = JApiChangeStatus.MODIFIED;
			}
    	}
		return changeStatus;
	}

	private JApiModifier<AccessModifier> extractAccessModifier(Optional<CtField> oldFieldOptional, Optional<CtField> newFieldOptional) {
		if(oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
			CtField oldField = oldFieldOptional.get();
			CtField newField = newFieldOptional.get();
			AccessModifier oldFieldAccessModifier = ModifierHelper.translateToModifierLevel(oldField.getModifiers());
			AccessModifier newFieldAccessModifier = ModifierHelper.translateToModifierLevel(newField.getModifiers());
			if(oldFieldAccessModifier != newFieldAccessModifier) {
				return new JApiModifier<AccessModifier>(Optional.of(oldFieldAccessModifier), Optional.of(newFieldAccessModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<AccessModifier>(Optional.of(oldFieldAccessModifier), Optional.of(newFieldAccessModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldFieldOptional.isPresent()) {
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
    	if(oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
    		CtField oldField = oldFieldOptional.get();
    		CtField newField = newFieldOptional.get();
			StaticModifier oldFieldFinalModifier = Modifier.isStatic(oldField.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			StaticModifier newFieldFinalModifier = Modifier.isStatic(newField.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			if(oldFieldFinalModifier != newFieldFinalModifier) {
				return new JApiModifier<StaticModifier>(Optional.of(oldFieldFinalModifier), Optional.of(newFieldFinalModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<StaticModifier>(Optional.of(oldFieldFinalModifier), Optional.of(newFieldFinalModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldFieldOptional.isPresent()) {
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
		if(oldFieldOptional.isPresent() && newFieldOptional.isPresent()) {
			CtField oldField = oldFieldOptional.get();
			CtField newField = newFieldOptional.get();
			FinalModifier oldFieldFinalModifier = Modifier.isFinal(oldField.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			FinalModifier newFieldFinalModifier = Modifier.isFinal(newField.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			if(oldFieldFinalModifier != newFieldFinalModifier) {
				return new JApiModifier<FinalModifier>(Optional.of(oldFieldFinalModifier), Optional.of(newFieldFinalModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<FinalModifier>(Optional.of(oldFieldFinalModifier), Optional.of(newFieldFinalModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldFieldOptional.isPresent()) {
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
    	if(oldFieldOptional.isPresent()) {
    		name = oldFieldOptional.get().getName();
    	}
    	if(newFieldOptional.isPresent()) {
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
}
