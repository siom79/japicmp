package japicmp.model;

import javax.xml.bind.annotation.XmlTransient;
import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparatorOptions;
import javassist.CtConstructor;

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
