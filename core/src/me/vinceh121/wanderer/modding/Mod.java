package me.vinceh121.wanderer.modding;

import com.badlogic.gdx.files.FileHandle;

public class Mod {
	private ModManifest manifest;
	private FileHandle folder;

	public ModManifest getManifest() {
		return manifest;
	}

	public void setManifest(ModManifest manifest) {
		this.manifest = manifest;
	}

	public FileHandle getFolder() {
		return folder;
	}

	public void setFolder(FileHandle folder) {
		this.folder = folder;
	}
}
