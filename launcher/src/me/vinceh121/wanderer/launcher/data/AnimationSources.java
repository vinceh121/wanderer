package me.vinceh121.wanderer.launcher.data;

public class AnimationSources {
	/**
	 * glTF output path relative to assets directory
	 */
	private String outputPath, animationFile, meshFile, scriptFile;

	public String getOutputPath() {
		return this.outputPath;
	}

	public void setOutputPath(final String outputPath) {
		this.outputPath = outputPath;
	}

	public String getAnimationFile() {
		return this.animationFile;
	}

	public void setAnimationFile(final String animationFile) {
		this.animationFile = animationFile;
	}

	public String getMeshFile() {
		return this.meshFile;
	}

	public void setMeshFile(final String meshFile) {
		this.meshFile = meshFile;
	}

	public String getScriptFile() {
		return this.scriptFile;
	}

	public void setScriptFile(final String scriptFile) {
		this.scriptFile = scriptFile;
	}

	@Override
	public String toString() {
		return "AnimationSources [outputPath=" + this.outputPath + ", animationFile=" + this.animationFile + ", meshFile="
				+ this.meshFile + ", scriptFile=" + this.scriptFile + "]";
	}
}
