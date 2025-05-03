package japicmp.util;

import japicmp.model.JApiGenericType;
import javassist.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

class SignatureParserTest {
	private SignatureParser subject;

	@BeforeEach
	public void before() {
		subject = new SignatureParser();
	}

	@Test
	void testNoParamsReturnsVoid() {
		subject.parse("()V");
		assertThat(subject.getReturnType().getType(), is("void"));
		assertThat(subject.getParameters().size(), is(0));
	}

	@Test
	void testTwoReferenceParamsReturnsReference() {
		subject.parse("(Lorg/apache/http/conn/routing/HttpRoute;Ljava/lang/Object;)Lorg/apache/http/conn/ManagedClientConnection;");
		assertThat(subject.getReturnType().getType(), is("org.apache.http.conn.ManagedClientConnection"));
		assertThat(subject.getParameters().size(), is(2));
		assertThat(subject.getParameters(), hasItem(new SignatureParser.ParsedParameter("org.apache.http.conn.routing.HttpRoute")));
		assertThat(subject.getParameters(), hasItem(new SignatureParser.ParsedParameter("java.lang.Object")));
	}

	@Test
	void testOneReferenceOnePrimParamsReturnsVoid() {
		subject.parse("(JLjava/util/concurrent/TimeUnit;)V");
		assertThat(subject.getReturnType().getType(), is("void"));
		assertThat(subject.getParameters().size(), is(2));
		assertThat(subject.getParameters(), hasItem(new SignatureParser.ParsedParameter("long")));
		assertThat(subject.getParameters(), hasItem(new SignatureParser.ParsedParameter("java.util.concurrent.TimeUnit")));
	}

	@Test
	void testArrayTwoPrimParamsReturnsVoid() {
		subject.parse("([BII)V");
		assertThat(subject.getReturnType().getType(), is("void"));
		assertThat(subject.getParameters().size(), is(3));
		assertThat(subject.getParameters(), hasItem(new SignatureParser.ParsedParameter("byte[]")));
		assertThat(subject.getParameters(), hasItem(new SignatureParser.ParsedParameter("int")));
	}

	@Test
	void testArrayPrimParamReturnsVoid() {
		subject.parse("([B)V");
		assertThat(subject.getReturnType().getType(), is("void"));
		assertThat(subject.getParameters().size(), is(1));
		assertThat(subject.getParameters(), hasItem(new SignatureParser.ParsedParameter("byte[]")));
	}

	@Test
	void testArrayRefParamReturnsVoid() {
		subject.parse("([Lorg/apache/http/cookie/Cookie;)V");
		assertThat(subject.getReturnType().getType(), is("void"));
		assertThat(subject.getParameters().size(), is(1));
		assertThat(subject.getParameters(), hasItem(new SignatureParser.ParsedParameter("org.apache.http.cookie.Cookie[]")));
	}

	@Test
	void testOneReferenceParamsReturnsVoid() {
		subject.parse("(Lorg/apache/http/impl/conn/tsccm/BasicPoolEntry;)V");
		assertThat(subject.getReturnType().getType(), is("void"));
		assertThat(subject.getParameters().size(), is(1));
		assertThat(subject.getParameters(), hasItem(new SignatureParser.ParsedParameter("org.apache.http.impl.conn.tsccm.BasicPoolEntry")));
	}

	@Test
	void testOneReferenceParamsReturnsOneReference() {
		subject.parse("(Ljava/util/List;)Ljava/util/List;");
		assertThat(subject.getReturnType().getType(), is("java.util.List"));
		assertThat(subject.getParameters().size(), is(1));
		assertThat(subject.getParameters(), hasItem(new SignatureParser.ParsedParameter("java.util.List")));
	}

	@Test
	void testNoParamsReturnsReference() {
		subject.parse("()Lorg/apache/http/conn/scheme/SchemeRegistry;");
		assertThat(subject.getReturnType().getType(), is("org.apache.http.conn.scheme.SchemeRegistry"));
		assertThat(subject.getParameters().size(), is(0));
	}

	@Test
	void testNoParamsReturnsI() {
		subject.parse("()I");
		assertThat(subject.getReturnType().getType(), is("int"));
		assertThat(subject.getParameters().size(), is(0));
	}

	@Test
	void testOneMapWithGenerics() {
		List<SignatureParser.ParsedParameter> parsedParameters = subject.parseTypes("Ljava/util/Map<Ljapicmp/test/Generics$GenericsTest;Ljava/lang/Long;>;");
		Assertions.assertEquals(1, parsedParameters.size());
		Assertions.assertEquals("java.util.Map", parsedParameters.get(0).getType());
		Assertions.assertEquals(2, parsedParameters.get(0).getGenericTypes().size());
		Assertions.assertEquals("japicmp.test.Generics$GenericsTest", parsedParameters.get(0).getGenericTypes().get(0).getType());
		Assertions.assertEquals("java.lang.Long", parsedParameters.get(0).getGenericTypes().get(1).getType());
	}

