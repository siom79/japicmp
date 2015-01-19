package japicmp.config;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import japicmp.exception.FormattedException;
import japicmp.model.AccessModifier;

public class Options {
	private File oldArchive;
	private File newArchive;
	private boolean outputOnlyModifications = false;
	private boolean outputOnlyBinaryIncompatibleModifications = false;
	private Optional<String> xmlOutputFile = Optional.absent();
	private Optional<String> htmlOutputFile = Optional.absent();
	private Optional<AccessModifier> accessModifier = Optional.of(AccessModifier.PUBLIC);
	private List<PackageFilter> packagesInclude = new LinkedList<>();
	private List<PackageFilter> packagesExclude = new LinkedList<>();
	private boolean showOnlySemverDiff = false;

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
		packagesExclude = applyExclude(packagesExcludeArg, packagesExclude);
	}

	 static ImmutableList<PackageFilter> applyExclude(Optional<String> packagesExcludeArg,
			List<PackageFilter> in) {
		return apply(packagesExcludeArg, in,
				"Wrong syntax for package exclude option '%s': %s");
	}

	public void addPackageIncludeFromArgument(String packagesIncludeArg) {
		addPackageIncludeFromArgument(Optional.of(packagesIncludeArg));
	}

	public void addPackageIncludeFromArgument(Optional<String> packagesIncludeArg) {
		packagesInclude = applyInclude(packagesIncludeArg, packagesInclude);
	}

	static ImmutableList<PackageFilter> applyInclude(Optional<String> packagesIncludeArg,
			List<PackageFilter> in) {
		return apply(packagesIncludeArg, in,
				"Wrong syntax for package include option '%s': %s");
	}

	void setPackagesInclude(ImmutableList<PackageFilter> packagesInclude) {
		this.packagesInclude = packagesInclude;
	}

	void setPackagesExclude(ImmutableList<PackageFilter> packagesExclude) {
		this.packagesExclude = packagesExclude;
	}

	private static ImmutableList<PackageFilter> apply(Optional<String> filterString,
			List<PackageFilter> packages,
			String format) {
		ImmutableList.Builder<PackageFilter> builder = ImmutableList.builder();
		builder.addAll(packages);
		for (String part : Splitter.on(",").trimResults().omitEmptyStrings()
				.split(filterString.or(""))) {
			try {
				builder.add(new PackageFilter(part));
			} catch (Exception e) {
				throw FormattedException.ofIAE(format, part, e.getMessage());
			}
		}
		return builder.build();
	}
	public void setOutputOnlyBinaryIncompatibleModifications(
			boolean outputOnlyBinaryIncompatibleModifications) {
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

	public void setShowOnlySemverDiff(boolean showOnlySemverDiff) {
		this.showOnlySemverDiff = showOnlySemverDiff;
	}

	public boolean isOnlySemverDiff() {
		return showOnlySemverDiff;
	}

}
