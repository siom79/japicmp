package japicmp.model;

import japicmp.cmp.JarArchiveComparator;
import javassist.CtConstructor;

import javax.xml.bind.annotation.XmlTransient;
import java.util.Optional;

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

	@Override
	public void enhanceGenericTypeToParameters() {
		super.enhanceGenericTypeToParameters(this.jApiClass, this.oldConstructor, this.newConstructor);
	}

	@Override
	public boolean isSourceCompatible() {
		boolean sourceCompatible = super.isSourceCompatible();
		for (JApiParameter jApiParameter : getParameters()) {
			for (JApiCompatibilityChange compatibilityChange : jApiParameter.getCompatibilityChanges()) {
				if (!compatibilityChange.isSourceCompatible()) {
					sourceCompatible = false;
					break;
				}
			}
		}
		return sourceCompatible;
	}
}
