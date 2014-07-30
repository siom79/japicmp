package japicmp.model;

import com.google.common.base.Optional;
import japicmp.util.ModifierHelper;
import javassist.CtBehavior;
import javassist.CtMethod;
import javassist.Modifier;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.LinkedList;
import java.util.List;

public class JApiMethod implements JApiModifier {
    private final String name;
    private JApiChangeStatus changeStatus;
    private final Optional<CtMethod> oldMethod;
    private final Optional<CtMethod> newMethod;
    private final String returnType;
    private Optional<AccessModifier> accessModifierOld;
    private Optional<AccessModifier> accessModifierNew;
    private final List<JApiParameter> parameters = new LinkedList<JApiParameter>();
    private final Optional<Boolean> finalModifierOld;
    private final Optional<Boolean> finalModifierNew;
    private final Optional<Boolean> staticModifierOld;
    private final Optional<Boolean> staticModifierNew;

    public JApiMethod(String name, JApiChangeStatus changeStatus, Optional<CtMethod> oldMethod, Optional<CtMethod> newMethod, String returnType) {
        this.name = name;
        this.changeStatus = changeStatus;
        this.oldMethod = oldMethod;
        this.newMethod = newMethod;
        this.returnType = returnType;
        this.accessModifierOld = extractAccessModifier(oldMethod);
        this.accessModifierNew = extractAccessModifier(newMethod);
        this.finalModifierOld = extractFinalModifier(oldMethod);
        this.finalModifierNew = extractFinalModifier(newMethod);
        this.staticModifierOld = extractStaticFinalModifier(oldMethod);
        this.staticModifierNew = extractStaticFinalModifier(newMethod);
        evaluateChangeStatus();
    }
    
    public void evaluateChangeStatus() {
    	if(this.changeStatus == JApiChangeStatus.UNCHANGED) {
    		if(this.accessModifierOld.isPresent() && this.accessModifierNew.isPresent()) {
    			if(this.accessModifierOld.get() != this.accessModifierNew.get()) {
    				this.changeStatus = JApiChangeStatus.MODIFIED;
    			}
    		} else if(this.accessModifierOld.isPresent() && !this.accessModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		} else if(!this.accessModifierOld.isPresent() && this.accessModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		if(this.finalModifierOld.isPresent() && this.finalModifierNew.isPresent()) {
    			if(this.finalModifierOld.get() != this.finalModifierNew.get()) {
    				this.changeStatus = JApiChangeStatus.MODIFIED;
    			}
    		} else if(this.finalModifierOld.isPresent() && !this.finalModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		} else if(!this.finalModifierOld.isPresent() && this.finalModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		}
    		if(this.staticModifierOld.isPresent() && this.staticModifierNew.isPresent()) {
    			if(this.staticModifierOld.get() != this.staticModifierNew.get()) {
    				this.changeStatus = JApiChangeStatus.MODIFIED;
    			}
    		} else if(this.staticModifierOld.isPresent() && !this.staticModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		} else if(!this.staticModifierOld.isPresent() && this.staticModifierNew.isPresent()) {
    			this.changeStatus = JApiChangeStatus.MODIFIED;
    		}
    	}
    }

    private Optional<Boolean> extractStaticFinalModifier(Optional<CtMethod> methodOptional) {
		if(methodOptional.isPresent()) {
			CtBehavior ctMethod = methodOptional.get();
			return Optional.of(Modifier.isStatic(ctMethod.getModifiers()));
		}
		return Optional.absent();
	}

	private Optional<Boolean> extractFinalModifier(Optional<CtMethod> methodOptional) {
		if(methodOptional.isPresent()) {
			CtBehavior ctMethod = methodOptional.get();
			return Optional.of(Modifier.isFinal(ctMethod.getModifiers()));
		}
		return Optional.absent();
	}

	private Optional<AccessModifier> extractAccessModifier(Optional<CtMethod> methodOptional) {
		if(methodOptional.isPresent()) {
			CtBehavior ctMethod = methodOptional.get();
			AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctMethod.getModifiers());
			return Optional.of(accessModifier);
		}
		return Optional.absent();
	}

	@XmlAttribute
    public JApiChangeStatus getChangeStatus() {
        return changeStatus;
    }

    @XmlAttribute
    public String getName() {
        return name;
    }

    @XmlTransient
    public Optional<CtMethod> getNewMethod() {
        return newMethod;
    }

    @XmlTransient
    public Optional<CtMethod> getOldMethod() {
        return oldMethod;
    }

    @XmlAttribute
    public String getReturnType() {
        return returnType;
    }

    @XmlElement(name = "parameter")
    public List<JApiParameter> getParameters() {
        return parameters;
    }

    public void addParameter(JApiParameter jApiParameter) {
        parameters.add(jApiParameter);
    }

    @XmlAttribute(name = "accessModifierNew")
    public String getAccessModifierNew() {
    	return optionalToString(accessModifierNew);
    }

    @XmlAttribute(name = "accessModifierOld")
    public String getAccessModifierOld() {
    	return optionalToString(accessModifierOld);
    }

    @XmlAttribute(name = "finalModifierOld")
	public String getFinalModifierOld() {
		return optionalToString(finalModifierOld);
	}

    @XmlAttribute(name = "finalModifierNew")
	public String getFinalModifierNew() {
		return optionalToString(finalModifierNew);
	}

    @XmlAttribute(name = "staticModifierOld")
	public String getStaticModifierOld() {
		return optionalToString(staticModifierOld);
	}

    @XmlAttribute(name = "staticModifierNew")
	public String getStaticModifierNew() {
		return optionalToString(staticModifierNew);
	}
    
    private <T> String optionalToString(Optional<T> optional) {
    	if(optional.isPresent()) {
    		return optional.get().toString();
    	}
    	return "n.a.";
    }
    
    @XmlTransient
	public Optional<Boolean> getFinalModifierOldOptional() {
		return finalModifierOld;
	}

    @XmlTransient
	public Optional<Boolean> getFinalModifierNewOptional() {
		return finalModifierNew;
	}

    @XmlTransient
	public Optional<Boolean> getStaticModifierOldOptional() {
		return staticModifierOld;
	}

    @XmlTransient
	public Optional<Boolean> getStaticModifierNewOptional() {
		return staticModifierNew;
	}
}
