package japicmp.output;

import japicmp.model.JApiAnnotation;
import japicmp.model.JApiClass;
import japicmp.model.JApiConstructor;
import japicmp.model.JApiField;
import japicmp.model.JApiHasAnnotations;
import japicmp.model.JApiImplementedInterface;
import japicmp.model.JApiMethod;
import japicmp.model.JApiSuperclass;

import java.util.Iterator;
import java.util.List;

public class Filter {

	public interface FilterVisitor {
		void visit(Iterator<JApiClass> iterator, JApiClass jApiClass);

		void visit(Iterator<JApiMethod> iterator, JApiMethod jApiMethod);

		void visit(Iterator<JApiConstructor> iterator, JApiConstructor jApiConstructor);

		void visit(Iterator<JApiImplementedInterface> iterator, JApiImplementedInterface jApiImplementedInterface);

		void visit(Iterator<JApiField> iterator, JApiField jApiField);

		void visit(Iterator<JApiAnnotation> iterator, JApiAnnotation jApiAnnotation);

		void visit(JApiSuperclass jApiSuperclass);
	}

	public static void filter(List<JApiClass> jApiClasses, FilterVisitor visitor) {
		Iterator<JApiClass> itClasses = jApiClasses.iterator();
		while (itClasses.hasNext()) {
			JApiClass jApiClass = itClasses.next();
			Iterator<JApiMethod> itMethods = jApiClass.getMethods().iterator();
			while (itMethods.hasNext()) {
				JApiMethod jApiMethod = itMethods.next();
				visitor.visit(itMethods, jApiMethod);
				visitAnnotations(visitor, jApiMethod);

			}
			Iterator<JApiConstructor> itConstructors = jApiClass.getConstructors().iterator();
			while (itConstructors.hasNext()) {
				JApiConstructor jApiConstructor = itConstructors.next();
				visitor.visit(itConstructors, jApiConstructor);
				visitAnnotations(visitor, jApiConstructor);
			}
			Iterator<JApiImplementedInterface> itInterfaces = jApiClass.getInterfaces().iterator();
			while (itInterfaces.hasNext()) {
				JApiImplementedInterface jApiImplementedInterface = itInterfaces.next();
				visitor.visit(itInterfaces, jApiImplementedInterface);
			}
			JApiSuperclass superclass = jApiClass.getSuperclass();
			visitor.visit(superclass);
			Iterator<JApiField> itFields = jApiClass.getFields().iterator();
			while (itFields.hasNext()) {
				JApiField jApiField = itFields.next();
				visitor.visit(itFields, jApiField);
				visitAnnotations(visitor, jApiField);
			}
			visitAnnotations(visitor, jApiClass);
			visitor.visit(itClasses, jApiClass);
		}
	}

	private static void visitAnnotations(FilterVisitor visitor, JApiHasAnnotations jApiHasAnnotations) {
		Iterator<JApiAnnotation> itAnnotations = jApiHasAnnotations.getAnnotations().iterator();
		while (itAnnotations.hasNext()) {
			JApiAnnotation jApiAnnotation = itAnnotations.next();
			visitor.visit(itAnnotations, jApiAnnotation);
		}
	}
}
