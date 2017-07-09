package japicmp.maven;

import java.util.List;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.Restriction;
import org.apache.maven.artifact.versioning.VersionRange;

public class SemanticVersionRange {
	
	/**
		Return a semantic version range from an existing maven VersionRange, by converting .
		
		This is a bit of a dirty hack, necessary because :
		- VersionRange hardcodes construction of DefaultArtifactVersion (not making it possible to use other implementations of ArtifactVersion)
		- All the internals of VersionRange are private, so it cannot be extended easily.
		
		This is not reliable either : since it requires an existing maven range to have been created, and VersionRange.parseRestriction checks ordering of restriction bounds (using DefaultArtifactVersion order), some ranges could not be generated when the ordering differs.
		
		However, in the current case, it is only meant to be used on the versionRangeWithProjectVersion setting, which has only one bound : (,${project.version}).
		Therefore this implementation is enough, and avoids rewriting all of VersionRange's parsing logic.
	*/
	public static VersionRange getFromExistingVersionRange(VersionRange existingRange) {
		if(existingRange.getRecommendedVersion() != null && !(existingRange.getRecommendedVersion() instanceof SemanticArtifactVersion)) {
			throw new UnsupportedOperationException("Cannot convert an already decided version range to semantic in current implementation.");
		}
		VersionRange newRange = existingRange.cloneOf();
		List<Restriction> restrictions = newRange.getRestrictions();
		for(int i = 0; i < restrictions.size(); i++) {
			Restriction currentRestriction = restrictions.get(i);
			
			restrictions.set(i, new Restriction(
				new SemanticArtifactVersion(currentRestriction.getLowerBound()),
				currentRestriction.isLowerBoundInclusive(),
				new SemanticArtifactVersion(currentRestriction.getUpperBound()),
				currentRestriction.isUpperBoundInclusive()
			));
		}
		
		return newRange;
	}

}