package japicmp.maven;

import com.google.common.base.Optional;

import java.io.File;
import java.util.List;

public class PluginParameters {
	private final String skipParam;
	private final Version newVersionParam;
	private final Version oldVersionParam;
	private final Parameter parameterParam;
	private final List<Dependency> dependenciesParam;
	private final Optional<File> projectBuildDirParam;
	private final Optional<String> outputDirectory;
	private final boolean writeToFiles;

	public PluginParameters(String skipParam, Version newVersionParam, Version oldVersionParam, Parameter parameterParam, List<Dependency> dependenciesParam, Optional<File> projectBuildDirParam, Optional<String> outputDirectory, boolean writeToFiles) {
		this.skipParam = skipParam;
		this.newVersionParam = newVersionParam;
		this.oldVersionParam = oldVersionParam;
		this.parameterParam = parameterParam;
		this.dependenciesParam = dependenciesParam;
		this.projectBuildDirParam = projectBuildDirParam;
		this.outputDirectory = outputDirectory;
		this.writeToFiles = writeToFiles;
	}

	public String getSkipParam() {
		return skipParam;
	}

	public Version getNewVersionParam() {
		return newVersionParam;
	}

	public Version getOldVersionParam() {
		return oldVersionParam;
	}

	public Parameter getParameterParam() {
		return parameterParam;
	}

	public List<Dependency> getDependenciesParam() {
		return dependenciesParam;
	}

	public Optional<File> getProjectBuildDirParam() {
		return projectBuildDirParam;
	}

	public Optional<String> getOutputDirectory() {
		return outputDirectory;
	}

	public boolean isWriteToFiles() {
		return writeToFiles;
	}
}
