package japicmp.maven;

import com.google.common.base.Optional;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.cmp.ClassesHelper;
import japicmp.maven.util.CtClassBuilder;
import japicmp.maven.util.CtFieldBuilder;
import japicmp.maven.util.CtInterfaceBuilder;
import japicmp.maven.util.CtMethodBuilder;
import japicmp.model.JApiClass;
import javassist.ClassPool;
import javassist.CtClass;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.maven.artifact.metadata.ArtifactMetadataSource;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.artifact.resolver.ArtifactResolver;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.junit.Test;
import org.mockito.Matchers;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class JApiCmpMojoTest {

	@Test
	public void testSimple() throws MojoFailureException {
		JApiCmpMojo mojo = new JApiCmpMojo();
		Version oldVersion = createVersion("groupId", "artifactId", "0.1.0");
		Version newVersion = createVersion("groupId", "artifactId", "0.1.1");
		PluginParameters pluginParameters = new PluginParameters(null, newVersion, oldVersion, new Parameter(), null, Optional.of(Paths.get(System.getProperty("user.dir"), "target", "simple").toFile()), Optional.<String>absent(), true, null, null, null, null);
		ArtifactResolver artifactResolver = mock(ArtifactResolver.class);
		ArtifactResolutionResult artifactResolutionResult = mock(ArtifactResolutionResult.class);
		Set<Artifact> artifactSet = new HashSet<>();
		Artifact resolvedArtifact = mock(Artifact.class);
		artifactSet.add(resolvedArtifact);
		when(resolvedArtifact.getFile()).thenReturn(Paths.get(System.getProperty("user.dir"), "target", "guava-18.0.jar").toFile());
		when(artifactResolutionResult.getArtifacts()).thenReturn(artifactSet);
		when(artifactResolver.resolve(Matchers.<ArtifactResolutionRequest>anyObject())).thenReturn(artifactResolutionResult);
		ArtifactFactory artifactFactory = mock(ArtifactFactory.class);
		when(artifactFactory.createArtifactWithClassifier(eq("groupId"), eq("artifactId"), eq("0.1.1"), anyString(), anyString())).thenReturn(mock(Artifact.class));
		MavenParameters mavenParameters = new MavenParameters(new ArrayList<ArtifactRepository>(), artifactFactory, mock(ArtifactRepository.class), artifactResolver, mock(MavenProject.class), mock(MojoExecution.class), "0.0.1", mock(ArtifactMetadataSource.class));
		mojo.executeWithParameters(pluginParameters, mavenParameters);
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple", "japicmp", "japicmp.diff")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple", "japicmp", "japicmp.xml")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "simple", "japicmp", "japicmp.html")), is(true));
	}

	@Test
	public void testNoXmlAndNoHtmlReport() throws MojoFailureException {
		JApiCmpMojo mojo = new JApiCmpMojo();
		Version oldVersion = createVersion("groupId", "artifactId", "0.1.0");
		Version newVersion = createVersion("groupId", "artifactId", "0.1.1");
		Parameter parameter = new Parameter();
		parameter.setSkipHtmlReport("true");
		parameter.setSkipXmlReport("true");
		PluginParameters pluginParameters = new PluginParameters(null, newVersion, oldVersion, parameter, null, Optional.of(Paths.get(System.getProperty("user.dir"), "target", "noXmlAndNoHtmlReport").toFile()), Optional.<String>absent(), true, null, null, null, null);
		ArtifactResolver artifactResolver = mock(ArtifactResolver.class);
		ArtifactResolutionResult artifactResolutionResult = mock(ArtifactResolutionResult.class);
		Set<Artifact> artifactSet = new HashSet<>();
		Artifact resolvedArtifact = mock(Artifact.class);
		artifactSet.add(resolvedArtifact);
		when(resolvedArtifact.getFile()).thenReturn(Paths.get(System.getProperty("user.dir"), "target", "guava-18.0.jar").toFile());
		when(artifactResolutionResult.getArtifacts()).thenReturn(artifactSet);
		when(artifactResolver.resolve(Matchers.<ArtifactResolutionRequest>anyObject())).thenReturn(artifactResolutionResult);
		ArtifactFactory artifactFactory = mock(ArtifactFactory.class);
		when(artifactFactory.createArtifactWithClassifier(eq("groupId"), eq("artifactId"), eq("0.1.1"), anyString(), anyString())).thenReturn(mock(Artifact.class));
		MavenParameters mavenParameters = new MavenParameters(new ArrayList<ArtifactRepository>(), artifactFactory, mock(ArtifactRepository.class), artifactResolver, mock(MavenProject.class), mock(MojoExecution.class), "0.0.1", mock(ArtifactMetadataSource.class));
		mojo.executeWithParameters(pluginParameters, mavenParameters);
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "noXmlAndNoHtmlReport", "japicmp", "japicmp.diff")), is(true));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "noXmlAndNoHtmlReport", "japicmp", "japicmp.xml")), is(false));
		assertThat(Files.exists(Paths.get(System.getProperty("user.dir"), "target", "noXmlAndNoHtmlReport", "japicmp", "japicmp.html")), is(false));
	}

	private Version createVersion(String groupId, String artifactId, String version) {
		Version versionInstance = new Version();
		Dependency dependency = new Dependency();
		dependency.setGroupId(groupId);
		dependency.setArtifactId(artifactId);
		dependency.setVersion(version);
		versionInstance.setDependency(dependency);
		return versionInstance;
	}

	@Test
	public void testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusion(false);
	}

	@Test(expected = MojoFailureException.class)
	public void testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusionTrue() throws Exception {
		testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusion(true);
	}

	private void testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusion(boolean breakBuildIfCausedByExclusion) throws Exception {
		Options options = Options.newDefault();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass interfaceCtClass = CtInterfaceBuilder.create().name("japicmp.ITest").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(interfaceCtClass).addToClassPool(classPool);
				return Arrays.asList(interfaceCtClass, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass interfaceCtClass = CtInterfaceBuilder.create().name("japicmp.ITest").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Arrays.asList(interfaceCtClass, ctClass);
			}
		});
		options.addExcludeFromArgument(Optional.of("japicmp.ITest")); // exclude japicmp.ITest
		JApiCmpMojo mojo = new JApiCmpMojo();
		Parameter parameterParam = new Parameter();
		parameterParam.setBreakBuildIfCausedByExclusion(breakBuildIfCausedByExclusion); //do not break the build if cause is excluded
		parameterParam.setBreakBuildOnBinaryIncompatibleModifications("true");
		parameterParam.setBreakBuildOnSourceIncompatibleModifications("true");
		mojo.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), parameterParam, options, new JarArchiveComparator(jarArchiveComparatorOptions));
	}

	@Test
	public void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(false);
	}

	@Test(expected = MojoFailureException.class)
	public void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusionTrue() throws Exception {
		testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(true);
	}

	private void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(boolean breakBuildIfCausedByExclusion) throws Exception {
		Options options = Options.newDefault();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass fieldTypeCtClass = CtClassBuilder.create().name("japicmp.FieldType").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().type(fieldTypeCtClass).name("field").addToClass(ctClass);
				return Arrays.asList(fieldTypeCtClass, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass fieldTypeCtClass = CtClassBuilder.create().name("japicmp.FieldType").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtFieldBuilder.create().type(classPool.get("java.lang.String")).name("field").addToClass(ctClass);
				return Arrays.asList(fieldTypeCtClass, ctClass);
			}
		});
		options.addExcludeFromArgument(Optional.of("japicmp.FieldType")); // exclude japicmp.FieldType
		JApiCmpMojo mojo = new JApiCmpMojo();
		Parameter parameterParam = new Parameter();
		parameterParam.setBreakBuildIfCausedByExclusion(breakBuildIfCausedByExclusion); //do not break the build if cause is excluded
		parameterParam.setBreakBuildOnBinaryIncompatibleModifications("true");
		parameterParam.setBreakBuildOnSourceIncompatibleModifications("true");
		mojo.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), parameterParam, options, compareClassesResult.getJarArchiveComparator());
	}

	@Test
	public void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(false);
	}

	@Test(expected = MojoFailureException.class)
	public void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusionTrue() throws Exception {
		testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(true);
	}

	private void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(boolean breakBuildIfCausedByExclusion) throws Exception {
		Options options = Options.newDefault();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass typeCtClass = CtClassBuilder.create().name("japicmp.MethodReturnType").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(typeCtClass).name("test").addToClass(ctClass);
				return Arrays.asList(typeCtClass, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass typeCtClass = CtClassBuilder.create().name("japicmp.MethodReturnType").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.lang.String")).name("test").addToClass(ctClass);
				return Arrays.asList(typeCtClass, ctClass);
			}
		});
		options.addExcludeFromArgument(Optional.of("japicmp.MethodReturnType")); // exclude japicmp.MethodReturnType
		JApiCmpMojo mojo = new JApiCmpMojo();
		Parameter parameterParam = new Parameter();
		parameterParam.setBreakBuildIfCausedByExclusion(breakBuildIfCausedByExclusion); //do not break the build if cause is excluded
		parameterParam.setBreakBuildOnBinaryIncompatibleModifications("true");
		parameterParam.setBreakBuildOnSourceIncompatibleModifications("true");
		mojo.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), parameterParam, options, compareClassesResult.getJarArchiveComparator());
	}

	@Test
	public void testBreakBuildIfNecessarySuperclassChangedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(false);
	}

	@Test(expected = MojoFailureException.class)
	public void testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusionTrue() throws Exception {
		testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(true);
	}

	private void testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(boolean breakBuildIfCausedByExclusion) throws Exception {
		Options options = Options.newDefault();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass typeCtClass = CtClassBuilder.create().name("japicmp.SuperType").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(typeCtClass).addToClassPool(classPool);
				return Arrays.asList(typeCtClass, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass typeCtClass = CtClassBuilder.create().name("japicmp.SuperType").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(classPool.get("java.lang.String")).addToClassPool(classPool);
				return Arrays.asList(typeCtClass, ctClass);
			}
		});
		options.addExcludeFromArgument(Optional.of("japicmp.SuperType")); // exclude japicmp.SuperType
		JApiCmpMojo mojo = new JApiCmpMojo();
		Parameter parameterParam = new Parameter();
		parameterParam.setBreakBuildIfCausedByExclusion(breakBuildIfCausedByExclusion); //do not break the build if cause is excluded
		parameterParam.setBreakBuildOnBinaryIncompatibleModifications("true");
		parameterParam.setBreakBuildOnSourceIncompatibleModifications("true");
		mojo.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), parameterParam, options, compareClassesResult.getJarArchiveComparator());
	}

	@Test
	public void testBreakBuildIfNecessaryMultipleChanges() throws Exception {
		Options options = Options.newDefault();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult compareClassesResult = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass typeCtClass = CtClassBuilder.create().name("japicmp.SuperType").addToClassPool(classPool);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(typeCtClass).addToClassPool(classPool);
				CtFieldBuilder.create().name("field").type(CtClass.intType).addToClass(ctClass);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("method").addToClass(ctClass);
				return Arrays.asList(typeCtClass, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiCmpMojo mojo = new JApiCmpMojo();
		Parameter parameterParam = new Parameter();
		parameterParam.setBreakBuildOnBinaryIncompatibleModifications("true");
		parameterParam.setBreakBuildOnSourceIncompatibleModifications("true");
		try {
			mojo.breakBuildIfNecessary(compareClassesResult.getjApiClasses(), parameterParam, options, compareClassesResult.getJarArchiveComparator());
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
