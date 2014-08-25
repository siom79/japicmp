package japicmp.output.xml;

import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.exception.JApiCmpException.Reason;
import japicmp.model.JApiClass;
import japicmp.output.OutputFilter;
import japicmp.output.xml.model.JApiCmpXmlRoot;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class XmlOutputGenerator {
	private static final String XSD_FILENAME = "japicmp.xsd";
	private static final String XML_NAMESPACE = "https://github.com/siom79/japicmp/schema/japicmp.xsd";
	private static final String XML_SCHEMA = XSD_FILENAME;
	private static final Logger LOGGER = Logger.getLogger(XmlOutputGenerator.class.getName());

    public void generate(File oldArchive, File newArchive, List<JApiClass> jApiClasses, Options options) {
        JApiCmpXmlRoot jApiCmpXmlRoot = createRootElement(oldArchive, newArchive, jApiClasses);
        filterClasses(jApiClasses, options);
        createXmlDocumentAndSchema(options, jApiCmpXmlRoot);
    }

	private void createXmlDocumentAndSchema(Options options, JApiCmpXmlRoot jApiCmpXmlRoot) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(JApiCmpXmlRoot.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, XML_NAMESPACE + " " + XML_SCHEMA);
			marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, XML_SCHEMA);
			final File xmlFile = new File(options.getXmlOutputFile().get());
			marshaller.marshal(jApiCmpXmlRoot, xmlFile);
			SchemaOutputResolver outputResolver = new SchemaOutputResolver() {
				@Override
				public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
					File schemaFile = xmlFile.getParentFile();
					if(schemaFile == null) {
						LOGGER.warning(String.format("File '%s' has no parent file. Using instead: '%s'.", xmlFile.getAbsolutePath(), XSD_FILENAME));
						schemaFile = new File(XSD_FILENAME);
					} else {
						schemaFile = new File(schemaFile + File.separator + XSD_FILENAME);
					}
			        StreamResult result = new StreamResult(schemaFile);
			        result.setSystemId(schemaFile.toURI().toURL().toString());
			        return result;
				}
			};
			jaxbContext.generateSchema(outputResolver);
		} catch (JAXBException e) {
			throw new JApiCmpException(Reason.JaxbException, String.format("Marshalling of XML document failed: %s", e.getMessage()), e);
		} catch (IOException e) {
			throw new JApiCmpException(Reason.IoException, String.format("Marshalling of XML document failed: %s", e.getMessage()), e);
		}
	}

	private void filterClasses(List<JApiClass> jApiClasses, Options options) {
		OutputFilter outputFilter = new OutputFilter(options);
		outputFilter.filter(jApiClasses);
	}

	private JApiCmpXmlRoot createRootElement(File oldArchive, File newArchive, List<JApiClass> jApiClasses) {
		JApiCmpXmlRoot jApiCmpXmlRoot = new JApiCmpXmlRoot();
        jApiCmpXmlRoot.setOldJar(oldArchive.getAbsolutePath());
        jApiCmpXmlRoot.setNewJar(newArchive.getAbsolutePath());
        jApiCmpXmlRoot.setClasses(jApiClasses);
		return jApiCmpXmlRoot;
	}
}
