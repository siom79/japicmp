package japicmp.model;

import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparatorOptions;
import javassist.CtConstructor;

import javax.xml.bind.annotation.XmlTransient;

public class JApiConstructor extends JApiBehavior {
    private final Optional<CtConstructor> oldConstructor;
    private final Optional<CtConstructor> newConstructor;

    public JApiConstructor(String name, JApiChangeStatus changeStatus, Optional<CtConstructor> oldConstructor, Optional<CtConstructor> newConstructor, JarArchiveComparatorOptions options) {
        super(name, oldConstructor, newConstructor, changeStatus, options);
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
