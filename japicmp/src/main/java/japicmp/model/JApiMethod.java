package japicmp.model;

import japicmp.util.AnnotationHelper;
import javassist.CtBehavior;
import javassist.CtMethod;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;
import javassist.bytecode.AnnotationsAttribute;

import java.util.LinkedList;
import java.util.List;

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
	
	public boolean hasSameReturnType(JApiMethod method) {
		boolean haveSameReturnValue = true;
		String returnType1 = getReturnType();
		String returnType2 = method.getReturnType();
		if(!returnType1.equals(returnType2)) {
			haveSameReturnValue = false;
		}
		return haveSameReturnValue;
	}
}
