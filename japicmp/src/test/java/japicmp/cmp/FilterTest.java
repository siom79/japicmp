package japicmp.cmp;

import japicmp.filter.BehaviorFilter;
import japicmp.filter.ClassFilter;
import japicmp.filter.PackageFilter;
import japicmp.model.JApiClass;
import japicmp.model.JApiMethod;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.ClassPool;
import javassist.CtClass;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static japicmp.util.Helper.*;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class FilterTest {

    @Test
    public void testOneClassNoExclude() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
                return Collections.singletonList(ctClass);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
                return Collections.singletonList(ctClass);
            }
        });
        assertThat(jApiClasses.size(), is(1));
    }

    @Test
    public void testOneClassExcluded() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getExcludes().add(new ClassFilter("japicmp.Test"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
                return Collections.singletonList(ctClass);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
                return Collections.singletonList(ctClass);
            }
        });
        assertThat(jApiClasses.size(), is(0));
    }

    @Test
    public void testTwoClassesOneExclude() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getExcludes().add(new ClassFilter("japicmp.Homer"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }
        });
        assertThat(jApiClasses.size(), is(1));
    }

    @Test
    public void testTwoClassesTwoExcludeWithWildcard() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getExcludes().add(new ClassFilter("japicmp.*"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }
        });
        assertThat(jApiClasses.size(), is(0));
    }

    @Test
    public void testTwoClassesTwoExcludeWithWildcardOneLetter() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getExcludes().add(new ClassFilter("japicmp.T*"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }
        });
        assertThat(jApiClasses.size(), is(0));
    }

    @Test
    public void testTwoClassesIncludePackageButExcludeClass() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getExcludes().add(new ClassFilter("japicmp.Test1"));
        options.getFilters().getIncludes().add(new PackageFilter("japicmp"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }
        });
        assertThat(jApiClasses.size(), is(1));
    }

    @Test
    public void testTwoClassesExcludePackageAndClass() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getExcludes().add(new ClassFilter("japicmp.Test1"));
        options.getFilters().getExcludes().add(new PackageFilter("japicmp"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Test1").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Test2").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }
        });
        assertThat(jApiClasses.size(), is(0));
    }

    @Test
    public void testTwoClassesExcludeClassThatDoesNotExist() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getExcludes().add(new ClassFilter("japicmp.Test1"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2);
            }
        });
        assertThat(jApiClasses.size(), is(2));
    }

    @Test
         public void testFourClassesFromTwoPackagesExcludeOnePerPackage() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getExcludes().add(new ClassFilter("japicmp.Marge"));
        options.getFilters().getExcludes().add(new ClassFilter("big.bang.theory.Sheldon"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
                CtClass ctClass3 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
                CtClass ctClass4 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("japicmp.Homer").addToClassPool(classPool);
                CtClass ctClass2 = CtClassBuilder.create().name("japicmp.Marge").addToClassPool(classPool);
                CtClass ctClass3 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
                CtClass ctClass4 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
            }
        });
        assertThat(jApiClasses.size(), is(2));
        assertThat(getJApiClass(jApiClasses, "big.bang.theory.Leonard"), is(notNullValue()));
        assertThat(getJApiClass(jApiClasses, "japicmp.Homer"), is(notNullValue()));
    }

    @Test
    public void testMethodExcluded() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getExcludes().add(new BehaviorFilter("big.bang.theory.Sheldon#study()"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
                CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
                return Arrays.asList(ctClass1, ctClass2);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
                CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
                return Arrays.asList(ctClass1, ctClass2);
            }
        });
        assertThat(jApiClasses.size(), is(2));
        assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasNoJApiMethodWithName("study"));
        assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiMethodWithName("knowItAll"));
        assertThat(getJApiClass(jApiClasses, "big.bang.theory.Leonard"), hasJApiMethodWithName("askSheldon"));
    }

    @Test
    public void testMethodIncluded() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getIncludes().add(new BehaviorFilter("big.bang.theory.Sheldon#study()"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
                CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
                return Arrays.asList(ctClass1, ctClass2);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
                CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
                return Arrays.asList(ctClass1, ctClass2);
            }
        });
        assertThat(jApiClasses.size(), is(1));
        assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiMethodWithName("study"));
        assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasNoJApiMethodWithName("knowItAll"));
    }

    @Test
    public void testPackageExcludedMethodIncluded() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getExcludes().add(new PackageFilter("simpsons"));
        options.getFilters().getIncludes().add(new BehaviorFilter("big.bang.theory.Sheldon#study()"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
                CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
                CtClass ctClass3 = CtClassBuilder.create().name("simpsons.Homer").addToClassPool(classPool);
                CtClass ctClass4 = CtClassBuilder.create().name("simpsons.Marge").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
                CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
                CtClass ctClass3 = CtClassBuilder.create().name("simpsons.Homer").addToClassPool(classPool);
                CtClass ctClass4 = CtClassBuilder.create().name("simpsons.Marge").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
            }
        });
        assertThat(jApiClasses.size(), is(1));
        assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiMethodWithName("study"));
        assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasNoJApiMethodWithName("knowItAll"));
    }

    @Test
    public void testPackageIncludedMethodExcluded() throws Exception {
        JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
        options.getFilters().getIncludes().add(new PackageFilter("big.bang.theory"));
        options.getFilters().getExcludes().add(new BehaviorFilter("big.bang.theory.Sheldon#study()"));
        List<JApiClass> jApiClasses = ClassesHelper.compareClasses(options, new ClassesHelper.ClassesGenerator() {
            @Override
            public List<CtClass> createOldClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
                CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
                CtClass ctClass3 = CtClassBuilder.create().name("simpsons.Homer").addToClassPool(classPool);
                CtClass ctClass4 = CtClassBuilder.create().name("simpsons.Marge").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
            }

            @Override
            public List<CtClass> createNewClasses(ClassPool classPool) throws Exception {
                CtClass ctClass1 = CtClassBuilder.create().name("big.bang.theory.Sheldon").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("study").addToClass(ctClass1);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("knowItAll").addToClass(ctClass1);
                CtClass ctClass2 = CtClassBuilder.create().name("big.bang.theory.Leonard").addToClassPool(classPool);
                CtMethodBuilder.create().publicAccess().returnType(CtClass.voidType).name("askSheldon").addToClass(ctClass2);
                CtClass ctClass3 = CtClassBuilder.create().name("simpsons.Homer").addToClassPool(classPool);
                CtClass ctClass4 = CtClassBuilder.create().name("simpsons.Marge").addToClassPool(classPool);
                return Arrays.asList(ctClass1, ctClass2, ctClass3, ctClass4);
            }
        });
        assertThat(jApiClasses.size(), is(2));
        assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasNoJApiMethodWithName("study"));
        assertThat(getJApiClass(jApiClasses, "big.bang.theory.Sheldon"), hasJApiMethodWithName("knowItAll"));
    }
}
