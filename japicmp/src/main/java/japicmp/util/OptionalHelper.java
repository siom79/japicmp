package japicmp.util;

import com.google.common.base.Optional;

public class OptionalHelper {

	private OptionalHelper() {
		
	}
	
    public static <T> String optionalToString(Optional<T> optional) {
    	if(optional.isPresent()) {
    		return optional.get().toString();
    	}
    	return "n.a.";
    }
}
