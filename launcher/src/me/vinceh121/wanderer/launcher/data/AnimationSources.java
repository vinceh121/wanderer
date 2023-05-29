package me.vinceh121.wanderer.launcher.data;

public class AnimationSources {
	/**
	 * glTF output path relative to assets directory
	 */
	private String outputPath, animationFile, meshFile, scriptFile;

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public String getAnimationFile() {
		return animationFile;
	}

	public void setAnimationFile(String animationFile) {
		this.animationFile = animationFile;
	}

	public String getMeshFile() {
		return meshFile;
	}

	public void setMeshFile(String meshFile) {
		this.meshFile = meshFile;
	}

	public String getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(String scriptFile) {
		this.scriptFile = scriptFile;
	}

	@Override
	public String toString() {
		return "AnimationSources [outputPath=" + outputPath + ", animationFile=" + animationFile + ", meshFile="
				+ meshFile + ", scriptFile=" + scriptFile + "]";
	}
}
