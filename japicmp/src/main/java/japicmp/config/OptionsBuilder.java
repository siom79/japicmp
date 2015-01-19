package japicmp.config;

import java.io.File;
import java.io.IOException;
import java.util.jar.JarFile;

import com.google.common.base.Optional;
import japicmp.exception.FormattedException;
import japicmp.exception.JApiCmpException;
import japicmp.model.AccessModifier;

public class OptionsBuilder {

	private String pathToNewVersionJar;
	private String pathToOldVersionJar;
	private String pathToXmlOutputFile;
	private String pathToHtmlOutputFile;
	private boolean modifiedOnly;
	private Optional<AccessModifier> accessModifier;
	private String packagesToInclude;
	private String packagesToExclude;
	private boolean onlyBinaryIncompatibleModifications;
	private boolean showOnlySemverDiff;

	public static Options create(OptionsBuilder builder) {
		Options options = new Options();
		try {
			options.setNewArchive(validFile(builder.pathToNewVersionJar, "no valid new archive found"));
			options.setOldArchive(validFile(builder.pathToOldVersionJar, "no valid old archive found"));
			options.setXmlOutputFile(Optional.fromNullable(builder.pathToXmlOutputFile));
			options.setHtmlOutputFile(Optional.fromNullable(builder.pathToHtmlOutputFile));
			options.setOutputOnlyModifications(builder.modifiedOnly);
			options.setAccessModifier(builder.accessModifier);
			options.addPackageIncludeFromArgument(Optional.fromNullable(builder.packagesToInclude));
			options.addPackagesExcludeFromArgument(Optional.fromNullable(builder.packagesToExclude));
			options.setOutputOnlyBinaryIncompatibleModifications(builder.onlyBinaryIncompatibleModifications);
			options.setShowOnlySemverDiff(builder.showOnlySemverDiff);
		} catch (IllegalArgumentException e) {
			throw new JApiCmpException(JApiCmpException.Reason.IllegalArgument, e.getMessage());
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		return options;
	}


	private static File validFile(String archive, String errorMessage) {
		File file = new File(checkNonNull(archive, errorMessage));
		verifyExisting(file);
		verifyCanRead(file);
		verifyJarArchive(file);
		return file;
	}

	private static void verifyCanRead(File file) {
		if (!file.canRead()) {
			throw JApiCmpException.of("Cannot read file '%s'.", file.getAbsolutePath());
		}
	}

	private static <T> T checkNonNull(T in, String errorMessage) {
		if (in == null) {
			throw new IllegalArgumentException(errorMessage);
		} else {
			return in;
		}
	}

	private static void verifyExisting(File newArchive) {
		if (!newArchive.exists()) {
			throw JApiCmpException.of("File '%s' does not exist.", newArchive.getAbsolutePath());
		}
	}

	private static void verifyJarArchive(File file) {
		try {
			new JarFile(file);
		} catch (IOException e) {
			throw JApiCmpException.of("File '%s' could not be opened as a jar file: %s", file.getAbsolutePath(), e.getMessage());
		}
	}

	private static Optional<AccessModifier> toModifier(String accessModifierArg) {
		Optional<String> stringOptional = Optional.fromNullable(accessModifierArg);
		if (stringOptional.isPresent()) {
			try {
				return Optional.of(AccessModifier.valueOf(stringOptional.get().toUpperCase()));
			} catch (IllegalArgumentException e) {
				throw FormattedException //
						.ofIAE("Invalid value for option -a: %s. Possible values are: %s.", //
								accessModifierArg, AccessModifier.listOfAccessModifier());
			}
		} else {
			return Optional.of(AccessModifier.PUBLIC);
		}
	}

	public static OptionsBuilder create() {
		return new OptionsBuilder();
	}

	public Options build() {
		return create(this);
	}

	public OptionsBuilder newJar(String pathToNewVersionJar) {
		this.pathToNewVersionJar = pathToNewVersionJar;
		return this;
	}

	public OptionsBuilder oldJar(String pathToOldVersionJar) {
		this.pathToOldVersionJar = pathToOldVersionJar;
		return this;
	}

	public OptionsBuilder xmlOutputFilename(String pathToXmlOutputFile) {
		this.pathToXmlOutputFile = pathToXmlOutputFile;
		return this;
	}

	public OptionsBuilder htmlOutputFilename(String pathToHtmlOutputFile) {
		this.pathToHtmlOutputFile = pathToHtmlOutputFile;
		return this;
	}

	public OptionsBuilder showModifiedOnly(boolean modifiedOnly) {
		this.modifiedOnly = modifiedOnly;
		return this;
	}

	public OptionsBuilder useOnlyVisibilty(String accessModifier) {
		this.accessModifier = toModifier(accessModifier);
		return this;
	}

	public OptionsBuilder includePackages(String packagesToInclude) {
		this.packagesToInclude = packagesToInclude;
		return this;
	}

	public OptionsBuilder excludePackages(String packagesToExclude) {
		this.packagesToExclude = packagesToExclude;
		return this;
	}

	public OptionsBuilder showOnlyBinaryIncompatibleModifications(boolean onlyBinaryIncompatible) {
		this.onlyBinaryIncompatibleModifications = onlyBinaryIncompatible;
		return this;
	}

	public OptionsBuilder showOnlySeverDiff(boolean showOnlySemverDiff) {
		this.showOnlySemverDiff = showOnlySemverDiff;
		return this;
	}

	public ImmutableOptions buildImmutable() {
		return ImmutableOptions.toImmutable(build());
	}
}
