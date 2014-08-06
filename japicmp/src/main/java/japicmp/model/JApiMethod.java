package japicmp.model;

import javassist.CtMethod;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;

public class JApiMethod extends JApiBehavior {
    private final Optional<CtMethod> oldMethod;
    private final Optional<CtMethod> newMethod;
    private final String returnType;

    public JApiMethod(String name, JApiChangeStatus changeStatus, Optional<CtMethod> oldMethod, Optional<CtMethod> newMethod, String returnType) {
    	super(name, oldMethod, newMethod, changeStatus);
        this.oldMethod = oldMethod;
        this.newMethod = newMethod;
        this.returnType = returnType;
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
