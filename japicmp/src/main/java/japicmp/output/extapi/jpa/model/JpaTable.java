package japicmp.output.extapi.jpa.model;

import japicmp.model.*;
import japicmp.output.extapi.jpa.JpaAnalyzer;
import japicmp.util.Optional;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.util.LinkedList;
import java.util.List;

public class JpaTable {
	private final List<JpaAttribute> attributes = new LinkedList<>();
	private final JApiClass jApiClass;
	private final JpaName jpaName;
	private final JApiChangeStatus changeStatus;

	public JpaTable(JApiClass jApiClass, JApiAnnotation entityAnnotation) {
		this.jApiClass = jApiClass;
		this.jpaName = extractName(entityAnnotation, jApiClass);
		extractAttributes();
		changeStatus = computeChangeStatus();
	}

	private JpaName extractName(JApiAnnotation entityAnnotation, JApiClass jApiClass) {
		JApiChangeStatus changeStatusClass = jApiClass.getChangeStatus();
		String tableName = computeTablename();
		for (JApiAnnotationElement element : entityAnnotation.getElements()) {
			if ("name".equals(element.getName())) {
				JApiChangeStatus elementChangeStatus = element.getChangeStatus();
				switch (elementChangeStatus) {
					case NEW:
						String newName = removeQuotationMarks(element.getNewValue().get().toString());
						if (changeStatusClass == JApiChangeStatus.NEW) {
							return new JpaName(Optional.<String>absent(), Optional.of(newName), JApiChangeStatus.NEW);
						}
						if (tableName.equals(newName)) {
							return new JpaName(Optional.of(newName), Optional.of(newName), JApiChangeStatus.UNCHANGED);
						} else {
							return new JpaName(Optional.of(tableName), Optional.of(newName), JApiChangeStatus.MODIFIED);
						}
					case REMOVED:
						String oldName = removeQuotationMarks(element.getOldValue().get().toString());
						if (changeStatusClass == JApiChangeStatus.REMOVED) {
							return new JpaName(Optional.of(oldName), Optional.<String>absent(), JApiChangeStatus.REMOVED);
						}
						if (tableName.equals(oldName)) {
							return new JpaName(Optional.of(oldName), Optional.of(oldName), JApiChangeStatus.UNCHANGED);
						} else {
							return new JpaName(Optional.of(oldName), Optional.of(tableName), JApiChangeStatus.MODIFIED);
						}
					case MODIFIED:
						newName = removeQuotationMarks(element.getNewValue().get().toString());
						oldName = removeQuotationMarks(element.getOldValue().get().toString());
						return new JpaName(Optional.of(oldName), Optional.of(newName), JApiChangeStatus.MODIFIED);
					case UNCHANGED:
						newName = removeQuotationMarks(element.getNewValue().get().toString());
						return new JpaName(Optional.of(newName), Optional.of(newName), JApiChangeStatus.UNCHANGED);
				}
			}
			break;
		}
		if (changeStatusClass == JApiChangeStatus.NEW) {
			return new JpaName(Optional.<String>absent(), Optional.of(tableName), JApiChangeStatus.NEW);
		} else if (changeStatusClass == JApiChangeStatus.REMOVED) {
			return new JpaName(Optional.of(tableName), Optional.<String>absent(), JApiChangeStatus.REMOVED);
		}
		return new JpaName(Optional.of(tableName), Optional.of(tableName), JApiChangeStatus.UNCHANGED);
	}

	private String removeQuotationMarks(String s) {
		return s.replace("\"", "");
	}

	private JApiChangeStatus computeChangeStatus() {
		JApiChangeStatus changeStatus = jApiClass.getChangeStatus();
		if (changeStatus == JApiChangeStatus.MODIFIED) {
			changeStatus = JApiChangeStatus.UNCHANGED;
		}
		if (jpaName.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
			changeStatus = JApiChangeStatus.MODIFIED;
		}
		return changeStatus;
	}

