package japicmp.test.output.xml;

import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class XmlHelper {

	private XmlHelper() {

	}

	static Elements getDivForClass(Document document, String className) {
		return document.select("div[id= " + className + "]");
	}
}
