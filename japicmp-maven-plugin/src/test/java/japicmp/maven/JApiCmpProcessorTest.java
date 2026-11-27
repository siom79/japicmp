package japicmp.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.maven.util.CtClassBuilder;
import japicmp.maven.util.CtFieldBuilder;
import japicmp.maven.util.CtInterfaceBuilder;
import japicmp.maven.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.resolution.DependencyResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * A collection of test of the JApiCmpProcessor class.
 */
final class JApiCmpProcessorTest extends AbstractTest {

	final static Log logger = mock(Log.class);

	ConfigParameters configParams;
	MavenParameters mavenParams;
	PluginParameters pluginParams;

	@BeforeEach
	void setup() {
		configParams = new ConfigParameters();
		mavenParams = createMavenParameters();
		pluginParams = createPluginParameters(configParams);
	}

	@Test
	void testSkipDiffReport() {
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, logger);
		assertFalse(processor.skipDiffReport());

		processor.pluginParameters.skipReport().setSkipDiffReport(true);
		assertTrue(processor.skipDiffReport());

		processor.pluginParameters.skipReport().setSkipDiffReport(false);
		configParams.setSkipDiffReport(true);
		assertTrue(processor.skipDiffReport());
	}

	@Test
	void testSkipHtmlReport() {
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, logger);
		assertFalse(processor.skipHtmlReport());

		processor.pluginParameters.skipReport().setSkipHtmlReport(true);
		assertTrue(processor.skipHtmlReport());

		processor.pluginParameters.skipReport().setSkipHtmlReport(false);
		configParams.setSkipHtmlReport(true);
		assertTrue(processor.skipHtmlReport());
	}

	@Test
	void testSkipMarkdownReport() {
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, logger);
		assertFalse(processor.skipMarkdownReport());

		processor.pluginParameters.skipReport().setSkipMarkdownReport(true);
		assertTrue(processor.skipMarkdownReport());

		processor.pluginParameters.skipReport().setSkipMarkdownReport(false);
		configParams.setSkipMarkdownReport(true);
		assertTrue(processor.skipMarkdownReport());
	}

	@Test
	void testSkipXmlReport() {
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, logger);
		assertFalse(processor.skipMarkdownReport());

		processor.pluginParameters.skipReport().setSkipXmlReport(true);
		assertTrue(processor.skipXmlReport());

		processor.pluginParameters.skipReport().setSkipXmlReport(false);
		configParams.setSkipXmlReport(true);
		assertTrue(processor.skipXmlReport());
	}

	@Test
	void testBreakIfNecessary() throws MojoExecutionException, MojoFailureException {
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, logger);
		Options options = Options.newDefault();
		JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(options);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(comparatorOptions);
		processor.breakBuildIfNecessary(new ArrayList<>(), configParams, options,
			jarArchiveComparator);

		processor.pluginParameters.breakBuild().setOnSemanticVersioning(true);
		processor.breakBuildIfNecessary(new ArrayList<>(), configParams, options,
			jarArchiveComparator);
		assertTrue(options.isErrorOnSemanticIncompatibility());

		processor.pluginParameters.breakBuild().setOnSemanticVersioningForMajorVersionZero(true);
		processor.breakBuildIfNecessary(new ArrayList<>(), configParams, options,
			jarArchiveComparator);
		assertTrue(options.isErrorOnSemanticIncompatibilityForMajorVersionZero());

		configParams.setIgnoreMissingNewVersion(true);
		processor.breakBuildIfNecessary(new ArrayList<>(), configParams, options,
			jarArchiveComparator);
		assertTrue(options.isIgnoreMissingNewVersion());
	}

	@Test
	void testBreakOnSemanticVersioning() {
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, logger);
		assertFalse(processor.breakBuildBasedOnSemanticVersioning(configParams));

		processor.pluginParameters.breakBuild().setOnSemanticVersioning(true);
		assertTrue(processor.breakBuildBasedOnSemanticVersioning(configParams));

		processor.pluginParameters.breakBuild().setOnSemanticVersioning(false);
		configParams.setBreakBuildBasedOnSemanticVersioning(true);
		assertTrue(processor.breakBuildBasedOnSemanticVersioning(configParams));
	}

	@Test
	void testBreakOnSemanticVersioningForMajorVersionZero() {
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, logger);
		assertFalse(processor.breakBuildBasedOnSemanticVersioningForMajorVersionZero(configParams));

		processor.pluginParameters.breakBuild().setOnSemanticVersioningForMajorVersionZero(true);
		assertTrue(processor.breakBuildBasedOnSemanticVersioningForMajorVersionZero(configParams));

		processor.pluginParameters.breakBuild().setOnSemanticVersioningForMajorVersionZero(false);
		configParams.setBreakBuildBasedOnSemanticVersioningForMajorVersionZero(true);
		assertTrue(processor.breakBuildBasedOnSemanticVersioningForMajorVersionZero(configParams));
	}

	@Test
	void testBreakOnModifications() {
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, logger);
		assertFalse(processor.breakBuildOnModifications(configParams));

		processor.pluginParameters.breakBuild().setOnModifications(true);
		assertTrue(processor.breakBuildOnModifications(configParams));

		processor.pluginParameters.breakBuild().setOnModifications(false);
		configParams.setBreakBuildOnModifications(true);
		assertTrue(processor.breakBuildOnModifications(configParams));
	}

	@Test
	void testBreakOnBinaryIncompatibleModifications() {
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, logger);
		assertFalse(processor.breakBuildOnBinaryIncompatibleModifications(configParams));

		processor.pluginParameters.breakBuild().setOnBinaryIncompatibleModifications(true);
		assertTrue(processor.breakBuildOnBinaryIncompatibleModifications(configParams));

		processor.pluginParameters.breakBuild().setOnBinaryIncompatibleModifications(false);
		configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
		assertTrue(processor.breakBuildOnBinaryIncompatibleModifications(configParams));
	}

	@Test
	void testBreakOnSourceIncompatibleModifications() {
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, logger);
		assertFalse(processor.breakBuildOnSourceIncompatibleModifications(configParams));

		processor.pluginParameters.breakBuild().setOnSourceIncompatibleModifications(true);
		assertTrue(processor.breakBuildOnSourceIncompatibleModifications(configParams));

		processor.pluginParameters.breakBuild().setOnSourceIncompatibleModifications(false);
		configParams.setBreakBuildOnSourceIncompatibleModifications(true);
		assertTrue(processor.breakBuildOnSourceIncompatibleModifications(configParams));
	}

	@Test
	void testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusion(false);
	}

	@Test
	void testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusionTrue() {
		assertThrows(MojoFailureException.class,
			() -> testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusion(true));
	}

	/**
	 * Common processing to test Break by Exclusion
	 *
	 * @param breakBuildIfCausedByExclusion Break by Exclusion flag
	 * @throws Exception if an error occurs
	 */
	private void testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusion(
		boolean breakBuildIfCausedByExclusion) throws Exception {
		final Options options = Options.newDefault();
		final JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(
			comparatorOptions, new ClassesHelper.ClassesGenerator() {
				@Override
				public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
					CtClass interfaceCtClass = CtInterfaceBuilder.create()
						.name("japicmp.ITest")
						.addToClassPool(classPool);
					CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(
						interfaceCtClass).addToClassPool(classPool);
					return Arrays.asList(interfaceCtClass, ctClass);
				}

				@Override
				public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
					CtClass interfaceCtClass = CtInterfaceBuilder.create()
						.name("japicmp.ITest")
						.addToClassPool(classPool);
					CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(
						classPool);
					return Arrays.asList(interfaceCtClass, ctClass);
				}
			});
		// exclude japicmp.ITest
		options.addExcludeFromArgument(Optional.of("japicmp.ITest"), false);
		// do not break the build if cause is excluded
		configParams.setBreakBuildIfCausedByExclusion(breakBuildIfCausedByExclusion);
		configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
		configParams.setBreakBuildOnSourceIncompatibleModifications(true);
		final JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams,
			mavenParams,
			logger);
		processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), pluginParams.parameter(),
			options,
			new JarArchiveComparator(comparatorOptions));
	}

	@Test
	void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(false);
	}

	@Test
	void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusionTrue() {
		Assertions.assertThrows(MojoFailureException.class,
			() -> testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(true));
	}

	private void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(
		boolean breakBuildIfCausedByExclusion) throws Exception {

		final Options options = Options.newDefault();
		final JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(options);
		final ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(
			comparatorOptions, new ClassesHelper.ClassesGenerator() {
				@Override
				public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
					CtClass fieldTypeCtClass = CtClassBuilder.create()
						.name("japicmp.FieldType")
						.addToClassPool(classPool);
					CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(
						classPool);
					CtFieldBuilder.create().type(fieldTypeCtClass).name("field").addToClass(ctClass);
					return Arrays.asList(fieldTypeCtClass, ctClass);
				}

				@Override
				public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
					CtClass fieldTypeCtClass = CtClassBuilder.create()
						.name("japicmp.FieldType")
						.addToClassPool(classPool);
					CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(
						classPool);
					CtFieldBuilder.create()
						.type(classPool.get("java.lang.String"))
						.name("field")
						.addToClass(ctClass);
					return Arrays.asList(fieldTypeCtClass, ctClass);
				}
			});
		// exclude japicmp.FieldType
		options.addExcludeFromArgument(Optional.of("japicmp.FieldType"), false);
		// do not break the build if cause is excluded
		configParams.setBreakBuildIfCausedByExclusion(breakBuildIfCausedByExclusion);
		configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
		configParams.setBreakBuildOnSourceIncompatibleModifications(true);

		final JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams,
			mavenParams,
			logger);
		processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), configParams,
			options, compareClassesResult.getJarArchiveComparator());
	}

	@Test
	void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusionFalse()
		throws Exception {
		testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(false);
	}

	@Test
	void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusionTrue() {
		Assertions.assertThrows(MojoFailureException.class,
			() -> testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(
				true));
	}

	private void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(
		boolean breakBuildIfCausedByExclusion) throws Exception {

		final Options options = Options.newDefault();
		final JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(options);
		final ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(
			comparatorOptions, new ClassesHelper.ClassesGenerator() {
				@Override
				public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
					CtClass typeCtClass = CtClassBuilder.create()
						.name("japicmp.MethodReturnType")
						.addToClassPool(classPool);
					CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(
						classPool);
					CtMethodBuilder.create().publicAccess().returnType(typeCtClass).name(
						"test").addToClass(
						ctClass);
					return Arrays.asList(typeCtClass, ctClass);
				}

				@Override
				public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
					CtClass typeCtClass = CtClassBuilder.create()
						.name("japicmp.MethodReturnType")
						.addToClassPool(classPool);
					CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(
						classPool);
					CtMethodBuilder.create()
						.publicAccess()
						.returnType(classPool.get("java.lang.String"))
						.name("test")
						.addToClass(ctClass);
					return Arrays.asList(typeCtClass, ctClass);
				}
			});
		// exclude japicmp.MethodReturnType
		options.addExcludeFromArgument(Optional.of("japicmp.MethodReturnType"), false);
		// do not break the build if cause is excluded
		configParams.setBreakBuildIfCausedByExclusion(breakBuildIfCausedByExclusion);
		configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
		configParams.setBreakBuildOnSourceIncompatibleModifications(true);

		final JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams,
			mavenParams,
			logger);
		processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), configParams,
			options, compareClassesResult.getJarArchiveComparator());
	}

	@Test
	void testBreakBuildIfNecessarySuperclassChangedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(false);
	}

	@Test
	void testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusionTrue() {
		Assertions.assertThrows(MojoFailureException.class,
			() -> testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(
				true));
	}

	private void testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(
		boolean breakBuildIfCausedByExclusion) throws Exception {

		final Options options = Options.newDefault();
		final JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(
			options);
		ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(
			comparatorOptions, new ClassesHelper.ClassesGenerator() {
				@Override
				public List<CtClass> createOldClasses(ClassPool classPool) {
					CtClass typeCtClass = CtClassBuilder.create().name(
						"japicmp.SuperType").addToClassPool(
						classPool);
					CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(
						typeCtClass).addToClassPool(classPool);
					return Arrays.asList(typeCtClass, ctClass);
				}

				@Override
				public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
					CtClass typeCtClass = CtClassBuilder.create().name(
						"japicmp.SuperType").addToClassPool(
						classPool);
					CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(
						classPool.get("java.text.SimpleDateFormat")).addToClassPool(classPool);
					return Arrays.asList(typeCtClass, ctClass);
				}
			});
		// exclude japicmp.SuperType
		options.addExcludeFromArgument(Optional.of("japicmp.SuperType"), false);
		// do not break the build if cause is excluded
		configParams.setBreakBuildIfCausedByExclusion(breakBuildIfCausedByExclusion);
		configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
		configParams.setBreakBuildOnSourceIncompatibleModifications(true);

		final JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams,
			mavenParams,
			logger);
		processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), configParams,
			options, compareClassesResult.getJarArchiveComparator());
	}

	@Test
	void testBreakBuildIfNecessaryMultipleChanges() throws Exception {
		final Options options = Options.newDefault();
		final JarArchiveComparatorOptions comparatorOptions = JarArchiveComparatorOptions.of(
			options);
		final ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(
			comparatorOptions, new ClassesHelper.ClassesGenerator() {
				@Override
				public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
					CtClass typeCtClass = CtClassBuilder.create().name(
						"japicmp.SuperType").addToClassPool(
						classPool);
					CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(
						typeCtClass).addToClassPool(classPool);
					CtFieldBuilder.create().name("field").type(CtClass.intType).addToClass(ctClass);
					CtMethodBuilder.create()
						.publicAccess()
						.returnType(CtClass.voidType)
						.name("method")
						.addToClass(ctClass);
					return Arrays.asList(typeCtClass, ctClass);
				}

				@Override
				public List<CtClass> createNewClasses(ClassPool classPool) {
					CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(
						classPool);
					return Collections.singletonList(ctClass);
				}
			});

		configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
		configParams.setBreakBuildOnSourceIncompatibleModifications(true);

		final JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams,
			mavenParams,
			logger);
		try {
			processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), configParams,
				options, compareClassesResult.getJarArchiveComparator());
			fail("No exception thrown.");
		} catch (MojoFailureException e) {
			String msg = e.getMessage();
			assertThat(msg, containsString("japicmp.SuperType:CLASS_REMOVED"));
			assertThat(msg, containsString("japicmp.Test.method():METHOD_REMOVED"));
			assertThat(msg, containsString("japicmp.Test.field:FIELD_REMOVED"));
			assertThat(msg, containsString("japicmp.Test:SUPERCLASS_REMOVED"));
		}
	}

	@Test
	void testSetUpClassPathUsingMavenProjectNoNPEWhenProjectArtifactUnresolvable() throws Exception {
		// Reproduce issue #504: when the project artifact cannot be resolved and the ignore flag
		// is set, resolveArtifact() returns null. Before the fix, that null was added to the set
		// and caused an NPE on the next artifact.getFile() call.
		configParams.setIgnoreNonResolvableArtifacts(Boolean.TRUE.toString());

		RepositorySystem repoSystem = mock(RepositorySystem.class);
		when(repoSystem.resolveArtifact(any(), any()))
			.thenThrow(new ArtifactResolutionException(Collections.emptyList()));

		RemoteRepository remoteRepo = new RemoteRepository.Builder("default", "releases",
			"https://repo.maven.apache.org/maven2").build();
		MavenParameters params = new MavenParameters(new ArrayList<>(), new MavenProject(),
			mock(MojoExecution.class), "", repoSystem,
			mock(RepositorySystemSession.class),
			Collections.singletonList(remoteRepo));

		JApiCmpProcessor processor = new JApiCmpProcessor(
			createPluginParameters(configParams), params, logger);

		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		Method method = JApiCmpProcessor.class.getDeclaredMethod(
			"setUpClassPathUsingMavenProject", JarArchiveComparatorOptions.class);
		method.setAccessible(true);

		assertDoesNotThrow(() -> method.invoke(processor, options));
	}

	@Test
	void testResolveSeparateClassPathsCannotBeCombinedWithDependencies() throws Exception {
		configParams.setResolveSeparateClassPaths(true);
		PluginParameters params = new PluginParameters(false,
			createVersion("groupId", "artifactId", "0.1.1"),
			createVersion("groupId", "artifactId", "0.1.0"), configParams,
			Collections.singletonList(createDependency("groupId", "other", "1.0")), null, null, false,
			new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
			new SkipReport(), new BreakBuild());
		JApiCmpProcessor processor = new JApiCmpProcessor(params, mavenParams, logger);

		InvocationTargetException e = assertThrows(InvocationTargetException.class,
			() -> setUpClassPathMethod().invoke(processor, new JarArchiveComparatorOptions()));
		assertTrue(e.getCause() instanceof MojoFailureException);
		assertThat(e.getCause().getMessage(), containsString("<resolveSeparateClassPaths/>"));
	}

	@Test
	void testResolveSeparateClassPaths() throws Exception {
		configParams.setResolveSeparateClassPaths(true);
		Path dir = Files.createTempDirectory("japicmp");
		File oldJar = Files.createFile(dir.resolve("artifactId-0.1.0.jar")).toFile();
		File newJar = Files.createFile(dir.resolve("artifactId-0.1.1.jar")).toFile();
		File oldDepJar = Files.createFile(dir.resolve("dep-1.0.jar")).toFile();
		File newDepJar = Files.createFile(dir.resolve("dep-2.0.jar")).toFile();
		File explicitJar = Files.createFile(dir.resolve("explicit-3.0.jar")).toFile();
		Map<String, File> files = new HashMap<>();
		files.put("artifactId:0.1.0", oldJar);
		files.put("artifactId:0.1.1", newJar);
		files.put("dep:1.0", oldDepJar);
		files.put("dep:2.0", newDepJar);
		files.put("explicit:3.0", explicitJar);

		RepositorySystem repoSystem = mock(RepositorySystem.class);
		when(repoSystem.resolveArtifact(any(), any())).thenAnswer(invocation -> {
			ArtifactRequest request = invocation.getArgument(1);
			Artifact artifact = request.getArtifact();
			return new ArtifactResult(request).setArtifact(
				artifact.setFile(files.get(artifact.getArtifactId() + ":" + artifact.getVersion())));
		});
		when(repoSystem.readArtifactDescriptor(any(), any())).thenAnswer(invocation -> {
			ArtifactDescriptorRequest request = invocation.getArgument(1);
			ArtifactDescriptorResult result = new ArtifactDescriptorResult(request);
			result.setArtifact(request.getArtifact());
			// version 0.1.0 depends on dep:1.0, version 0.1.1 on dep:2.0
			String depVersion = "0.1.0".equals(request.getArtifact().getVersion()) ? "1.0" : "2.0";
			result.addDependency(new org.eclipse.aether.graph.Dependency(
				new DefaultArtifact("groupId", "dep", "jar", depVersion), "compile"));
			return result;
		});
		when(repoSystem.resolveDependencies(any(), any())).thenAnswer(invocation -> {
			DependencyRequest request = invocation.getArgument(1);
			DependencyResult result = new DependencyResult(request);
			List<ArtifactResult> artifactResults = new ArrayList<>();
			for (org.eclipse.aether.graph.Dependency dependency : request.getCollectRequest().getDependencies()) {
				Artifact artifact = dependency.getArtifact();
				artifactResults.add(new ArtifactResult(new ArtifactRequest(artifact, null, null)).setArtifact(
					artifact.setFile(files.get(artifact.getArtifactId() + ":" + artifact.getVersion()))));
			}
			result.setArtifactResults(artifactResults);
			return result;
		});
		MavenParameters params = new MavenParameters(new ArrayList<>(), new MavenProject(),
			mock(MojoExecution.class), "", repoSystem, mock(RepositorySystemSession.class),
			mavenParams.remoteRepos());
		PluginParameters pluginParameters = new PluginParameters(false,
			createVersion("groupId", "artifactId", "0.1.1"),
			createVersion("groupId", "artifactId", "0.1.0"), configParams, null, null, null, false,
			null, null, Collections.singletonList(createDependency("groupId", "explicit", "3.0")), null,
			new SkipReport(), new BreakBuild());
		JApiCmpProcessor processor = new JApiCmpProcessor(pluginParameters, params, logger);

		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		invokeSetUpClassPath(processor, options);

		assertEquals(JarArchiveComparatorOptions.ClassPathMode.TWO_SEPARATE_CLASSPATHS,
			options.getClassPathMode());
		assertEquals(Arrays.asList(explicitJar.getAbsolutePath(), oldJar.getAbsolutePath(),
			oldDepJar.getAbsolutePath()), options.getOldClassPath());
		assertEquals(Arrays.asList(newJar.getAbsolutePath(), newDepJar.getAbsolutePath()),
			options.getNewClassPath());
		assertTrue(options.getClassPathEntries().isEmpty());
	}

	private static void invokeSetUpClassPath(JApiCmpProcessor processor,
											 JarArchiveComparatorOptions options) throws Exception {
		processor.getOptions();
		setUpClassPathMethod().invoke(processor, options);
	}

	private static Method setUpClassPathMethod() throws NoSuchMethodException {
		Method method = JApiCmpProcessor.class.getDeclaredMethod("setUpClassPath",
			JarArchiveComparatorOptions.class);
		method.setAccessible(true);
		return method;
	}
}

