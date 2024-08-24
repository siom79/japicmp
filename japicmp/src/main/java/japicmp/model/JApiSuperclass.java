package japicmp.model;

import japicmp.cmp.JarArchiveComparator;
import japicmp.util.ClassHelper;
import japicmp.util.OptionalHelper;
import javassist.CtClass;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class JApiSuperclass implements JApiHasChangeStatus, JApiCompatibility {
	private final JApiClass jApiClass;
	private final Optional<CtClass> oldSuperclassOptional;
	private final Optional<CtClass> newSuperclassOptional;
	private final JApiChangeStatus changeStatus;
	private final JarArchiveComparator jarArchiveComparator;
	private final List<JApiCompatibilityChange> compatibilityChanges = new LinkedList<>();
	private Optional<JApiClass> correspondingJApiClass = Optional.empty();

	public JApiSuperclass(JApiClass jApiClass, Optional<CtClass> oldSuperclassOptional, Optional<CtClass> newSuperclassOptional, JApiChangeStatus changeStatus, JarArchiveComparator jarArchiveComparator) {
		this.jApiClass = jApiClass;
		this.oldSuperclassOptional = oldSuperclassOptional;
		this.newSuperclassOptional = newSuperclassOptional;
		this.changeStatus = changeStatus;
		this.jarArchiveComparator = jarArchiveComparator;
	}

	/**
	 * Returns the {@link japicmp.model.JApiClass} representation of this superclass.
	 * The return value is Optional.empty() in case the superclass for the old and new version is absent.
	 *
	 * @return the {@link japicmp.model.JApiClass} representation of this superclass as {@link java.util.Optional}
	 */
	public Optional<JApiClass> getJApiClass() {
		if (oldSuperclassOptional.isPresent() && newSuperclassOptional.isPresent()) {
			CtClass oldSuperclass = oldSuperclassOptional.get();
			CtClass newSuperclass = newSuperclassOptional.get();
			String oldSuperclassName = oldSuperclass.getName();
			String newSuperclassName = newSuperclass.getName();
			if (oldSuperclassName.equals(newSuperclassName)) {
				JApiClassType classType = new JApiClassType(Optional.of(ClassHelper.getType(oldSuperclass)), Optional.of(ClassHelper.getType(newSuperclass)), JApiChangeStatus.UNCHANGED);
				JApiClass jApiClass = new JApiClass(jarArchiveComparator, oldSuperclassName, Optional.of(oldSuperclass), Optional.of(newSuperclass), JApiChangeStatus.UNCHANGED, classType);
				return Optional.of(jApiClass);
			} else {
				return Optional.empty();
			}
		} else if (oldSuperclassOptional.isPresent()) {
			CtClass oldSuperclass = oldSuperclassOptional.get();
			String oldSuperclassName = oldSuperclass.getName();
			JApiClassType classType = new JApiClassType(Optional.of(ClassHelper.getType(oldSuperclass)), Optional.<JApiClassType.ClassType>empty(), JApiChangeStatus.REMOVED);
			JApiClass jApiClass = new JApiClass(jarArchiveComparator, oldSuperclassName, Optional.of(oldSuperclass), Optional.<CtClass>empty(), JApiChangeStatus.REMOVED, classType);
			return Optional.of(jApiClass);
		} else if (newSuperclassOptional.isPresent()) {
			CtClass newSuperclass = newSuperclassOptional.get();
			String newSuperclassName = newSuperclass.getName();
			JApiClassType classType = new JApiClassType(Optional.<JApiClassType.ClassType>empty(), Optional.of(ClassHelper.getType(newSuperclass)), JApiChangeStatus.NEW);
			JApiClass jApiClass = new JApiClass(jarArchiveComparator, newSuperclassName, Optional.<CtClass>empty(), Optional.of(newSuperclass), JApiChangeStatus.NEW, classType);
			return Optional.of(jApiClass);
		}
		return Optional.empty();
	}

	@XmlTransient
	public Optional<CtClass> getOldSuperclass() {
		return oldSuperclassOptional;
	}

	@XmlTransient
	public Optional<CtClass> getNewSuperclass() {
		return newSuperclassOptional;
	}

	@XmlTransient
	public Optional<String> getOldSuperclassName() {
		return oldSuperclassOptional.isPresent() ? Optional.of(oldSuperclassOptional.get().getName()) : Optional.<String>empty();
	}

	@XmlTransient
	public Optional<String> getNewSuperclassName() {
		return newSuperclassOptional.isPresent() ? Optional.of(newSuperclassOptional.get().getName()) : Optional.<String>empty();
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return changeStatus;
	}

	@XmlAttribute(name = "superclassOld")
	public String getSuperclassOld() {
		return OptionalHelper.optionalToString(getOldSuperclassName());
	}

	@XmlAttribute(name = "superclassNew")
	public String getSuperclassNew() {
		return OptionalHelper.optionalToString(getNewSuperclassName());
	}

	@Override
	@XmlAttribute
	public boolean isBinaryCompatible() {
		boolean binaryCompatible = true;
		for (JApiCompatibilityChange compatibilityChange : compatibilityChanges) {
			if (!compatibilityChange.isBinaryCompatible()) {
				binaryCompatible = false;
			}
		}
		return binaryCompatible;
	}

	@Override
	@XmlAttribute
	public boolean isSourceCompatible() {
		boolean sourceCompatible = true;
		for (JApiCompatibilityChange compatibilityChange : compatibilityChanges) {
			if (!compatibilityChange.isSourceCompatible()) {
				sourceCompatible = false;
			}
		}
		return sourceCompatible;
	}

	@XmlElementWrapper(name = "compatibilityChanges")
	@XmlElement(name = "compatibilityChange")
	public List<JApiCompatibilityChange> getCompatibilityChanges() {
		return this.compatibilityChanges;
	}

	public void setJApiClass(JApiClass jApiClass) {
		this.correspondingJApiClass = Optional.of(jApiClass);
	}

	public Optional<JApiClass> getCorrespondingJApiClass() {
		return correspondingJApiClass;
	}

	/**
	 * Returns the {@link japicmp.model.JApiClass} this superclass belongs to.
	 * @return the JApiClass this superclass belongs to.
     */
	@XmlTransient
	public JApiClass getJApiClassOwning() {
		return jApiClass;
	}

	public String toString()
	{
		String oldSuperClass = "n.a.";
		if(oldSuperclassOptional.isPresent()) {
			oldSuperClass = oldSuperclassOptional.get().getName();
		}
		String newSuperClass = "n.a.";
		if(newSuperclassOptional.isPresent()) {
			newSuperClass = newSuperclassOptional.get().getName();
		}

		return "JApiSuperclass [jApiClass="
			+ jApiClass
			+ ", oldSuperclass="
			+ oldSuperClass
			+ ", newSuperclass="
			+ newSuperClass
			+ ", changeStatus="
			+ changeStatus
			+ ", compatibilityChanges="
			+ compatibilityChanges
			+ "]";
	}


}
