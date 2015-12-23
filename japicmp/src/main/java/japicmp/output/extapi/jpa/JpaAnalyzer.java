package japicmp.output.extapi.jpa;

import japicmp.model.JApiAnnotation;
import japicmp.model.JApiClass;
import japicmp.output.extapi.jpa.model.JpaTable;

import java.util.LinkedList;
import java.util.List;

public class JpaAnalyzer {
	public static final String JPA_ANNOTATION_ENTITY = "javax.persistence.Entity";
	public static final String JPA_ANNOTATION_TRANSIENT = "javax.persistence.Transient";

	public List<JpaTable> analyze(List<JApiClass> classes) {
		List<JpaTable> jpaTables = new LinkedList<>();
		for (JApiClass jApiClass : classes) {
			List<JApiAnnotation> annotations = jApiClass.getAnnotations();
			for (JApiAnnotation jApiAnnotation : annotations) {
				String fullyQualifiedName = jApiAnnotation.getFullyQualifiedName();
				if (JPA_ANNOTATION_ENTITY.equals(fullyQualifiedName)) {
					JpaTable jpaTable = new JpaTable(jApiClass, jApiAnnotation);
					jpaTables.add(jpaTable);
				}
			}
		}
		return jpaTables;
	}
}
