package japicmp.model;

import com.google.common.base.Optional;
import javassist.CtMethod;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.LinkedList;
import java.util.List;

public class JApiMethod extends JApiBehavior {
    private final Optional<CtMethod> oldMethod;
    private final Optional<CtMethod> newMethod;
    private final String returnType;

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
}
