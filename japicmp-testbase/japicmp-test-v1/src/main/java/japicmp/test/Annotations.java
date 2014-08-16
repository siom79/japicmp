package japicmp.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.xml.bind.annotation.XmlRootElement;

public class Annotations {

	@Retention(RetentionPolicy.RUNTIME)
	public @interface Author {
		String name();
		int year();
	}
	
	@XmlRootElement
	@Author(name = "Shakespeare", year = 1564)
	public class Shakespeare {
		
	}
	
	public class Goethe {
		
	}
}
