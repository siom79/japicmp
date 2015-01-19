package japicmp.output;

import japicmp.config.ImmutableOptions;
import japicmp.config.Options;
import japicmp.model.*;
import japicmp.util.ModifierHelper;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class OutputFilter extends Filter {
    private final ImmutableOptions options;

    public OutputFilter(ImmutableOptions options) {
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
                if (!ModifierHelper.matchesModifierLevel(element, OutputFilter.this.options.getAccessModifier())) {
                    remove = true;
                }
                if (remove) {
                    iterator.remove();
                }
            }

            @Override
            public void visit(Iterator<JApiAnnotation> iterator, JApiAnnotation element) {
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
                if (!ModifierHelper.matchesModifierLevel(element, OutputFilter.this.options.getAccessModifier())) {
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
                if (!ModifierHelper.matchesModifierLevel(element, OutputFilter.this.options.getAccessModifier())) {
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
                if (!ModifierHelper.matchesModifierLevel(element, OutputFilter.this.options.getAccessModifier())) {
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