	@Test
	void testOneMapWithGenericsWithInnerGenerics() {
		List<SignatureParser.ParsedParameter> parsedParameters = subject.parseTypes("Ljava/util/Map<Ljapicmp/test/Generics$GenericsTest<Ljava/lang/String;Ljava/lang/Integer;>;Ljava/lang/Long;>;");
		Assertions.assertEquals(1, parsedParameters.size());
		Assertions.assertEquals("java.util.Map", parsedParameters.get(0).getType());
		Assertions.assertEquals(2, parsedParameters.get(0).getGenericTypes().size());
		Assertions.assertEquals("japicmp.test.Generics$GenericsTest", parsedParameters.get(0).getGenericTypes().get(0).getType());
		Assertions.assertEquals("java.lang.Long", parsedParameters.get(0).getGenericTypes().get(1).getType());
		Assertions.assertEquals(2, parsedParameters.get(0).getGenericTypes().get(0).getGenericTypes().size());
		Assertions.assertEquals("java.lang.String", parsedParameters.get(0).getGenericTypes().get(0).getGenericTypes().get(0).getType());
		Assertions.assertEquals("java.lang.Integer", parsedParameters.get(0).getGenericTypes().get(0).getGenericTypes().get(1).getType());
	}

	@Test
	void testOneMapWithGenericsWithInnerGenericsAndObjectWithGenerics() {
		List<SignatureParser.ParsedParameter> parsedParameters = subject.parseTypes("Ljava/util/Map<Ljapicmp/test/Generics$GenericsParamTest<Ljava/lang/String;Ljava/lang/Integer;>;Ljava/lang/Long;>;Ljapicmp/test/Generics$GenericsParamTest<Ljava/lang/Byte;Ljava/lang/Short;>;");
		Assertions.assertEquals(2, parsedParameters.size());
		Assertions.assertEquals("java.util.Map", parsedParameters.get(0).getType());
		Assertions.assertEquals(2, parsedParameters.get(0).getGenericTypes().size());
		Assertions.assertEquals("japicmp.test.Generics$GenericsParamTest", parsedParameters.get(0).getGenericTypes().get(0).getType());
		Assertions.assertEquals("java.lang.Long", parsedParameters.get(0).getGenericTypes().get(1).getType());
		Assertions.assertEquals(2, parsedParameters.get(0).getGenericTypes().get(0).getGenericTypes().size());
		Assertions.assertEquals("java.lang.String", parsedParameters.get(0).getGenericTypes().get(0).getGenericTypes().get(0).getType());
		Assertions.assertEquals("java.lang.Integer", parsedParameters.get(0).getGenericTypes().get(0).getGenericTypes().get(1).getType());
		Assertions.assertEquals("japicmp.test.Generics$GenericsParamTest", parsedParameters.get(1).getType());
		Assertions.assertEquals("java.lang.Byte", parsedParameters.get(1).getGenericTypes().get(0).getType());
		Assertions.assertEquals("java.lang.Short", parsedParameters.get(1).getGenericTypes().get(1).getType());
	}

	@Test
	void testGenericObjectWithArrays() {
		List<SignatureParser.ParsedParameter> parsedParameters = subject.parseTypes("[Ljapicmp/test/Generics$GenericsParamTest<[Ljava/lang/Byte;Ljava/lang/Short;>;");
		Assertions.assertEquals(1, parsedParameters.size());
		Assertions.assertEquals("japicmp.test.Generics$GenericsParamTest[]", parsedParameters.get(0).getType());
		Assertions.assertEquals(2, parsedParameters.get(0).getGenericTypes().size());
		Assertions.assertEquals("java.lang.Byte[]", parsedParameters.get(0).getGenericTypes().get(0).getType());
		Assertions.assertEquals("java.lang.Short", parsedParameters.get(0).getGenericTypes().get(1).getType());
	}

	@Test
	void testMethodWithGenericsOfClass() {
		List<SignatureParser.ParsedParameter> parsedParameters = subject.parseTypes("TT;TU;");
		Assertions.assertEquals(2, parsedParameters.size());
		Assertions.assertEquals("T", parsedParameters.get(0).getType());
		Assertions.assertEquals("U", parsedParameters.get(1).getType());
	}

	@Test
	void testMethodWithGenericExtends() {
		subject.parse("(Ljava/util/List<+Ljava/lang/Integer;>;)V");
		List<SignatureParser.ParsedParameter> parsedParameters = subject.getParameters();
		Assertions.assertEquals(1, parsedParameters.size());
		Assertions.assertEquals("java.util.List", parsedParameters.get(0).getType());
		Assertions.assertEquals(1, parsedParameters.get(0).getGenericTypes().size());
		Assertions.assertEquals("java.lang.Integer", parsedParameters.get(0).getGenericTypes().get(0).getType());
		Assertions.assertEquals(JApiGenericType.JApiGenericWildCard.EXTENDS, parsedParameters.get(0).getGenericTypes().get(0).getGenericWildCard());
	}

