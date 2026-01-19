package japicmp.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
}
