package japicmp.output.markdown;

import static japicmp.model.AccessModifier.PRIVATE;
import static japicmp.model.JApiCompatibilityChangeType.FIELD_GENERICS_CHANGED;
import static japicmp.model.JApiCompatibilityChangeType.METHOD_PARAMETER_GENERICS_CHANGED;
import static japicmp.model.JApiCompatibilityChangeType.METHOD_REMOVED;
import static japicmp.model.JApiCompatibilityChangeType.METHOD_RETURN_TYPE_GENERICS_CHANGED;
import static japicmp.model.JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.NOT_SERIALIZABLE;
import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.markdown.config.MarkdownMessageOptions;
import japicmp.output.markdown.config.MarkdownOptions;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtFieldBuilder;
import japicmp.util.CtInterfaceBuilder;
import japicmp.util.CtMethodBuilder;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import org.junit.Assert;
import org.junit.Test;

public class MarkdownOutputGeneratorTest {

	static final String TEST_CLASS_FQN = "japicmp.Test";
	static final String TEST_INTERFACE_NAME = "MyInterface";
	static final String TEST_METHOD_NAME = "method";
	static final String TEST_METHOD_BODY = "return;";
	static final String TEST_FIELD_NAME = "list";
	static final String TEST_TYPE = "java.util.List";
	static final String OLD_VERSION = "1.2.3";
	static final String NEW_VERSION = "1.3.0";
	static final JApiCmpArchive OLD_ARCHIVE = new JApiCmpArchive(new File("my-old-archive.jar"), OLD_VERSION);
	static final JApiCmpArchive NEW_ARCHIVE = new JApiCmpArchive(new File("my-new-archive.jar"), NEW_VERSION);
	static final MarkdownMessageOptions MSG = MarkdownOptions.newDefault().message;

	@Test
	public void testNoChanges() {
		Options options = Options.newDefault();
		options.setOldArchives(singletonList(OLD_ARCHIVE));
		options.setNewArchives(singletonList(NEW_ARCHIVE));
		MarkdownOutputGenerator generator = new MarkdownOutputGenerator(options, emptyList());
		String generated = generator.generate();
		assertThat(generated, containsString(MSG.badgeNoChanges));
		assertThat(generated, containsString(
			format(MSG.summaryNoChanges, format(MSG.oneNewVersion, NEW_VERSION), format(MSG.oneOldVersion, OLD_VERSION))));
	}

	@Test
	public void testWarningWhenIgnoreMissingClasses() {
		Options options = Options.newDefault();
		options.setOldArchives(Arrays.asList(OLD_ARCHIVE, OLD_ARCHIVE));
		options.setNewArchives(Arrays.asList(NEW_ARCHIVE, NEW_ARCHIVE));
		options.setIgnoreMissingClasses(true);
		MarkdownOutputGenerator generator = new MarkdownOutputGenerator(options, emptyList());
		String generated = generator.generate();
		assertThat(generated, containsString(MSG.warningAllMissingClassesIgnored));
		assertThat(generated, containsString(
			format(MSG.summaryNoChanges, MSG.manyNewArchives, MSG.manyOldArchives)));
	}

