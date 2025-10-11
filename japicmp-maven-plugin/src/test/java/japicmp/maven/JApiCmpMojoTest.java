package japicmp.maven;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.jupiter.api.Assertions.fail;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.maven.util.CtClassBuilder;
import japicmp.maven.util.CtFieldBuilder;
import japicmp.maven.util.CtMethodBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

final class JApiCmpMojoTest extends AbstractTest {


  /*
   * TODO: Move th JApiCmpProcessorTest
   * @Test
   */
  void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusionFalse() throws Exception {
    testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(false);
  }

  /*
   * TODO: Move th JApiCmpProcessorTest
   * @Test
   */
  void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusionTrue() throws Exception {
    Assertions.assertThrows(MojoFailureException.class,
                            () -> testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(true));
  }

  private void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(
          boolean breakBuildIfCausedByExclusion) throws Exception {
    Options options = Options.newDefault();
    JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(
            options);
    ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(
            jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
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
    options.addExcludeFromArgument(Optional.of("japicmp.FieldType"),
                                   false); // exclude japicmp.FieldType
    JApiCmpMojo mojo = new JApiCmpMojo();
    ConfigParameters parameterParam = new ConfigParameters();
    parameterParam.setBreakBuildIfCausedByExclusion(
            breakBuildIfCausedByExclusion); // do not break the build if cause is excluded
    parameterParam.setBreakBuildOnBinaryIncompatibleModifications(true);
    parameterParam.setBreakBuildOnSourceIncompatibleModifications(true);
    mojo.processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), parameterParam,
                                         options, compareClassesResult.getJarArchiveComparator());
  }

  /*
   * TODO: Move th JApiCmpProcessorTest
   * @Test
   */
  void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusionFalse()
          throws Exception {
    testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(false);
  }

  /*
   * TODO: Move th JApiCmpProcessorTest
   * @Test
   */
  void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusionTrue()
          throws Exception {
    Assertions.assertThrows(MojoFailureException.class,
                            () -> testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(
                                    true));
  }

  private void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(
          boolean breakBuildIfCausedByExclusion) throws Exception {
    Options options = Options.newDefault();
    JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(
            options);
    ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(
            jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
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
    options.addExcludeFromArgument(Optional.of("japicmp.MethodReturnType"),
                                   false); // exclude japicmp.MethodReturnType
    JApiCmpMojo mojo = new JApiCmpMojo();
    ConfigParameters parameterParam = new ConfigParameters();
    parameterParam.setBreakBuildIfCausedByExclusion(
            breakBuildIfCausedByExclusion); // do not break the build if cause is excluded
    parameterParam.setBreakBuildOnBinaryIncompatibleModifications(true);
    parameterParam.setBreakBuildOnSourceIncompatibleModifications(true);
    mojo.processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), parameterParam,
                                         options, compareClassesResult.getJarArchiveComparator());
  }

  /*
   * TODO: Move th JApiCmpProcessorTest
   * @Test
   */
  void testBreakBuildIfNecessarySuperclassChangedCausedByExclusionFalse() throws Exception {
    testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(false);
  }

  /*
   * TODO: Move th JApiCmpProcessorTest
   * @Test
   */
  void testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusionTrue() {
    Assertions.assertThrows(MojoFailureException.class,
                            () -> testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(
                                    true));
  }

  private void testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(
          boolean breakBuildIfCausedByExclusion) throws Exception {
    Options options = Options.newDefault();
    JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(
            options);
    ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(
            jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
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
    options.addExcludeFromArgument(Optional.of("japicmp.SuperType"),
                                   false); // exclude japicmp.SuperType
    JApiCmpMojo mojo = new JApiCmpMojo();
    ConfigParameters parameterParam = new ConfigParameters();
    parameterParam.setBreakBuildIfCausedByExclusion(
            breakBuildIfCausedByExclusion); // do not break the build if cause is excluded
    parameterParam.setBreakBuildOnBinaryIncompatibleModifications(true);
    parameterParam.setBreakBuildOnSourceIncompatibleModifications(true);
    mojo.processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), parameterParam,
                                         options, compareClassesResult.getJarArchiveComparator());
  }

  /*
   * TODO: Move th JApiCmpProcessorTest
   * @Test
   */
  void testBreakBuildIfNecessaryMultipleChanges() throws Exception {
    Options options = Options.newDefault();
    JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(
            options);
    ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(
            jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
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
    JApiCmpProcessor processor = new JApiCmpProcessor(null, null, null);
    ConfigParameters parameterParam = new ConfigParameters();
    parameterParam.setBreakBuildOnBinaryIncompatibleModifications(true);
    parameterParam.setBreakBuildOnSourceIncompatibleModifications(true);
    try {
      processor.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), parameterParam,
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
  void testIgnoreMissingVersions()
          throws MojoFailureException, IOException, MojoExecutionException {
    //		JApiCmpMojo mojo = new JApiCmpMojo();
    //		Version oldVersion = createVersion("groupId", "artifactId", "0.1.0");
    //		Version newVersion = createVersion("groupId", "artifactId", "0.1.1");
    //		Parameter parameterParam = new Parameter();
    //		parameterParam.setIgnoreMissingNewVersion(true);
    //		parameterParam.setIgnoreMissingOldVersion(true);
    //		PluginParameters pluginParameters = new PluginParameters(false, newVersion, oldVersion,
    // parameterParam, null, Optional.of(Paths.get(System.getProperty("user.dir"), "target",
    // "simple").toFile()), Optional.<String>absent(), true, null, null, null, null);
    //		ArtifactResolutionResult artifactResolutionResult = mock(ArtifactResolutionResult
    //		.class);
    //		Set<Artifact> artifactSet = new HashSet<>();
    //		when(artifactResolutionResult.getArtifacts()).thenReturn(artifactSet);
    //
    //	when(artifactResolver.resolve(Matchers.<ArtifactResolutionRequest>anyObject())).thenReturn
    //	(artifactResolutionResult);
    //		ArtifactFactory artifactFactory = mock(ArtifactFactory.class);
    //		when(artifactFactory.createArtifactWithClassifier(eq("groupId"), eq("artifactId"),
    // eq("0.1.1"), anyString(), anyString())).thenReturn(mock(Artifact.class));
    //		MojoExecution mojoExecution = mock(MojoExecution.class);
    //		String executionId = "ignoreMissingVersions";
    //		when(mojoExecution.getExecutionId()).thenReturn(executionId);
    //		MavenProject mavenProject = mock(MavenProject.class);
    //		when(mavenProject.getArtifact()).thenReturn(mock(Artifact.class));
    //		MavenParameters mavenParameters = new MavenParameters(new
    //		ArrayList<ArtifactRepository>(),
    // artifactFactory, mock(ArtifactRepository.class), artifactResolver, mavenProject,
    // mojoExecution, "0.0.1", mock(ArtifactMetadataSource.class));
    //		mojo.executeWithParameters(pluginParameters, mavenParameters);
    //		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple",
    // "japicmp", executionId + ".diff")), is(false));
    //		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple",
    // "japicmp", executionId + ".xml")), is(false));
    //		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple",
    // "japicmp", executionId + ".html")), is(false));
  }
}
