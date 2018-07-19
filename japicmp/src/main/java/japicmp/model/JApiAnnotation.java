package japicmp.model;

import japicmp.util.Optional;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.MemberValue;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JApiAnnotation implements JApiHasChangeStatus, JApiCompatibility {
	private final String fullyQualifiedName;
	private final Optional<Annotation> oldAnnotation;
	private final Optional<Annotation> newAnnotation;
	private final List<JApiAnnotationElement> elements = new LinkedList<>();
	private final JApiChangeStatus changeStatus;

	public JApiAnnotation(String fullyQualifiedName, Optional<Annotation> oldAnnotation, Optional<Annotation> newAnnotation, JApiChangeStatus changeStatus) {
		this.fullyQualifiedName = fullyQualifiedName;
		this.oldAnnotation = oldAnnotation;
		this.newAnnotation = newAnnotation;
		computeElements(this.elements, oldAnnotation, newAnnotation);
		this.changeStatus = evaluateChangeStatus(changeStatus);
	}


	public String toString()
	{
		return "JApiAnnotation [fullyQualifiedName="
			+ fullyQualifiedName
			+ ", oldAnnotation="
			+ oldAnnotation
			+ ", newAnnotation="
			+ newAnnotation
			+ ", changeStatus="
			+ changeStatus
			+ ", compatibilityChanges="
			+ getCompatibilityChanges()
			+ "]";
	}


	private void computeElements(List<JApiAnnotationElement> elements, Optional<Annotation> oldAnnotationOptional, Optional<Annotation> newAnnotationOptional) {
		if (oldAnnotationOptional.isPresent() && newAnnotationOptional.isPresent()) {
			Annotation oldAnnotation = oldAnnotationOptional.get();
			Annotation newAnnotation = newAnnotationOptional.get();
			Map<String, Optional<MemberValue>> oldMemberValueMap = buildMemberValueMap(oldAnnotation);
			Map<String, Optional<MemberValue>> newMemberValueMap = buildMemberValueMap(newAnnotation);
			for (String memberName : oldMemberValueMap.keySet()) {
				Optional<MemberValue> foundOptional = newMemberValueMap.get(memberName);
				if (foundOptional == null) {
					JApiAnnotationElement jApiAnnotationElement = new JApiAnnotationElement(memberName, oldMemberValueMap.get(memberName), Optional.<MemberValue>absent(),
						JApiChangeStatus.REMOVED);
					elements.add(jApiAnnotationElement);
				} else {
					JApiAnnotationElement jApiAnnotationElement = new JApiAnnotationElement(memberName, oldMemberValueMap.get(memberName), foundOptional,
						JApiChangeStatus.UNCHANGED);
					elements.add(jApiAnnotationElement);
				}
			}
			for (String memberName : newMemberValueMap.keySet()) {
				Optional<MemberValue> foundOptional = oldMemberValueMap.get(memberName);
				if (foundOptional == null) {
					JApiAnnotationElement jApiAnnotationElement = new JApiAnnotationElement(memberName, Optional.<MemberValue>absent(), newMemberValueMap.get(memberName),
						JApiChangeStatus.NEW);
					elements.add(jApiAnnotationElement);
				}
			}
		} else {
			if (oldAnnotationOptional.isPresent()) {
				Annotation oldAnnotation = oldAnnotationOptional.get();
				Map<String, Optional<MemberValue>> oldMemberValueMap = buildMemberValueMap(oldAnnotation);
				for (String memberName : oldMemberValueMap.keySet()) {
					JApiAnnotationElement jApiAnnotationElement = new JApiAnnotationElement(memberName, oldMemberValueMap.get(memberName), Optional.<MemberValue>absent(),
						JApiChangeStatus.REMOVED);
					elements.add(jApiAnnotationElement);
				}
			}
			if (newAnnotationOptional.isPresent()) {
				Annotation newAnnotation = newAnnotationOptional.get();
				Map<String, Optional<MemberValue>> newMemberValueMap = buildMemberValueMap(newAnnotation);
				for (String memberName : newMemberValueMap.keySet()) {
					JApiAnnotationElement jApiAnnotationElement = new JApiAnnotationElement(memberName, Optional.<MemberValue>absent(), newMemberValueMap.get(memberName),
						JApiChangeStatus.NEW);
					elements.add(jApiAnnotationElement);
				}
			}
		}
	}

	private Map<String, Optional<MemberValue>> buildMemberValueMap(Annotation annotation) {
		Map<String, Optional<MemberValue>> map = new HashMap<>();
		@SuppressWarnings("unchecked")
		Set<String> memberNames = annotation.getMemberNames();
		if (memberNames != null) {
			for (String memberName : memberNames) {
				MemberValue memberValue = annotation.getMemberValue(memberName);
				if (memberValue == null) {
					map.put(memberName, Optional.<MemberValue>absent());
				} else {
					map.put(memberName, Optional.of(memberValue));
				}
			}
		}
		return map;
	}

	private JApiChangeStatus evaluateChangeStatus(JApiChangeStatus changeStatus) {
		if (changeStatus == JApiChangeStatus.UNCHANGED) {
			for (JApiAnnotationElement annotationElement : elements) {
				if (annotationElement.getChangeStatus() != JApiChangeStatus.UNCHANGED) {
					changeStatus = JApiChangeStatus.MODIFIED;
				}
			}
		}
		return changeStatus;
	}

	@Override
	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return this.changeStatus;
	}

	@XmlAttribute(name = "fullyQualifiedName")
	public String getFullyQualifiedName() {
		return fullyQualifiedName;
	}

	@XmlTransient
	public Optional<Annotation> getOldAnnotation() {
		return oldAnnotation;
	}

	@XmlTransient
	public Optional<Annotation> getNewAnnotation() {
		return newAnnotation;
	}

	@XmlElementWrapper(name = "elements")
	@XmlElement(name = "element")
	public List<JApiAnnotationElement> getElements() {
		return elements;
	}

	@Override
	@XmlAttribute
	public boolean isBinaryCompatible() {
		return true;
	}

	@Override
	@XmlAttribute
	public boolean isSourceCompatible() {
		return true;
	}

	@XmlElementWrapper(name = "compatibilityChanges")
	@XmlElement(name = "compatibilityChange")
	public List<JApiCompatibilityChange> getCompatibilityChanges() {
		return Collections.emptyList();
	}
}
