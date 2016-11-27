package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlEnum;

@XmlEnum
public enum JApiCompatibilityChange {
	CLASS_REMOVED(false, false),
	CLASS_NOW_ABSTRACT(false, false),
	CLASS_NOW_FINAL(false, false),
	CLASS_NO_LONGER_PUBLIC(false, false),
	CLASS_TYPE_CHANGED(false, false),
	CLASS_NOW_CHECKED_EXCEPTION(true, false),
	SUPERCLASS_REMOVED(false, false),
	SUPERCLASS_ADDED(true, true),
	SUPERCLASS_MODIFIED_INCOMPATIBLE(false, false),
	INTERFACE_ADDED(true, true),
	INTERFACE_REMOVED(false, false),
	METHOD_REMOVED(false, false),
	METHOD_REMOVED_IN_SUPERCLASS(false, false),
	METHOD_LESS_ACCESSIBLE(false, false),
	METHOD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS(false, false),
	METHOD_IS_STATIC_AND_OVERRIDES_NOT_STATIC(false, false),
	METHOD_RETURN_TYPE_CHANGED(false, false),
	METHOD_NOW_ABSTRACT(false, false),
	METHOD_NOW_FINAL(false, false),
	METHOD_NOW_STATIC(false, false),
	METHOD_NO_LONGER_STATIC(false, false),
	METHOD_ADDED_TO_INTERFACE(true, false),
	METHOD_NOW_THROWS_CHECKED_EXCEPTION(true, false),
	METHOD_ABSTRACT_ADDED_TO_CLASS(true, false),
	METHOD_ABSTRACT_ADDED_IN_SUPERCLASS(true, false),
	METHOD_ABSTRACT_ADDED_IN_IMPLEMENTED_INTERFACE(true, false),
	FIELD_STATIC_AND_OVERRIDES_STATIC(false, false),
	FIELD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS(false, false),
	FIELD_NOW_FINAL(false, false),
	FIELD_NOW_STATIC(false, false),
	FIELD_NO_LONGER_STATIC(false, false),
	FIELD_TYPE_CHANGED(false, false),
	FIELD_REMOVED(false, false),
	FIELD_REMOVED_IN_SUPERCLASS(false, false),
	FIELD_LESS_ACCESSIBLE(false, false),
	CONSTRUCTOR_REMOVED(false, false),
	CONSTRUCTOR_LESS_ACCESSIBLE(false, false);

	private final boolean binaryCompatible;
	private final boolean sourceCompatible;

	JApiCompatibilityChange(boolean binaryCompatible, boolean sourceCompatible) {
		this.binaryCompatible = binaryCompatible;
		this.sourceCompatible = sourceCompatible;
	}

	@XmlAttribute(name = "binaryCompatible")
	public boolean isBinaryCompatible() {
		return binaryCompatible;
	}

	@XmlAttribute(name = "sourceCompatible")
	public boolean isSourceCompatible() {
		return sourceCompatible;
	}
}
