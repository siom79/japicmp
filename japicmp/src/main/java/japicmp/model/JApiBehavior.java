package japicmp.model;

import com.google.common.base.Optional;
import japicmp.util.ModifierHelper;
import javassist.CtBehavior;
import javassist.Modifier;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.LinkedList;
import java.util.List;

public class JApiBehavior implements JApiHasModifier, JApiHasChangeStatus {
    protected String name;
    protected List<JApiParameter> parameters = new LinkedList<>();
    protected Optional<AccessModifier> accessModifierOld;
    protected Optional<AccessModifier> accessModifierNew;
    protected Optional<Boolean> finalModifierOld;
    protected Optional<Boolean> finalModifierNew;
    protected Optional<Boolean> staticModifierOld;
    protected Optional<Boolean> staticModifierNew;
    protected JApiChangeStatus changeStatus;

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

    protected Optional<Boolean> extractStaticFinalModifier(Optional<? extends CtBehavior> classOptional) {
        if(classOptional.isPresent()) {
            CtBehavior ctClass = classOptional.get();
            return Optional.of(Modifier.isStatic(ctClass.getModifiers()));
        }
        return Optional.absent();
    }

    protected Optional<Boolean> extractFinalModifier(Optional<? extends CtBehavior> classOptional) {
        if(classOptional.isPresent()) {
            CtBehavior ctClass = classOptional.get();
            return Optional.of(Modifier.isFinal(ctClass.getModifiers()));
        }
        return Optional.absent();
    }

    protected Optional<AccessModifier> extractAccessModifier(Optional<? extends CtBehavior> classOptional) {
        if(classOptional.isPresent()) {
            CtBehavior ctClass = classOptional.get();
            AccessModifier accessModifier = ModifierHelper.translateToModifierLevel(ctClass.getModifiers());
            return Optional.of(accessModifier);
        }
        return Optional.absent();
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

    @XmlElement(name = "parameter")
    public List<JApiParameter> getParameters() {
        return parameters;
    }

    public void addParameter(JApiParameter jApiParameter) {
        parameters.add(jApiParameter);
    }
}
