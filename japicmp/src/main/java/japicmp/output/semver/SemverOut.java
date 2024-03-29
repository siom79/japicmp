package japicmp.output.semver;

import com.google.common.collect.ImmutableSet;
import japicmp.config.Options;
import japicmp.model.*;
import japicmp.output.Filter;
import japicmp.output.OutputGenerator;
import japicmp.util.ModifierHelper;

import java.util.Iterator;
import java.util.List;

public class SemverOut extends OutputGenerator<String> {

	public static final String SEMVER_MAJOR = "1.0.0";
	public static final String SEMVER_MINOR = "0.1.0";
	public static final String SEMVER_PATCH = "0.0.1";
	public static final String SEMVER_COMPATIBLE = "0.0.0";

	private Listener m_Listener = Listener.NULL;

	public SemverOut(Options options, List<JApiClass> jApiClasses) {
		this(options, jApiClasses, Listener.NULL);
	}

	public SemverOut(Options options, List<JApiClass> jApiClasses, Listener listener) {
		super(options, jApiClasses);
		m_Listener = listener == null ? Listener.NULL: listener;
	}

	@Override
	public String generate() {
		final ImmutableSet.Builder<JApiSemanticVersionLevel> builder = ImmutableSet.builder();
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
		ImmutableSet<JApiSemanticVersionLevel> build = builder.build();
		if (build.contains(JApiSemanticVersionLevel.MAJOR)) {
			return SEMVER_MAJOR;
		} else if (build.contains(JApiSemanticVersionLevel.MINOR)) {
			return SEMVER_MINOR;
		} else if (build.contains(JApiSemanticVersionLevel.PATCH)) {
			return SEMVER_PATCH;
		} else if (build.isEmpty()) {
			return SEMVER_COMPATIBLE;
		} else {
			return "N/A";
		}
	}

	private JApiSemanticVersionLevel signs(JApiCompatibility jApiCompatibility) {
		JApiSemanticVersionLevel ret = signsPriv(jApiCompatibility);
		m_Listener.onChange(jApiCompatibility, ret);
		return ret;
	}

	private JApiSemanticVersionLevel signsPriv(JApiCompatibility jApiCompatibility) {
		JApiSemanticVersionLevel semanticVersionLevel = JApiSemanticVersionLevel.PATCH;
		List<JApiCompatibilityChange> compatibilityChanges = jApiCompatibility.getCompatibilityChanges();
		for (JApiCompatibilityChange change : compatibilityChanges) {
			if (change.getSemanticVersionLevel().getLevel() > semanticVersionLevel.getLevel()) {
				semanticVersionLevel = change.getSemanticVersionLevel();
			}
		}


		if (jApiCompatibility instanceof JApiHasAccessModifier) {
			JApiHasAccessModifier jApiHasAccessModifier = (JApiHasAccessModifier) jApiCompatibility;
			if (ModifierHelper.matchesModifierLevel(jApiHasAccessModifier, AccessModifier.PUBLIC)
				|| ModifierHelper.matchesModifierLevel(jApiHasAccessModifier, AccessModifier.PROTECTED)) {
				return semanticVersionLevel;
			} else {
				return JApiSemanticVersionLevel.PATCH;
			}
		} else {
			return semanticVersionLevel;
		}
	}

	public interface Listener {

		void onChange(JApiCompatibility change, JApiSemanticVersionLevel semanticVersionLevel);

		Listener NULL = new Listener() {
			public void onChange(JApiCompatibility change, JApiSemanticVersionLevel semanticVersionLevel)
			{
				// Empty
			}
		};
	}

}
