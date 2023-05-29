package me.vinceh121.wanderer.launcher.data;

public class ExternalDownload {
	private String url;
	/**
	 * Output path, relative to the assets folder
	 */
	private String outPath;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getOutPath() {
		return outPath;
	}

	public void setOutPath(String outPath) {
		this.outPath = outPath;
	}

	@Override
	public String toString() {
		return "ExternalDownload [url=" + url + ", outPath=" + outPath + "]";
	}
}
