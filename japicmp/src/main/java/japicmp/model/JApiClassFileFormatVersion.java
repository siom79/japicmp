package japicmp.model;

import javax.xml.bind.annotation.XmlAttribute;
import java.util.ArrayList;
import java.util.List;

public class JApiClassFileFormatVersion implements JApiHasChangeStatus, JApiCompatibility {

	private final int majorVersionOld;
	private final int minorVersionOld;
	private final int majorVersionNew;
	private final int minorVersionNew;
	private final JApiChangeStatus changeStatus;
	private final List<JApiCompatibilityChange> compatibilityChanges = new ArrayList<>();

	public JApiClassFileFormatVersion(int majorVersionOld, int minorVersionOld, int majorVersionNew, int minorVersionNew) {
		this.majorVersionOld = majorVersionOld;
		this.minorVersionOld = minorVersionOld;
		this.majorVersionNew = majorVersionNew;
		this.minorVersionNew = minorVersionNew;
		this.changeStatus = computeChangeStatus();
	}

	@XmlAttribute(name = "changeStatus")
	public JApiChangeStatus getChangeStatus() {
		return this.changeStatus;
	}

	private JApiChangeStatus computeChangeStatus() {
		JApiChangeStatus changeStatus = JApiChangeStatus.UNCHANGED;
		if (this.majorVersionOld != -1 && this.majorVersionNew != -1) {
			if (this.majorVersionOld != this.majorVersionNew) {
				changeStatus = JApiChangeStatus.MODIFIED;
			} else {
				if (this.minorVersionOld != this.minorVersionNew) {
					changeStatus = JApiChangeStatus.MODIFIED;
				}
			}
		} else if (this.majorVersionOld == -1 && this.majorVersionNew != -1) {
			changeStatus = JApiChangeStatus.NEW;
		} else if (this.majorVersionOld != -1 && this.majorVersionNew == -1) {
			changeStatus = JApiChangeStatus.REMOVED;
		}
		return changeStatus;
	}

	@XmlAttribute(name = "majorVersionOld")
	public int getMajorVersionOld() {
		return majorVersionOld;
	}

	@XmlAttribute(name = "minorVersionOld")
	public int getMinorVersionOld() {
		return minorVersionOld;
	}

	@XmlAttribute(name = "majorVersionNew")
	public int getMajorVersionNew() {
		return majorVersionNew;
	}

	@XmlAttribute(name = "minorVersionNew")
	public int getMinorVersionNew() {
		return minorVersionNew;
	}

	@Override
	public boolean isBinaryCompatible() {
		boolean compatible = true;
		if (this.majorVersionOld != -1 && this.majorVersionNew != -1) {
			if (this.majorVersionOld != this.majorVersionNew) {
				compatible = false;
			}
		}
		return compatible;
	}

	@Override
	public boolean isSourceCompatible() {
		return true;
	}

	@Override
	public List<JApiCompatibilityChange> getCompatibilityChanges() {
		return this.compatibilityChanges;
	}

	public String toString()
	{
		return "JApiClassFileFormatVersion [majorVersionOld="
			+ majorVersionOld
			+ ", minorVersionOld="
			+ minorVersionOld
			+ ", majorVersionNew="
			+ majorVersionNew
			+ ", minorVersionNew="
			+ minorVersionNew
			+ ", changeStatus="
			+ changeStatus
			+ ", compatibilityChanges="
			+ compatibilityChanges
			+ "]";
	}


}
