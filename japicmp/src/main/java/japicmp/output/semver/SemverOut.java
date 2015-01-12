package japicmp.output.semver;

import java.util.List;

import com.google.common.collect.ImmutableSet;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiAnnotation;
import japicmp.model.JApiAnnotationElement;
import japicmp.model.JApiBinaryCompatibility;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiConstructor;
import japicmp.model.JApiField;
import japicmp.model.JApiHasAnnotations;
import japicmp.model.JApiHasChangeStatus;
import japicmp.model.JApiImplementedInterface;
import japicmp.model.JApiMethod;
import japicmp.model.JApiSuperclass;
import japicmp.output.OutputFilter;
import japicmp.output.OutputGenerator;

public class SemverOut extends OutputGenerator {

	public SemverOut(Options options, List<JApiClass> jApiClasses) {
		super(options, jApiClasses);
	}

	@Override
	public void generate() {
		System.err.println(value());
	}

	public String value() {
		options.setOutputOnlyModifications(true);
		options.setAccessModifier(AccessModifier.PUBLIC);

		return generate(jApiClasses);
	}

	public String generate(List<JApiClass> jApiClasses) {
		OutputFilter outputFilter = new OutputFilter(options);
		outputFilter.filter(jApiClasses);

		ImmutableSet.Builder<SemverStatus> builder = ImmutableSet.builder();
		for (JApiClass jApiClass : jApiClasses) {
			builder.addAll(processClass(jApiClass));
			builder.addAll(processConstructors(jApiClass));
			builder.addAll(processMethods(jApiClass));
			builder.addAll(processAnnotations(jApiClass));
		}
		ImmutableSet<SemverStatus> build = builder.build();
		if (build.contains(SemverStatus.MAJOR)) {
			return "1.0.0";
		} else if (build.contains(SemverStatus.MINOR)) {
			return "0.1.0";
		} else if (build.isEmpty() || build.contains(SemverStatus.PATCH)) {
			return "0.0.1";
		} else {
			return "N/A";
		}

	}

	private ImmutableSet<SemverStatus> processAnnotations(JApiHasAnnotations jApiClass) {
		List<JApiAnnotation> annotations = jApiClass.getAnnotations();
		ImmutableSet.Builder<SemverStatus> builder = ImmutableSet.builder();
		for (JApiAnnotation jApiAnnotation : annotations) {
			builder.add(signs(jApiAnnotation));
			List<JApiAnnotationElement> elements = jApiAnnotation.getElements();
			for (JApiAnnotationElement jApiAnnotationElement : elements) {
				builder.add(signs(jApiAnnotationElement));
			}
		}
		return builder.build();
	}

	private ImmutableSet<SemverStatus> processConstructors(JApiClass jApiClass) {
		List<JApiConstructor> constructors = jApiClass.getConstructors();
		ImmutableSet.Builder<SemverStatus> builder = ImmutableSet.builder();
		for (JApiConstructor jApiConstructor : constructors) {
			builder.add(signs(jApiConstructor));
			builder.addAll(processAnnotations(jApiConstructor));
		}
		return builder.build();
	}

	private ImmutableSet<SemverStatus> processMethods(JApiClass jApiClass) {
		ImmutableSet.Builder<SemverStatus> builder = ImmutableSet.builder();
		List<JApiMethod> methods = jApiClass.getMethods();
		for (JApiMethod jApiMethod : methods) {
			builder.add(signs(jApiMethod));
			builder.addAll(processAnnotations(jApiMethod));
		}
		return builder.build();
	}

	private ImmutableSet<SemverStatus> processClass(JApiClass jApiClass) {
		ImmutableSet.Builder<SemverStatus> builder = ImmutableSet.builder();
		// builder.add(signs(jApiClass)); // XXX why not
		builder.addAll(processInterfaceChanges(jApiClass));
		builder.add(processSuperclassChanges(jApiClass));
		builder.addAll(processFieldChanges(jApiClass));
		return builder.build();
	}

	static SemverStatus signs(JApiHasChangeStatus hasChangeStatus) {
		JApiChangeStatus changeStatus = hasChangeStatus.getChangeStatus();
		switch (changeStatus) {
		case UNCHANGED:
			return SemverStatus.PATCH;
		case NEW:
			return SemverStatus.MINOR;
		case REMOVED:
			return SemverStatus.MAJOR;
		case MODIFIED:
			if (hasChangeStatus instanceof JApiBinaryCompatibility) {
				JApiBinaryCompatibility binaryCompatibility = (JApiBinaryCompatibility) hasChangeStatus;
				if (binaryCompatibility.isBinaryCompatible()) {
					return SemverStatus.MINOR;
				} else {
					return SemverStatus.MAJOR;
				}
			}
		default:
			throw new IllegalStateException();
		}

	}

	private ImmutableSet<SemverStatus> processFieldChanges(JApiClass jApiClass) {
		List<JApiField> fields = jApiClass.getFields();
		ImmutableSet.Builder<SemverStatus> builder = ImmutableSet.builder();
		for (JApiField field : fields) {
			builder.add(signs(field));
			builder.addAll(processAnnotations(field));
		}
		return builder.build();
	}

	private SemverStatus processSuperclassChanges(JApiClass jApiClass) {
		JApiSuperclass jApiSuperclass = jApiClass.getSuperclass();
		return signs(jApiSuperclass);
	}

	private ImmutableSet<SemverStatus> processInterfaceChanges(JApiClass jApiClass) {
		List<JApiImplementedInterface> interfaces = jApiClass.getInterfaces();
		ImmutableSet.Builder<SemverStatus> builder = ImmutableSet.builder();
		for (JApiImplementedInterface implementedInterface : interfaces) {
			builder.add(signs(implementedInterface));
		}
		return builder.build();
	}

	static enum SemverStatus {
		MAJOR, MINOR, PATCH;
	}
}
