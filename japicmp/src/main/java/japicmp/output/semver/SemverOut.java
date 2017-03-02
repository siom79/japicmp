package japicmp.output.semver;

import com.google.common.collect.ImmutableSet;
import japicmp.config.Options;
import japicmp.model.*;
import japicmp.output.Filter;
import japicmp.output.OutputGenerator;

import java.util.Iterator;
import java.util.List;

import static japicmp.util.ModifierHelper.isNotPrivate;

public class SemverOut extends OutputGenerator<String> {
	private enum SemverStatus {
		UNCHANGED, CHANGED_BINARY_COMPATIBLE, CHANGED_BINARY_INCOMPATIBLE;
	}

	public SemverOut(Options options, List<JApiClass> jApiClasses) {
		super(options, jApiClasses);
	}

	@Override
	public String generate() {
		final ImmutableSet.Builder<SemverStatus> builder = ImmutableSet.builder();
		Filter.filter(jApiClasses, new Filter.FilterVisitor() {
			@Override
			public void visit(Iterator<JApiClass> iterator, JApiClass jApiClass) {
				builder.add(signs(jApiClass));
			}

			@Override
			public void visit(Iterator<JApiMethod> iterator, JApiMethod jApiMethod) {
				builder.add(signs(jApiMethod));
			}

			@Override
			public void visit(Iterator<JApiConstructor> iterator, JApiConstructor jApiConstructor) {
				builder.add(signs(jApiConstructor));
			}

			@Override
			public void visit(Iterator<JApiImplementedInterface> iterator, JApiImplementedInterface jApiImplementedInterface) {
				builder.add(signs(jApiImplementedInterface));
			}

			@Override
			public void visit(Iterator<JApiField> iterator, JApiField jApiField) {
				builder.add(signs(jApiField));
			}

			@Override
			public void visit(Iterator<JApiAnnotation> iterator, JApiAnnotation jApiAnnotation) {
				builder.add(signs(jApiAnnotation));
			}

			@Override
			public void visit(JApiSuperclass jApiSuperclass) {
				builder.add(signs(jApiSuperclass));
			}
		});
		ImmutableSet<SemverStatus> build = builder.build();
		if (build.contains(SemverStatus.CHANGED_BINARY_INCOMPATIBLE)) {
			return "1.0.0";
		} else if (build.contains(SemverStatus.CHANGED_BINARY_COMPATIBLE)) {
			return "0.1.0";
		} else if (build.contains(SemverStatus.UNCHANGED)) {
			return "0.0.1";
		} else if (build.isEmpty()) {
			return "0.0.0";
		} else {
			return "N/A";
		}
	}

	private SemverStatus signs(JApiHasChangeStatus hasChangeStatus) {
		JApiChangeStatus changeStatus = hasChangeStatus.getChangeStatus();
		switch (changeStatus) {
			case UNCHANGED:
				return SemverStatus.UNCHANGED;
			case NEW:
			case REMOVED:
			case MODIFIED:
				if (hasChangeStatus instanceof JApiCompatibility) {
					JApiCompatibility binaryCompatibility = (JApiCompatibility) hasChangeStatus;
					if (binaryCompatibility.isBinaryCompatible()) {
						if (hasChangeStatus instanceof JApiHasAccessModifier) {
							JApiHasAccessModifier jApiHasAccessModifier = (JApiHasAccessModifier) hasChangeStatus;
							if (isNotPrivate(jApiHasAccessModifier)) {
								if (jApiHasAccessModifier instanceof JApiClass) {
									JApiClass jApiClass = (JApiClass) jApiHasAccessModifier;
									if (jApiClass.isChangeCausedByClassElement()) {
										return SemverStatus.UNCHANGED;
									} else {
										return SemverStatus.CHANGED_BINARY_COMPATIBLE;
									}
								}
								return SemverStatus.CHANGED_BINARY_COMPATIBLE;
							} else {
								return SemverStatus.UNCHANGED;
							}
						} else {
							return SemverStatus.CHANGED_BINARY_COMPATIBLE;
						}
					} else {
						if (hasChangeStatus instanceof JApiHasAccessModifier) {
							JApiHasAccessModifier jApiHasAccessModifier = (JApiHasAccessModifier) hasChangeStatus;
							if (isNotPrivate(jApiHasAccessModifier)) {
								return SemverStatus.CHANGED_BINARY_INCOMPATIBLE;
							} else {
								return SemverStatus.CHANGED_BINARY_COMPATIBLE;
							}
						} else {
							return SemverStatus.CHANGED_BINARY_INCOMPATIBLE;
						}
					}
				} else {
					throw new IllegalStateException("Element '" + hasChangeStatus.getClass().getCanonicalName() + " does not implement '" + JApiCompatibility.class.getCanonicalName() + "'.");
				}
			default:
				throw new IllegalStateException("The following JApiChangeStatus is not supported: " + (changeStatus == null ? "null" : changeStatus.name()));
		}
	}
}
