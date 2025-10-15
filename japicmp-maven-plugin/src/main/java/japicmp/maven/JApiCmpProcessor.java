package japicmp.maven;

import com.google.common.base.Joiner;
import japicmp.cli.JApiCli;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;
import japicmp.model.JApiCompatibilityChangeType;
import japicmp.model.JApiSemanticVersionLevel;
import japicmp.output.html.HtmlOutput;
import japicmp.output.html.HtmlOutputGenerator;
import japicmp.output.html.HtmlOutputGeneratorOptions;
import japicmp.output.incompatible.IncompatibleErrorOutput;
import japicmp.output.markdown.MarkdownOutputGenerator;
import japicmp.output.markdown.config.MarkdownOptions;
import japicmp.output.semver.SemverOut;
import japicmp.output.stdout.StdoutOutputGenerator;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.output.xml.XmlOutputGeneratorOptions;
import japicmp.versioning.SemanticVersion;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.maven.RepositoryUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;

/**
 * Core class for running japicmp against the specified project.
 */
public class JApiCmpProcessor {

	final MavenParameters mavenParameters;
	final PluginParameters pluginParameters;
	final Log log;

	private Options options;

	/**
	 * Constructs a japicmp processor.
	 *
	 * @param pluginParameters Plugin parameters
	 * @param mavenParameters  maven parameters
	 * @param log              maven log
	 */
	public JApiCmpProcessor(final PluginParameters pluginParameters,
							final MavenParameters mavenParameters,
							final Log log) {
		this.pluginParameters = pluginParameters;
		this.mavenParameters = mavenParameters;
		this.log = log;
	}

	/**
	 * Executes the japicmp processor.
	 *
	 * @return HTML Output (optional)
	 *
	 * @throws MojoExecutionException if an error occurs during execution
	 * @throws MojoFailureException   if an error occurs during processing
	 */
	public Optional<HtmlOutput> execute() throws MojoExecutionException, MojoFailureException {
		if (pluginParameters.skip()) {
			log.info("japicmp skipped.");
			return Optional.empty();
		}

		if (skipModule()) {
			return Optional.empty();
		}

		final Options options = getOptions();
		final JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(options);

		setUpClassPath(comparatorOptions);
		setUpOverrideCompatibilityChanges(comparatorOptions);

		final JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
		if (options.getNewArchives().isEmpty()) {
			log.warn("Skipping execution because no new version could be resolved/found.");
			return Optional.empty();
		}

		List<JApiClass> jApiClasses = jarArchiveComparator.compare(options.getOldArchives(),
				options.getNewArchives());
		try {
			PostAnalysisScriptExecutor postAnalysisScriptExecutor = new PostAnalysisScriptExecutor();
/*
			jApiClasses = postAnalysisScriptExecutor.apply(pluginParameters.parameter(), jApiClasses,
					log);
*/
			postAnalysisScriptExecutor.apply(pluginParameters.parameter(), jApiClasses, log);
			final File jApiCmpBuildDir = createJApiCmpBaseDir();
			final SemverOut semverOut = new SemverOut(options, jApiClasses);
			final String semanticVersioningInformation = semverOut.generate();

			if (!skipDiffReport()) {
				generateDiffOutput(options, jApiClasses, jApiCmpBuildDir, semanticVersioningInformation);
			}
			if (!skipMarkdownReport()) {
				generateMarkdownOutput(jApiClasses, jApiCmpBuildDir);
			}
			if (!skipXmlReport()) {
				generateXmlOutput(jApiClasses, jApiCmpBuildDir, semanticVersioningInformation);
			}

			Optional<HtmlOutput> retVal = Optional.empty();
			if (!skipHtmlReport()) {
				retVal = Optional.of(generateHtmlOutput(jApiClasses, jApiCmpBuildDir,
						semanticVersioningInformation));
			}

			breakBuildIfNecessary(jApiClasses, pluginParameters.parameter(), options,
					jarArchiveComparator);
			return retVal;
		} catch (IOException e) {
			throw new MojoFailureException(
					String.format("Failed to construct output directory: %s", e.getMessage()), e);
		}
	}

	/**
	 * @param comparatorOptions
	 *
	 * @throws MojoFailureException
	 */
	private void setUpOverrideCompatibilityChanges(
			final JarArchiveComparatorOptions comparatorOptions)
			throws MojoFailureException {
		if (pluginParameters.parameter().getOverrideCompatibilityChangeParameters() != null) {
			final List<ConfigParameters.OverrideCompatibilityChangeParameter>
					overrideCompatibilityChangeParameters =
					pluginParameters.parameter().getOverrideCompatibilityChangeParameters();
			for (final ConfigParameters.OverrideCompatibilityChangeParameter configChange :
					overrideCompatibilityChangeParameters) {

				final String compatibilityChange = configChange.getCompatibilityChange();
				JApiCompatibilityChangeType foundChange = null;
				for (JApiCompatibilityChangeType change : JApiCompatibilityChangeType.values()) {
					if (change.name().equalsIgnoreCase(compatibilityChange)) {
						foundChange = change;
						break;
					}
				}
				if (foundChange == null) {
					throw new MojoFailureException("Unknown compatibility change '"
							+ compatibilityChange
							+ "'. Supported values: "
							+ Joiner.on(',')
							.join(JApiCompatibilityChangeType.values()));
				}

				final String semanticVersionLevel = configChange.getSemanticVersionLevel();
				JApiSemanticVersionLevel foundSemanticVersionLevel = foundChange.getSemanticVersionLevel();
				for (final JApiSemanticVersionLevel level : JApiSemanticVersionLevel.values()) {
					if (level.name().equalsIgnoreCase(semanticVersionLevel)) {
						foundSemanticVersionLevel = level;
						break;
					}
				}
				if (foundSemanticVersionLevel == null) {
					throw new MojoFailureException("Unknown semantic version level '"
							+ semanticVersionLevel
							+ "'. Supported values: "
							+ Joiner.on(',').join(
							JApiSemanticVersionLevel.values()));
				}

				comparatorOptions.addOverrideCompatibilityChange(
						new JarArchiveComparatorOptions.OverrideCompatibilityChange(foundChange,
								configChange.isBinaryCompatible(),
								configChange.isSourceCompatible(),
								foundSemanticVersionLevel));
			}
		}
	}

