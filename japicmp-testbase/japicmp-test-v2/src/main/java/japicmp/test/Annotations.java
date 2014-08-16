package japicmp.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Annotations {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Author {
		String name();
		int year();
	}
	
	public class Shakespeare {
		
	}
	
	@Author(name = "Goethe", year = 1749)
	public class Goethe {
		
	}
}
