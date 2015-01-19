package japicmp.output;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableList;
import japicmp.model.JApiClass;

public interface Output {
	Optional<String> generate(ImmutableList<JApiClass> jApiClasses);
}