	/**
	 * A local enum for specifying old or new versions.
	 */
	enum ConfigurationVersion {
		OLD, NEW
	}

	/**
	 * @param mavenProject
	 * @param version
	 *
	 * @return
	 */
	private DefaultArtifact createDefaultArtifact(final MavenProject mavenProject,
												  final String version) {
		final org.apache.maven.artifact.Artifact artifact = mavenProject.getArtifact();
		return createDefaultArtifact(artifact.getGroupId(), artifact.getArtifactId(),
				artifact.getClassifier(), artifact.getType(), version);
	}

	/**
	 * @param groupId
	 * @param artifactId
	 * @param classifier
	 * @param type
	 * @param version
	 *
	 * @return
	 */
	private DefaultArtifact createDefaultArtifact(final String groupId,
												  final String artifactId,
												  final String classifier,
												  final String type,
												  final String version) {
		String mappedType = type;
		if ("bundle".equals(type) || "ejb".equals(type)) {
			mappedType = "jar";
		}
		return new DefaultArtifact(groupId, artifactId, classifier, mappedType, version);
	}

	/**
	 * Returns am artifact to compare.
	 *
	 * @return the found artifact
	 *
	 * @throws MojoFailureException   if a Mojo error occurs
	 * @throws MojoExecutionException if an execution error occurs
	 */
	private Artifact getComparisonArtifact()
			throws MojoFailureException, MojoExecutionException {
		final MavenProject mavenProject = mavenParameters.mavenProject();
		// create a version range of the form (,<current-version-of-the-project>), that includes all
		// versions up to this version
		final DefaultArtifact artifactVersionRange = createDefaultArtifact(mavenProject,
				mavenParameters.versionRangeWithProjectVersion());
		final VersionRangeRequest versionRangeRequest = new VersionRangeRequest(artifactVersionRange,
				mavenParameters.remoteRepos(),
				null);
		try {
			log.debug("Trying version range: " + versionRangeRequest);
			final VersionRangeResult versionRangeResult = mavenParameters.repoSystem().resolveVersionRange(
					mavenParameters.repoSession(), versionRangeRequest);
			log.debug("Version range result: " + versionRangeRequest);
			final List<org.eclipse.aether.version.Version> versions = versionRangeResult.getVersions();
			filterSnapshots(versions);
			filterVersionPattern(versions);
			log.debug("Version range after filtering: " + versions);
			if (!versions.isEmpty()) {
				final DefaultArtifact artifactVersion = createDefaultArtifact(mavenProject,
						versions.get(versions.size() - 1)
								.toString());
				final ArtifactRequest artifactRequest = new ArtifactRequest(artifactVersion,
						mavenParameters.remoteRepos(), null);
				final ArtifactResult artifactResult = mavenParameters.repoSystem().resolveArtifact(
						mavenParameters.repoSession(), artifactRequest);
				processArtifactResult(artifactVersion, artifactResult);
				return artifactResult.getArtifact();
			} else {
				if (ignoreMissingOldVersion(ConfigurationVersion.OLD)) {
					log.warn("Ignoring missing old artifact version: "
							+ artifactVersionRange.getGroupId()
							+ ":"
							+ artifactVersionRange.getArtifactId());
					return null;
				} else {
					throw new MojoFailureException("Could not find previous version for artifact: "
							+ artifactVersionRange.getGroupId()
							+ ":"
							+ artifactVersionRange.getArtifactId());
				}
			}
		} catch (final VersionRangeResolutionException | ArtifactResolutionException e) {
			log.error("Failed to retrieve comparison artifact: " + e.getMessage(), e);
			throw new MojoFailureException(e.getMessage(), e);
		}
	}

	/**
	 * @param artifactVersion
	 * @param artifactResult
	 *
	 * @throws MojoFailureException
	 */
	private void processArtifactResult(final DefaultArtifact artifactVersion,
									   final ArtifactResult artifactResult)
			throws MojoFailureException {
		if (artifactResult.getExceptions() != null && !artifactResult.getExceptions().isEmpty()) {
			final List<Exception> exceptions = artifactResult.getExceptions();
			for (Exception exception : exceptions) {
				log.debug(exception.getMessage(), exception);
			}
		}
		if (artifactResult.isMissing()) {
			if (ignoreMissingArtifact(ConfigurationVersion.OLD)) {
				log.warn("Ignoring missing artifact: " + artifactResult.getArtifact());
			} else {
				throw new MojoFailureException("Could not resolve artifact: " + artifactVersion);
			}
		}
	}

	/**
	 * @param availableVersions
	 *
	 * @throws MojoFailureException
	 */
	private void filterVersionPattern(
			final List<org.eclipse.aether.version.Version> availableVersions)
			throws MojoFailureException {
		if (pluginParameters.parameter().getOldVersionPattern() != null) {
			final String versionPattern = pluginParameters.parameter().getOldVersionPattern();
			Pattern pattern;
			try {
				pattern = Pattern.compile(versionPattern);
			} catch (PatternSyntaxException e) {
				throw new MojoFailureException("Could not compile provided versionPattern '"
						+ versionPattern
						+ "' as regular expression: "
						+ e.getMessage(), e);
			}
			for (final Iterator<org.eclipse.aether.version.Version> versionIterator =
				 availableVersions.iterator(); versionIterator.hasNext(); ) {
				final org.eclipse.aether.version.Version version = versionIterator.next();
				final Matcher matcher = pattern.matcher(version.toString());
				if (!matcher.matches()) {
					versionIterator.remove();
					log.debug("Filtering version '"
							+ version
							+ "' because it does not match configured versionPattern '"
							+ versionPattern
							+ "'.");
				}
			}
		} else {
			log.debug("Parameter <oldVersionPattern> not configured, i.e. no version filtered.");
		}
	}

	/**
	 * @param versions
	 */
	private void filterSnapshots(final List<org.eclipse.aether.version.Version> versions) {
		if (!pluginParameters.parameter().isIncludeSnapshots()) {
			versions.removeIf(
					version -> version.toString() != null && version.toString().endsWith("SNAPSHOT"));
		}
	}

