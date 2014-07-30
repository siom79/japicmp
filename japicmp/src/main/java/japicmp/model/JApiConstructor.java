package japicmp.model;

import com.google.common.base.Optional;
import javassist.CtConstructor;
import javassist.CtMethod;

import javax.xml.bind.annotation.XmlTransient;

public class JApiConstructor extends JApiBehavior {
    private final Optional<CtConstructor> oldConstructor;
    private final Optional<CtConstructor> newConstructor;

    public JApiConstructor(String name, JApiChangeStatus changeStatus, Optional<CtConstructor> oldConstructor, Optional<CtConstructor> newConstructor) {
        this.name = name;
        this.changeStatus = changeStatus;
        this.oldConstructor = oldConstructor;
        this.newConstructor = newConstructor;
        this.accessModifierOld = extractAccessModifier(oldConstructor);
        this.accessModifierNew = extractAccessModifier(newConstructor);
        this.finalModifierOld = extractFinalModifier(oldConstructor);
        this.finalModifierNew = extractFinalModifier(newConstructor);
        this.staticModifierOld = extractStaticFinalModifier(oldConstructor);
        this.staticModifierNew = extractStaticFinalModifier(newConstructor);
        evaluateChangeStatus();
    }

    @XmlTransient
    public Optional<CtConstructor> getNewConstructor() {
        return newConstructor;
    }

    @XmlTransient
    public Optional<CtConstructor> getOldConstructor() {
        return oldConstructor;
    }
}
