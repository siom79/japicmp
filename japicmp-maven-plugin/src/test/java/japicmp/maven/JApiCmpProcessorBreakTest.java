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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.junit.jupiter.api.Assertions;
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

  @Test
  void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusionFalse() throws Exception {
    testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(false);
  }

  @Test
  void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusionTrue() throws Exception {
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
                                                            mock(Log.class));
    processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), configParams,
                                    options, compareClassesResult.getJarArchiveComparator());
  }


  @Test
  void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusionFalse()
          throws Exception {
    testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(false);
  }

  @Test
  void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusionTrue()
          throws Exception {
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

    configParams.setBreakBuildIfCausedByExclusion(
            breakBuildIfCausedByExclusion); // do not break the build if cause is excluded
    configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
    configParams.setBreakBuildOnSourceIncompatibleModifications(true);

    final JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams,
                                                            mavenParams,
                                                            mock(Log.class));
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
                                                            mock(Log.class));
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
              public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(
                        classPool);
                return Collections.singletonList(ctClass);
              }
            });

    configParams.setBreakBuildOnBinaryIncompatibleModifications(true);
    configParams.setBreakBuildOnSourceIncompatibleModifications(true);

    final JApiCmpProcessor processor = new JApiCmpProcessor(pluginParams,
                                                            mavenParams,
                                                            mock(Log.class));
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
