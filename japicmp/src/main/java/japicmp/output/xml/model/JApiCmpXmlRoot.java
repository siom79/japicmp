package japicmp.output.xml.model;

import japicmp.model.JApiClass;
import japicmp.output.extapi.jpa.model.JpaTable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@XmlRootElement(name = "japicmp")
public class JApiCmpXmlRoot {
    private String oldJar = "";
    private String newJar = "";
    private List<JApiClass> classes = new LinkedList<>();
	private List<JpaTable> jpaTables = new LinkedList<>();

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
}
