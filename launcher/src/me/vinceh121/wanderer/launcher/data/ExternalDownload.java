package me.vinceh121.wanderer.launcher.data;

public class ExternalDownload {
	private String url;
	/**
	 * Output path, relative to the assets folder
	 */
	private String outPath;

	public String getUrl() {
		return this.url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	public String getOutPath() {
		return this.outPath;
	}

	public void setOutPath(final String outPath) {
		this.outPath = outPath;
	}

	@Override
	public String toString() {
		return "ExternalDownload [url=" + this.url + ", outPath=" + this.outPath + "]";
	}
}
