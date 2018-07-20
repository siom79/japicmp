package japicmp.model;

import japicmp.util.Optional;
import japicmp.cmp.JarArchiveComparator;
import javassist.CtConstructor;

import javax.xml.bind.annotation.XmlTransient;

public class JApiConstructor extends JApiBehavior {
	private final Optional<CtConstructor> oldConstructor;
	private final Optional<CtConstructor> newConstructor;

	public JApiConstructor(JApiClass jApiClass, String name, JApiChangeStatus changeStatus, Optional<CtConstructor> oldConstructor, Optional<CtConstructor> newConstructor, JarArchiveComparator jarArchiveComparator) {
		super(jApiClass, name, oldConstructor, newConstructor, changeStatus, jarArchiveComparator);
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

	public String toString()
	{
		return "JApiConstructor [oldConstructor="
			+ oldConstructor
			+ ", newConstructor="
			+ newConstructor
			+ ", getNewConstructor()="
			+ getNewConstructor()
			+ ", getOldConstructor()="
			+ getOldConstructor()
			+ ", getCompatibilityChanges()="
			+ getCompatibilityChanges()
			+ "]";
	}


}