	private void extractAttributes() {
		List<JApiField> fields = jApiClass.getFields();
		for (JApiField field : fields) {
			Optional<JApiAnnotation> transientAnnotationOfFieldOptional = getTransientAnnotationOfField(field);
			Optional<JApiAnnotation> transientAnnotationOfPropertyOptional = getTransientAnnotationOfProperty(field);
			if (!transientAnnotationOfFieldOptional.isPresent() && !transientAnnotationOfPropertyOptional.isPresent()) {
				JApiChangeStatus fieldChangeStatus = field.getChangeStatus();
				if (fieldChangeStatus == JApiChangeStatus.NEW) {
					JpaAttribute jpaAttribute = new JpaAttribute(JApiChangeStatus.NEW);
				}
			}
		}
	}

	private Optional<JApiAnnotation> getTransientAnnotationOfField(JApiField field) {
		Optional<JApiAnnotation> returnValue = Optional.absent();
		for (JApiAnnotation annotation : field.getAnnotations()) {
			if (JpaAnalyzer.JPA_ANNOTATION_TRANSIENT.equals(annotation.getFullyQualifiedName())) {
				returnValue = Optional.of(annotation);
				break;
			}
		}
		return returnValue;
	}

	private Optional<JApiAnnotation> getTransientAnnotationOfProperty(JApiField field) {
		Optional<JApiAnnotation> returnValue = Optional.absent();
		Optional<JApiMethod> propertyMethodOptional = getPropertyMethod(field);
		if (propertyMethodOptional.isPresent()) {
			JApiMethod propertyMethod = propertyMethodOptional.get();
			for (JApiAnnotation annotation : propertyMethod.getAnnotations()) {
				if (JpaAnalyzer.JPA_ANNOTATION_TRANSIENT.equals(annotation.getFullyQualifiedName())) {
					returnValue = Optional.of(annotation);
					break;
				}
			}
		}
		return returnValue;
	}

	private Optional<JApiMethod> getPropertyMethod(JApiField field) {
		Optional<JApiMethod> propertyMethod = Optional.absent();
		String fieldName = field.getName();
		String getterName = "get" + (Character.isUpperCase(fieldName.charAt(0)) ?
			fieldName :
			fieldName.length() > 1 ?
				Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1) :
				Character.toUpperCase(fieldName.charAt(0)));
		for (JApiMethod method : jApiClass.getMethods()) {
			if (getterName.equals(method.getName())) {
				propertyMethod = Optional.of(method);
				break;
			}
		}
		if (!propertyMethod.isPresent()) {
			String isName = "is" + getterName.substring(3);
			for (JApiMethod method : jApiClass.getMethods()) {
				if (isName.equals(method.getName())) {
					propertyMethod = Optional.of(method);
					break;
				}
			}
		}
		return propertyMethod;
	}

	@XmlElementWrapper(name = "attributes")
	@XmlElement(name = "attribute")
	public List<JpaAttribute> getAttributes() {
		return attributes;
	}

	@XmlAttribute(name = "fullyQualifiedName")
	public String getFullyQualifiedName() {
		return jApiClass.getFullyQualifiedName();
	}

	public String computeTablename() {
		String className = toClassName(jApiClass.getFullyQualifiedName());
		return toJpaName(className);
	}

	static String toJpaName(String javaName) {
		boolean lastCharWasUpperCase = true;
		StringBuilder jpaName = new StringBuilder();
		for (int i = 0; i < javaName.length(); i++) {
			char c = javaName.charAt(i);
			if (Character.isUpperCase(c)) {
				if (!lastCharWasUpperCase) {
					jpaName.append("_");
				}
				jpaName.append(c);
				lastCharWasUpperCase = true;
			} else {
				jpaName.append(Character.toUpperCase(c));
				lastCharWasUpperCase = false;
			}
		}
		return jpaName.toString();
	}

	private String toClassName(String className) {
		String[] parts = className.split("\\.");
		if (parts.length > 0) {
			className = parts[parts.length - 1];
		}
		return className;
	}

	@XmlAttribute(name = "changeStatus")
	public String getChangeStatus() {
		return changeStatus.toString();
	}

	@XmlElement(name = "name")
	public JpaName getJpaName() {
		return this.jpaName;
	}
}
