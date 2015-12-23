package japicmp.util;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SignatureParserTest {
	private SignatureParser subject;

	@Before
	public void before() throws Exception {
		subject = new SignatureParser();
	}

	@Test
	public void testNoParamsReturnsVoid() {
		subject.parse("()V");
		assertThat(subject.getReturnType(), is("void"));
		assertThat(subject.getParameters().size(), is(0));
	}

	@Test
	public void testTwoReferenceParamsReturnsReference() {
		subject.parse("(Lorg/apache/http/conn/routing/HttpRoute;Ljava/lang/Object;)Lorg/apache/http/conn/ManagedClientConnection;");
		assertThat(subject.getReturnType(), is("org.apache.http.conn.ManagedClientConnection"));
		assertThat(subject.getParameters().size(), is(2));
		assertThat(subject.getParameters(), hasItem("org.apache.http.conn.routing.HttpRoute"));
		assertThat(subject.getParameters(), hasItem("java.lang.Object"));
	}

	@Test
	public void testOneReferenceOnePrimParamsReturnsVoid() {
		subject.parse("(JLjava/util/concurrent/TimeUnit;)V");
		assertThat(subject.getReturnType(), is("void"));
		assertThat(subject.getParameters().size(), is(2));
		assertThat(subject.getParameters(), hasItem("long"));
		assertThat(subject.getParameters(), hasItem("java.util.concurrent.TimeUnit"));
	}

	@Test
	public void testArrayTwoPrimParamsReturnsVoid() {
		subject.parse("([BII)V");
		assertThat(subject.getReturnType(), is("void"));
		assertThat(subject.getParameters().size(), is(3));
		assertThat(subject.getParameters(), hasItem("byte[]"));
		assertThat(subject.getParameters(), hasItem("int"));
	}

	@Test
	public void testArrayPrimParamReturnsVoid() {
		subject.parse("([B)V");
		assertThat(subject.getReturnType(), is("void"));
		assertThat(subject.getParameters().size(), is(1));
		assertThat(subject.getParameters(), hasItem("byte[]"));
	}

	@Test
	public void testArrayRefParamReturnsVoid() {
		subject.parse("([Lorg/apache/http/cookie/Cookie;)V");
		assertThat(subject.getReturnType(), is("void"));
		assertThat(subject.getParameters().size(), is(1));
		assertThat(subject.getParameters(), hasItem("org.apache.http.cookie.Cookie[]"));
	}

	@Test
	public void testOneReferenceParamsReturnsVoid() {
		subject.parse("(Lorg/apache/http/impl/conn/tsccm/BasicPoolEntry;)V");
		assertThat(subject.getReturnType(), is("void"));
		assertThat(subject.getParameters().size(), is(1));
		assertThat(subject.getParameters(), hasItem("org.apache.http.impl.conn.tsccm.BasicPoolEntry"));
	}

	@Test
	public void testOneReferenceParamsReturnsOneReference() {
		subject.parse("(Ljava/util/List;)Ljava/util/List;");
		assertThat(subject.getReturnType(), is("java.util.List"));
		assertThat(subject.getParameters().size(), is(1));
		assertThat(subject.getParameters(), hasItem("java.util.List"));
	}

	@Test
	public void testNoParamsReturnsReference() {
		subject.parse("()Lorg/apache/http/conn/scheme/SchemeRegistry;");
		assertThat(subject.getReturnType(), is("org.apache.http.conn.scheme.SchemeRegistry"));
		assertThat(subject.getParameters().size(), is(0));
	}

	@Test
	public void testNoParamsReturnsI() {
		subject.parse("()I");
		assertThat(subject.getReturnType(), is("int"));
		assertThat(subject.getParameters().size(), is(0));
	}
}
