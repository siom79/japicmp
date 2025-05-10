package japicmp.output.xml;

import japicmp.config.Options;
import japicmp.exception.JApiCmpException;
import japicmp.exception.JApiCmpException.Reason;
import japicmp.model.JApiClass;
import japicmp.output.OutputFilter;
import japicmp.output.OutputGenerator;
import japicmp.output.xml.model.JApiCmpXmlRoot;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import static japicmp.util.StringHelper.filtersAsString;

public class XmlOutputGenerator extends OutputGenerator<XmlOutput> {
	private static final String XSD_FILENAME = "japicmp.xsd";
	private static final String XML_SCHEMA = XSD_FILENAME;
	private static final Logger LOGGER = Logger.getLogger(XmlOutputGenerator.class.getName());
	private final XmlOutputGeneratorOptions xmlOutputGeneratorOptions;

	@Deprecated
	public XmlOutputGenerator(List<JApiClass> jApiClasses, Options options, boolean createSchemaFile) {
		super(options, jApiClasses);
		this.xmlOutputGeneratorOptions = new XmlOutputGeneratorOptions();
		this.xmlOutputGeneratorOptions.setCreateSchemaFile(createSchemaFile);
	}

	public XmlOutputGenerator(List<JApiClass> jApiClasses, Options options, XmlOutputGeneratorOptions xmlOutputGeneratorOptions) {
		super(options, jApiClasses);
		this.xmlOutputGeneratorOptions = xmlOutputGeneratorOptions;
	}

	@Override
	public XmlOutput generate() {
		JApiCmpXmlRoot jApiCmpXmlRoot = createRootElement(jApiClasses, options);
		filterClasses(jApiClasses, options);
		return createXmlDocumentAndSchema(options, jApiCmpXmlRoot);
	}

	public static List<File> writeToFiles(Options options, XmlOutput xmlOutput) {
		return xmlOutput.getXmlOutputStream()
			.flatMap(out -> options.getXmlOutputFile().map(file -> writeToFiles(out, file, xmlOutput)))
			.orElseGet(ArrayList::new);
	}

	private static List<File> writeToFiles(ByteArrayOutputStream outputStream, String xmlOutputFile, XmlOutput xmlOutput) {
		List<File> filesWritten = new ArrayList<>();
		try {
			File xmlFile = new File(xmlOutputFile);
			try (FileOutputStream fos = new FileOutputStream(xmlFile)) {
				outputStream.writeTo(fos);
				filesWritten.add(xmlFile);
			} catch (IOException e) {
				throw new JApiCmpException(JApiCmpException.Reason.IoException, "Failed to write XML file '" + xmlFile.getAbsolutePath() + "': " + e.getMessage(), e);
			}
		} finally {
			try {
				xmlOutput.close();
			} catch (Exception e) {
				LOGGER.log(Level.FINE, "Failed to close XML file: " + e.getLocalizedMessage(), e);
			}
		}
		return filesWritten;
	}

	private XmlOutput createXmlDocumentAndSchema(Options options, JApiCmpXmlRoot jApiCmpXmlRoot) {
		XmlOutput xmlOutput = new XmlOutput();
		xmlOutput.setJApiCmpXmlRoot(jApiCmpXmlRoot);
		ByteArrayOutputStream xmlBaos;
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(JApiCmpXmlRoot.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
			marshaller.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, XML_SCHEMA);
			xmlBaos = new ByteArrayOutputStream();
			marshaller.marshal(jApiCmpXmlRoot, xmlBaos);
			if (options.getXmlOutputFile().isPresent()) {
				xmlOutput.setXmlOutputStream(Optional.of(xmlBaos));
				if (xmlOutputGeneratorOptions.isCreateSchemaFile()) {
					final File xmlFile = new File(options.getXmlOutputFile().get());
					SchemaOutputResolver outputResolver = new SchemaOutputResolver() {
						@Override
						public Result createOutput(String namespaceUri, String suggestedFileName) {
							File schemaFile = xmlFile.getParentFile();
							if (schemaFile == null) {
								LOGGER.warning(String.format("File '%s' has no parent file. Using instead: '%s'.", xmlFile.getAbsolutePath(), XSD_FILENAME));
								schemaFile = new File(XSD_FILENAME);
							} else {
								schemaFile = new File(schemaFile + File.separator + XSD_FILENAME);
							}
							StreamResult result = new StreamResult(schemaFile);
							result.setSystemId(schemaFile.getAbsolutePath());
							return result;
						}
					};
					jaxbContext.generateSchema(outputResolver);
				}
			}
		} catch (JAXBException e) {
			throw new JApiCmpException(Reason.JaxbException, String.format("Marshalling of XML document failed: %s", e.getMessage()), e);
		} catch (IOException e) {
			throw new JApiCmpException(Reason.IoException, String.format("Marshalling of XML document failed: %s", e.getMessage()), e);
		}
		return xmlOutput;
	}

	private void filterClasses(List<JApiClass> jApiClasses, Options options) {
		OutputFilter outputFilter = new OutputFilter(options);
		outputFilter.filter(jApiClasses);
	}

	private JApiCmpXmlRoot createRootElement(List<JApiClass> jApiClasses, Options options) {
		JApiCmpXmlRoot jApiCmpXmlRoot = new JApiCmpXmlRoot();
		jApiCmpXmlRoot.setOldJar(options.joinOldArchives());
		jApiCmpXmlRoot.setNewJar(options.joinNewArchives());
		jApiCmpXmlRoot.setOldVersion(options.joinOldVersions());
		jApiCmpXmlRoot.setNewVersion(options.joinNewVersions());
		jApiCmpXmlRoot.setClasses(jApiClasses);
		jApiCmpXmlRoot.setAccessModifier(options.getAccessModifier().name());
		jApiCmpXmlRoot.setOnlyModifications(options.isOutputOnlyModifications());
		jApiCmpXmlRoot.setOnlyBinaryIncompatibleModifications(options.isOutputOnlyBinaryIncompatibleModifications());
		jApiCmpXmlRoot.setPackagesInclude(filtersAsString(options.getIncludes(), true));
		jApiCmpXmlRoot.setPackagesExclude(filtersAsString(options.getExcludes(), false));
		jApiCmpXmlRoot.setIgnoreMissingClasses(options.getIgnoreMissingClasses().isIgnoreAllMissingClasses());
		jApiCmpXmlRoot.setIgnoreMissingClassesByRegularExpressions(regExAsString(options.getIgnoreMissingClasses().getIgnoreMissingClassRegularExpression()));
		xmlOutputGeneratorOptions.getTitle().ifPresent(jApiCmpXmlRoot::setTitle);
		jApiCmpXmlRoot.setSemanticVersioning(xmlOutputGeneratorOptions.getSemanticVersioningInformation());
		return jApiCmpXmlRoot;
	}

	private String regExAsString(List<Pattern> ignoreMissingClassRegularExpression) {
		StringBuilder sb = new StringBuilder();
		for (Pattern pattern : ignoreMissingClassRegularExpression) {
			if (sb.length() > 0) {
				sb.append(";");
			}
			sb.append(pattern.toString());
		}
		return sb.toString();
	}
}
