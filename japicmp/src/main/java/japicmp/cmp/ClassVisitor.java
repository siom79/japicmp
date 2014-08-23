package japicmp.cmp;

import japicmp.model.JApiClass;

public interface ClassVisitor {
	
	/**
	 * Called before visiting starts.
	 */
	void preVisit();
	
	/**
	 * Called for each class detected in the provided archives.
	 * @param jApiClass the class
	 * @param classMap a lookup map for all available classes
	 */
	void visit(JApiClass jApiClass);
	
	/**
	 * Called after all classes have been visited.
	 */
	void postVisit();
}
