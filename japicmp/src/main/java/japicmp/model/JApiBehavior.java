package japicmp.model;

import japicmp.util.ModifierHelper;

import java.util.LinkedList;
import java.util.List;

import javassist.CtBehavior;
import javassist.Modifier;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;

public class JApiBehavior implements JApiHasModifier, JApiHasChangeStatus {
    private final String name;
    private final List<JApiParameter> parameters = new LinkedList<>();
    private final JApiModifier<AccessModifier> accessModifier;
    private final JApiModifier<FinalModifier> finalModifier;
    private final JApiModifier<StaticModifier> staticModifier;
    private final JApiModifier<AbstractModifier> abstractModifier;
    private final JApiChangeStatus changeStatus;
    
    public JApiBehavior(String name, Optional<? extends CtBehavior> oldBehavior, Optional<? extends CtBehavior> newBehavior, JApiChangeStatus changeStatus) {
    	this.name = name;
		this.accessModifier = extractAccessModifier(oldBehavior, newBehavior);
        this.finalModifier = extractFinalModifier(oldBehavior, newBehavior);
        this.staticModifier = extractStaticFinalModifier(oldBehavior, newBehavior);
        this.abstractModifier = extractAbstractModifier(oldBehavior, newBehavior);
        this.changeStatus = evaluateChangeStatus(changeStatus);
    }

    public JApiChangeStatus evaluateChangeStatus(JApiChangeStatus changeStatus) {
        if(changeStatus == JApiChangeStatus.UNCHANGED) {
        	if(this.staticModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    			changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		if(this.finalModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    			changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		if(this.accessModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    			changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		if(this.abstractModifier.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
    			changeStatus = JApiChangeStatus.MODIFIED;
    		}
        }
        return changeStatus;
    }

    protected JApiModifier<StaticModifier> extractStaticFinalModifier(Optional<? extends CtBehavior> oldClassOptional, Optional<? extends CtBehavior> newClassOptional) {
    	if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
    		CtBehavior oldClass = oldClassOptional.get();
    		CtBehavior newClass = newClassOptional.get();
			StaticModifier oldClassFinalModifier = Modifier.isStatic(oldClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			StaticModifier newClassFinalModifier = Modifier.isStatic(newClass.getModifiers()) ? StaticModifier.STATIC : StaticModifier.NON_STATIC;
			if(oldClassFinalModifier != newClassFinalModifier) {
				return new JApiModifier<StaticModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<StaticModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldClassOptional.isPresent()) {
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
		if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtBehavior oldClass = oldClassOptional.get();
			CtBehavior newClass = newClassOptional.get();
			FinalModifier oldClassFinalModifier = Modifier.isFinal(oldClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			FinalModifier newClassFinalModifier = Modifier.isFinal(newClass.getModifiers()) ? FinalModifier.FINAL : FinalModifier.NON_FINAL;
			if(oldClassFinalModifier != newClassFinalModifier) {
				return new JApiModifier<FinalModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<FinalModifier>(Optional.of(oldClassFinalModifier), Optional.of(newClassFinalModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldClassOptional.isPresent()) {
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
		if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtBehavior oldClass = oldClassOptional.get();
			CtBehavior newClass = newClassOptional.get();
			AccessModifier oldClassAccessModifier = ModifierHelper.translateToModifierLevel(oldClass.getModifiers());
			AccessModifier newClassAccessModifier = ModifierHelper.translateToModifierLevel(newClass.getModifiers());
			if(oldClassAccessModifier != newClassAccessModifier) {
				return new JApiModifier<AccessModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<AccessModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldClassOptional.isPresent()) {
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
    
	private JApiModifier<AbstractModifier> extractAbstractModifier(Optional<? extends CtBehavior> oldClassOptional, Optional<? extends CtBehavior> newClassOptional) {
		if(oldClassOptional.isPresent() && newClassOptional.isPresent()) {
			CtBehavior oldClass = oldClassOptional.get();
			CtBehavior newClass = newClassOptional.get();
			AbstractModifier oldClassAccessModifier = Modifier.isAbstract(oldClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			AbstractModifier newClassAccessModifier = Modifier.isAbstract(newClass.getModifiers()) ? AbstractModifier.ABSTRACT : AbstractModifier.NON_ABSTRACT;
			if(oldClassAccessModifier != newClassAccessModifier) {
				return new JApiModifier<AbstractModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.MODIFIED);
			} else {
				return new JApiModifier<AbstractModifier>(Optional.of(oldClassAccessModifier), Optional.of(newClassAccessModifier), JApiChangeStatus.UNCHANGED);
			}
		} else {
			if(oldClassOptional.isPresent()) {
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
    
    @XmlAttribute
    public String getName() {
        return name;
    }

    @XmlAttribute
    public JApiChangeStatus getChangeStatus() {
        return changeStatus;
    }

    @XmlAttribute(name = "accessModifierNew")
    public String getAccessModifierNew() {
        return optionalToString(accessModifier.getNewModifier());
    }

    @XmlAttribute(name = "accessModifierOld")
    public String getAccessModifierOld() {
        return optionalToString(accessModifier.getOldModifier());
    }

    @XmlAttribute(name = "finalModifierOld")
    public String getFinalModifierOld() {
        return optionalToString(finalModifier.getOldModifier());
    }

    @XmlAttribute(name = "finalModifierNew")
    public String getFinalModifierNew() {
        return optionalToString(finalModifier.getNewModifier());
    }

    @XmlAttribute(name = "staticModifierOld")
    public String getStaticModifierOld() {
        return optionalToString(staticModifier.getOldModifier());
    }

    @XmlAttribute(name = "staticModifierNew")
    public String getStaticModifierNew() {
        return optionalToString(staticModifier.getNewModifier());
    }

    private <T> String optionalToString(Optional<T> optional) {
        if(optional.isPresent()) {
            return optional.get().toString();
        }
        return "n.a.";
    }

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

	@Override
	public JApiModifier<AbstractModifier> getAbstractModifier() {
		return this.abstractModifier;
	}

	@Override
	public String getAbstractModifierOld() {
		return optionalToString(this.abstractModifier.getOldModifier());
	}

	@Override
	public String getAbstractModifierNew() {
		return optionalToString(this.abstractModifier.getNewModifier());
	}
}
