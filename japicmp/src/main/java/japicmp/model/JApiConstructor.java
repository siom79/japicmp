package japicmp.model;

import javassist.CtConstructor;

import javax.xml.bind.annotation.XmlTransient;

import com.google.common.base.Optional;

public class JApiConstructor extends JApiBehavior {
    private final Optional<CtConstructor> oldConstructor;
    private final Optional<CtConstructor> newConstructor;

    public JApiConstructor(String name, JApiChangeStatus changeStatus, Optional<CtConstructor> oldConstructor, Optional<CtConstructor> newConstructor) {
        super(name, oldConstructor, newConstructor, changeStatus);
        this.oldConstructor = oldConstructor;
        this.newConstructor = newConstructor;
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
