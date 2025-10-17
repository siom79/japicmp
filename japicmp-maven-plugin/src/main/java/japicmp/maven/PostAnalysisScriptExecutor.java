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
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;

/**
 * Class to execute the post-analysis script.
 */
public class PostAnalysisScriptExecutor {

	/**
	 * Applies the post-analysis script.
	 *
	 * @param parameter   the current configuration parameters
	 * @param jApiClasses the {@code List} of classes being analyzed
	 * @param log         the Maven logger
	 *
	 * @return a filtered list of analyzed classes
	 *
	 * @throws MojoExecutionException if the script fails
	 */
	public List<JApiClass> apply(final ConfigParameters parameter, final List<JApiClass> jApiClasses, final Log log)
			throws MojoExecutionException {

		List<JApiClass> filteredList = jApiClasses;
		final String postAnalysisFilterScript = parameter.getPostAnalysisScript();
		if (postAnalysisFilterScript != null) {
			try {
				final InputStream inputStream = getInputStream(postAnalysisFilterScript);
				if (inputStream != null) {
					final ScriptEngine scriptEngine = new ScriptEngineManager().getEngineByName("groovy");
					final Bindings bindings = scriptEngine.createBindings();
					bindings.put("jApiClasses", jApiClasses);
					try (final InputStreamReader fileReader = new InputStreamReader(inputStream,
							StandardCharsets.UTF_8)) {
						final Object returnValue = scriptEngine.eval(fileReader, bindings);
						if (returnValue instanceof List) {
							final List returnedList = (List) returnValue;
							filteredList = new ArrayList<>(returnedList.size());
							for (final Object obj : returnedList) {
								if (obj instanceof JApiClass) {
									final JApiClass jApiClass = (JApiClass) obj;
									filteredList.add(jApiClass);
								}
							}
						} else {
							throw new MojoExecutionException("Post-analysis script does not return a list.");
						}
					} catch (ScriptException e) {
						throw new MojoExecutionException(
								"Execution of post-analysis script failed: " + e.getMessage(), e);
					} catch (IOException e) {
						throw new MojoExecutionException("Failed to load post-analysis script '"
								+ postAnalysisFilterScript
								+ ": "
								+ e.getMessage(), e);
					}
				} else {
					throw new MojoExecutionException(
							"Post-analysis script '" + postAnalysisFilterScript + "' does not exist.");
				}
			} catch (FileNotFoundException e) {
				throw new MojoExecutionException(
						"Post-analysis script '" + postAnalysisFilterScript + " does not exist.", e);
			}
		} else {
			log.debug("No post-analysis script provided.");
		}

		return filteredList;
	}

	/**
	 * Returns a stream of the specified post-analysis script
	 *
	 * @param postAnalysisFilterScript the file name of the post-analysis script
	 *
	 * @return the stream of the post-analysis script
	 *
	 * @throws FileNotFoundException if the post-analysis script is not found
	 */
	private InputStream getInputStream(final String postAnalysisFilterScript) throws FileNotFoundException {
		if (Files.exists(Paths.get(postAnalysisFilterScript))) {
			return new FileInputStream(postAnalysisFilterScript);
		}
		return PostAnalysisScriptExecutor.class.getClassLoader().getResourceAsStream(
				postAnalysisFilterScript);
	}
}