	/**
	 * @param oldArchives
	 * @param newArchives
	 *
	 * @throws MojoFailureException
	 */
	private void populateArchivesListsFromParameters(final List<JApiCmpArchive> oldArchives,
													 final List<JApiCmpArchive> newArchives)
			throws MojoFailureException {
		if (pluginParameters.oldVersion() != null) {
			oldArchives.addAll(retrieveFileFromConfiguration(pluginParameters.oldVersion(), "oldVersion",
					ConfigurationVersion.OLD));
		}
		if (pluginParameters.oldVersions() != null) {
			for (final DependencyDescriptor dependencyDescriptor : pluginParameters.oldVersions()) {
				if (dependencyDescriptor != null) {
					oldArchives.addAll(retrieveFileFromConfiguration(dependencyDescriptor, "oldVersions",
							ConfigurationVersion.OLD));
				}
			}
		}
		if (pluginParameters.oldVersion() == null && pluginParameters.oldVersions() == null) {
			try {
				final Artifact comparisonArtifact = getComparisonArtifact();
				if (comparisonArtifact != null && comparisonArtifact.getVersion() != null) {
					Set<Artifact> artifacts = resolveArtifact(comparisonArtifact, ConfigurationVersion.OLD);
					for (Artifact artifact : artifacts) {
						final File file = artifact.getFile();
						if (file != null) {
							oldArchives.add(new JApiCmpArchive(file, guessVersion(file)));
						} else {
							handleMissingArtifactFile(artifact);
						}
					}
				}
			} catch (MojoExecutionException e) {
				throw new MojoFailureException(
						"Computing and resolving comparison artifact failed: " + e.getMessage(), e);
			}
		}
		if (pluginParameters.newVersion() != null) {
			newArchives.addAll(retrieveFileFromConfiguration(pluginParameters.newVersion(), "newVersion",
					ConfigurationVersion.NEW));
		}
		if (pluginParameters.newVersions() != null) {
			for (final DependencyDescriptor dependencyDescriptor : pluginParameters.newVersions()) {
				if (dependencyDescriptor != null) {
					newArchives.addAll(retrieveFileFromConfiguration(dependencyDescriptor, "newVersions",
							ConfigurationVersion.NEW));
				}
			}
		}
		if (pluginParameters.newVersion() == null && pluginParameters.newVersions() == null) {
			final MavenProject mavenProject = mavenParameters.mavenProject();
			if (mavenProject != null && mavenProject.getArtifact() != null) {
				final DefaultArtifact defaultArtifact = createDefaultArtifact(mavenProject,
						mavenProject.getVersion());
				final Set<Artifact> artifacts = resolveArtifact(defaultArtifact, ConfigurationVersion.NEW);
				for (Artifact artifact : artifacts) {
					final File file = artifact.getFile();
					if (file != null) {
						try (JarFile jarFile = new JarFile(file)) {
							log.debug("Could open file '"
									+ file.getAbsolutePath()
									+ "' of artifact as jar archive: "
									+ jarFile.getName());
							newArchives.add(new JApiCmpArchive(file, guessVersion(file)));
						} catch (IOException e) {
							log.warn("No new version specified and file '"
									+ file.getAbsolutePath()
									+ "' of artifact could not be opened as jar archive: "
									+ e.getMessage(), e);
						}
					} else {
						log.warn("Artifact "
								+ artifact
								+ " does not have a file. Cannot resolve artifact automatically.");
					}
				}
			}
		}
		if (oldArchives.isEmpty()) {
			String message =
					"Please provide at least one resolvable old version using one of the configuration "
							+ "elements <oldVersion/> or <oldVersions/>.";
			if (ignoreMissingArtifact(ConfigurationVersion.OLD)) {
				log.warn(message);
			} else {
				throw new MojoFailureException(message);
			}
		}
		if (newArchives.isEmpty()) {
			String message =
					"Please provide at least one resolvable new version using one of the configuration "
							+ "elements <newVersion/> or <newVersions/>.";
			if (ignoreMissingArtifact(ConfigurationVersion.NEW)) {
				log.warn(message);
			} else {
				throw new MojoFailureException(message);
			}
		}
	}

	/**
	 * @param jApiClasses
	 * @param parameterParam
	 * @param options
	 * @param jarArchiveComparator
	 *
	 * @throws MojoFailureException
	 * @throws MojoExecutionException
	 */
	void breakBuildIfNecessary(final List<JApiClass> jApiClasses,
							   final ConfigParameters parameterParam,
							   final Options options,
							   final JarArchiveComparator jarArchiveComparator)
			throws MojoFailureException, MojoExecutionException {
		if (breakBuildBasedOnSemanticVersioning(parameterParam)) {
			options.setErrorOnSemanticIncompatibility(true);
		}
		if (breakBuildBasedOnSemanticVersioningForMajorVersionZero(parameterParam)) {
			options.setErrorOnSemanticIncompatibilityForMajorVersionZero(true);
		}
		if (breakBuildOnBinaryIncompatibleModifications(parameterParam)) {
			options.setErrorOnBinaryIncompatibility(true);
		}
		if (breakBuildOnSourceIncompatibleModifications(parameterParam)) {
			options.setErrorOnSourceIncompatibility(true);
		}
		if (breakBuildOnModifications(parameterParam)) {
			options.setErrorOnModifications(true);
		}
		if (!parameterParam.isBreakBuildIfCausedByExclusion()) {
			options.setErrorOnExclusionIncompatibility(false);
		}
		if (pluginParameters.parameter().getIgnoreMissingOldVersion()) {
			options.setIgnoreMissingOldVersion(true);
		}
		if (pluginParameters.parameter().getIgnoreMissingNewVersion()) {
			options.setIgnoreMissingNewVersion(true);
		}

		IncompatibleErrorOutput errorOutput = new IncompatibleErrorOutput(options, jApiClasses,
				jarArchiveComparator) {

			@Override
			protected void warn(String msg, Throwable error) {
				log.warn(msg, error);
			}
		};
		try {
			errorOutput.generate();
		} catch (JApiCmpException e) {
			if (e.getReason() == JApiCmpException.Reason.IncompatibleChange) {
				throw new MojoFailureException(e.getMessage());
			} else {
				throw new MojoExecutionException("Error while checking for incompatible changes", e);
			}
		}
	}

