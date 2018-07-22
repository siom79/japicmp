/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package japicmp.output.incompatible;

import japicmp.cmp.ClassesHelper;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtFieldBuilder;
import japicmp.util.CtInterfaceBuilder;
import japicmp.util.CtMethodBuilder;
import japicmp.util.Helper;
import japicmp.util.Optional;
import javassist.ClassPool;
import javassist.CtClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class IncompatibleErrorOutputTest {

	@Test
	public void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(false);
	}

	@Test(expected = JApiCmpException.class)
	public void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusionTrue() throws Exception {
		testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(true);
	}

	private void testBreakBuildIfNecessaryFieldTypeChangedCausedByExclusion(boolean breakBuildIfCausedByExclusion) throws Exception {
		Options options = Options.newDefault();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult result = ClassesHelper.compareClassesWithResult(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
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
		options.addExcludeFromArgument(Optional.of("japicmp.FieldType"), false); // exclude japicmp.FieldType
		options.setErrorOnBinaryIncompatibility(true);
		options.setErrorOnSourceIncompatibility(true);
		options.setErrorOnExclusionIncompatibility(breakBuildIfCausedByExclusion);
		new IncompatibleErrorOutput(options, result.getjApiClasses(), result.getJarArchiveComparator()).generate();
	}

	@Test
	public void testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusion(false);
	}

	@Test(expected = JApiCmpException.class)
	public void testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusionTrue() throws Exception {
		testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusion(true);
	}

	private void testBreakBuildIfNecessaryInterfaceRemovedCausedByExclusion(boolean breakBuildIfCausedByExclusion) throws Exception {
		Options options = Options.newDefault();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult result = ClassesHelper.compareClassesWithResult(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
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
		options.addExcludeFromArgument(Optional.of("japicmp.ITest"), false); // exclude japicmp.ITest
		options.setErrorOnBinaryIncompatibility(true);
		options.setErrorOnSourceIncompatibility(true);
		options.setErrorOnExclusionIncompatibility(breakBuildIfCausedByExclusion);
		new IncompatibleErrorOutput(options, result.getjApiClasses(), result.getJarArchiveComparator()).generate();
	}

	@Test
	public void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(false);
	}

	@Test(expected = JApiCmpException.class)
	public void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusionTrue() throws Exception {
		testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(true);
	}

	private void testBreakBuildIfNecessaryMethodReturnTypeChangedCausedByExclusion(boolean breakBuildIfCausedByExclusion) throws Exception {
		Options options = Options.newDefault();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult result = ClassesHelper.compareClassesWithResult(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
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
		options.addExcludeFromArgument(Optional.of("japicmp.MethodReturnType"), false); // exclude japicmp.MethodReturnType
		options.setErrorOnBinaryIncompatibility(true);
		options.setErrorOnSourceIncompatibility(true);
		options.setErrorOnExclusionIncompatibility(breakBuildIfCausedByExclusion);
		new IncompatibleErrorOutput(options, result.getjApiClasses(), result.getJarArchiveComparator()).generate();
	}

	@Test
	public void testBreakBuildIfNecessarySuperclassChangedCausedByExclusionFalse() throws Exception {
		testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(false);
	}

	@Test(expected = JApiCmpException.class)
	public void testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusionTrue() throws Exception {
		testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(true);
	}

	private void testBreakBuildIfNecessarySuperclassTypeChangedCausedByExclusion(boolean breakBuildIfCausedByExclusion) throws Exception {
		Options options = Options.newDefault();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult result = ClassesHelper.compareClassesWithResult(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
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
		options.addExcludeFromArgument(Optional.of("japicmp.SuperType"), false); // exclude japicmp.SuperType
		options.setErrorOnBinaryIncompatibility(true);
		options.setErrorOnSourceIncompatibility(true);
		options.setErrorOnExclusionIncompatibility(breakBuildIfCausedByExclusion);
		new IncompatibleErrorOutput(options, result.getjApiClasses(), result.getJarArchiveComparator()).generate();
	}

	@Test(expected = JApiCmpException.class)
	public void testSemVerMinorChangeMissingOldVersion() throws Exception {
		testMissingVersions("1.0.0", true, "1.1.0", false);
	}

	@Test
	public void testSemVerMajjorChangeMissingOldVersion() throws Exception {
		testMissingVersions("1.0.0", true, "2.0.0", false);
	}

	@Test(expected = JApiCmpException.class)
	public void testSemVerMinorChangeMissingNewVersion() throws Exception {
		testMissingVersions("1.0.0", true, "1.1.0",false);
	}

	@Test
	public void testSemVerMajorChangeMissingNewVersion() throws Exception {
		testMissingVersions("1.0.0", true, "2.0.0",false);
	}

	@Test(expected = JApiCmpException.class)
	public void testSemVerMinorChange() throws Exception {
		testMissingVersions("1.0.0", false, "1.1.0", false);
	}

	@Test
	public void testSemVerMajorChange() throws Exception {
		testMissingVersions("1.0.0", false, "2.0.0", false);
	}

	private void testMissingVersions(String oldVersion, boolean missingOld, String newVersion, boolean missingNew) throws Exception {
		Options options = Options.newDefault();
		JarArchiveComparatorOptions jarArchiveComparatorOptions = JarArchiveComparatorOptions.of(options);
		ClassesHelper.CompareClassesResult result = ClassesHelper.compareClassesWithResult(jarArchiveComparatorOptions, new ClassesHelper.ClassesGenerator() {
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
		options.addExcludeFromArgument(Optional.of("japicmp.SuperType"), false); // exclude japicmp.SuperType

		options.setErrorOnSemanticIncompatibility(true);
		options.setIgnoreMissingNewVersion(missingNew);
		options.setIgnoreMissingOldVersion(missingOld);

		JApiCmpArchive oldFile = Helper.getArchive("japicmp-test-v"+oldVersion+".jar", oldVersion);
		options.setOldArchives(Collections.singletonList(oldFile));
		JApiCmpArchive newFile = Helper.getArchive("japicmp-test-v"+newVersion+".jar", newVersion);
		options.setNewArchives(Collections.singletonList(newFile));
		new IncompatibleErrorOutput(options, result.getjApiClasses(), result.getJarArchiveComparator()).generate();
	}
}
