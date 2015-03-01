package japicmp.model;

/**
 * This interface defines a method to check whether the implementing
 * element has changed in a way that is compatible or incompatible
 * regarding the Java Object Serialization mechanism as defined in
 * http://docs.oracle.com/javase/7/docs/platform/serialization/spec/serialTOC.html.
 */
public interface JApiJavaObjectSerializationCompatibility {
	/**
	 * Returns true if this element has changed in a way that is compatible
	 * with the Java Object Serialization Specification.
	 * @return true if this element has changed in a compatible way
	 */
	boolean isJavaObjectSerializationCompatible();
}
