package japicmp.filter;

import javassist.CtBehavior;

public interface BehaviorFilter extends Filter {

	boolean matches(CtBehavior ctBehavior);
}
