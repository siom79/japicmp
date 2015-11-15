package japicmp.filter;

import javassist.CtBehavior;

public interface BehaviorFilter extends ClassFilter {

	boolean matches(CtBehavior ctBehavior);
}
