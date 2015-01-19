package japicmp.config;

import java.io.File;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import japicmp.model.AccessModifier;

public class ImmutableOptions {

	private final File oldArchive;
	private final File newArchive;
	private final boolean outputOnlyModifications;
	private final boolean outputOnlyBinaryIncompatibleModifications;
	private final Optional<String> xmlOutputFileName;
	private final Optional<String> htmlOutputFileName;
	private final Optional<AccessModifier> accessModifier;
	private final ImmutableList<PackageFilter> packagesInclude;
	private final ImmutableList<PackageFilter> packagesExclude;
	private final boolean showOnlySemverDiff;

	private ImmutableOptions(Builder builder) {
		this.oldArchive = Preconditions.checkNotNull(builder.oldArchive, "oldArchive must not null");
		this.newArchive = Preconditions.checkNotNull(builder.newArchive, "newArchive must not null");
		this.outputOnlyModifications = builder.outputOnlyModifications;

		this.outputOnlyBinaryIncompatibleModifications = //
				builder.outputOnlyBinaryIncompatibleModifications;
		this.xmlOutputFileName = //
				Preconditions.checkNotNull(builder.xmlOutputFileName, "xmlOutputFileName must not null");
		this.htmlOutputFileName = //
				Preconditions.checkNotNull(builder.htmlOutputFileName, "htmlOutputFileName must not null");
		this.accessModifier = //
				Preconditions.checkNotNull(builder.accessModifier, "accessModifier must not null");

		this.packagesInclude = nullToEmpty(builder.packagesInclude);
		this.packagesExclude = nullToEmpty(builder.packagesExclude);

		this.showOnlySemverDiff = builder.showOnlySemverDiff;
	}

	private ImmutableList<PackageFilter> nullToEmpty(ImmutableList<PackageFilter> nullableList) {
		if (nullableList == null) {
			return ImmutableList.of();
		} else {
			return nullableList;
		}
	}

	public Options copyToOptions() {
		Options options = new Options();
		options.setAccessModifier(accessModifier);
		options.setNewArchive(newArchive);
		options.setOldArchive(oldArchive);
		options.setXmlOutputFile(xmlOutputFileName);
		options.setHtmlOutputFile(htmlOutputFileName);
		options.setPackagesInclude(packagesInclude);
		options.setPackagesExclude(packagesExclude);
		options.setOutputOnlyBinaryIncompatibleModifications(outputOnlyBinaryIncompatibleModifications);
		options.setShowOnlySemverDiff(showOnlySemverDiff);
		options.setOutputOnlyModifications(outputOnlyModifications);
		return options;
	}

	public File getOldArchive() {
		return oldArchive;
	}

	public File getNewArchive() {
		return newArchive;
	}

	public boolean isOutputOnlyModifications() {
		return outputOnlyModifications;
	}

	public boolean isOutputOnlyBinaryIncompatibleModifications() {
		return outputOnlyBinaryIncompatibleModifications;
	}

	public Optional<String> getXmlOutputFile() {
		return xmlOutputFileName;
	}

	public Optional<String> getHtmlOutputFile() {
		return htmlOutputFileName;
	}

	public AccessModifier getAccessModifier() {
		return accessModifier.get();
	}

	public ImmutableList<PackageFilter> getPackagesInclude() {
		return packagesInclude;
	}

	public ImmutableList<PackageFilter> getPackagesExclude() {
		return packagesExclude;
	}

	public boolean isOnlySemverDiff() {
		return showOnlySemverDiff;
	}

