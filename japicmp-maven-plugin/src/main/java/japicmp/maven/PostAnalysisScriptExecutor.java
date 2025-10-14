package japicmp.maven;

import japicmp.model.JApiClass;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;

public class PostAnalysisScriptExecutor {

	public List<JApiClass> apply(ConfigParameters parameter, List<JApiClass> jApiClasses, Log log)
			throws MojoFailureException {
		List<JApiClass> filteredList = jApiClasses;
		if (parameter!=null) {
			String postAnalysisFilterScript = parameter.getPostAnalysisScript();
			if (postAnalysisFilterScript!=null) {
				try {
					InputStream inputStream = getInputStream(postAnalysisFilterScript);
					if (inputStream!=null) {
						ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("groovy");
						Bindings bindings = scriptEngine.createBindings();
						bindings.put("jApiClasses", jApiClasses);
						try (InputStreamReader fileReader = new InputStreamReader(inputStream,
								StandardCharsets.UTF_8)) {
							Object returnValue = scriptEngine.eval(fileReader, bindings);
							if (returnValue instanceof List) {
								List returnedList = (List) returnValue;
								filteredList = new ArrayList<>(returnedList.size());
								for (Object obj : returnedList) {
									if (obj instanceof JApiClass) {
										JApiClass jApiClass = (JApiClass) obj;
										filteredList.add(jApiClass);
									}
								}
							} else {
								throw new MojoFailureException("Post-analysis script does not return a list.");
							}
						} catch (ScriptException e) {
							throw new MojoFailureException(
									"Execution of post-analysis script failed: " + e.getMessage(), e);
						} catch (IOException e) {
							throw new MojoFailureException("Failed to load post-analysis script '"
									+ postAnalysisFilterScript
									+ ": "
									+ e.getMessage(), e);
						}
					} else {
						throw new MojoFailureException(
								"Post-analysis script '" + postAnalysisFilterScript + " does not exist.");
					}
				} catch (FileNotFoundException e) {
					throw new MojoFailureException(
							"Post-analysis script '" + postAnalysisFilterScript + " does not exist.", e);
				}
			} else {
				log.debug("No post-analysis script provided.");
			}
		}
		return filteredList;
	}

	private InputStream getInputStream(String postAnalysisFilterScript) throws FileNotFoundException {
		if (Files.exists(Paths.get(postAnalysisFilterScript))) {
			return new FileInputStream(postAnalysisFilterScript);
		}
		return PostAnalysisScriptExecutor.class.getClassLoader().getResourceAsStream(
				postAnalysisFilterScript);
	}
}
