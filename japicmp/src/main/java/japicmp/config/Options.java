package japicmp.config;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import japicmp.exception.JApiCmpException;
import japicmp.model.AccessModifier;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class Options {
	private File oldArchive;
	private File newArchive;
	private boolean outputOnlyModifications = false;
	private boolean outputOnlyBinaryIncompatibleModifications = false;
	private Optional<String> xmlOutputFile = Optional.absent();
	private Optional<String> htmlOutputFile = Optional.absent();
	private Optional<AccessModifier> accessModifier = Optional.of(AccessModifier.PROTECTED);
	private List<PackageFilter> packagesInclude = new LinkedList<>();
	private List<PackageFilter> packagesExclude = new LinkedList<>();

	public File getNewArchive() {
		return newArchive;
	}

	public void setNewArchive(File newArchive) {
		this.newArchive = newArchive;
	}

	public File getOldArchive() {
		return oldArchive;
	}

	public void setOldArchive(File oldArchive) {
		this.oldArchive = oldArchive;
	}

	public boolean isOutputOnlyModifications() {
		return outputOnlyModifications;
	}

	public void setOutputOnlyModifications(boolean outputOnlyModifications) {
		this.outputOnlyModifications = outputOnlyModifications;
	}

	public Optional<String> getXmlOutputFile() {
		return xmlOutputFile;
	}

	public void setXmlOutputFile(Optional<String> xmlOutputFile) {
		this.xmlOutputFile = xmlOutputFile;
	}

	public void setAccessModifier(Optional<AccessModifier> accessModifier) {
		this.accessModifier = accessModifier;
	}

	public void setAccessModifier(AccessModifier accessModifier) {
		this.accessModifier = Optional.of(accessModifier);
	}

	public AccessModifier getAccessModifier() {
		return accessModifier.get();
	}

	public List<PackageFilter> getPackagesInclude() {
		return ImmutableList.copyOf(packagesInclude);
	}

	public List<PackageFilter> getPackagesExclude() {
		return ImmutableList.copyOf(packagesExclude);
	}

	public void addPackagesExcludeFromArgument(String packagesExcludeArg) {
		addPackagesExcludeFromArgument(Optional.of(packagesExcludeArg));
	}

	public void addPackagesExcludeFromArgument(Optional<String> packagesExcludeArg) {
		packagesExclude = apply(packagesExcludeArg, packagesExclude, "Wrong syntax for package exclude option '%s': %s");
	}

	public void addPackageIncludeFromArgument(String packagesIncludeArg) {
		addPackageIncludeFromArgument(Optional.of(packagesIncludeArg));
	}

	public void addPackageIncludeFromArgument(Optional<String> packagesIncludeArg) {
		packagesInclude = apply(packagesIncludeArg, packagesInclude, "Wrong syntax for include exclude option '%s': %s");
	}

	private List<PackageFilter> apply(Optional<String> filterString, List<PackageFilter> packages, String format) {
		for (String part : Splitter.on(",").trimResults().omitEmptyStrings().split(filterString.or(""))) {
			try {
				packages.add(new PackageFilter(part));
			} catch (Exception e) {
				throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, String.format(format, part, e.getMessage()));
			}
		}
		return packages;
	}

	public void setOutputOnlyBinaryIncompatibleModifications(boolean outputOnlyBinaryIncompatibleModifications) {
		this.outputOnlyBinaryIncompatibleModifications = outputOnlyBinaryIncompatibleModifications;
	}

	public boolean isOutputOnlyBinaryIncompatibleModifications() {
		return outputOnlyBinaryIncompatibleModifications;
	}

	public Optional<String> getHtmlOutputFile() {
		return htmlOutputFile;
	}

	public void setHtmlOutputFile(Optional<String> htmlOutputFile) {
		this.htmlOutputFile = htmlOutputFile;
	}
}