	/**
	 * @return
	 *
	 * @throws MojoFailureException
	 */
	Options getOptions() throws MojoFailureException {
		if (this.options != null) {
			return this.options;
		}
		this.options = Options.newDefault();
		populateArchivesListsFromParameters(this.options.getOldArchives(),
				this.options.getNewArchives());
		ConfigParameters parameterParam = pluginParameters.parameter();
		if (parameterParam != null) {
			String accessModifierArg = parameterParam.getAccessModifier();
			if (accessModifierArg != null) {
				try {
					AccessModifier accessModifier = AccessModifier.valueOf(accessModifierArg.toUpperCase());
					this.options.setAccessModifier(accessModifier);
				} catch (IllegalArgumentException e) {
					throw new MojoFailureException(
							String.format(
									"Invalid value for option accessModifier: %s. Possible values are: %s.",
									accessModifierArg, AccessModifier.listOfAccessModifier()), e);
				}
			}
			this.options.setOutputOnlyBinaryIncompatibleModifications(
					parameterParam.getOnlyBinaryIncompatible());
			this.options.setOutputOnlyModifications(parameterParam.getOnlyModified());

			List<String> excludes = parameterParam.getExcludes();
			if (excludes != null) {
				for (String exclude : excludes) {
					this.options.addExcludeFromArgument(Optional.ofNullable(exclude),
							parameterParam.isExcludeExclusively());
				}
			}
			List<String> includes = parameterParam.getIncludes();
			if (includes != null) {
				for (String include : includes) {
					this.options.addIncludeFromArgument(Optional.ofNullable(include),
							parameterParam.isIncludeExlusively());
				}
			}

			this.options.setIncludeSynthetic(parameterParam.getIncludeSynthetic());
			this.options.setIgnoreMissingClasses(parameterParam.getIgnoreMissingClasses());

			List<String> ignoreMissingClassesByRegularExpressions =
					parameterParam.getIgnoreMissingClassesByRegularExpressions();
			if (ignoreMissingClassesByRegularExpressions != null) {
				for (String ignoreMissingClassRegularExpression :
						ignoreMissingClassesByRegularExpressions) {
					this.options.addIgnoreMissingClassRegularExpression(ignoreMissingClassRegularExpression);
				}
			}
			String htmlStylesheet = parameterParam.getHtmlStylesheet();
			if (htmlStylesheet != null) {
				this.options.setHtmlStylesheet(Optional.of(htmlStylesheet));
			}
			this.options.setNoAnnotations(parameterParam.getNoAnnotations());
			this.options.setReportOnlyFilename(parameterParam.isReportOnlyFilename());
			this.options.setReportOnlySummary(parameterParam.isReportOnlySummary());
		}
		return this.options;
	}

	/**
	 * @param params
	 *
	 * @return
	 */
	boolean breakBuildOnModifications(final ConfigParameters params) {
		return pluginParameters.breakBuild().onModifications()
				| params.getBreakBuildOnModifications();
	}

	/**
	 * @param params
	 *
	 * @return
	 */
	boolean breakBuildOnBinaryIncompatibleModifications(final ConfigParameters params) {
		return pluginParameters.breakBuild().onBinaryIncompatibleModifications()
				| params.getBreakBuildOnBinaryIncompatibleModifications();
	}

	/**
	 * @param params
	 *
	 * @return
	 */
	boolean breakBuildOnSourceIncompatibleModifications(final ConfigParameters params) {
		return pluginParameters.breakBuild().onSourceIncompatibleModifications()
				| params.getBreakBuildOnSourceIncompatibleModifications();
	}

	/**
	 * @param params
	 *
	 * @return
	 */
	boolean breakBuildBasedOnSemanticVersioning(final ConfigParameters params) {
		return pluginParameters.breakBuild().onSemanticVersioning()
				| params.getBreakBuildBasedOnSemanticVersioning();
	}

	/**
	 * @param params
	 *
	 * @return
	 */
	boolean breakBuildBasedOnSemanticVersioningForMajorVersionZero(final ConfigParameters params) {
		return pluginParameters.breakBuild().onSemanticVersioningForMajorVersionZero()
				| params.isBreakBuildBasedOnSemanticVersioningForMajorVersionZero();
	}

	/**
	 * @return
	 *
	 * @throws MojoFailureException
	 */
	File createJApiCmpBaseDir() throws MojoFailureException {
		File baseDir;
		try {
			if (pluginParameters.outputDirectory() != null) {
				baseDir = pluginParameters.outputDirectory();
			} else {
				final File projectBuildDirParam = pluginParameters.projectBuildDir();
				baseDir = new File(
						projectBuildDirParam.getCanonicalPath() + File.separator + "japicmp");
			}

			boolean madeDir = true;
			if (!baseDir.exists()) {
				madeDir = baseDir.mkdirs();
			}

			if ((!madeDir || !baseDir.isDirectory()) || !baseDir.canWrite()) {
				throw new IOException("mkDirs failed");
			}
		} catch (IOException e) {
			throw new MojoFailureException("Failed to create output directory: " + e.getMessage(), e);
		}

		return baseDir;
	}

	/**
	 * @param options
	 * @param jApiClasses
	 * @param jApiCmpBuildDir
	 * @param semanticVersioningInformation
	 *
	 * @throws IOException
	 * @throws MojoFailureException
	 */
	private void generateDiffOutput(final Options options,
									final List<JApiClass> jApiClasses,
									final File jApiCmpBuildDir,
									final String semanticVersioningInformation)
			throws IOException, MojoFailureException {
		StdoutOutputGenerator stdoutOutputGenerator = new StdoutOutputGenerator(options, jApiClasses);
		String diffOutput = stdoutOutputGenerator.generate();
		diffOutput += "\nSemantic versioning suggestion: " + semanticVersioningInformation;
		File output = new File(
				jApiCmpBuildDir.getCanonicalPath() + File.separator + createFilename() + ".diff");
		writeToFile(diffOutput, output);
	}

