package japicmp.model;

import com.google.common.base.Optional;
import javassist.CtClass;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.LinkedList;
import java.util.List;

public class JApiClass {
    private final String fullyQualifiedName;
    private final Optional<CtClass> oldClass;
    private final Optional<CtClass> newClass;
    private List<JApiMethod> methods = new LinkedList<JApiMethod>();
    private JApiChangeStatus changeStatus;
    private final Type type;
    private Optional<AccessModifier> accessModifierOld;
    private Optional<AccessModifier> accessModifierNew;

    public enum Type {
        ANNOTATION, INTERFACE, CLASS, ENUM
    }

    public JApiClass(String fullyQualifiedName, Optional<CtClass> oldClass, Optional<CtClass> newClass, JApiChangeStatus changeStatus, Type type, Optional<AccessModifier> oldModifierLevel, Optional<AccessModifier> newModifierLevel) {
        this.changeStatus = changeStatus;
        this.fullyQualifiedName = fullyQualifiedName;
        this.newClass = newClass;
        this.oldClass = oldClass;
        this.type = type;
        this.accessModifierOld = oldModifierLevel;
        this.accessModifierNew = newModifierLevel;
    }

    public void addMethod(JApiMethod jApiMethod) {
        methods.add(jApiMethod);
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

    @Override
    public String toString() {
        return "JApiClass{" +
                "changeStatus=" + changeStatus +
                ", fullyQualifiedName='" + fullyQualifiedName + '\'' +
                ", oldClass=" + oldClass +
                ", newClass=" + newClass +
                '}';
    }
}
