package japicmp.output.xml;

import japicmp.config.Options;
import japicmp.model.JApiClass;
import japicmp.output.OutputFilter;
import japicmp.output.xml.model.JApiCmpXmlRoot;

import javax.xml.bind.JAXB;
import java.io.File;
import java.util.List;

public class XmlOutputGenerator {

    public void generate(File oldArchive, File newArchive, List<JApiClass> jApiClasses, Options options) {
        JApiCmpXmlRoot jApiCmpXmlRoot = new JApiCmpXmlRoot();
        jApiCmpXmlRoot.setOldJar(oldArchive.getAbsolutePath());
        jApiCmpXmlRoot.setNewJar(newArchive.getAbsolutePath());
        jApiCmpXmlRoot.setClasses(jApiClasses);
        OutputFilter outputFilter = new OutputFilter(options);
		outputFilter.filter(jApiClasses);
        JAXB.marshal(jApiCmpXmlRoot, new File(options.getXmlOutputFile().get()));
    }
}
