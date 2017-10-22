package japicmp.test.util;

import japicmp.util.Optional;
import japicmp.cmp.JApiCmpArchive;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.Options;
import japicmp.model.AccessModifier;
import japicmp.model.JApiAnnotation;
import japicmp.model.JApiAnnotationElement;
import japicmp.model.JApiClass;
import japicmp.model.JApiField;
import japicmp.model.JApiImplementedInterface;
import japicmp.model.JApiMethod;
import japicmp.output.xml.XmlOutput;
import japicmp.output.xml.XmlOutputGenerator;
import japicmp.output.xml.XmlOutputGeneratorOptions;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;

import java.io.File;
import java.util.List;

public class Helper {

	public static JApiCmpArchive getArchive(String filename) {
		File file = new File(System.getProperty("user.dir") + File.separator + "target" + File.separator + filename);
		return toJApiCmpArchive(file);
	}

	public static JApiCmpArchive toJApiCmpArchive(File file) {
		return new JApiCmpArchive(file, "n.a.");
	}

	public static JApiClass getJApiClass(List<JApiClass> jApiClasses, String fqn) {
		for (JApiClass jApiClass : jApiClasses) {
			if (jApiClass.getFullyQualifiedName().equals(fqn)) {
				return jApiClass;
			}
		}
		throw new IllegalArgumentException("No class found with name " + fqn + ".");
	}

	public static JApiMethod getJApiMethod(List<JApiMethod> jApiMethods, String name) {
		for (JApiMethod jApiMethod : jApiMethods) {
			if (jApiMethod.getName().equals(name)) {
				return jApiMethod;
			}
		}
		throw new IllegalArgumentException("No method found with name " + name + ".");
	}

	public static Matcher<JApiClass> hasJApiMethodWithName(final String methodName) {
		return new TypeSafeMatcher<JApiClass>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("JApiClass should have a method with name '").appendValue(methodName)
					.appendText("'.");
			}

			@Override
			protected boolean matchesSafely(JApiClass jApiClass) {
				List<JApiMethod> jApiMethods = jApiClass.getMethods();
				boolean found = false;
				for (JApiMethod jApiMethod : jApiMethods) {
					if (methodName.equals(jApiMethod.getName())) {
						found = true;
						break;
					}
				}
				return found;
			}
		};
	}

	public static Matcher<JApiClass> hasNoJApiMethodWithName(final String methodName) {
		return new TypeSafeMatcher<JApiClass>() {

			@Override
			public void describeTo(Description description) {
				description.appendText("JApiClass should not have a method with name '").appendValue(methodName)
					.appendText("'.");
			}

			@Override
			protected boolean matchesSafely(JApiClass jApiClass) {
				List<JApiMethod> jApiMethods = jApiClass.getMethods();
				boolean found = false;
				for (JApiMethod jApiMethod : jApiMethods) {
					if (methodName.equals(jApiMethod.getName())) {
						found = true;
						break;
					}
				}
				return !found;
			}
		};
	}

	public static JApiField getJApiField(List<JApiField> jApiFields, String name) {
		for (JApiField jApiField : jApiFields) {
			if (jApiField.getName().equals(name)) {
				return jApiField;
			}
		}
		throw new IllegalArgumentException("No field found with name " + name + ".");
	}

	public static JApiImplementedInterface getJApiImplementedInterface(List<JApiImplementedInterface> jApiImplementedInterfaces, String name) {
		for (JApiImplementedInterface jApiImplementedInterface : jApiImplementedInterfaces) {
			if (jApiImplementedInterface.getFullyQualifiedName().equals(name)) {
				return jApiImplementedInterface;
			}
		}
		throw new IllegalArgumentException("No interface found with name " + name + ".");
	}

	public static JApiAnnotation getJApiAnnotation(List<JApiAnnotation> annotations, String name) {
		for (JApiAnnotation annotation : annotations) {
			if (annotation.getFullyQualifiedName().equals(name)) {
				return annotation;
			}
		}
		throw new IllegalArgumentException("No annotation found with name " + name + ".");
	}

	public static JApiAnnotationElement getJApiAnnotationElement(List<JApiAnnotationElement> annotationElements, String name) {
		for (JApiAnnotationElement annotationElement : annotationElements) {
			if (annotationElement.getName().equals(name)) {
				return annotationElement;
			}
		}
		throw new IllegalArgumentException("No annotation element found with name " + name + ".");
	}

	public static String replaceLastDotWith$(String str) {
		int lastIndex = str.lastIndexOf('.');
		if (lastIndex > -1) {
			if (lastIndex == 0) {
				if (str.length() > 1) {
					str = "$" + str.substring(1);
				} else {
					str = "$";
				}
			} else {
				if (str.length() > lastIndex + 1) {
					str = str.substring(0, lastIndex) + "$" + str.substring(lastIndex + 1);
				} else {
					str = str.substring(0, lastIndex) + "$";
				}
			}
		}
		return str;
	}

	public static List<JApiClass> compareTestV1WithTestV2(AccessModifier accessModifier) {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.setAccessModifier(accessModifier);
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		return jarArchiveComparator.compare(getArchive("japicmp-test-v1.jar"), getArchive("japicmp-test-v2.jar"));
	}

	public static void generateHtmlOutput(List<JApiClass> jApiClasses, String xmlOutputFile, String htmlOutputFile, boolean outputOnlyModifications, AccessModifier accessModifier) {
		Options options = Options.newDefault();
		options.setXmlOutputFile(Optional.of(xmlOutputFile));
		options.setHtmlOutputFile(Optional.of(htmlOutputFile));
		options.setOutputOnlyModifications(outputOnlyModifications);
		options.setAccessModifier(Optional.of(accessModifier));
		XmlOutputGeneratorOptions xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
		xmlOutputGeneratorOptions.setCreateSchemaFile(true);
		XmlOutputGenerator generator = new XmlOutputGenerator(jApiClasses, options, xmlOutputGeneratorOptions);
		XmlOutput xmlOutput = generator.generate();
		XmlOutputGenerator.writeToFiles(options, xmlOutput);
	}

	public interface SimpleExceptionVerifier {
		void execute();
	}

	public static void assertThatExceptionIsThrown(SimpleExceptionVerifier verifier, Class<? extends Exception> exceptionClass) {
		boolean exceptionThrown = false;
		try {
			verifier.execute();
		} catch (Exception e) {
			exceptionThrown = true;
			Assert.assertThat(e.getClass().isAssignableFrom(exceptionClass), CoreMatchers.is(true));
		}
		Assert.assertThat(exceptionThrown, CoreMatchers.is(true));
	}
}
