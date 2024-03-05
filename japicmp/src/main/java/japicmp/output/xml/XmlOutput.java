package japicmp.output.xml;

import japicmp.output.xml.model.JApiCmpXmlRoot;
import japicmp.util.Optional;

import java.io.ByteArrayOutputStream;

public class XmlOutput implements AutoCloseable {
	private Optional<ByteArrayOutputStream> xmlOutputStream = Optional.absent();
	private japicmp.output.xml.model.JApiCmpXmlRoot JApiCmpXmlRoot;

	public Optional<ByteArrayOutputStream> getXmlOutputStream() {
		return xmlOutputStream;
	}

	public void setXmlOutputStream(Optional<ByteArrayOutputStream> xmlOutputStream) {
		this.xmlOutputStream = xmlOutputStream;
	}

	@Override
	public void close() throws Exception {
		if (this.xmlOutputStream.isPresent()) {
			this.xmlOutputStream.get().close();
		}
	}

	public void setJApiCmpXmlRoot(JApiCmpXmlRoot JApiCmpXmlRoot) {
		this.JApiCmpXmlRoot = JApiCmpXmlRoot;
	}

	public JApiCmpXmlRoot getJApiCmpXmlRoot() {
		return JApiCmpXmlRoot;
	}
}
