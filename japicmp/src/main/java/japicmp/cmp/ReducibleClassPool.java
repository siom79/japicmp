package japicmp.cmp;

import javassist.ClassPool;
import javassist.CtClass;

/**
 * A {@link ClassPool} that allows to remove a class from the pool.
 */
public class ReducibleClassPool extends ClassPool {
  public void remove(CtClass ctClass) {
	removeCached(ctClass.getName());
  }
}

