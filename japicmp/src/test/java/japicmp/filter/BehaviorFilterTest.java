package japicmp.filter;

import japicmp.exception.JApiCmpException;
import japicmp.util.CtClassBuilder;
import japicmp.util.CtConstructorBuilder;
import japicmp.util.CtMethodBuilder;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BehaviorFilterTest {

	@Test
	public void testMethodTwoParamsIntLongSuccessful() throws CannotCompileException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("japicmp.Test#method(int,long)");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
		CtBehavior ctBehavior = CtMethodBuilder.create().name("method").parameters(new CtClass[]{CtClass.intType, CtClass.longType}).addToClass(ctClass);
		assertThat(filter.matches(ctBehavior), is(true));
	}

	@Test
	public void testMethodOneParamsStringSuccessful() throws CannotCompileException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("japicmp.Test#method(japicmp.Param)");
		ClassPool classPool = new ClassPool();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
		CtClass paramCtClass = CtClassBuilder.create().name("japicmp.Param").addToClassPool(classPool);
		CtBehavior ctBehavior = CtMethodBuilder.create().name("method").parameters(new CtClass[]{paramCtClass}).addToClass(ctClass);
		assertThat(filter.matches(ctBehavior), is(true));
	}

	@Test
	public void testMethodNoParamsSuccessful() throws CannotCompileException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("japicmp.Test#method()");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
		CtBehavior ctBehavior = CtMethodBuilder.create().name("method").parameters(new CtClass[]{}).addToClass(ctClass);
		assertThat(filter.matches(ctBehavior), is(true));
	}

	@Test(expected = JApiCmpException.class)
	public void testMethodMissingParenthesis() throws CannotCompileException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("japicmp.Test#method(");
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(new ClassPool());
		CtBehavior ctBehavior = CtMethodBuilder.create().name("method").parameters(new CtClass[]{}).addToClass(ctClass);
		assertThat(filter.matches(ctBehavior), is(true));
	}

	@Test
	public void testConstructorNoParamsSuccessful() throws CannotCompileException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("japicmp.Test#Test()");
		ClassPool classPool = new ClassPool();
		classPool.appendSystemPath();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
		CtConstructor ctConstructor = CtConstructorBuilder.create().addToClass(ctClass);
		assertThat(filter.matches(ctConstructor), is(true));
	}

	@Test
	public void testConstructorOneParamLongSuccessful() throws CannotCompileException, NotFoundException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("japicmp.Test#Test(java.lang.Long)");
		ClassPool classPool = new ClassPool();
		classPool.appendSystemPath();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
		CtConstructor ctConstructor = CtConstructorBuilder.create().parameter(classPool.get("java.lang.Long")).addToClass(ctClass);
		assertThat(filter.matches(ctConstructor), is(true));
	}

	@Test
	public void testConstructorOneParamLongUnsuccessful() throws CannotCompileException, NotFoundException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("japicmp.Test#Test(java.lang.Long)");
		ClassPool classPool = new ClassPool();
		classPool.appendSystemPath();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
		CtConstructor ctConstructor = CtConstructorBuilder.create().parameter(classPool.get("java.lang.Double")).addToClass(ctClass);
		assertThat(filter.matches(ctConstructor), is(false));
	}

	@Test
	public void testConstructorNoParamLongUnsuccessful() throws CannotCompileException, NotFoundException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("japicmp.Test#Test()");
		ClassPool classPool = new ClassPool();
		classPool.appendSystemPath();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
		CtConstructor ctConstructor = CtConstructorBuilder.create().parameter(classPool.get("java.lang.Double")).addToClass(ctClass);
		assertThat(filter.matches(ctConstructor), is(false));
	}

	@Test
	public void testCoberturaMethodWithWildcards() throws CannotCompileException, NotFoundException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("japicmp.*#__cobertura*()");
		ClassPool classPool = new ClassPool();
		classPool.appendSystemPath();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test").addToClassPool(classPool);
		CtMethod ctMethod = CtMethodBuilder.create().name("__cobertura_classmap").addToClass(ctClass);
		assertThat(filter.matches(ctMethod), is(true));
	}

	@Test
	public void testMethodOfInnerClass() throws CannotCompileException, NotFoundException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("japicmp.Test$InnerClass#method()");
		ClassPool classPool = new ClassPool();
		classPool.appendSystemPath();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.Test$InnerClass").addToClassPool(classPool);
		CtMethod ctMethod = CtMethodBuilder.create().name("method").addToClass(ctClass);
		assertThat(filter.matches(ctMethod), is(true));
	}

	@Test
	public void testMethodWithDollarSignInName() throws CannotCompileException, NotFoundException {
		JavadocLikeBehaviorFilter filter = new JavadocLikeBehaviorFilter("org.apache.flink.streaming.api.scala.DataStream#iterate$default$3[R]()");
		ClassPool classPool = new ClassPool();
		classPool.appendSystemPath();
		CtClass ctClass = CtClassBuilder.create().name("org.apache.flink.streaming.api.scala.DataStream").addToClassPool(classPool);
		CtMethod ctMethod = CtMethodBuilder.create().name("iterate$default$3[R]").addToClass(ctClass);
		assertThat(filter.matches(ctMethod), is(true));
	}
}