	/**
	 * @param jApiClasses
	 * @param jApiCmpBuildDir
	 *
	 * @throws IOException
	 * @throws MojoFailureException
	 */
	private void generateMarkdownOutput(final List<JApiClass> jApiClasses, final File jApiCmpBuildDir)
			throws IOException, MojoFailureException {
		if (pluginParameters.isWriteToFiles()) {
			MarkdownOptions mdOptions = MarkdownOptions.newDefault(options);
			if (pluginParameters.parameter().getMarkdownTitle() != null) {
				mdOptions.title.report = pluginParameters.parameter().getMarkdownTitle();
			}
			MarkdownOutputGenerator mdOut = new MarkdownOutputGenerator(mdOptions, jApiClasses);
			String markdown = mdOut.generate();
			File output = new File(
					jApiCmpBuildDir.getCanonicalPath() + File.separator + createFilename() + ".md");
			writeToFile(markdown, output);
		}
	}

	/**
	 *
	 * @param jApiClasses
	 * @param jApiCmpBuildDir
	 * @param semanticVersioningInformation
	 *
	 * @throws IOException
	 */
	private void generateXmlOutput(final List<JApiClass> jApiClasses,
								   final File jApiCmpBuildDir,
								   final String semanticVersioningInformation)
			throws IOException {
		if (pluginParameters.isWriteToFiles()) {
			XmlOutput xmlOutput = createXmlOutput(jApiClasses, jApiCmpBuildDir,
					semanticVersioningInformation);
			List<File> filesWritten = XmlOutputGenerator.writeToFiles(options, xmlOutput);
			for (File file : filesWritten) {
				log.info("Created file '" + file.getAbsolutePath() + "'.");
			}
		}
	}

	/**
	 * @param jApiClasses
	 * @param jApiCmpBuildDir
	 * @param semanticVersioningInformation
	 *
	 * @return
	 *
	 * @throws IOException
	 */
	private XmlOutput createXmlOutput(final List<JApiClass> jApiClasses,
									  final File jApiCmpBuildDir,
									  final String semanticVersioningInformation)
			throws IOException {
		String filename = createFilename();
		options.setXmlOutputFile(
				Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + filename + ".xml"));
		XmlOutputGeneratorOptions xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
		xmlOutputGeneratorOptions.setCreateSchemaFile(true);
		xmlOutputGeneratorOptions.setSemanticVersioningInformation(semanticVersioningInformation);

		String optionalTitle = pluginParameters.parameter().getHtmlTitle();
		xmlOutputGeneratorOptions.setTitle(
				optionalTitle != null ? optionalTitle:options.getDifferenceDescription());

