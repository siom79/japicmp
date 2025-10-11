package japicmp.maven;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.maven.util.CtClassBuilder;
import japicmp.maven.util.CtInterfaceBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Collection of tests of the {@link JApiCmpProcessor} breaking logic.
 */
final class JApiCmpProcessorBreakTest extends AbstractTest {

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
  void testBreakOnSemanticVersioning() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.breakBuildBasedOnSemanticVersioning(configParams));

    processor.pluginParameters.breakBuild().setOnSemanticVersioning(true);
    assertTrue(processor.breakBuildBasedOnSemanticVersioning(configParams));

    processor.pluginParameters.breakBuild().setOnSemanticVersioning(false);
    configParams.setBreakBuildBasedOnSemanticVersioning(true);
    assertTrue(processor.breakBuildBasedOnSemanticVersioning(configParams));
  }

  @Test
  void testBreakOnSemanticVersioningForMajorVersionZero() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.breakBuildBasedOnSemanticVersioningForMajorVersionZero(configParams));

    processor.pluginParameters.breakBuild().setOnSemanticVersioningForMajorVersionZero(true);
    assertTrue(processor.breakBuildBasedOnSemanticVersioningForMajorVersionZero(configParams));

    processor.pluginParameters.breakBuild().setOnSemanticVersioningForMajorVersionZero(false);
    configParams.setBreakBuildBasedOnSemanticVersioningForMajorVersionZero(true);
    assertTrue(processor.breakBuildBasedOnSemanticVersioningForMajorVersionZero(configParams));
  }

  @Test
  void testBreakOnModifications() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.breakBuildOnModifications(configParams));

    processor.pluginParameters.breakBuild().setOnModifications(true);
    assertTrue(processor.breakBuildOnModifications(configParams));

    processor.pluginParameters.breakBuild().setOnModifications(false);
    configParams.setBreakBuildOnModifications(true);
    assertTrue(processor.breakBuildOnModifications(configParams));
  }

  @Test
  void testBreakOnBinaryIncompatibleModifications() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
    assertFalse(processor.breakBuildOnBinaryIncompatibleModifications(configParams));

    processor.pluginParameters.breakBuild().setOnBinaryIncompatibleModifications(true);
    assertTrue(processor.breakBuildOnBinaryIncompatibleModifications(configParams));

    processor.pluginParameters.breakBuild().setOnBinaryIncompatibleModifications(false);
    configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
    assertTrue(processor.breakBuildOnBinaryIncompatibleModifications(configParams));
  }

  @Test
  void testBreakOnSourceIncompatibleModifications() {
    JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams, mavenParams, mock(Log.class));
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
   *
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

    options.addExcludeFromArgument(Optional.of("japicmp.ITest"), false); // exclude japicmp.ITest

    // do not break the build if cause is excluded
    configParams.setBreakBuildIfCausedByExclusion(breakBuildIfCausedByExclusion);
    configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
    configParams.setBreakBuildOnSourceIncompatibleModifications(true);
    final JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams,
                                                            mavenParams,
                                                            mock(Log.class));
    processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), pluginParams.parameter(),
                                    options,
                                    new JarArchiveComparator(comparatorOptions));
  }

}