	public static ImmutableOptions toImmutable(Options options) {
		return builder() //
				.withAccessModifier( //
						Optional.fromNullable(options.getAccessModifier()).or(AccessModifier.PUBLIC)) //
				.withOldArchive(options.getOldArchive()) //
				.withNewArchive(options.getNewArchive()) //
				.withXmlOutputFileName(options.getXmlOutputFile()) //
				.withHtmlOutputFileName(options.getHtmlOutputFile()) //
				.withOnlyModifications(options.isOutputOnlyModifications()) //
				.withOnlySemverDiff(options.isOnlySemverDiff()) //
				.withOnlyBinaryIncompatible(options.isOutputOnlyBinaryIncompatibleModifications()) //
				.withPackagesInclude(ImmutableList.copyOf(options.getPackagesInclude())) //
				.withPackagesExclude(ImmutableList.copyOf(options.getPackagesExclude())) //
				.build();
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this) //
				.add("oldArchive", oldArchive) //
				.add("newArchive", newArchive) //
				.add("outputOnlyModifications", outputOnlyModifications) //
				.add("outputOnlyBinaryIncompatibleModifications",
						outputOnlyBinaryIncompatibleModifications) //
				.add("xmlOutputFileName", xmlOutputFileName) //
				.add("htmlOutputFileName", htmlOutputFileName) //
				.add("accessModifier", accessModifier) //
				.add("packagesInclude", packagesInclude) //
				.add("packagesExclude", packagesExclude) //
				.add("showOnlySemverDiff", showOnlySemverDiff) //
				.toString();
	}

	@Override
	public int hashCode() {
		return java.util.Objects.hash(oldArchive, newArchive, outputOnlyModifications,
				outputOnlyBinaryIncompatibleModifications, xmlOutputFileName, htmlOutputFileName,
				accessModifier, packagesInclude, packagesExclude, showOnlySemverDiff);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ImmutableOptions) {
			ImmutableOptions that = (ImmutableOptions) obj;
			return Objects.equal(that.oldArchive, this.oldArchive) &&
					Objects.equal(that.newArchive, this.newArchive) &&
					Objects.equal(that.outputOnlyModifications, this.outputOnlyModifications) &&
					Objects.equal(that.outputOnlyBinaryIncompatibleModifications,
							this.outputOnlyBinaryIncompatibleModifications) &&
					Objects.equal(that.xmlOutputFileName, this.xmlOutputFileName) &&
					Objects.equal(that.htmlOutputFileName, this.htmlOutputFileName) &&
					Objects.equal(that.accessModifier, this.accessModifier) &&
					Objects.equal(that.packagesInclude, this.packagesInclude) &&
					Objects.equal(that.packagesExclude, this.packagesExclude) &&
					Objects.equal(that.showOnlySemverDiff, this.showOnlySemverDiff);
		} else {
			return false;
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {

		public File oldArchive;
		public boolean outputOnlyModifications;
		public File newArchive;
		public boolean outputOnlyBinaryIncompatibleModifications;
		public Optional<String> xmlOutputFileName;
		public Optional<String> htmlOutputFileName;
		public Optional<AccessModifier> accessModifier;
		public ImmutableList<PackageFilter> packagesInclude;
		public ImmutableList<PackageFilter> packagesExclude;
		public boolean showOnlySemverDiff;

		public ImmutableOptions build() {
			return new ImmutableOptions(this);
		}

		public Builder withOldArchive(File oldArchive) {
			this.oldArchive = Preconditions.checkNotNull(oldArchive);
			return this;
		}

		public Builder withNewArchive(File newArchive) {
			this.newArchive = Preconditions.checkNotNull(newArchive);
			return this;
		}

		public Builder withAccessModifier(AccessModifier accessModifier) {
			this.accessModifier = Optional.of(accessModifier);
			return this;
		}

		public Builder withPackagesInclude(ImmutableList<PackageFilter> packagesInclude) {
			this.packagesInclude = packagesInclude;
			return this;
		}

		public Builder withPackagesInclude(String s) {
			return withPackagesInclude(Options.applyInclude(Optional.of(s), //
					ImmutableList.<PackageFilter>of()));
		}

		public Builder withPackagesExclude(String s) {
			return withPackagesExclude(Options.applyExclude(Optional.of(s), //
					ImmutableList.<PackageFilter>of()));
		}

		public Builder withPackagesExclude(ImmutableList<PackageFilter> packagesExclude) {
			this.packagesExclude = packagesExclude;
			return this;
		}

		public Builder withOnlyBinaryIncompatible(boolean outputOnlyBinaryIncompatibleModifications) {
			this.outputOnlyBinaryIncompatibleModifications = outputOnlyBinaryIncompatibleModifications;
			return this;
		}

		public Builder withOnlySemverDiff(boolean showOnlySemverDiff) {
			this.showOnlySemverDiff = showOnlySemverDiff;
			return this;
		}

		public Builder withOnlyModifications(boolean outputOnlyModifications) {
			this.outputOnlyModifications = outputOnlyModifications;
			return this;
		}

		public Builder withoutXmlOutputFileName() {
			return withXmlOutputFileName(Optional.<String>absent());
		}

		Builder withXmlOutputFileName(Optional<String> xmlOutputFile) {
			this.xmlOutputFileName = xmlOutputFile;
			return this;
		}

		public Builder withXmlOutputFileName(String xmlOutputFileName) {
			return withXmlOutputFileName(Optional.fromNullable(xmlOutputFileName));
		}

		public Builder withHtmlOutputFileName(String htmlOutputFileName) {
			return withHtmlOutputFileName(Optional.of(htmlOutputFileName));
		}

		public Builder withHtmlOutputFileName(Optional<String> htmlOutputFileName) {
			this.htmlOutputFileName = htmlOutputFileName;
			return this;
		}

		public Builder withoutHtmlOutputFileName() {
			return withHtmlOutputFileName(Optional.<String>absent());
		}

	}
}
