package japicmp.test;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Generics<T, U extends List<Integer> & Serializable, V extends List<Long>> {

	public List<Integer> list;

	public <W extends List> Generics(GenericsParamTest<Short, Long> p, W list) {

	}

	public void method1(List<String> strings) {

	}

	private static class GenericsParamTest<T, U> {

		public void m(T t, U u) {

		}
	}

	private static class GenericsReturnValueTest<T, U> {

	}

	public GenericsReturnValueTest<Object, Object> methodWithGenericArgs(Map<String, Integer> map, GenericsParamTest<Short, Byte> genericsParamTest) {
		return null;
	}

	public GenericsReturnValueTest<Object, Object> methodWithReturnType() {
		return null;
	}

	public void methodWithGenericExtends(List<? extends Integer> l) {
	}

	public void methodWithGenericSuper(List<? super Integer> l) {
	}

	public void methodWithGenericQuestionMark(GenericsParamTest<?, Short>[] param, List<? extends Integer> l2) {
	}

	public <TEST extends GenericsParamTest<?, Short>, TEST2 extends Integer> T methodWithTemplate(TEST t, TEST2 u) {
		return null;
	}

	public T methodWithTemplate2(T t, T u) {
		return null;
	}

	public <X extends List<Integer>> X methodX(X test) {
		return null;
	}

	public interface MyInterface<T> {

	}

	public <X extends List<Integer> & Serializable & MyInterface<Integer>> X methodWithAmpersandTemplate(X x) {
		return null;
	}
}
