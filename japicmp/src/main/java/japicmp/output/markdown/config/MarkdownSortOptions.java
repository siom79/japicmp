package japicmp.output.markdown.config;

import static java.util.Comparator.comparing;

import japicmp.model.JApiAnnotation;
import japicmp.model.JApiClass;
import japicmp.model.JApiConstructor;
import japicmp.model.JApiField;
import japicmp.model.JApiGenericTemplate;
import japicmp.model.JApiImplementedInterface;
import japicmp.model.JApiMethod;
import java.util.Comparator;

public class MarkdownSortOptions {

	public Comparator<JApiClass> classes = comparing(JApiClass::getFullyQualifiedName);
	public Comparator<JApiGenericTemplate> generics = null;
	public Comparator<JApiImplementedInterface> interfaces = comparing(JApiImplementedInterface::getFullyQualifiedName);
	public Comparator<JApiAnnotation> annotations = comparing(JApiAnnotation::getFullyQualifiedName);
	public Comparator<JApiConstructor> constructors = null;
	public Comparator<JApiMethod> methods = comparing(JApiMethod::getName);
	public Comparator<JApiField> fields = comparing(JApiField::getName);
}
