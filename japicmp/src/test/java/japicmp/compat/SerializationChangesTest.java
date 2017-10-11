package japicmp.compat;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.JApiChangeStatus;
import japicmp.model.JApiClass;
import japicmp.model.JApiClassType;
import japicmp.model.JApiJavaObjectSerializationCompatibility;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtFieldBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.getJApiClass;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SerializationChangesTest {

	@Test
	public void testEnumUnchanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().enumModifier().name("japicmp.Test").withSuperclass(classPool.get(Enum.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(ctClass).staticAccess().finalAccess().name("OPTION1").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().enumModifier().name("japicmp.Test").withSuperclass(classPool.get(Enum.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(ctClass).staticAccess().finalAccess().name("OPTION1").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getNewTypeOptional().get(), is(JApiClassType.ClassType.ENUM));
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getSerialVersionUid().isSerializableOld(), is(true));
		assertThat(jApiClass.getSerialVersionUid().isSerializableNew(), is(true));
		assertThat(jApiClass.getJavaObjectSerializationCompatible().isIncompatible(), is(false));
	}

	@Test
	public void testEnumElementAdded() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().enumModifier().name("japicmp.Test").withSuperclass(classPool.get(Enum.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(ctClass).staticAccess().finalAccess().name("OPTION1").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().enumModifier().name("japicmp.Test").withSuperclass(classPool.get(Enum.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(ctClass).staticAccess().finalAccess().name("OPTION1").addToClass(ctClass);
				CtFieldBuilder.create().type(ctClass).staticAccess().finalAccess().name("OPTION2").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getNewTypeOptional().get(), is(JApiClassType.ClassType.ENUM));
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getSerialVersionUid().isSerializableOld(), is(true));
		assertThat(jApiClass.getSerialVersionUid().isSerializableNew(), is(true));
		assertThat(jApiClass.getJavaObjectSerializationCompatible().isIncompatible(), is(false));
	}

	@Test
	public void testEnumElementRemoved() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().enumModifier().name("japicmp.Test").withSuperclass(classPool.get(Enum.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(ctClass).staticAccess().finalAccess().name("OPTION1").addToClass(ctClass);
				CtFieldBuilder.create().type(ctClass).staticAccess().finalAccess().name("OPTION2").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().enumModifier().name("japicmp.Test").withSuperclass(classPool.get(Enum.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(ctClass).staticAccess().finalAccess().name("OPTION1").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getNewTypeOptional().get(), is(JApiClassType.ClassType.ENUM));
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getSerialVersionUid().isSerializableOld(), is(true));
		assertThat(jApiClass.getSerialVersionUid().isSerializableNew(), is(true));
		assertThat(jApiClass.getJavaObjectSerializationCompatible().isIncompatible(), is(true));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_FIELD_REMOVED));
	}

	@Test
	 public void testClassCompatible() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(classPool.get(Serializable.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).staticAccess().finalAccess().name("CONST").addToClass(ctClass);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(classPool.get(Serializable.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).staticAccess().finalAccess().name("CONST").addToClass(ctClass);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getNewTypeOptional().get(), is(JApiClassType.ClassType.CLASS));
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getSerialVersionUid().isSerializableOld(), is(true));
		assertThat(jApiClass.getSerialVersionUid().isSerializableNew(), is(true));
		assertThat(jApiClass.getJavaObjectSerializationCompatible().isIncompatible(), is(false));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
	}

	@Test
	public void testClassCompatibleWithSerialVersionUid() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(classPool.get(Serializable.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).staticAccess().finalAccess().name("CONST").addToClass(ctClass);
				CtFieldBuilder.create().type(CtClass.longType).staticAccess().finalAccess().name("serialVersionUID").withConstantValue(1L).addToClass(ctClass);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(classPool.get(Serializable.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).staticAccess().finalAccess().name("CONST").addToClass(ctClass);
				CtFieldBuilder.create().type(CtClass.longType).staticAccess().finalAccess().name("serialVersionUID").withConstantValue(1L).addToClass(ctClass);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getNewTypeOptional().get(), is(JApiClassType.ClassType.CLASS));
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getSerialVersionUid().isSerializableOld(), is(true));
		assertThat(jApiClass.getSerialVersionUid().isSerializableNew(), is(true));
		assertThat(jApiClass.getJavaObjectSerializationCompatible().isIncompatible(), is(false));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_COMPATIBLE));
	}

	@Test
	public void testClassSerialVersionUidChanged() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(classPool.get(Serializable.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).staticAccess().finalAccess().name("CONST").addToClass(ctClass);
				CtFieldBuilder.create().type(CtClass.longType).staticAccess().finalAccess().name("serialVersionUID").withConstantValue(1L).addToClass(ctClass);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(classPool.get(Serializable.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).staticAccess().finalAccess().name("CONST").addToClass(ctClass);
				CtFieldBuilder.create().type(CtClass.longType).staticAccess().finalAccess().name("serialVersionUID").withConstantValue(2L).addToClass(ctClass);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getNewTypeOptional().get(), is(JApiClassType.ClassType.CLASS));
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getSerialVersionUid().isSerializableOld(), is(true));
		assertThat(jApiClass.getSerialVersionUid().isSerializableNew(), is(true));
		assertThat(jApiClass.getJavaObjectSerializationCompatible().isIncompatible(), is(true));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SERIALVERSIONUID_MODIFIED));
	}

	@Test
	public void testClassSerialVersionUidRemovedAndNotMatchesNewDefault() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(classPool.get(Serializable.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).staticAccess().finalAccess().name("CONST").addToClass(ctClass);
				CtFieldBuilder.create().type(CtClass.longType).staticAccess().finalAccess().name("serialVersionUID").withConstantValue(1L).addToClass(ctClass);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").implementsInterface(classPool.get(Serializable.class.getName())).addToClassPool(classPool);
				CtFieldBuilder.create().type(CtClass.intType).staticAccess().finalAccess().name("CONST").addToClass(ctClass);
				CtMethodBuilder.create().returnType(CtClass.intType).name("method").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getClassType().getChangeStatus(), is(JApiChangeStatus.UNCHANGED));
		assertThat(jApiClass.getClassType().getNewTypeOptional().get(), is(JApiClassType.ClassType.CLASS));
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.getSerialVersionUid().isSerializableOld(), is(true));
		assertThat(jApiClass.getSerialVersionUid().isSerializableNew(), is(true));
		assertThat(jApiClass.getJavaObjectSerializationCompatible().isIncompatible(), is(true));
		assertThat(jApiClass.getJavaObjectSerializationCompatible(), is(JApiJavaObjectSerializationCompatibility.JApiJavaObjectSerializationChangeStatus.SERIALIZABLE_INCOMPATIBLE_SERIALVERSIONUID_REMOVED_AND_NOT_MATCHES_NEW_DEFAULT));
	}
}
