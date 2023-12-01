package me.vinceh121.wanderer.modding;

public class ModManifest {
	private String name, version, license, homepage;

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(final String version) {
		this.version = version;
	}

	public String getLicense() {
		return this.license;
	}

	public void setLicense(final String license) {
		this.license = license;
	}

	public String getHomepage() {
		return this.homepage;
	}

	public void setHomepage(final String homepage) {
		this.homepage = homepage;
	}

	@Override
	public String toString() {
		return "ModManifest [name=" + this.name + ", version=" + this.version + ", license=" + this.license
				+ ", homepage=" + this.homepage + "]";
	}
}
