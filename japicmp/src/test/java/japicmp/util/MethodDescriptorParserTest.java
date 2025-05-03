package japicmp.util;

import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;

class MethodDescriptorParserTest {
	private MethodDescriptorParser subject;

	@BeforeEach
	public void before() {
		subject = new MethodDescriptorParser();
	}

	@Test
	void testNoParamsReturnsVoid() {
		subject.parse("()V");
		MatcherAssert.assertThat(subject.getReturnType(), is("void"));
		MatcherAssert.assertThat(subject.getParameters().size(), is(0));
	}

	@Test
	void testTwoReferenceParamsReturnsReference() {
		subject.parse("(Lorg/apache/http/conn/routing/HttpRoute;Ljava/lang/Object;)Lorg/apache/http/conn/ManagedClientConnection;");
		MatcherAssert.assertThat(subject.getReturnType(), is("org.apache.http.conn.ManagedClientConnection"));
		MatcherAssert.assertThat(subject.getParameters().size(), is(2));
		MatcherAssert.assertThat(subject.getParameters(), hasItem("org.apache.http.conn.routing.HttpRoute"));
		MatcherAssert.assertThat(subject.getParameters(), hasItem("java.lang.Object"));
	}

	@Test
	void testOneReferenceOnePrimParamsReturnsVoid() {
		subject.parse("(JLjava/util/concurrent/TimeUnit;)V");
		MatcherAssert.assertThat(subject.getReturnType(), is("void"));
		MatcherAssert.assertThat(subject.getParameters().size(), is(2));
		MatcherAssert.assertThat(subject.getParameters(), hasItem("long"));
		MatcherAssert.assertThat(subject.getParameters(), hasItem("java.util.concurrent.TimeUnit"));
	}

	@Test
	void testArrayTwoPrimParamsReturnsVoid() {
		subject.parse("([BII)V");
		MatcherAssert.assertThat(subject.getReturnType(), is("void"));
		MatcherAssert.assertThat(subject.getParameters().size(), is(3));
		MatcherAssert.assertThat(subject.getParameters(), hasItem("byte[]"));
		MatcherAssert.assertThat(subject.getParameters(), hasItem("int"));
	}

	@Test
	void testArrayPrimParamReturnsVoid() {
		subject.parse("([B)V");
		MatcherAssert.assertThat(subject.getReturnType(), is("void"));
		MatcherAssert.assertThat(subject.getParameters().size(), is(1));
		MatcherAssert.assertThat(subject.getParameters(), hasItem("byte[]"));
	}

	@Test
	void testArrayRefParamReturnsVoid() {
		subject.parse("([Lorg/apache/http/cookie/Cookie;)V");
		MatcherAssert.assertThat(subject.getReturnType(), is("void"));
		MatcherAssert.assertThat(subject.getParameters().size(), is(1));
		MatcherAssert.assertThat(subject.getParameters(), hasItem("org.apache.http.cookie.Cookie[]"));
	}

	@Test
	void testOneReferenceParamsReturnsVoid() {
		subject.parse("(Lorg/apache/http/impl/conn/tsccm/BasicPoolEntry;)V");
		MatcherAssert.assertThat(subject.getReturnType(), is("void"));
		MatcherAssert.assertThat(subject.getParameters().size(), is(1));
		MatcherAssert.assertThat(subject.getParameters(), hasItem("org.apache.http.impl.conn.tsccm.BasicPoolEntry"));
	}

	@Test
	void testOneReferenceParamsReturnsOneReference() {
		subject.parse("(Ljava/util/List;)Ljava/util/List;");
		MatcherAssert.assertThat(subject.getReturnType(), is("java.util.List"));
		MatcherAssert.assertThat(subject.getParameters().size(), is(1));
		MatcherAssert.assertThat(subject.getParameters(), hasItem("java.util.List"));
	}

	@Test
	void testNoParamsReturnsReference() {
		subject.parse("()Lorg/apache/http/conn/scheme/SchemeRegistry;");
		MatcherAssert.assertThat(subject.getReturnType(), is("org.apache.http.conn.scheme.SchemeRegistry"));
		MatcherAssert.assertThat(subject.getParameters().size(), is(0));
	}

	@Test
	void testNoParamsReturnsI() {
		subject.parse("()I");
		MatcherAssert.assertThat(subject.getReturnType(), is("int"));
		MatcherAssert.assertThat(subject.getParameters().size(), is(0));
	}
}
