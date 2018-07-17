package japicmp.output.semver;

import static japicmp.util.ModifierHelper.isNotPrivate;

import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableSet;

import japicmp.config.Options;
import japicmp.model.JApiAnnotation;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibility;
import japicmp.model.JApiConstructor;
import japicmp.model.JApiField;
import japicmp.model.JApiHasAbstractModifier;
import japicmp.model.JApiHasAccessModifier;
import japicmp.model.JApiHasChangeStatus;
import japicmp.model.JApiImplementedInterface;
import japicmp.model.JApiMethod;
import japicmp.model.JApiSuperclass;
import japicmp.output.Filter;
import japicmp.output.OutputGenerator;
import japicmp.versioning.SemanticVersion.ChangeType;

public class SemverOut extends OutputGenerator<String>
{
	private boolean allowBinaryCompatibleElementsInPatch;
	private boolean allowNewAbstractElementsInMinor;

	public SemverOut(final Options options, final List<JApiClass> jApiClasses)
	{
		super(options, jApiClasses);
	}

	@Override
	public String generate()
	{
		final ImmutableSet.Builder<ChangeType> builder = ImmutableSet.builder();
		Filter.filter(jApiClasses, new Filter.FilterVisitor()
		{
			@Override
			public void visit(final Iterator<JApiClass> iterator, final JApiClass jApiClass)
			{
				builder.add(signs(jApiClass));
			}

			@Override
			public void visit(final Iterator<JApiMethod> iterator, final JApiMethod jApiMethod)
			{
				builder.add(signs(jApiMethod));
			}

			@Override
			public void visit(final Iterator<JApiConstructor> iterator, final JApiConstructor jApiConstructor)
			{
				builder.add(signs(jApiConstructor));
			}

			@Override
			public void visit(final Iterator<JApiImplementedInterface> iterator,
					final JApiImplementedInterface jApiImplementedInterface)
			{
				builder.add(signs(jApiImplementedInterface));
			}

			@Override
			public void visit(final Iterator<JApiField> iterator, final JApiField jApiField)
			{
				builder.add(signs(jApiField));
			}

			@Override
			public void visit(final Iterator<JApiAnnotation> iterator, final JApiAnnotation jApiAnnotation)
			{
				builder.add(signs(jApiAnnotation));
			}

			@Override
			public void visit(final JApiSuperclass jApiSuperclass)
			{
				builder.add(signs(jApiSuperclass));
			}
		});
		ImmutableSet<ChangeType> build = builder.build();
		if(build.contains(ChangeType.MAJOR))
		{
			return "1.0.0";
		}
		else if(build.contains(ChangeType.MINOR))
		{
			return "0.1.0";
		}
		else if(build.contains(ChangeType.UNCHANGED))
		{
			return "0.0.1";
		}
		else if(build.isEmpty())
		{
			return "0.0.0";
		}
		else
		{
			return "N/A";
		}
	}

	private ChangeType signs(final JApiHasChangeStatus hasChangeStatus)
	{
		JApiChangeStatus changeStatus = hasChangeStatus.getChangeStatus();
		switch(changeStatus)
		{
			case UNCHANGED:
				return ChangeType.UNCHANGED;
			case NEW:
			case REMOVED:
			case MODIFIED:
				if(hasChangeStatus instanceof JApiCompatibility)
				{
					JApiCompatibility binaryCompatibility = (JApiCompatibility)hasChangeStatus;
					if(binaryCompatibility.isBinaryCompatible())
					{
						if(hasChangeStatus instanceof JApiHasAccessModifier)
						{
							JApiHasAccessModifier jApiHasAccessModifier = (JApiHasAccessModifier)hasChangeStatus;
							if(isNotPrivate(jApiHasAccessModifier))
							{
								if(jApiHasAccessModifier instanceof JApiClass)
								{
									JApiClass jApiClass = (JApiClass)jApiHasAccessModifier;
									if(jApiClass.isChangeCausedByClassElement())
									{
										return ChangeType.UNCHANGED;
									}
									return getChangeTypeFromOptions(ChangeType.MINOR, hasChangeStatus);
								}
								return getChangeTypeFromOptions(ChangeType.MINOR, hasChangeStatus);
							}
							return ChangeType.UNCHANGED;
						}
						return getChangeTypeFromOptions(ChangeType.MINOR, hasChangeStatus);
					}
					if(hasChangeStatus instanceof JApiHasAccessModifier)
					{
						JApiHasAccessModifier jApiHasAccessModifier = (JApiHasAccessModifier)hasChangeStatus;
						if(isNotPrivate(jApiHasAccessModifier))
						{
							return getChangeTypeFromOptions(ChangeType.MAJOR, hasChangeStatus);
						}
						return getChangeTypeFromOptions(ChangeType.MINOR, hasChangeStatus);
					}
					return getChangeTypeFromOptions(ChangeType.MAJOR, hasChangeStatus);
				}
				throw new IllegalStateException("Element '"
					+ hasChangeStatus.getClass().getCanonicalName()
					+ " does not implement '"
					+ JApiCompatibility.class.getCanonicalName()
					+ "'.");
			default:
				throw new IllegalStateException("The following JApiChangeStatus is not supported: "
					+ (changeStatus == null ? "null" : changeStatus.name()));
		}
	}

	/**
	 * Get the chanage type from the options of the SemverOutput.<br>
	 * Evaluates the options {@link #isAllowBinaryCompatibleElementsInPatch()} and
	 * {@link #isAllowNewAbstractElementsInMinor()}
	 *
	 * @param changeType the change type
	 * @param hasChangeStatus the hasChangeStatus
	 * @return the resulting change type
	 */
	private ChangeType getChangeTypeFromOptions(final ChangeType changeType, final JApiHasChangeStatus hasChangeStatus)
	{
		switch(changeType)
		{
			case MINOR:
				if(hasChangeStatus instanceof JApiCompatibility)
				{
					JApiCompatibility comp = (JApiCompatibility)hasChangeStatus;
					if(comp.isBinaryCompatible() && isAllowBinaryCompatibleElementsInPatch())
					{
						return ChangeType.PATCH;
					}
					return ChangeType.MINOR;
				}
				break;
			case MAJOR:
				if(hasChangeStatus instanceof JApiHasAbstractModifier && isAllowNewAbstractElementsInMinor())
				{
					return ChangeType.MINOR;
				}
				break;
			default:
		}
		return changeType;
	}

	public boolean isAllowBinaryCompatibleElementsInPatch()
	{
		return allowBinaryCompatibleElementsInPatch;
	}

	public void setAllowBinaryCompatibleElementsInPatch(final boolean allowBinaryCompatibleElementsInPatch)
	{
		this.allowBinaryCompatibleElementsInPatch = allowBinaryCompatibleElementsInPatch;
	}

	public boolean isAllowNewAbstractElementsInMinor()
	{
		return allowNewAbstractElementsInMinor;
	}

	public void setAllowNewAbstractElementsInMinor(final boolean allowNewAbstractElementsInMinor)
	{
		this.allowNewAbstractElementsInMinor = allowNewAbstractElementsInMinor;
	}
}
