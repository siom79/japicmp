package japicmp.model;

import com.google.common.base.Optional;
import japicmp.util.AnnotationHelper;
import javassist.CtConstructor;
import javassist.bytecode.AnnotationsAttribute;

import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

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
