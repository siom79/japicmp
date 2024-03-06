package japicmp.output.html;

import japicmp.exception.JApiCmpException;
import japicmp.util.Streams;

import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TemplateEngine {
	private static final Map<String, String> templateCache = new ConcurrentHashMap<>();

	public String loadAndFillTemplate(String path, Map<String, String> params) {
		String template = loadTemplate(path);
		for (Map.Entry<String, String> entry : params.entrySet()) {
			template = template.replace("${" + entry.getKey() + "}", entry.getValue());
		}
		return template;
	}

	public String loadTemplate(String path) {
		String template = templateCache.get(path);
		if (template == null) {
			InputStream resourceAsStream = HtmlOutputGenerator.class.getResourceAsStream(path);
			if (resourceAsStream == null) {
				throw new JApiCmpException(JApiCmpException.Reason.ResourceNotFound, "Failed to load: " + path);
			}
			template = Streams.asString(resourceAsStream);
			templateCache.put(path, template);
		}
		return template;
	}
}
