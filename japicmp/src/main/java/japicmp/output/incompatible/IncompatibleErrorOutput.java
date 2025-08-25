/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package japicmp.output.incompatible;

import com.google.common.base.Joiner;
import japicmp.JApiCmp;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.filter.ClassFilter;
import japicmp.model.*;
import japicmp.output.Filter;
import japicmp.output.OutputGenerator;
import japicmp.output.semver.SemverOut;
import japicmp.versioning.SemanticVersion;
import japicmp.versioning.VersionChange;
import javassist.CtClass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IncompatibleErrorOutput extends OutputGenerator<Void> {

	private static final Logger LOGGER = Logger.getLogger(JApiCmp.class.getName());

	private final JarArchiveComparator jarArchiveComparator;

	public IncompatibleErrorOutput(Options options, List<JApiClass> jApiClasses, JarArchiveComparator jarArchiveComparator) {
		super(options, jApiClasses);
		this.jarArchiveComparator = jarArchiveComparator;
	}

	protected void warn(String msg, Throwable exception) {
		LOGGER.log(Level.WARNING, msg, exception);
	}

	protected void warn(String msg) {
		LOGGER.log(Level.WARNING, msg);
	}

	protected void info(String msg) {
		LOGGER.log(Level.INFO, msg);
	}

	protected void debug(String msg) {
		LOGGER.log(Level.FINE, msg);
	}

	protected boolean isDebugEnabled() {
		return LOGGER.isLoggable(Level.FINE);
	}

	@Override
	public Void generate() {
		if (options.isErrorOnModifications()) {
			for (JApiClass jApiClass : jApiClasses) {
				if (jApiClass.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					throw new JApiCmpException(JApiCmpException.Reason.IncompatibleChange, String.format("There is at least one modified class: %s", jApiClass.getFullyQualifiedName()));
				}
			}
		}
		breakBuildIfNecessaryByApplyingFilter(jApiClasses, options, jarArchiveComparator);
		if (options.isErrorOnSemanticIncompatibility()) {
			boolean ignoreMissingOldVersion = options.isIgnoreMissingOldVersion();
			boolean ignoreMissingNewVersion = options.isIgnoreMissingNewVersion();
			List<SemanticVersion> oldVersions = new ArrayList<>();
			List<SemanticVersion> newVersions = new ArrayList<>();
			for (JApiCmpArchive file : options.getOldArchives()) {
				Optional<SemanticVersion> semanticVersion = file.getVersion().getSemanticVersion();
				semanticVersion.ifPresent(oldVersions::add);
			}
			for (JApiCmpArchive file : options.getNewArchives()) {
				Optional<SemanticVersion> semanticVersion = file.getVersion().getSemanticVersion();
				semanticVersion.ifPresent(newVersions::add);
			}
			VersionChange versionChange = new VersionChange(oldVersions, newVersions, ignoreMissingOldVersion, ignoreMissingNewVersion);
			if (!versionChange.isAllMajorVersionsZero() || options.isErrorOnSemanticIncompatibilityForMajorVersionZero()) {
				Optional<SemanticVersion.ChangeType> changeTypeOptional = versionChange.computeChangeType();
				if (changeTypeOptional.isPresent()) {
					final SemanticVersion.ChangeType changeType = changeTypeOptional.get();

					SemverOut semverOut = new SemverOut(options, jApiClasses, (change, semanticVersionLevel) -> {
                        switch(semanticVersionLevel) {
                        case MAJOR:
                            if (changeType.ordinal() > SemanticVersion.ChangeType.MAJOR.ordinal()) {
                                warn("Incompatibility detected: Requires semantic version level " + semanticVersionLevel + ": " + change);
                            }
                            break;
                        case MINOR:
                            if (changeType.ordinal() > SemanticVersion.ChangeType.MINOR.ordinal()) {
                                warn("Incompatibility detected: Requires semantic version level	 " + semanticVersionLevel + ": " + change);
                            }
                            break;
                        case PATCH:
                            if (changeType.ordinal() > SemanticVersion.ChangeType.PATCH.ordinal()) {
                                warn("Incompatibility detected: Requires semantic version level	 " + semanticVersionLevel + ": " + change);
                            }
                            break;
                        default:
                            // Ignore
                        }
                    });

					String semver = semverOut.generate();
					if (changeType == SemanticVersion.ChangeType.MINOR && semver.equals(SemverOut.SEMVER_MAJOR)) {
						throw new JApiCmpException(JApiCmpException.Reason.IncompatibleChange, "Versions of archives indicate a minor change but binary incompatible changes found.");
					}
					if (changeType == SemanticVersion.ChangeType.PATCH && semver.equals(SemverOut.SEMVER_MAJOR)) {
						throw new JApiCmpException(JApiCmpException.Reason.IncompatibleChange, "Versions of archives indicate a patch change but binary incompatible changes found.");
					}
					if (changeType == SemanticVersion.ChangeType.PATCH && semver.equals(SemverOut.SEMVER_MINOR)) {
						throw new JApiCmpException(JApiCmpException.Reason.IncompatibleChange, "Versions of archives indicate a patch change but binary compatible changes found.");
					}
					if (changeType == SemanticVersion.ChangeType.UNCHANGED && semver.equals(SemverOut.SEMVER_MAJOR)) {
						throw new JApiCmpException(JApiCmpException.Reason.IncompatibleChange, "Versions of archives indicate no API changes but binary incompatible changes found.");
					}
					if (changeType == SemanticVersion.ChangeType.UNCHANGED && semver.equals(SemverOut.SEMVER_MINOR)) {
						throw new JApiCmpException(JApiCmpException.Reason.IncompatibleChange, "Versions of archives indicate no API changes but binary compatible changes found.");
					}
					if (changeType == SemanticVersion.ChangeType.UNCHANGED && semver.equals(SemverOut.SEMVER_PATCH)) {
						throw new JApiCmpException(JApiCmpException.Reason.IncompatibleChange, "Versions of archives indicate no API changes but found API changes.");
					}
				} else {
					if (isDebugEnabled()) {
						Joiner joiner = Joiner.on(';');
						debug("No change type available for old version(s) " + joiner.join(oldVersions) + " and new version(s) " + joiner.join(newVersions) + ".");
					}
				}
			} else {
				info("Skipping semantic version check because all major versions are zero (see http://semver.org/#semantic-versioning-specification-semver, section 4).");
			}
		}
		return null;
	}


	private static class BreakBuildResult {
		private final boolean breakBuildOnBinaryIncompatibleModifications;
		private final boolean breakBuildOnSourceIncompatibleModifications;
		boolean binaryIncompatibleChanges = false;
		boolean sourceIncompatibleChanges = false;

		public BreakBuildResult(boolean breakBuildOnBinaryIncompatibleModifications, boolean breakBuildOnSourceIncompatibleModifications) {
			this.breakBuildOnBinaryIncompatibleModifications = breakBuildOnBinaryIncompatibleModifications;
			this.breakBuildOnSourceIncompatibleModifications = breakBuildOnSourceIncompatibleModifications;
		}

		public boolean breakTheBuild() {
			return binaryIncompatibleChanges && this.breakBuildOnBinaryIncompatibleModifications ||
				sourceIncompatibleChanges && this.breakBuildOnSourceIncompatibleModifications;
		}
	}

	private String methodParameterToList(JApiBehavior jApiMethod) {
		StringBuilder sb = new StringBuilder();
		for (JApiParameter jApiParameter : jApiMethod.getParameters()) {
			if (sb.length() > 0) {
				sb.append(',');
			}
			sb.append(jApiParameter.getType());
		}
		return sb.toString();
	}

	void breakBuildIfNecessaryByApplyingFilter(List<JApiClass> jApiClasses, final Options options,
											   final JarArchiveComparator jarArchiveComparator) {
		final StringBuilder sb = new StringBuilder();
		final BreakBuildResult breakBuildResult = new BreakBuildResult(options.isErrorOnBinaryIncompatibility(),
			options.isErrorOnSourceIncompatibility());
		final boolean breakBuildIfCausedByExclusion = options.isErrorOnExclusionIncompatibility();
		Filter.filter(jApiClasses, new Filter.FilterVisitor() {
			@Override
			public void visit(Iterator<JApiClass> iterator, JApiClass jApiClass) {
				for (JApiCompatibilityChange change : jApiClass.getCompatibilityChanges()) {
					if (!change.isBinaryCompatible() || !change.isSourceCompatible()) {
						if (!change.isBinaryCompatible()) {
							breakBuildResult.binaryIncompatibleChanges = true;
						}
						if (!change.isSourceCompatible()) {
							breakBuildResult.sourceIncompatibleChanges = true;
						}
						if (sb.length() > 1) {
							sb.append(',');
						}
						sb.append(jApiClass.getFullyQualifiedName()).append(":").append(change.getType().name());
					}
				}
			}

			@Override
			public void visit(Iterator<JApiMethod> iterator, JApiMethod jApiMethod) {
				final List<JApiCompatibilityChange> changes = Stream.concat(
						jApiMethod.getCompatibilityChanges().stream(),
						jApiMethod.getReturnType().getCompatibilityChanges().stream()
					)
					.collect(Collectors.toList());

				for (JApiCompatibilityChange change : changes) {
					if (!change.isBinaryCompatible() || !change.isSourceCompatible()) {
						if (!change.isBinaryCompatible() && breakBuildIfCausedByExclusion(jApiMethod)) {
							breakBuildResult.binaryIncompatibleChanges = true;
						}
						if (!change.isSourceCompatible() && breakBuildIfCausedByExclusion(jApiMethod)) {
							breakBuildResult.sourceIncompatibleChanges = true;
						}
						if (sb.length() > 1) {
							sb.append(',');
						}
						sb.append(jApiMethod.getjApiClass().getFullyQualifiedName()).append(".")
							.append(jApiMethod.getName()).append("(").append(methodParameterToList(jApiMethod))
							.append(")").append(":").append(change.getType().name());
					}
				}
			}

			private boolean breakBuildIfCausedByExclusion(JApiMethod jApiMethod) {
				if (!breakBuildIfCausedByExclusion) {
					JApiReturnType returnType = jApiMethod.getReturnType();
					String oldType = returnType.getOldReturnType();
					try {
						Optional<CtClass> ctClassOptional = jarArchiveComparator.loadClass(JarArchiveComparator.ArchiveType.OLD, oldType);
						if (ctClassOptional.isPresent()) {
							if (classExcluded(ctClassOptional.get())) {
								return false;
							}
						}
					} catch (Exception e) {
						warn("Failed to load class " + oldType + ": " + e.getMessage(), e);
					}
					String newType = returnType.getNewReturnType();
					try {
						Optional<CtClass> ctClassOptional = jarArchiveComparator.loadClass(JarArchiveComparator.ArchiveType.NEW, newType);
						if (ctClassOptional.isPresent()) {
							if (classExcluded(ctClassOptional.get())) {
								return false;
							}
						}
					} catch (Exception e) {
						warn("Failed to load class " + newType + ": " + e.getMessage(), e);
					}
				}
				return true;
			}

			@Override
			public void visit(Iterator<JApiConstructor> iterator, JApiConstructor jApiConstructor) {
				for (JApiCompatibilityChange change : jApiConstructor.getCompatibilityChanges()) {
					if (!change.isBinaryCompatible() || !change.isSourceCompatible()) {
						if (!change.isBinaryCompatible()) {
							breakBuildResult.binaryIncompatibleChanges = true;
						}
						if (!change.isSourceCompatible()) {
							breakBuildResult.sourceIncompatibleChanges = true;
						}
						if (sb.length() > 1) {
							sb.append(',');
						}
						sb.append(jApiConstructor.getjApiClass().getFullyQualifiedName()).append(".")
							.append(jApiConstructor.getName()).append("(").append(methodParameterToList(jApiConstructor))
							.append(")").append(":").append(change.getType().name());
					}
				}
			}

			@Override
			public void visit(Iterator<JApiImplementedInterface> iterator, JApiImplementedInterface jApiImplementedInterface) {
				for (JApiCompatibilityChange change : jApiImplementedInterface.getCompatibilityChanges()) {
					if (!change.isBinaryCompatible() || !change.isSourceCompatible()) {
						if (!change.isBinaryCompatible() && breakBuildIfCausedByExclusion(jApiImplementedInterface)) {
							breakBuildResult.binaryIncompatibleChanges = true;
						}
						if (!change.isSourceCompatible() && breakBuildIfCausedByExclusion(jApiImplementedInterface)) {
							breakBuildResult.sourceIncompatibleChanges = true;
						}
						if (sb.length() > 1) {
							sb.append(',');
						}
						sb.append(jApiImplementedInterface.getFullyQualifiedName()).append("[")
							.append(jApiImplementedInterface.getFullyQualifiedName()).append("]")
							.append(":").append(change.getType().name());
					}
				}
			}

			private boolean breakBuildIfCausedByExclusion(JApiImplementedInterface jApiImplementedInterface) {
				if (!breakBuildIfCausedByExclusion) {
					CtClass ctClass = jApiImplementedInterface.getCtClass();
                    return !classExcluded(ctClass);
				}
				return true;
			}

			@Override
			public void visit(Iterator<JApiField> iterator, JApiField jApiField) {
				for (JApiCompatibilityChange change : jApiField.getCompatibilityChanges()) {
					if (!change.isBinaryCompatible() || !change.isSourceCompatible()) {
						if (!change.isBinaryCompatible() && breakBuildIfCausedByExclusion(jApiField)) {
							breakBuildResult.binaryIncompatibleChanges = true;
						}
						if (!change.isSourceCompatible() && breakBuildIfCausedByExclusion(jApiField)) {
							breakBuildResult.sourceIncompatibleChanges = true;
						}
						if (sb.length() > 1) {
							sb.append(',');
						}
						sb.append(jApiField.getjApiClass().getFullyQualifiedName()).append(".")
							.append(jApiField.getName()).append(":").append(change.getType().name());
					}
				}
			}

			private boolean breakBuildIfCausedByExclusion(JApiField jApiField) {
				if (!breakBuildIfCausedByExclusion) {
					JApiType type = jApiField.getType();
					Optional<String> oldTypeOptional = type.getOldTypeOptional();
					if (oldTypeOptional.isPresent()) {
						String oldType = oldTypeOptional.get();
						try {
							Optional<CtClass> ctClassOptional = jarArchiveComparator.loadClass(JarArchiveComparator.ArchiveType.OLD, oldType);
							if (ctClassOptional.isPresent()) {
								if (classExcluded(ctClassOptional.get())) {
									return false;
								}
							}
						} catch (Exception e) {
							warn("Failed to load class " + oldType + ": " + e.getMessage(), e);
						}
					}
					Optional<String> newTypeOptional = type.getNewTypeOptional();
					if (newTypeOptional.isPresent()) {
						String newType = newTypeOptional.get();
						try {
							Optional<CtClass> ctClassOptional = jarArchiveComparator.loadClass(JarArchiveComparator.ArchiveType.NEW, newType);
							if (ctClassOptional.isPresent()) {
								if (classExcluded(ctClassOptional.get())) {
									return false;
								}
							}
						} catch (Exception e) {
							warn("Failed to load class " + newType + ": " + e.getMessage(), e);
						}
					}
				}
				return true;
			}

			@Override
			public void visit(Iterator<JApiAnnotation> iterator, JApiAnnotation jApiAnnotation) {
				//no incompatible changes
			}

			@Override
			public void visit(JApiSuperclass jApiSuperclass) {
				for (JApiCompatibilityChange change : jApiSuperclass.getCompatibilityChanges()) {
					if (!change.isBinaryCompatible() || !change.isSourceCompatible()) {
						if (!change.isBinaryCompatible() && breakBuildIfCausedByExclusion(jApiSuperclass)) {
							breakBuildResult.binaryIncompatibleChanges = true;
						}
						if (!change.isSourceCompatible() && breakBuildIfCausedByExclusion(jApiSuperclass)) {
							breakBuildResult.sourceIncompatibleChanges = true;
						}
						if (sb.length() > 1) {
							sb.append(',');
						}
						sb.append(jApiSuperclass.getJApiClassOwning().getFullyQualifiedName()).append(":")
							.append(change.getType().name());
					}
				}
			}

			private boolean breakBuildIfCausedByExclusion(JApiSuperclass jApiSuperclass) {
				if (!breakBuildIfCausedByExclusion) {
					Optional<CtClass> oldSuperclassOptional = jApiSuperclass.getOldSuperclass();
					if (oldSuperclassOptional.isPresent()) {
						CtClass ctClass = oldSuperclassOptional.get();
						if (classExcluded(ctClass)) {
							return false;
						}
					}
					Optional<CtClass> newSuperclassOptional = jApiSuperclass.getNewSuperclass();
					if (newSuperclassOptional.isPresent()) {
						CtClass ctClass = newSuperclassOptional.get();
                        return !classExcluded(ctClass);
					}
				}
				return true;
			}

			private boolean classExcluded(CtClass ctClass) {
				List<japicmp.filter.Filter> excludes = options.getExcludes();
				for (japicmp.filter.Filter exclude : excludes) {
					if (exclude instanceof ClassFilter) {
						ClassFilter classFilter = (ClassFilter) exclude;
						if (classFilter.matches(ctClass)) {
							return true;
						}
					}
				}
				return false;
			}
		});
		if (breakBuildResult.breakTheBuild()) {
			throw new JApiCmpException(JApiCmpException.Reason.IncompatibleChange, String.format("There is at least one incompatibility: %s", sb));
		}
	}
}
