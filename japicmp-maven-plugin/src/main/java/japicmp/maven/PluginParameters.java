package japicmp.maven;

import japicmp.util.Optional;

import java.io.File;
import java.util.List;

public class PluginParameters {
	private final String skipParam;
	private final Version oldVersionParam;
	private final List<DependencyDescriptor> oldVersionsParam;
	private final Version newVersionParam;
	private final List<DependencyDescriptor> newVersionsParam;
	private final Parameter parameterParam;
	private final List<Dependency> dependenciesParam;
	private final List<Dependency> oldClassPathDependencies;
	private final List<Dependency> newClassPathDependencies;
	private final Optional<File> projectBuildDirParam;
	private final Optional<String> outputDirectory;
	private final boolean writeToFiles;

	public PluginParameters(String skipParam, Version newVersionParam, Version oldVersionParam, Parameter parameterParam, List<Dependency> dependenciesParam, Optional<File> projectBuildDirParam, Optional<String> outputDirectory, boolean writeToFiles, List<DependencyDescriptor> oldVersionsParam, List<DependencyDescriptor> newVersionsParam, List<Dependency> oldClassPathDependencies, List<Dependency> newClassPathDependencies) {
		this.skipParam = skipParam;
		this.newVersionParam = newVersionParam;
		this.oldVersionParam = oldVersionParam;
		this.parameterParam = parameterParam == null ? new Parameter() : parameterParam;
		this.dependenciesParam = dependenciesParam;
		this.oldClassPathDependencies = oldClassPathDependencies;
		this.newClassPathDependencies = newClassPathDependencies;
		this.projectBuildDirParam = projectBuildDirParam;
		this.outputDirectory = outputDirectory;
		this.writeToFiles = writeToFiles;
		this.oldVersionsParam = oldVersionsParam;
		this.newVersionsParam = newVersionsParam;
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

	public List<DependencyDescriptor> getOldVersionsParam() {
		return oldVersionsParam;
	}

	public List<DependencyDescriptor> getNewVersionsParam() {
		return newVersionsParam;
	}

	public List<Dependency> getOldClassPathDependencies() {
		return oldClassPathDependencies;
	}

	public List<Dependency> getNewClassPathDependencies() {
		return newClassPathDependencies;
	}
}