	@Test
	void testMethodWithGenericSuper() {
		subject.parse("(Ljava/util/List<-Ljava/lang/Integer;>;)V");
		List<SignatureParser.ParsedParameter> parsedParameters = subject.getParameters();
		Assertions.assertEquals(1, parsedParameters.size());
		Assertions.assertEquals("java.util.List", parsedParameters.get(0).getType());
		Assertions.assertEquals(1, parsedParameters.get(0).getGenericTypes().size());
		Assertions.assertEquals("java.lang.Integer", parsedParameters.get(0).getGenericTypes().get(0).getType());
		Assertions.assertEquals(JApiGenericType.JApiGenericWildCard.SUPER, parsedParameters.get(0).getGenericTypes().get(0).getGenericWildCard());
	}

	@Test
	void testMethodWithGenericUnbounded() {
		subject.parse("(Ljava/util/List<*>;)V");
		List<SignatureParser.ParsedParameter> parsedParameters = subject.getParameters();
		Assertions.assertEquals(1, parsedParameters.size());
		Assertions.assertEquals("java.util.List", parsedParameters.get(0).getType());
		Assertions.assertEquals(1, parsedParameters.get(0).getGenericTypes().size());
		Assertions.assertEquals("?", parsedParameters.get(0).getGenericTypes().get(0).getType());
		Assertions.assertEquals(JApiGenericType.JApiGenericWildCard.UNBOUNDED, parsedParameters.get(0).getGenericTypes().get(0).getGenericWildCard());
	}

	@Test
	void testMethodWithDoubleArray() {
		subject.parse("([[B)V)");
		List<SignatureParser.ParsedParameter> parsedParameters = subject.getParameters();
		Assertions.assertEquals(1, parsedParameters.size());
		Assertions.assertEquals("byte[][]", parsedParameters.get(0).getType());
		Assertions.assertEquals(0, parsedParameters.get(0).getGenericTypes().size());
	}

	@Test
	void testMethodWithMethodParameters() {
		subject.parse("<T:Ljava/lang/Integer;>(TT;)V");
		List<SignatureParser.ParsedParameter> parameters = subject.getParameters();
		Assertions.assertEquals(1, parameters.size());
		Assertions.assertEquals("T", parameters.get(0).getType());
	}

	@Test
	void testMethodWithMethodTemplate() {
		subject.parse("<TEST:Ljava/lang/Integer;U:Ljava/lang/Short;>(TTEST;TU;)TU;");
		List<SignatureParser.ParsedParameter> parameters = subject.getParameters();
		Assertions.assertEquals(2, parameters.size());
		Assertions.assertEquals("TEST", parameters.get(0).getType());
		Assertions.assertEquals("U", parameters.get(1).getType());
		Assertions.assertEquals("U", subject.getReturnType().getType());
		List<SignatureParser.ParsedTemplate> templates = subject.getTemplates();
		Assertions.assertEquals(2, templates.size());
		Assertions.assertEquals("TEST", templates.get(0).name);
		Assertions.assertEquals("java.lang.Integer", templates.get(0).type);
		Assertions.assertEquals("U", templates.get(1).name);
		Assertions.assertEquals("java.lang.Short", templates.get(1).type);
	}

	@Test
	void testMethodWithMethodTemplates() {
		subject.parse("<TEST:Ljapicmp/test/Generics$GenericsParamTest<*Ljava/lang/Short;>;TEST2:Ljava/lang/Integer;>(TTEST;TTEST2;)TT;");
		List<SignatureParser.ParsedParameter> parameters = subject.getParameters();
		Assertions.assertEquals(2, parameters.size());
		Assertions.assertEquals("TEST", parameters.get(0).getType());
		Assertions.assertEquals("TEST2", parameters.get(1).getType());
		Assertions.assertEquals("T", subject.getReturnType().getType());
		List<SignatureParser.ParsedTemplate> templates = subject.getTemplates();
		Assertions.assertEquals(2, templates.size());
		Assertions.assertEquals("TEST", templates.get(0).name);
		Assertions.assertEquals("japicmp.test.Generics$GenericsParamTest", templates.get(0).type);
		Assertions.assertEquals(2, templates.get(0).genericTypes.size());
		Assertions.assertEquals("?", templates.get(0).genericTypes.get(0).type);
		Assertions.assertEquals("java.lang.Short", templates.get(0).genericTypes.get(1).type);
		Assertions.assertEquals("TEST2", templates.get(1).name);
		Assertions.assertEquals("java.lang.Integer", templates.get(1).type);
	}

