package japicmp.visitor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import japicmp.cmp.ClassVisitor;
import japicmp.model.JApiAnnotation;
import japicmp.model.JApiClass;

public class JaxbVisitor implements ClassVisitor {
	private Map<String, JApiClass> xmlRootElements = new HashMap<String, JApiClass>();

	@Override
	public void preVisit() {
		
	}

	@Override
	public void visit(JApiClass jApiClass) {
		List<JApiAnnotation> annotations = jApiClass.getAnnotations();
		for(JApiAnnotation jApiAnnotation : annotations) {
			String fullyQualifiedName = jApiAnnotation.getFullyQualifiedName();
			if("javax.xml.bind.annotation.XmlRootElement".equals(fullyQualifiedName)) {
				xmlRootElements.put(fullyQualifiedName, jApiClass);
			}
		}
	}

	@Override
	public void postVisit() {

	}
}
