package japicmp.output.xml.model;

import japicmp.util.Optional;
import japicmp.model.JApiClass;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "japicmp")
public class JApiCmpXmlRoot {
	private String oldJar = "";
	private String newJar = "";
	private String oldVersion = "";
	private String newVersion = "";
	private String accessModifier = "";
	private List<JApiClass> classes = new LinkedList<>();
	private boolean onlyModifications;
	private boolean onlyBinaryIncompatibleModifications;
	private String packagesInclude;
	private String packagesExclude;
	private boolean ignoreMissingClasses;
	private String ignoreMissingClassesByRegularExpressions;
	private Optional<String> titleOptional = Optional.absent();
	private String semanticVersioning = "n.a.";

	@XmlElementWrapper(name = "classes")
	@XmlElement(name = "class")
	public List<JApiClass> getClasses() {
		return classes;
	}

	public void setClasses(List<JApiClass> classes) {
		this.classes = classes;
	}

	@XmlAttribute
	public String getNewJar() {
		return newJar;
	}

	public void setNewJar(String newJar) {
		this.newJar = newJar;
	}

	@XmlAttribute
	public String getOldJar() {
		return oldJar;
	}

	public void setOldJar(String oldJar) {
		this.oldJar = oldJar;
	}

	@XmlAttribute
	public String getCreationTimestamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
		return sdf.format(new Date());
	}

	@XmlAttribute
	public String getAccessModifier() {
		return accessModifier;
	}

	public void setAccessModifier(String accessModifier) {
		this.accessModifier = accessModifier;
	}

	public void setOnlyModifications(boolean onlyModifications) {
		this.onlyModifications = onlyModifications;
	}

	@XmlAttribute
	public boolean isOnlyModifications() {
		return onlyModifications;
	}

	public void setOnlyBinaryIncompatibleModifications(boolean onlyBinaryIncompatibleModifications) {
		this.onlyBinaryIncompatibleModifications = onlyBinaryIncompatibleModifications;
	}

	@XmlAttribute
	public boolean isOnlyBinaryIncompatibleModifications() {
		return onlyBinaryIncompatibleModifications;
	}

	public void setPackagesInclude(String packagesInclude) {
		this.packagesInclude = packagesInclude;
	}

	@XmlAttribute
	public String getPackagesInclude() {
		return packagesInclude;
	}

	public void setPackagesExclude(String packagesExclude) {
		this.packagesExclude = packagesExclude;
	}

	@XmlAttribute
	public String getPackagesExclude() {
		return packagesExclude;
	}

	@XmlAttribute
	public boolean isIgnoreMissingClasses() {
		return ignoreMissingClasses;
	}

	public void setIgnoreMissingClasses(boolean ignoreMissingClasses) {
		this.ignoreMissingClasses = ignoreMissingClasses;
	}

	@XmlAttribute
	public String getTitle() {
		String title;
		if (this.titleOptional.isPresent()) {
			title = this.titleOptional.get();
		} else {
			title = "JApiCmp-Report";
		}
		return title;
	}

	public void setTitle(String title) {
		this.titleOptional = Optional.fromNullable(title);
	}

	@XmlAttribute
	public String getSemanticVersioning() {
		return semanticVersioning;
	}

	public void setSemanticVersioning(String semanticVersioning) {
		this.semanticVersioning = semanticVersioning;
	}

	@XmlAttribute
	public String getIgnoreMissingClassesByRegularExpressions() {
		return ignoreMissingClassesByRegularExpressions;
	}

	public void setIgnoreMissingClassesByRegularExpressions(String ignoreMissingClassesByRegularExpressions) {
		this.ignoreMissingClassesByRegularExpressions = ignoreMissingClassesByRegularExpressions;
	}

	@XmlTransient
	public Optional<String> getTitleOptional() {
		return titleOptional;
	}

	public void setTitleOptional(Optional<String> titleOptional) {
		this.titleOptional = titleOptional;
	}

	@XmlAttribute
	public String getOldVersion() {
		return oldVersion;
	}

	public void setOldVersion(String oldVersion) {
		this.oldVersion = oldVersion;
	}

	@XmlAttribute
	public String getNewVersion() {
		return newVersion;
	}

	public void setNewVersion(String newVersion) {
		this.newVersion = newVersion;
	}
}
