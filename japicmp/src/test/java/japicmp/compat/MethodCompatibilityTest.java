package japicmp.compat;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.model.*;
import japicmp.util.*;
import javassist.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

class MethodCompatibilityTest {

	@Test
	void testMethodRemoved() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "isRemoved");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_REMOVED)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
		JApiSuperclass superclass = jApiClass.getSuperclass();
		Assertions.assertEquals("japicmp.Test", superclass.getJApiClassOwning().getFullyQualifiedName());
		Assertions.assertEquals("java.lang.Object", superclass.getCorrespondingJApiClass().get().getFullyQualifiedName());
		assertThat(superclass.isBinaryCompatible(), is(true));
		assertThat(superclass.getCompatibilityChanges().size(), is(0));
	}

	@Test
	void testMethodLessAccessiblePublicToPrivate() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().privateAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "isRemoved");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_LESS_ACCESSIBLE)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	void testMethodLessAccessibleThanInSuperclass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(superclass, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				CtMethodBuilder.create().protectedAccess().returnType(CtClass.booleanType).name("isRemoved").body("return true;").addToClass(ctClass);
				return Arrays.asList(superclass, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "isRemoved");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_LESS_ACCESSIBLE_THAN_IN_SUPERCLASS)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	void testMethodStaticOverridesNonStatic() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isOverridden").body("return true;").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				return Arrays.asList(superclass, ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superclass = CtClassBuilder.create().name("japicmp.Superclass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("isOverridden").body("return true;").addToClass(superclass);
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").withSuperclass(superclass).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().staticAccess().returnType(CtClass.booleanType).name("isOverridden").body("return true;").addToClass(ctClass);
				return Arrays.asList(superclass, ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "isOverridden");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_IS_STATIC_AND_OVERRIDES_NOT_STATIC)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	void testMethodReturnTypeChanges() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.intType).name("returnTypeChanges").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("returnTypeChanges").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "returnTypeChanges");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_RETURN_TYPE_CHANGED)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	void testMethodReturnTypeChangesAndVisibilityIncreasesProtectedToPublic() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().protectedAccess().returnType(CtClass.intType).name("returnTypeChanges").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("returnTypeChanges").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "returnTypeChanges");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_RETURN_TYPE_CHANGED)));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
	}

	@Test
	void testMethodReturnTypeChangesAndVisibilityIncreasesPrivateToProtected() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().privateAccess().returnType(CtClass.intType).name("returnTypeChanges").body("return 42;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().protectedAccess().returnType(CtClass.booleanType).name("returnTypeChanges").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "returnTypeChanges");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_RETURN_TYPE_CHANGED)));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
	}

	@Test
	void testMethodReturnTypeCovariantChange() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.lang.Object")).name("getValue").body("return null;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				// Covariant override — Integer is a subtype of Object
				CtMethodBuilder.create().publicAccess().returnType(classPool.get("java.lang.Integer")).name("getValue").body("return Integer.valueOf(0);").addToClass(ctClass);
				// Compiler-generated bridge method with original return type
				CtMethodBuilder.create().publicAccess().bridgeModifier().syntheticModifier().returnType(classPool.get("java.lang.Object")).name("getValue").body("return null;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		JApiMethod nonBridgeMethod = null;
		for (JApiMethod m : jApiClass.getMethods()) {
			if (m.getName().equals("getValue") &&
				m.getBridgeModifier().getNewModifier().map(bm -> bm == BridgeModifier.NON_BRIDGE).orElse(false)) {
				nonBridgeMethod = m;
				break;
			}
		}
		assertThat(nonBridgeMethod, is(notNullValue()));
		assertThat(nonBridgeMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_RETURN_TYPE_COVARIANT_CHANGED)));
		assertThat(nonBridgeMethod.getCompatibilityChanges(), not(hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_RETURN_TYPE_CHANGED))));
		assertThat(nonBridgeMethod.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
	}

	@Test
	void testMethodNowAbstract() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("methodBecomesAbstract").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().abstractMethod().returnType(CtClass.booleanType).name("methodBecomesAbstract").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodBecomesAbstract");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NOW_ABSTRACT)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	void testMethodNowFinal() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("methodBecomesFinal").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().finalMethod().returnType(CtClass.booleanType).name("methodBecomesFinal").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodBecomesFinal");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NOW_FINAL)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	void testMethodRemainsEffectivelyFinal() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").finalModifier().addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("effectivelyFinal").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").finalModifier().addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().finalMethod().returnType(CtClass.booleanType).name("effectivelyFinal").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "effectivelyFinal");
		assertThat(jApiMethod.getCompatibilityChanges().size(), is(0));
		assertThat(jApiClass.isSourceCompatible(), is(true));
	}

	@Test
	void testMethodNowStatic() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("methodBecomesStatic").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().staticAccess().returnType(CtClass.booleanType).name("methodBecomesStatic").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodBecomesStatic");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NOW_STATIC)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	void testMethodNoLongerStatic() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().staticAccess().returnType(CtClass.booleanType).name("methodNoLongerStatic").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().returnType(CtClass.booleanType).name("methodNoLongerStatic").body("return true;").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodNoLongerStatic");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NO_LONGER_STATIC)));
		assertThat(jApiMethod.isBinaryCompatible(), is(false));
	}

	@Test
	void testMethodNowVarargs() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass arrayClass = classPool.getCtClass("[I");

				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().parameter(arrayClass).name("methodNowVarargs").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass arrayClass = classPool.getCtClass("[I");

				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().modifier(Modifier.VARARGS).publicAccess().parameter(arrayClass).name("methodNowVarargs").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodNowVarargs");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NOW_VARARGS)));
	}

	@Test
	void testMethodNoLongerVarargs() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass arrayClass = classPool.getCtClass("[I");

				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().modifier(Modifier.VARARGS).publicAccess().parameter(arrayClass).name("methodNoLongerVarargs").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass arrayClass = classPool.getCtClass("[I");

				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().parameter(arrayClass).name("methodNoLongerVarargs").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(false));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "methodNoLongerVarargs");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NO_LONGER_VARARGS)));
	}

	@Test
	void testMethodAddedToPublicClass() throws Exception {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setIncludeSynthetic(true);
		options.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				return Collections.singletonList(ctClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").addToClass(ctClass);
				return Collections.singletonList(ctClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "japicmp.Test");
		assertThat(jApiClass.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiClass.isBinaryCompatible(), is(true));
		assertThat(jApiClass.isSourceCompatible(), is(true));
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_ADDED_TO_PUBLIC_CLASS)));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
	}

	@Test
	void testMoveMethodToSuperclassAndMakeFinal() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("SuperClass").addToClassPool(classPool);
				CtClass subClass = CtClassBuilder.create().name("SubClass").withSuperclass(superClass).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").addToClass(subClass);
				return Arrays.asList(superClass, subClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("SuperClass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().finalMethod().name("method").addToClass(superClass);
				CtClass subClass = CtClassBuilder.create().name("SubClass").withSuperclass(superClass).addToClassPool(classPool);
				return Arrays.asList(superClass, subClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "SubClass");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NOW_FINAL)));
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_MOVED_TO_SUPERCLASS)));
		jApiClass = getJApiClass(jApiClasses, "SuperClass");
		jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getCompatibilityChanges(), hasItem(new JApiCompatibilityChange(JApiCompatibilityChangeType.METHOD_NOW_FINAL)));
	}

	@Test
	void testRemoveMethodInSubClassAndSuperClass() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass superClass = CtClassBuilder.create().name("SuperClass").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").addToClass(superClass);
				CtClass subClass = CtClassBuilder.create().name("SubClass").withSuperclass(superClass).addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().name("method").addToClass(subClass);
				return Arrays.asList(superClass, subClass);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) {
				CtClass superClass = CtClassBuilder.create().name("SuperClass").addToClassPool(classPool);
				CtClass subClass = CtClassBuilder.create().name("SubClass").withSuperclass(superClass).addToClassPool(classPool);
				return Arrays.asList(superClass, subClass);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "SubClass");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
		jApiClass = getJApiClass(jApiClasses, "SuperClass");
		jApiMethod = getJApiMethod(jApiClass.getMethods(), "method");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.REMOVED));
	}

	@Test
	void testPackagePrivateClassChangesToPublicClassWithMethodReturnTypeChangeIssue365() throws Exception {
		JarArchiveComparatorOptions jarArchiveComparatorOptions = new JarArchiveComparatorOptions();
		jarArchiveComparatorOptions.setAccessModifier(AccessModifier.PRIVATE);
		List<JApiClass> jApiClasses = ClassesHelper.compareClasses(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
			@Override
			public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
				CtClass ctInterfaceF = CtInterfaceBuilder.create().name("F").addToClassPool(classPool);
				CtClass ctClassC = CtClassBuilder.create().name("C").notPublicModifier().addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().staticAccess().name("M").returnType(ctInterfaceF).addToClass(ctClassC);
				return Arrays.asList(ctInterfaceF, ctClassC);
			}

			@Override
			public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
				CtClass ctInterfaceF = CtInterfaceBuilder.create().name("F").addToClassPool(classPool);
				CtClass ctClassFImpl = CtClassBuilder.create().name("FImpl").implementsInterface(ctInterfaceF).addToClassPool(classPool);
				CtClass ctClassC = CtClassBuilder.create().name("C").addToClassPool(classPool);
				CtMethodBuilder.create().publicAccess().staticAccess().name("M").returnType(ctClassFImpl).addToClass(ctClassC);
				return Arrays.asList(ctInterfaceF, ctClassC);
			}
		});
		JApiClass jApiClass = getJApiClass(jApiClasses, "C");
		JApiMethod jApiMethod = getJApiMethod(jApiClass.getMethods(), "M");
		assertThat(jApiMethod.getChangeStatus(), is(JApiChangeStatus.MODIFIED));
		assertThat(jApiMethod.isBinaryCompatible(), is(true));
		assertThat(jApiMethod.isSourceCompatible(), is(true));
		assertThat(jApiMethod.getCompatibilityChanges().size(), is(0));
	}
}
