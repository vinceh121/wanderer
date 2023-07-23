package me.vinceh121.wanderer.modding;

public class ModManifest {
	private String name, version, license, homepage;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}

	public String getHomepage() {
		return homepage;
	}

	public void setHomepage(String homepage) {
		this.homepage = homepage;
	}

	@Override
	public String toString() {
		return "ModManifest [name=" + name + ", version=" + version + ", license=" + license + ", homepage=" + homepage
				+ "]";
	}
}
