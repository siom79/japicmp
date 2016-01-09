package japicmp.test.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "simpleDocument")
public class SimpleDocument {
	private String title;
	private String author;
	private List<Section> sections;

	public static class Section {
		private String title;
		private String text;

		@XmlElement
		public String getTitle() {
			return title;
		}

		@XmlElement
		public String getText() {
			return text;
		}
	}

	@XmlAttribute
	public String getTitle() {
		return title;
	}

	@XmlAttribute
	public String getAuthor() {
		return author;
	}

	@XmlElement
	@XmlElementWrapper
	public List<Section> getSections() {
		return sections;
	}
}
