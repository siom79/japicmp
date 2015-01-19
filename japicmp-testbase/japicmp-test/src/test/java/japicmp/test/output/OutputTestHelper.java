package japicmp.test.output;

import static japicmp.test.util.Helper.getArchive;

import java.io.File;

import com.google.common.collect.ImmutableList;
import japicmp.cmp.JarArchiveComparator;
import japicmp.cmp.JarArchiveComparatorOptions;
import japicmp.config.ImmutableOptions;
import japicmp.config.PackageFilter;
import japicmp.model.AccessModifier;
import japicmp.model.JApiClass;

public class OutputTestHelper {
	public static Config newTestConfig() {
		JarArchiveComparator jarArchiveComparator =
				new JarArchiveComparator(new JarArchiveComparatorOptions());
		return configByComparator(jarArchiveComparator);
	}

	private static Config configByComparator(JarArchiveComparator jarArchiveComparator) {
		File oldArchive = getArchive("japicmp-test-v1.jar");
		File newArchive = getArchive("japicmp-test-v2.jar");

		ImmutableList<JApiClass> jApiClasses =
				ImmutableList.copyOf(jarArchiveComparator.compare(oldArchive, newArchive));
		ImmutableOptions.Builder builder = ImmutableOptions.builder() //
				.withOldArchive(oldArchive) //
				.withNewArchive(newArchive) //
				.withoutXmlOutputFileName() //
				.withoutHtmlOutputFileName() //
				.withAccessModifier(AccessModifier.PUBLIC) //
				;
		return new Config(jApiClasses, builder);
	}

	public static Config newTestConfigWithInclude(String packageName) {
		JarArchiveComparatorOptions options = new JarArchiveComparatorOptions();
		options.getPackagesInclude().add(new PackageFilter(packageName));
		JarArchiveComparator jarArchiveComparator = new JarArchiveComparator(options);
		return configByComparator(jarArchiveComparator);
	}

	public static class Config {

		private final ImmutableList<JApiClass> apiClasses;
		private final ImmutableOptions.Builder builder;

		public Config(ImmutableList<JApiClass> apiClasses, ImmutableOptions.Builder builder) {
			this.apiClasses = apiClasses;
			this.builder = builder;
		}

		public ImmutableList<JApiClass> classes() {
			return apiClasses;
		}

		public ImmutableOptions.Builder options() {
			return builder;
		}
	}
}
