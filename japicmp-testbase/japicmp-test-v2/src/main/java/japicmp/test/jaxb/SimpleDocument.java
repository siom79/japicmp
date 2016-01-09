package japicmp.test.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "document")
public class SimpleDocument {
	private String title;
	private String author;
	private List<Section> sections;

	public static class Section {
		private String title;
		private String content;

		@XmlElement
		public String getTitle() {
			return title;
		}

		@XmlElement
		public String getContent() {
			return content;
		}
	}

	@XmlElement
	public String getTitle() {
		return title;
	}

	@XmlElement
	public String getAuthor() {
		return author;
	}

	@XmlElement
	@XmlElementWrapper
	public List<Section> getSections() {
		return sections;
	}
}
