package japicmp.model;

public enum JApiCompatibilityChange {
	CLASS_REMOVED(false, false),
	CLASS_NOW_ABSTRACT(false, false),
	CLASS_NOW_FINAL(false, false),
	CLASS_NO_LONGER_PUBLIC(false, false),
	CLASS_TYPE_CHANGED(false, false),
	SUPERCLASS_REMOVED(false, false),
	SUPERCLASS_CHANGED(false, false),
	SUPERCLASS_ADDED(false, false),
	SUPERCLASS_MODIFIED_INCOMPATIBLE(false, false),
	METHOD_REMOVED(false, false),
	METHOD_LESS_ACCESSIBLE(false, false),
	METHOD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS(false, false),
	METHOD_IS_STATIC_AND_OVERRIDES_NOT_STATIC(false, false),
	METHOD_RETURN_TYPE_CHANGED(false, false),
	METHOD_NOW_ABSTRACT(false, false),
	METHOD_NOW_FINAL(false, false),
	METHOD_NOW_STATIC(false, false),
	METHOD_NO_LONGER_STATIC(false, false),
	FIELD_STATIC_AND_OVERRIDES_STATIC(false, false),
	FIELD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS(false, false),
	FIELD_NOW_FINAL(false, false),
	FIELD_NOW_STATIC(false, false),
	FIELD_NO_LONGER_STATIC(false, false),
	FIELD_TYPE_CHANGED(false, false),
	FIELD_REMOVED(false, false),
	FIELD_LESS_ACCESSIBLE(false, false),
	CONSTRUCTOR_REMOVED(false, false),
	CONSTRUCTOR_LESS_ACCESSIBLE(false, false);

	private final boolean binaryCompatible;
	private final boolean sourceCompatible;

	JApiCompatibilityChange(boolean binaryCompatible, boolean sourceCompatible) {
		this.binaryCompatible = binaryCompatible;
		this.sourceCompatible = sourceCompatible;
	}

	public boolean isBinaryCompatible() {
		return binaryCompatible;
	}

	public boolean isSourceCompatible() {
		return sourceCompatible;
	}
}
