package japicmp.test.client;

import japicmp.test.Interfaces;

import java.net.URL;
import java.net.URLClassLoader;

public class JApiCmpClient {

	public static void main(String[] args) {
		JApiCmpClient client = new JApiCmpClient();
		client.run();
	}

	private void run() {
		printClassPath();
		callMethodThatWillBePulledUpToSuperInterface();
	}

	private void printClassPath() {
		StringBuilder sb = new StringBuilder();
		sb.append(JApiCmpClient.class.getSimpleName()).append(" has the following classpath:\n");
		ClassLoader cl = JApiCmpClient.class.getClassLoader();
		if (cl instanceof  URLClassLoader) {
			URL[] urls = ((URLClassLoader) cl).getURLs();
			int count = 0;
			for (URL url : urls) {
				if (count > 0) {
					sb.append("\n");
				}
				sb.append(url.getFile());
				count++;
			}
		}
		System.out.println(sb.toString());
	}

	public static class MethodPulledToSuperInterfaceClass implements Interfaces.MethodPulledToSuperInterfaceChild {
		@Override
		public void methodPulledUp() {

		}
	}

	private void callMethodThatWillBePulledUpToSuperInterface() {
		MethodPulledToSuperInterfaceClass methodPulledToSuperInterfaceClass = new MethodPulledToSuperInterfaceClass();
		methodPulledToSuperInterfaceClass.methodPulledUp();
	}
}
