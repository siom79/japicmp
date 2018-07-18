package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum JApiCompatibilityChange {
	ANNOTATION_DEPRECATED_ADDED(true, true, JApiSemanticVersionLevel.MINOR),
	CLASS_REMOVED(false, false, JApiSemanticVersionLevel.MAJOR),
	CLASS_NOW_ABSTRACT(false, false, JApiSemanticVersionLevel.MAJOR),
	CLASS_NOW_FINAL(false, false, JApiSemanticVersionLevel.MAJOR),
	CLASS_NO_LONGER_PUBLIC(false, false, JApiSemanticVersionLevel.MAJOR),
	CLASS_TYPE_CHANGED(false, false, JApiSemanticVersionLevel.MAJOR),
	CLASS_NOW_CHECKED_EXCEPTION(true, false, JApiSemanticVersionLevel.MINOR),
	CLASS_LESS_ACCESSIBLE(false, false, JApiSemanticVersionLevel.MAJOR),
	SUPERCLASS_REMOVED(false, false, JApiSemanticVersionLevel.MAJOR),
	SUPERCLASS_ADDED(true, true, JApiSemanticVersionLevel.MINOR),
	SUPERCLASS_MODIFIED_INCOMPATIBLE(false, false, JApiSemanticVersionLevel.MAJOR),
	INTERFACE_ADDED(true, true, JApiSemanticVersionLevel.MINOR),
	INTERFACE_REMOVED(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_REMOVED(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_REMOVED_IN_SUPERCLASS(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_LESS_ACCESSIBLE(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_IS_STATIC_AND_OVERRIDES_NOT_STATIC(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_RETURN_TYPE_CHANGED(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_NOW_ABSTRACT(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_NOW_FINAL(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_NOW_STATIC(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_NO_LONGER_STATIC(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_ADDED_TO_INTERFACE(true, false, JApiSemanticVersionLevel.MINOR),
	METHOD_NOW_THROWS_CHECKED_EXCEPTION(true, false, JApiSemanticVersionLevel.MINOR),
	METHOD_ABSTRACT_ADDED_TO_CLASS(true, false, JApiSemanticVersionLevel.MINOR),
	METHOD_ABSTRACT_ADDED_IN_SUPERCLASS(true, false, JApiSemanticVersionLevel.MINOR),
	METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE(true, false, JApiSemanticVersionLevel.MINOR),
	METHOD_NEW_DEFAULT(false, false, JApiSemanticVersionLevel.MAJOR),
	METHOD_ABSTRACT_NOW_DEFAULT(false, false, JApiSemanticVersionLevel.MAJOR),
	FIELD_STATIC_AND_OVERRIDES_STATIC(false, false, JApiSemanticVersionLevel.MAJOR),
	FIELD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS(false, false, JApiSemanticVersionLevel.MAJOR),
	FIELD_NOW_FINAL(false, false, JApiSemanticVersionLevel.MAJOR),
	FIELD_NOW_STATIC(false, false, JApiSemanticVersionLevel.MAJOR),
	FIELD_NO_LONGER_STATIC(false, false, JApiSemanticVersionLevel.MAJOR),
	FIELD_TYPE_CHANGED(false, false, JApiSemanticVersionLevel.MAJOR),
	FIELD_REMOVED(false, false, JApiSemanticVersionLevel.MAJOR),
	FIELD_REMOVED_IN_SUPERCLASS(false, false, JApiSemanticVersionLevel.MAJOR),
	FIELD_LESS_ACCESSIBLE(false, false, JApiSemanticVersionLevel.MAJOR),
	CONSTRUCTOR_REMOVED(false, false, JApiSemanticVersionLevel.MAJOR),
	CONSTRUCTOR_LESS_ACCESSIBLE(false, false, JApiSemanticVersionLevel.MAJOR);

	private boolean binaryCompatible;
	private boolean sourceCompatible;
	private JApiSemanticVersionLevel semanticVersionLevel;
	private Boolean binaryCompatibleOverridden = null;
	private Boolean sourceCompatibleOverridden = null;
	private JApiSemanticVersionLevel semanticVersionLevelOverridden = null;

	JApiCompatibilityChange(boolean binaryCompatible, boolean sourceCompatible,
							JApiSemanticVersionLevel jApiSemanticVersionLevel) {
		this.binaryCompatible = binaryCompatible;
		this.sourceCompatible = sourceCompatible;
		this.semanticVersionLevel = jApiSemanticVersionLevel;
	}

	@XmlAttribute(name = "binaryCompatible")
	public boolean isBinaryCompatible() {
		if (binaryCompatibleOverridden != null) {
			return binaryCompatibleOverridden;
		}
		return binaryCompatible;
	}

	@XmlAttribute(name = "sourceCompatible")
	public boolean isSourceCompatible() {
		if (sourceCompatibleOverridden != null) {
			return sourceCompatibleOverridden;
		}
		return sourceCompatible;
	}

	public JApiSemanticVersionLevel getSemanticVersionLevel() {
		if (semanticVersionLevelOverridden != null) {
			return semanticVersionLevelOverridden;
		}
		return semanticVersionLevel;
	}

	public void setBinaryCompatible(boolean binaryCompatible) {
		this.binaryCompatibleOverridden = binaryCompatible;
	}

	public void setSourceCompatible(boolean sourceCompatible) {
		this.sourceCompatibleOverridden = sourceCompatible;
	}

	public void setSemanticVersionLevel(JApiSemanticVersionLevel semanticVersionLevel) {
		this.semanticVersionLevelOverridden = semanticVersionLevel;
	}

	public void resetOverrides() {
		this.binaryCompatibleOverridden = null;
		this.sourceCompatibleOverridden = null;
		this.semanticVersionLevelOverridden = null;
	}
}
