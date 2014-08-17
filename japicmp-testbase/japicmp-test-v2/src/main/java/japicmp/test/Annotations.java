package japicmp.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Annotations {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Author {
		String name();
		int year();
        String language() default "en";
	}
	
	public class Shakespeare {
		
	}
	
	@Author(name = "Goethe", year = 1749)
	public class Goethe {
		
	}

    @Author(name = "Schiller", year = 1759)
    public class AuthorAnnotationChanges {

    }

    @Author(name = "Brecht", year = 1898, language = "de")
    public class AuthorAnnotationGetsNewValue {

    }
}