		XmlOutputGenerator xmlGenerator = new XmlOutputGenerator(jApiClasses, options,
				xmlOutputGeneratorOptions);
		return xmlGenerator.generate();
	}

	/**
	 *
	 * @param jApiClasses
	 * @param jApiCmpBuildDir
	 * @param semanticVersioningInformation
	 *
	 * @return
	 *
	 * @throws IOException
	 */
	private HtmlOutput generateHtmlOutput(final List<JApiClass> jApiClasses,
										  final File jApiCmpBuildDir,
										  final String semanticVersioningInformation)
			throws IOException {
		HtmlOutput htmlOutput = createHtmlOutput(jApiClasses, jApiCmpBuildDir,
				semanticVersioningInformation);
		if (pluginParameters.isWriteToFiles() && options.getHtmlOutputFile().isPresent()) {
			Path path = Paths.get(options.getHtmlOutputFile().get());
			Files.write(path, htmlOutput.getHtml().getBytes(StandardCharsets.UTF_8));
			log.info("Written file '" + path + "'.");
		}
		return htmlOutput;
	}

	/**
	 * @param jApiClasses
	 * @param jApiCmpBuildDir
	 * @param semanticVersioningInformation
	 *
	 * @return
	 *
	 * @throws IOException
	 */
	private HtmlOutput createHtmlOutput(final List<JApiClass> jApiClasses,
										final File jApiCmpBuildDir,
										final String semanticVersioningInformation)
			throws IOException {
		final String filename = createFilename();
		options.setHtmlOutputFile(
				Optional.of(jApiCmpBuildDir.getCanonicalPath() + File.separator + filename + ".html"));
		HtmlOutputGeneratorOptions htmlOutputGeneratorOptions = new HtmlOutputGeneratorOptions();
		htmlOutputGeneratorOptions.setSemanticVersioningInformation(semanticVersioningInformation);
		final String title = pluginParameters.parameter().getHtmlTitle();
		htmlOutputGeneratorOptions.setTitle(title != null ? title:options.getDifferenceDescription());

		final HtmlOutputGenerator htmlOutputGenerator = new HtmlOutputGenerator(jApiClasses, options,
				htmlOutputGeneratorOptions);
		return htmlOutputGenerator.generate();
	}

	/**
	 * @return
	 */
	boolean skipModule() {
		SkipModuleStrategy skipModuleStrategy = new SkipModuleStrategy(pluginParameters,
				mavenParameters, log);
		return skipModuleStrategy.skip();
	}

	/**
	 * Returns {@code true} if the generation of HTML reports should be skipped.
	 *
	 * @return {@code true} if the generation of HTML reports should be skipped
	 */
	boolean skipDiffReport() {
		return pluginParameters.skipReport().diffReport() |
				pluginParameters.parameter().skipDiffReport();
	}

	/**
	 * Returns {@code true} if the generation of HTML reports should be skipped.
	 *
	 * @return {@code true} if the generation of HTML reports should be skipped
	 */
	boolean skipHtmlReport() {
		return pluginParameters.skipReport().htmlReport() |
				pluginParameters.parameter().skipHtmlReport();
	}

	/**
	 * Returns {@code true} if the generation of Markdown reports should be skipped.
	 *
	 * @return {@code true} if the generation of Markdown reports should be skipped
	 */
	boolean skipMarkdownReport() {
		return pluginParameters.skipReport().markdownReport() |
				pluginParameters.parameter().skipMarkdownReport();
	}

	/**
	 * Returns {@code true} if the generation of XML reports should be skipped.
	 *
	 * @return {@code true} if the generation of XML reports should be skipped
	 */
	boolean skipXmlReport() {
		return pluginParameters.skipReport().xmlReport() |
				pluginParameters.parameter().skipXmlReport();
	}

	/**
	 * @return
	 */
	private String createFilename() {
		String filename = "japicmp";
		String executionId = mavenParameters.mojoExecution().getExecutionId();
		if (executionId != null && !"default".equals(executionId)) {
			filename = executionId;
		}
		StringBuilder sb = new StringBuilder();
		for (char c : filename.toCharArray()) {
			if (c == '.' || Character.isJavaIdentifierPart(c) || c == '-') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	/**
	 * @param comparatorOptions
	 *
	 * @throws MojoFailureException
	 */
	private void setUpClassPath(final JarArchiveComparatorOptions comparatorOptions)
			throws MojoFailureException {
		if (pluginParameters != null) {
			if (pluginParameters.dependencies() != null) {
				if (pluginParameters.oldClassPathDependencies() != null
						|| pluginParameters.newClassPathDependencies() != null) {
					throw new MojoFailureException(
							"Please specify either a <dependencies/> element or the two elements "
									+ "<oldClassPathDependencies/> and <newClassPathDependencies/>. "
									+
									"With <dependencies/> you can specify one common classpath for both versions and "
									+ "with <oldClassPathDependencies/> and <newClassPathDependencies/> a "
									+ "separate classpath for the new and old version.");
				} else {
					log.debug("Element <dependencies/> found. Using "
							+ JApiCli.ClassPathMode.ONE_COMMON_CLASSPATH);
					for (Dependency dependency : pluginParameters.dependencies()) {
						List<JApiCmpArchive> jApiCmpArchives = resolveDependencyToFile("dependencies",
								dependency,
								ConfigurationVersion.NEW);

						for (JApiCmpArchive jApiCmpArchive : jApiCmpArchives) {
							comparatorOptions.getClassPathEntries().add(
									jApiCmpArchive.getFile().getAbsolutePath());
						}
						comparatorOptions.setClassPathMode(
								JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH);
					}
				}
			} else {
				if (pluginParameters.oldClassPathDependencies() != null
						|| pluginParameters.newClassPathDependencies() != null) {
					log.debug("At least one of the elements <oldClassPathDependencies/> or "
							+ "<newClassPathDependencies/> found. Using "
							+ JApiCli.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
					if (pluginParameters.oldClassPathDependencies() != null) {
						for (Dependency dependency : pluginParameters.oldClassPathDependencies()) {
							List<JApiCmpArchive> jApiCmpArchives = resolveDependencyToFile(
									"oldClassPathDependencies", dependency, ConfigurationVersion.OLD);
							for (JApiCmpArchive archive : jApiCmpArchives) {
								comparatorOptions.getOldClassPath().add(archive.getFile().getAbsolutePath());
							}
						}
					}
					if (pluginParameters.newClassPathDependencies() != null) {
						for (Dependency dependency : pluginParameters.newClassPathDependencies()) {
							List<JApiCmpArchive> jApiCmpArchives = resolveDependencyToFile(
									"newClassPathDependencies", dependency, ConfigurationVersion.NEW);
							for (JApiCmpArchive archive : jApiCmpArchives) {
								comparatorOptions.getNewClassPath().add(archive.getFile().getAbsolutePath());
							}
						}
					}
					comparatorOptions.setClassPathMode(
							JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS);
				} else {
					log.debug(
							"None of the elements <oldClassPathDependencies/>, <newClassPathDependencies/> or"
									+ " <dependencies/> found. Using "
									+ JApiCli.ClassPathMode.ONE_COMMON_CLASSPATH);

					comparatorOptions.setClassPathMode(
							JarArchiveComparatorOptions.ClassPathMode.ONE_COMMON_CLASSPATH);
				}
			}
		}
		setUpClassPathUsingMavenProject(comparatorOptions);
	}

	/**
	 * @param comparatorOptions
	 *
	 * @throws MojoFailureException
	 */
	private void setUpClassPathUsingMavenProject(final JarArchiveComparatorOptions comparatorOptions)
			throws MojoFailureException {
		MavenProject mavenProject = mavenParameters.mavenProject();
		notNull(mavenProject, "Maven parameter mavenProject should be provided by maven container.");
		Set<String> classPathEntries = new HashSet<>();
		for (Artifact artifact : getCompileArtifacts(mavenProject)) {
			File resolvedFile = artifact.getFile();
			if (resolvedFile != null) {
				String absolutePath = resolvedFile.getAbsolutePath();
				if (classPathEntries.add(absolutePath)) {
					log.debug("Adding to classpath: " + absolutePath);
				}
			} else {
				handleMissingArtifactFile(artifact);
			}
		}
		comparatorOptions.getClassPathEntries().addAll(classPathEntries);
	}

	/**
	 * @param mavenProject
	 *
	 * @return
	 */
	private Set<Artifact> getCompileArtifacts(final MavenProject mavenProject) {
		Set<org.apache.maven.artifact.Artifact> projectDependencies =
				mavenProject.getArtifacts(); // dependencies that this project has, including transitive
		// ones
		HashSet<Artifact> result = new HashSet<>(1 + projectDependencies.size());
		result.add(RepositoryUtils.toArtifact(mavenProject.getArtifact())); // distinguished: the
		// project artifact
		for (org.apache.maven.artifact.Artifact dep : projectDependencies) {
			if (dep.getArtifactHandler().isAddedToClasspath()) {
				if (org.apache.maven.artifact.Artifact.SCOPE_COMPILE.equals(dep.getScope())
						|| org.apache.maven.artifact.Artifact.SCOPE_PROVIDED.equals(dep.getScope())
						|| org.apache.maven.artifact.Artifact.SCOPE_SYSTEM.equals(dep.getScope())) {
					result.add(RepositoryUtils.toArtifact(dep));
				}
			}
		}
		return result;
	}

	/**
	 * @param artifact
	 */
	private void handleMissingArtifactFile(final Artifact artifact) {
		if (pluginParameters.parameter().isIgnoreMissingOptionalDependency()) {
			log.info("Ignoring missing optional dependency: " + toDescriptor(artifact));
		} else {
			log.warn("Could not resolve optional artifact: " + toDescriptor(artifact));
		}
	}

	/**
	 * @param artifact
	 *
	 * @return
	 */
	private String toDescriptor(final Artifact artifact) {
		return artifact.getGroupId() + ":" + artifact.getArtifactId() + ":" + artifact.getVersion();
	}

	/**
	 * @param dependencyDescriptor
	 * @param parameterName
	 * @param configurationVersion
	 *
	 * @return
	 *
	 * @throws MojoFailureException
	 */
	private List<JApiCmpArchive> retrieveFileFromConfiguration(
			final DependencyDescriptor dependencyDescriptor,
			final String parameterName,
			final ConfigurationVersion configurationVersion)
			throws MojoFailureException {
		List<JApiCmpArchive> jApiCmpArchives;
		if (dependencyDescriptor instanceof Dependency) {
			Dependency dependency = (Dependency) dependencyDescriptor;
			jApiCmpArchives = resolveDependencyToFile(parameterName, dependency, configurationVersion);
		} else if (dependencyDescriptor instanceof ConfigurationFile) {
			ConfigurationFile configurationFile = (ConfigurationFile) dependencyDescriptor;
			jApiCmpArchives = resolveConfigurationFileToFile(parameterName, configurationFile,
					configurationVersion);
		} else {
			throw new MojoFailureException(
					"DependencyDescriptor is not of type <dependency/> nor of type <configurationFile/>.");
		}
		return jApiCmpArchives;
	}

	/**
	 * @param version
	 * @param parameterName
	 * @param configurationVersion
	 *
	 * @return
	 *
	 * @throws MojoFailureException if an error occurs during processing
	 */
	private List<JApiCmpArchive> retrieveFileFromConfiguration(final Version version,
															   final String parameterName,
															   final ConfigurationVersion configurationVersion)
			throws MojoFailureException {
		if (version != null) {
			final Dependency dependency = version.getDependency();
			if (dependency != null) {
				return resolveDependencyToFile(parameterName, dependency, configurationVersion);
			} else if (version.getFile() != null) {
				final ConfigurationFile configurationFile = version.getFile();
				return resolveConfigurationFileToFile(parameterName, configurationFile,
						configurationVersion);
			} else {
				throw new MojoFailureException("Missing configuration parameter 'dependency'.");
			}
		}
		throw new MojoFailureException(
				String.format("Missing configuration parameter: %s", parameterName));
	}

	/**
	 * @param parameterName
	 * @param configurationFile
	 * @param configurationVersion
	 *
	 * @return
	 *
	 * @throws MojoFailureException
	 */
	private List<JApiCmpArchive> resolveConfigurationFileToFile(final String parameterName,
																final ConfigurationFile configurationFile,
																final ConfigurationVersion configurationVersion)
			throws MojoFailureException {
		String path = configurationFile.getPath();
		if (path == null) {
			throw new MojoFailureException(
					String.format(
							"The path element in the configuration of the plugin is missing for %s.",
							parameterName));
		}
		File file = new File(path);
		if (!file.exists()) {
			if (!ignoreMissingArtifact(configurationVersion)) {
				throw new MojoFailureException(
						String.format("The path '%s' does not point to an existing file.", path));
			} else {
				log.warn("The file given by path '" + file.getAbsolutePath() + "' does not exist.");
			}
		}
		if (!file.isFile() || !file.canRead()) {
			if (!ignoreMissingArtifact(configurationVersion)) {
				throw new MojoFailureException(
						String.format(
								"The file given by path '%s' is either not a file or is not readable.",
								path));
			} else {
				log.warn("The file given by path '"
						+ file.getAbsolutePath()
						+ "' is either not a file or is not readable.");
			}
		}
		return Collections.singletonList(new JApiCmpArchive(file, guessVersion(file)));
	}

	/**
	 * @param parameterName
	 * @param dependency
	 * @param configurationVersion
	 *
	 * @return
	 *
	 * @throws MojoFailureException
	 */
	List<JApiCmpArchive> resolveDependencyToFile(final String parameterName,
												 final Dependency dependency,
												 final ConfigurationVersion configurationVersion)
			throws MojoFailureException {
		List<JApiCmpArchive> jApiCmpArchives = new ArrayList<>();

		log.debug("Trying to resolve dependency '" + dependency + "' to file.");

		MavenProject mavenProject = mavenParameters.mavenProject();
		if (dependency.getSystemPath() == null) {
			String descriptor = dependency.getGroupId()
					+ ":"
					+ dependency.getArtifactId()
					+ ":"
					+ dependency.getVersion();
			log.debug(parameterName + ": " + descriptor);
			String projectDescriptor = mavenProject.getGroupId()
					+ ":"
					+ mavenProject.getArtifactId()
					+ ":"
					+ mavenProject.getVersion();

			Set<Artifact> artifacts;
			if (descriptor.equals(projectDescriptor)) {
				// do not repeat what Maven already did for us
				artifacts = getCompileArtifacts(mavenProject);
			} else {
				artifacts = resolveArtifact(dependency, configurationVersion);
			}

			for (Artifact artifact : artifacts) {
				File file = artifact.getFile();
				if (file != null) {
					jApiCmpArchives.add(new JApiCmpArchive(file, artifact.getVersion()));
				} else {
					handleMissingArtifactFile(artifact);
				}
			}
			if (jApiCmpArchives.isEmpty()) {
				String message = String.format("Could not resolve dependency with descriptor '%s'.",
						descriptor);
				if (ignoreMissingArtifact(configurationVersion)) {
					log.warn(message);
				} else {
					throw new MojoFailureException(message);
				}
			}
		} else {
			String systemPath = dependency.getSystemPath();
			Pattern pattern = Pattern.compile("\\$\\{([^}])");
			Matcher matcher = pattern.matcher(systemPath);
			if (matcher.matches()) {
				for (int i = 1; i <= matcher.groupCount(); i++) {
					String property = matcher.group(i);
					String propertyResolved = mavenParameters.mavenProject().getProperties().getProperty(
							property);
					if (propertyResolved != null) {
						systemPath = systemPath.replaceAll("${" + property + "}", propertyResolved);
					} else {
						throw new MojoFailureException("Could not resolve property '" + property + "'.");
					}
				}
			}
			File file = new File(systemPath);
			boolean addFile = true;
			if (!file.exists()) {
				if (ignoreMissingArtifact(configurationVersion)) {
					log.warn("Could not find file, but ignoreMissingOldVersion is set to true: "
							+ file.getAbsolutePath());
				} else {
					throw new MojoFailureException("File '" + file.getAbsolutePath() + "' does not exist.");
				}
				addFile = false;
			}
			if (!file.canRead()) {
				if (ignoreMissingArtifact(configurationVersion)) {
					log.warn("File is not readable, but ignoreMissingOldVersion is set tot true: "
							+ file.getAbsolutePath());
				} else {
					throw new MojoFailureException("File '" + file.getAbsolutePath() + "' is not readable.");
				}
				addFile = false;
			}
			String version = guessVersion(file);
			if (addFile) {
				jApiCmpArchives.add(new JApiCmpArchive(file, version));
			}
		}
		return jApiCmpArchives;
	}

	/**
	 * @param file
	 *
	 * @return
	 */
	private String guessVersion(final File file) {
		String name = file.getName();
		Optional<SemanticVersion> semanticVersion = japicmp.versioning.Version.getSemanticVersion(name);
		String version = semanticVersion.isPresent() ? semanticVersion.get().toString():"n.a.";
		if (name.contains("SNAPSHOT")) {
			version += "-SNAPSHOT";
		}
		return version;
	}

	/**
	 * @param configurationVersion
	 *
	 * @return
	 */
	private boolean ignoreMissingArtifact(final ConfigurationVersion configurationVersion) {
		return ignoreNonResolvableArtifacts()
				|| ignoreMissingOldVersion(configurationVersion)
				|| ignoreMissingNewVersion(configurationVersion);
	}

	/**
	 * @return
	 */
	private boolean ignoreNonResolvableArtifacts() {
		boolean ignoreNonResolvableArtifacts = false;
		ConfigParameters parameterParam = pluginParameters.parameter();
		if (parameterParam != null) {
			String ignoreNonResolvableArtifactsAsString =
					parameterParam.getIgnoreNonResolvableArtifacts();
			if (Boolean.TRUE.toString().equalsIgnoreCase(ignoreNonResolvableArtifactsAsString)) {
				ignoreNonResolvableArtifacts = true;
			}
		}
		return ignoreNonResolvableArtifacts;
	}

	/**
	 * @param configurationVersion
	 *
	 * @return
	 */
	private boolean ignoreMissingOldVersion(final ConfigurationVersion configurationVersion) {
		return (configurationVersion == ConfigurationVersion.OLD &&
				pluginParameters.parameter().getIgnoreMissingOldVersion());
	}

	/**
	 * @param configurationVersion
	 *
	 * @return
	 */
	private boolean ignoreMissingNewVersion(final ConfigurationVersion configurationVersion) {
		return (configurationVersion == ConfigurationVersion.NEW &&
				pluginParameters.parameter().getIgnoreMissingNewVersion());
	}

	/**
	 * @param output
	 * @param outputFile
	 *
	 * @throws MojoFailureException
	 */
	private void writeToFile(final String output, final File outputFile) throws MojoFailureException {
		try (OutputStreamWriter fileWriter = new OutputStreamWriter(
				Files.newOutputStream(outputFile.toPath()), StandardCharsets.UTF_8)) {
			fileWriter.write(output);
			log.info("Written file '" + outputFile.getAbsolutePath() + "'.");
		} catch (IOException e) {
			throw new MojoFailureException(String.format("Failed to write file: %s", e.getMessage()), e);
		}
	}

	/**
	 * @param dependency
	 * @param configurationVersion
	 *
	 * @return
	 *
	 * @throws MojoFailureException
	 */
	private Set<Artifact> resolveArtifact(final Dependency dependency,
										  final ConfigurationVersion configurationVersion)
			throws MojoFailureException {
		notNull(mavenParameters.artifactRepositories(),
				"Maven parameter artifactRepositories should be provided by maven container.");
		Artifact artifact = createDefaultArtifact(dependency.getGroupId(), dependency.getArtifactId(),
				dependency.getClassifier(), dependency.getType(),
				dependency.getVersion());
		return resolveArtifact(artifact, configurationVersion);
	}

	/**
	 * @param artifact
	 * @param configurationVersion
	 *
	 * @return
	 *
	 * @throws MojoFailureException
	 */
	private Set<Artifact> resolveArtifact(final Artifact artifact,
										  final ConfigurationVersion configurationVersion)
			throws MojoFailureException {
		notNull(mavenParameters.repoSystem(),
				"Maven parameter repoSystem should be provided by maven container.");
		notNull(mavenParameters.repoSession(),
				"Maven parameter repoSession should be provided by maven container.");
		ArtifactRequest request = new ArtifactRequest();
		request.setArtifact(artifact);
		request.setRepositories(mavenParameters.remoteRepos());
		ArtifactResult resolutionResult;
		try {
			log.debug("Trying to resolve artifact: " + request);
			resolutionResult = mavenParameters.repoSystem().resolveArtifact(mavenParameters.repoSession(),
					request);
			log.debug("Resolved artifact: " + resolutionResult);
			if (resolutionResult != null) {
				if (resolutionResult.getExceptions() != null && !resolutionResult.getExceptions()
						.isEmpty()) {
					List<Exception> exceptions = resolutionResult.getExceptions();
					for (Exception exception : exceptions) {
						log.debug(exception.getMessage(), exception);
					}
				}
				if (resolutionResult.isMissing()) {
					if (ignoreMissingArtifact(configurationVersion)) {
						log.warn("Ignoring missing artifact " + request.getArtifact());
					} else {
						throw new MojoFailureException("Could not resolve artifact " + request.getArtifact());
					}
					return new HashSet<>();
				}
				return new HashSet<>(Collections.singletonList(resolutionResult.getArtifact()));
			}

		} catch (final ArtifactResolutionException e) {
			if (ignoreMissingArtifact(configurationVersion)) {
				log.warn(e.getMessage());
			} else {
				throw new MojoFailureException(e.getMessage(), e);
			}
		}
		return new HashSet<>();
	}

	/**
	 * Throws an exception if given the value is {@code null}.
	 *
	 * @param value the value to check
	 * @param msg   the error message to include in the exception
	 * @param <T>   the given value type
	 *
	 * @throws MojoFailureException if the given value is {@code null}
	 */
	private <T> void notNull(final T value, final String msg) throws MojoFailureException {
		if (value == null) {
			throw new MojoFailureException(msg);
		}
	}
}
