package japicmp.output;

import japicmp.model.*;

import java.util.Iterator;
import java.util.List;

public class Filter {

	public interface FilterVisitor {
		void visit(Iterator<JApiClass> iterator, JApiClass element);

		void visit(Iterator<JApiMethod> iterator, JApiMethod element);

		void visit(Iterator<JApiConstructor> iterator, JApiConstructor element);

		void visit(Iterator<JApiImplementedInterface> iterator, JApiImplementedInterface element);

		void visit(Iterator<JApiField> iterator, JApiField element);

		void visit(Iterator<JApiAnnotation> iterator, JApiAnnotation jApiAnnotation);
	}

	public static void filter(List<JApiClass> jApiClasses, FilterVisitor visitor) {
		Iterator<JApiClass> itClasses = jApiClasses.iterator();
		while (itClasses.hasNext()) {
			JApiClass jApiClass = itClasses.next();
			Iterator<JApiMethod> itMethods = jApiClass.getMethods().iterator();
			while (itMethods.hasNext()) {
				JApiMethod jApiMethod = itMethods.next();
				visitor.visit(itMethods, jApiMethod);
			}
			Iterator<JApiConstructor> itConstructors = jApiClass.getConstructors().iterator();
			while (itConstructors.hasNext()) {
				JApiConstructor jApiConstructor = itConstructors.next();
				visitor.visit(itConstructors, jApiConstructor);
			}
			Iterator<JApiImplementedInterface> itInterfaces = jApiClass.getInterfaces().iterator();
			while (itInterfaces.hasNext()) {
				JApiImplementedInterface jApiImplementedInterface = itInterfaces.next();
				visitor.visit(itInterfaces, jApiImplementedInterface);
			}
			Iterator<JApiField> itFields = jApiClass.getFields().iterator();
			while (itFields.hasNext()) {
				JApiField jApiField = itFields.next();
				visitor.visit(itFields, jApiField);
			}
			Iterator<JApiAnnotation> itAnnotations = jApiClass.getAnnotations().iterator();
			while (itAnnotations.hasNext()) {
				JApiAnnotation jApiAnnotation = itAnnotations.next();
				visitor.visit(itAnnotations, jApiAnnotation);
			}
			visitor.visit(itClasses, jApiClass);
		}
	}
}
