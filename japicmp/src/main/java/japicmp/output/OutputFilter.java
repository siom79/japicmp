package japicmp.output;

import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiConstructor;
import japicmp.model.JApiField;
import japicmp.model.JApiHasAccessModifier;
import japicmp.model.JApiImplementedInterface;
import japicmp.model.JApiMethod;
import japicmp.model.JApiModifier;
import japicmp.util.ModifierHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class OutputFilter {
	private final Options options;

	private interface FilterVisitor {
		void visit(Iterator<JApiClass> iterator, JApiClass element);

		void visit(Iterator<JApiMethod> iterator, JApiMethod element);

		void visit(Iterator<JApiConstructor> iterator, JApiConstructor element);

		void visit(Iterator<JApiImplementedInterface> iterator, JApiImplementedInterface element);

		void visit(Iterator<JApiField> iterator, JApiField element);
	}

	public OutputFilter(Options options) {
		this.options = options;
	}

	public void filter(List<JApiClass> jApiClasses) {
		filter(jApiClasses, new FilterVisitor() {
			@Override
			public void visit(Iterator<JApiField> iterator, JApiField element) {
				boolean remove = false;
				if (options.isOutputOnlyModifications()) {
					if (element.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
						remove = true;
					}
				}
				if (options.isOutputOnlyBinaryIncompatibleModifications()) {
					if (element.isBinaryCompatible()) {
						remove = true;
					}
				}
				if (!matchesModifierLevel(element)) {
					remove = true;
				}
				if (remove) {
					iterator.remove();
				}
			}

			@Override
			public void visit(Iterator<JApiImplementedInterface> iterator, JApiImplementedInterface element) {
				boolean remove = false;
				if (options.isOutputOnlyModifications()) {
					if (element.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
						remove = true;
					}
				}
				if (options.isOutputOnlyBinaryIncompatibleModifications()) {
					if (element.isBinaryCompatible()) {
						remove = true;
					}
				}
				if (remove) {
					iterator.remove();
				}
			}

			@Override
			public void visit(Iterator<JApiConstructor> iterator, JApiConstructor element) {
				boolean remove = false;
				if (options.isOutputOnlyModifications()) {
					if (element.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
						remove = true;
					}
				}
				if (options.isOutputOnlyBinaryIncompatibleModifications()) {
					if (element.isBinaryCompatible()) {
						remove = true;
					}
				}
				if (!matchesModifierLevel(element)) {
					remove = true;
				}
				if (remove) {
					iterator.remove();
				}
			}

			@Override
			public void visit(Iterator<JApiMethod> iterator, JApiMethod element) {
				boolean remove = false;
				if (options.isOutputOnlyModifications()) {
					if (element.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
						remove = true;
					}
				}
				if (options.isOutputOnlyBinaryIncompatibleModifications()) {
					if (element.isBinaryCompatible()) {
						remove = true;
					}
				}
				if (!matchesModifierLevel(element)) {
					remove = true;
				}
				if (remove) {
					iterator.remove();
				}
			}

			@Override
			public void visit(Iterator<JApiClass> iterator, JApiClass element) {
				boolean remove = false;
				if (options.isOutputOnlyModifications()) {
					if (element.getChangeStatus() == JApiChangeStatus.UNCHANGED) {
						remove = true;
					}
				}
				if (options.isOutputOnlyBinaryIncompatibleModifications()) {
					if (element.isBinaryCompatible()) {
						remove = true;
					}
				}
				if (!matchesModifierLevel(element)) {
					remove = true;
				}
				if (element.getChangeStatus() == JApiChangeStatus.MODIFIED) {
					if (element.getMethods().size() == 0 && element.getConstructors().size() == 0 && element.getInterfaces().size() == 0 && element.getFields().size() == 0
							&& element.getAnnotations().size() == 0 && element.getAbstractModifier().getChangeStatus() == JApiChangeStatus.UNCHANGED
							&& element.getAccessModifier().getChangeStatus() == JApiChangeStatus.UNCHANGED
							&& element.getFinalModifier().getChangeStatus() == JApiChangeStatus.UNCHANGED
							&& element.getStaticModifier().getChangeStatus() == JApiChangeStatus.UNCHANGED
							&& element.getSuperclass().getChangeStatus() == JApiChangeStatus.UNCHANGED) {
						remove = true;
					}
				}
				if (remove) {
					iterator.remove();
				}
			}
		});
	}

	private void filter(List<JApiClass> jApiClasses, FilterVisitor visitor) {
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
			visitor.visit(itClasses, jApiClass);
		}
	}

	private boolean matchesModifierLevel(JApiHasAccessModifier hasAccessModifier) {
		JApiModifier<AccessModifier> accessModifier = hasAccessModifier.getAccessModifier();
		if (accessModifier.getOldModifier().isPresent()) {
			AccessModifier oldModifier = accessModifier.getOldModifier().get();
			if (ModifierHelper.matchesModifierLevel(oldModifier, options.getAccessModifier())) {
				return true;
			}
		}
		if (accessModifier.getNewModifier().isPresent()) {
			AccessModifier newModifier = accessModifier.getNewModifier().get();
			if (ModifierHelper.matchesModifierLevel(newModifier, options.getAccessModifier())) {
				return true;
			}
		}
		return false;
	}

	public static void sortClassesAndMethods(List<JApiClass> jApiClasses) {
		Collections.sort(jApiClasses, new Comparator<JApiClass>() {
			public int compare(JApiClass o1, JApiClass o2) {
				return o1.getFullyQualifiedName().compareToIgnoreCase(o2.getFullyQualifiedName());
			}
		});
		for (JApiClass jApiClass : jApiClasses) {
			Collections.sort(jApiClass.getMethods(), new Comparator<JApiMethod>() {
				public int compare(JApiMethod o1, JApiMethod o2) {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
			});
		}
	}
}
