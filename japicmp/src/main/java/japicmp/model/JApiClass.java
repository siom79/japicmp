package japicmp.model;

import com.google.common.base.Optional;
import japicmp.util.ModifierHelper;
import javassist.CtClass;
import javassist.Modifier;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.LinkedList;
import java.util.List;

public class JApiClass implements JApiModifier {
    private final String fullyQualifiedName;
    private final Optional<CtClass> oldClass;
    private final Optional<CtClass> newClass;
    private List<JApiMethod> methods = new LinkedList<JApiMethod>();
    private JApiChangeStatus changeStatus;
    private final Type type;
    private Optional<AccessModifier> accessModifierOld;
    private Optional<AccessModifier> accessModifierNew;
    private final Optional<Boolean> finalModifierOld;
    private final Optional<Boolean> finalModifierNew;
    private final Optional<Boolean> staticModifierOld;
    private final Optional<Boolean> staticModifierNew;

    public enum Type {
        ANNOTATION, INTERFACE, CLASS, ENUM
    }

    public JApiClass(String fullyQualifiedName, Optional<CtClass> oldClass, Optional<CtClass> newClass, JApiChangeStatus changeStatus, Type type) {
        this.changeStatus = changeStatus;
        this.fullyQualifiedName = fullyQualifiedName;
        this.newClass = newClass;
        this.oldClass = oldClass;
        this.type = type;
        this.accessModifierOld = extractAccessModifier(oldClass);
        this.accessModifierNew = extractAccessModifier(newClass);
        this.finalModifierOld = extractFinalModifier(oldClass);
        this.finalModifierNew = extractFinalModifier(newClass);
        this.staticModifierOld = extractStaticFinalModifier(oldClass);
        this.staticModifierNew = extractStaticFinalModifier(newClass);
        evaluateChangeStatus();
    }

    public void addMethod(JApiMethod jApiMethod) {
        methods.add(jApiMethod);
        if(jApiMethod.getChangeStatus() != JApiChangeStatus.UNCHANGED && this.changeStatus == JApiChangeStatus.UNCHANGED) {
        	this.changeStatus = JApiChangeStatus.MODIFIED;
        }
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
    
    private Optional<Boolean> extractStaticFinalModifier(Optional<CtClass> classOptional) {
		if(classOptional.isPresent()) {
			CtClass ctClass = classOptional.get();
			return Optional.of(Modifier.isStatic(ctClass.getModifiers()));
		}
		return Optional.absent();
	}

	private Optional<Boolean> extractFinalModifier(Optional<CtClass> classOptional) {
		if(classOptional.isPresent()) {
			CtClass ctClass = classOptional.get();
			return Optional.of(Modifier.isFinal(ctClass.getModifiers()));
		}
		return Optional.absent();
	}

	private Optional<AccessModifier> extractAccessModifier(Optional<CtClass> classOptional) {
		if(classOptional.isPresent()) {
			CtClass ctClass = classOptional.get();
			AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctClass.getModifiers());
			return Optional.of(accessModifier);
		}
		return Optional.absent();
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

    public void setChangeStatus(JApiChangeStatus changeStatus) {
        this.changeStatus = changeStatus;
    }

    @XmlElement(name = "method")
    public List<JApiMethod> getMethods() {
        return methods;
    }

    public void setMethods(List<JApiMethod> methods) {
        this.methods = methods;
    }

    @XmlAttribute
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "JApiClass{" +
                "changeStatus=" + changeStatus +
                ", fullyQualifiedName='" + fullyQualifiedName + '\'' +
                ", oldClass=" + oldClass +
                ", newClass=" + newClass +
                '}';
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
