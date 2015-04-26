package japicmp.model;

/**
 * Implemented by all elements that can have a synthetic attribute
 * as described in the Class File Format Specification.
 */
public interface JApiHasSyntheticAttribute {

	JApiAttribute<SyntheticAttribute> getSyntheticAttribute();
}
