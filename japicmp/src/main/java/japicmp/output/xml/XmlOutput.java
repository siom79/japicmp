package japicmp.output.xml;

import japicmp.util.Optional;
import japicmp.output.xml.model.JApiCmpXmlRoot;

import java.io.ByteArrayOutputStream;

public class XmlOutput implements AutoCloseable {
	private Optional<ByteArrayOutputStream> xmlOutputStream = Optional.absent();
	private Optional<ByteArrayOutputStream> htmlOutputStream = Optional.absent();
	private japicmp.output.xml.model.JApiCmpXmlRoot JApiCmpXmlRoot;

	public Optional<ByteArrayOutputStream> getXmlOutputStream() {
		return xmlOutputStream;
	}

	public void setXmlOutputStream(Optional<ByteArrayOutputStream> xmlOutputStream) {
		this.xmlOutputStream = xmlOutputStream;
	}

	public Optional<ByteArrayOutputStream> getHtmlOutputStream() {
		return htmlOutputStream;
	}

	public void setHtmlOutputStream(Optional<ByteArrayOutputStream> htmlOutputStream) {
		this.htmlOutputStream = htmlOutputStream;
	}

	@Override
	public void close() throws Exception {
		if (this.xmlOutputStream.isPresent()) {
			this.xmlOutputStream.get().close();
		}
		if (this.htmlOutputStream.isPresent()) {
			this.htmlOutputStream.get().close();
		}
	}

	public void setJApiCmpXmlRoot(JApiCmpXmlRoot JApiCmpXmlRoot) {
		this.JApiCmpXmlRoot = JApiCmpXmlRoot;
	}

	public JApiCmpXmlRoot getJApiCmpXmlRoot() {
		return JApiCmpXmlRoot;
	}
}
