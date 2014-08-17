package japicmp.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.xml.bind.annotation.XmlRootElement;

public class Annotations {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Author {
		String name();
		int year();
        String language() default "en";
	}
	
	@XmlRootElement
	@Author(name = "Shakespeare", year = 1564)
	public class Shakespeare {
		
	}
	
	public class Goethe {
		
	}

    @Author(name = "Brecht", year = 1898)
    public class AuthorAnnotationChanges {

    }

    @Author(name = "Brecht", year = 1898)
    public class AuthorAnnotationGetsNewValue {

    }
}