	@Test
	void testClassWithParameter() {
		ClassPool cp = new ClassPool();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.test.Test").addToClassPool(cp);
		ctClass.setGenericSignature("<T:Ljava/lang/Object;>Ljava/lang/Object;");
		SignatureParser signatureParser = new SignatureParser();
		List<SignatureParser.ParsedTemplate> parsedTemplates = signatureParser.parseTemplatesOfClass(ctClass);
		Assertions.assertEquals(1, parsedTemplates.size());
		Assertions.assertEquals("T", parsedTemplates.get(0).name);
		Assertions.assertEquals("java.lang.Object", parsedTemplates.get(0).type);
	}

	@Test
	void testClassWithTwoParameters() {
		ClassPool cp = new ClassPool();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.test.Test").addToClassPool(cp);
		ctClass.setGenericSignature("<T:Ljava/lang/Object;U:Ljava/lang/Short;>Ljava/lang/Object;");
		SignatureParser signatureParser = new SignatureParser();
		List<SignatureParser.ParsedTemplate> parsedTemplates = signatureParser.parseTemplatesOfClass(ctClass);
		Assertions.assertEquals(2, parsedTemplates.size());
		Assertions.assertEquals("T", parsedTemplates.get(0).name);
		Assertions.assertEquals("java.lang.Object", parsedTemplates.get(0).type);
		Assertions.assertEquals("U", parsedTemplates.get(1).name);
		Assertions.assertEquals("java.lang.Short", parsedTemplates.get(1).type);
	}

	@Test
	void testClassWithThreeParametersAndDoubleColon() {
		ClassPool cp = new ClassPool();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.test.Test").addToClassPool(cp);
		ctClass.setGenericSignature("<T:Ljava/lang/Object;U::Ljava/util/List<Ljava/lang/Integer;>;V::Ljava/util/List<Ljava/lang/Long;>;>Ljava/lang/Object;");
		SignatureParser signatureParser = new SignatureParser();
		List<SignatureParser.ParsedTemplate> parsedTemplates = signatureParser.parseTemplatesOfClass(ctClass);
		Assertions.assertEquals(3, parsedTemplates.size());
		Assertions.assertEquals("T", parsedTemplates.get(0).name);
		Assertions.assertEquals("java.lang.Object", parsedTemplates.get(0).type);
		Assertions.assertEquals("U", parsedTemplates.get(1).name);
		Assertions.assertEquals("java.util.List", parsedTemplates.get(1).type);
		Assertions.assertEquals(1, parsedTemplates.get(1).genericTypes.size());
		Assertions.assertEquals("java.lang.Integer", parsedTemplates.get(1).genericTypes.get(0).type);
		Assertions.assertEquals("V", parsedTemplates.get(2).name);
		Assertions.assertEquals("java.util.List", parsedTemplates.get(2).type);
		Assertions.assertEquals(1, parsedTemplates.get(2).genericTypes.size());
		Assertions.assertEquals("java.lang.Long", parsedTemplates.get(2).genericTypes.get(0).type);
	}

	@Test
	void testMethodWithTemplateAndOneInterface() throws NotFoundException, CannotCompileException {
		ClassPool cp = new ClassPool();
		cp.appendSystemPath();
		CtClass ctClass = CtClassBuilder.create().name("japicmp.test.Test").addToClassPool(cp);
		CtMethod ctMethod = CtMethodBuilder.create().publicAccess().returnType(cp.get("java.util.List")).name("m").parameter(cp.get("java.util.List")).addToClass(ctClass);
		ctMethod.setGenericSignature("<X::Ljava/util/List<Ljava/lang/Integer;>;:Ljava/io/Serializable;:Ljapicmp/test/Generics$MyInterface<Ljava/lang/Integer;>;>(TX;)TX;");
		SignatureParser signatureParser = new SignatureParser();
		signatureParser.parse(ctMethod.getGenericSignature());
		List<SignatureParser.ParsedTemplate> templates = signatureParser.getTemplates();
		Assertions.assertEquals(1, templates.size());
		Assertions.assertEquals("X", templates.get(0).getName());
		Assertions.assertEquals("java.util.List", templates.get(0).getType());
		Assertions.assertEquals("java.lang.Integer", templates.get(0).getGenericTypes().get(0).getType());
		Assertions.assertEquals(2, templates.get(0).getInterfaces().size());
		Assertions.assertEquals("java.io.Serializable", templates.get(0).getInterfaces().get(0).getType());
		Assertions.assertEquals("japicmp.test.Generics$MyInterface", templates.get(0).getInterfaces().get(1).getType());
		Assertions.assertEquals("java.lang.Integer", templates.get(0).getInterfaces().get(1).getGenericTypes().get(0).getType());
	}
}
