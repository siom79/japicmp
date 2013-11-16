package japicmp.model;

import com.google.common.base.Optional;
import javassist.CtMethod;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.LinkedList;
import java.util.List;

public class JApiMethod {
    private final String name;
    private final JApiChangeStatus changeStatus;
    private final Optional<CtMethod> oldMethod;
    private final Optional<CtMethod> newMethod;
    private final String returnType;
    private Optional<AccessModifier> accessModifierOld;
    private Optional<AccessModifier> accessModifierNew;
    private final List<JApiParameter> parameters = new LinkedList<JApiParameter>();

    public JApiMethod(String name, JApiChangeStatus changeStatus, Optional<CtMethod> oldClass, Optional<CtMethod> newClass, String returnType, Optional<AccessModifier> oldModifierLevel, Optional<AccessModifier> newModifierLevel) {
        this.name = name;
        this.changeStatus = changeStatus;
        this.oldMethod = oldClass;
        this.newMethod = newClass;
        this.returnType = returnType;
        this.accessModifierOld = oldModifierLevel;
        this.accessModifierNew = newModifierLevel;
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
        if(this.accessModifierNew.isPresent()) {
            return this.accessModifierNew.get().toString();
        }
        return "n.a.";
    }

    @XmlAttribute(name = "accessModifierOld")
    public String getAccessModifierOld() {
        if(this.accessModifierOld.isPresent()) {
            return this.accessModifierOld.get().toString();
        }
        return "n.a.";
    }
}