	@Test
	public void testNoClassFileFormatVersionIfInterfaceRemoved() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass myInterface = CtInterfaceBuilder.create()
					.name(TEST_INTERFACE_NAME)
					.addToClassPool(classPool);
				return singletonList(myInterface);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) {
				return emptyList();
			}
		});
		MarkdownOptions mdOptions = MarkdownOptions.newDefault();
		mdOptions.setTargetOldVersion(OLD_VERSION);
		mdOptions.setTargetNewVersion(NEW_VERSION);
		mdOptions.options.setOldArchives(Arrays.asList(OLD_ARCHIVE, OLD_ARCHIVE));
		mdOptions.options.setNewArchives(Arrays.asList(NEW_ARCHIVE, NEW_ARCHIVE));
		MarkdownOutputGenerator generator = new MarkdownOutputGenerator(mdOptions, jApiClasses);
		String generated = generator.generate();
		assertThat(generated, containsString(
			format(MSG.summaryMajorChanges, format(MSG.oneNewVersion, NEW_VERSION), format(MSG.oneOldVersion, OLD_VERSION))));
		Assert.assertFalse(generated.contains("-1.-1"));
	}

	@Test
	public void testMethodWithGenericTypes() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create()
					.name(TEST_CLASS_FQN)
					.addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create()
					.publicAccess()
					.returnType(classPool.get(TEST_TYPE))
					.name(TEST_METHOD_NAME)
					.parameter(classPool.get(TEST_TYPE))
					.body(TEST_METHOD_BODY)
					.addToClass(ctClass);
				method.setGenericSignature("(Ljava/util/List<Ljava/lang/Integer;>;)Ljava/util/List<Ljava/lang/Byte;>;");
				return singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create()
					.name(TEST_CLASS_FQN)
					.addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create()
					.publicAccess()
					.returnType(classPool.get(TEST_TYPE))
					.name(TEST_METHOD_NAME)
					.parameter(classPool.get(TEST_TYPE))
					.body(TEST_METHOD_BODY)
					.addToClass(ctClass);
				method.setGenericSignature("(Ljava/util/List<Ljava/lang/Long;>;)Ljava/util/List<Ljava/lang/Short;>;");
				return singletonList(ctClass);
			}
		});
		Options options = Options.newDefault();
		MarkdownOutputGenerator generator = new MarkdownOutputGenerator(options, jApiClasses);
		String generated = generator.generate();
		assertThat(generated, containsString(format("\n| %s | [%s] | ![%s] | ![%s] ![%s] |\n",
			MSG.statusSourceIncompatible,
			TEST_CLASS_FQN,
			MSG.serializationCompatibility.get(NOT_SERIALIZABLE),
			MSG.compatibilityChangeType.get(METHOD_RETURN_TYPE_GENERICS_CHANGED),
			MSG.compatibilityChangeType.get(METHOD_PARAMETER_GENERICS_CHANGED))));
		assertThat(generated, containsString(MSG.expandResults));
		assertThat(generated, containsString(format(MSG.compatibilityBinary, MSG.checked)));
		assertThat(generated, containsString(format(MSG.compatibilitySource, MSG.unchecked)));
		assertThat(generated, containsString(format(MSG.compatibilitySerialization, MSG.checked)));
	}

	@Test
	public void testMethodWithGenericTypesReportOnlySummary() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create()
					.name(TEST_CLASS_FQN)
					.addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create()
					.publicAccess()
					.returnType(classPool.get(TEST_TYPE))
					.name(TEST_METHOD_NAME)
					.parameter(classPool.get(TEST_TYPE))
					.body(TEST_METHOD_BODY)
					.addToClass(ctClass);
				method.setGenericSignature("(Ljava/util/List<Ljava/lang/Integer;>;)Ljava/util/List<Ljava/lang/Byte;>;");
				return singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create()
					.name(TEST_CLASS_FQN)
					.addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create()
					.publicAccess()
					.returnType(classPool.get(TEST_TYPE))
					.name(TEST_METHOD_NAME)
					.parameter(classPool.get(TEST_TYPE))
					.body(TEST_METHOD_BODY)
					.addToClass(ctClass);
				method.setGenericSignature("(Ljava/util/List<Ljava/lang/Long;>;)Ljava/util/List<Ljava/lang/Short;>;");
				return singletonList(ctClass);
			}
		});
		Options options = Options.newDefault();
		options.setReportOnlySummary(true);
		MarkdownOutputGenerator generator = new MarkdownOutputGenerator(options, jApiClasses);
		String generated = generator.generate();
		assertThat(generated, containsString(format("\n| %s | %s | ![%s] | ![%s] ![%s] |\n",
			MSG.statusSourceIncompatible,
			TEST_CLASS_FQN,
			MSG.serializationCompatibility.get(NOT_SERIALIZABLE),
			MSG.compatibilityChangeType.get(METHOD_RETURN_TYPE_GENERICS_CHANGED),
			MSG.compatibilityChangeType.get(METHOD_PARAMETER_GENERICS_CHANGED))));
		assertThat(generated, not(containsString(MSG.expandResults)));
		assertThat(generated, not(containsString(format(MSG.compatibilityBinary, MSG.checked))));
		assertThat(generated, not(containsString(format(MSG.compatibilitySource, MSG.unchecked))));
		assertThat(generated, not(containsString(format(MSG.compatibilitySerialization, MSG.checked))));
	}

	@Test
	public void testMethodWithGenericTypesRemoved() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create()
					.name(TEST_CLASS_FQN)
					.addToClassPool(classPool);
				CtMethod method = CtMethodBuilder.create()
					.publicAccess()
					.returnType(classPool.get(TEST_TYPE))
					.name(TEST_METHOD_NAME)
					.parameter(classPool.get(TEST_TYPE))
					.body(TEST_METHOD_BODY)
					.addToClass(ctClass);
				method.setGenericSignature("(Ljava/util/List<Ljava/lang/Integer;>;)Ljava/util/List<Ljava/lang/Byte;>;");
				return singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) {
				CtClass ctClass = CtClassBuilder.create()
					.name(TEST_CLASS_FQN)
					.addToClassPool(classPool);
				return singletonList(ctClass);
			}
		});
		Options options = Options.newDefault();
		MarkdownOutputGenerator generator = new MarkdownOutputGenerator(options, jApiClasses);
		String generated = generator.generate();
		assertThat(generated, containsString(format("\n| %s | [%s] | ![%s] | ![%s]     |\n",
			MSG.statusModified,
			TEST_CLASS_FQN,
			MSG.serializationCompatibility.get(NOT_SERIALIZABLE),
			MSG.compatibilityChangeType.get(METHOD_REMOVED))));
		assertThat(generated, containsString(format(MSG.compatibilityBinary, MSG.unchecked)));
		assertThat(generated, containsString(format(MSG.compatibilitySource, MSG.unchecked)));
		assertThat(generated, containsString(format(MSG.compatibilitySerialization, MSG.checked)));
	}

	@Test
	public void testFieldWithGenericTypes() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create()
					.name(TEST_CLASS_FQN)
					.addToClassPool(classPool);
				CtField ctField = CtFieldBuilder.create()
					.type(classPool.get(TEST_TYPE))
					.name(TEST_FIELD_NAME)
					.addToClass(ctClass);
				ctField.setGenericSignature("Ljava/util/List<Ljava/lang/Byte;>;");
				return singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create()
					.name(TEST_CLASS_FQN)
					.addToClassPool(classPool);
				CtField ctField = CtFieldBuilder.create()
					.type(classPool.get(TEST_TYPE))
					.name(TEST_FIELD_NAME)
					.addToClass(ctClass);
				ctField.setGenericSignature("Ljava/util/List<Ljava/lang/Short;>;");
				return singletonList(ctClass);
			}
		});
		Options options = Options.newDefault();
		MarkdownOutputGenerator generator = new MarkdownOutputGenerator(options, jApiClasses);
		String generated = generator.generate();
		assertThat(generated, containsString(format("\n| %s | [%s] | ![%s] | ![%s] |\n",
			MSG.statusSourceIncompatible,
			TEST_CLASS_FQN,
			MSG.serializationCompatibility.get(NOT_SERIALIZABLE),
			MSG.compatibilityChangeType.get(FIELD_GENERICS_CHANGED))));
		assertThat(generated, containsString(format(MSG.compatibilityBinary, MSG.checked)));
		assertThat(generated, containsString(format(MSG.compatibilitySource, MSG.unchecked)));
		assertThat(generated, containsString(format(MSG.compatibilitySerialization, MSG.checked)));
	}
}
