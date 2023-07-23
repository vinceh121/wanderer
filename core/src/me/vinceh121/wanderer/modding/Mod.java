package me.vinceh121.wanderer.modding;

import com.badlogic.gdx.files.FileHandle;

public class Mod {
	private ModManifest manifest;
	private FileHandle folder;

	public ModManifest getManifest() {
		return this.manifest;
	}

	public void setManifest(final ModManifest manifest) {
		this.manifest = manifest;
	}

	public FileHandle getFolder() {
		return this.folder;
	}

	public void setFolder(final FileHandle folder) {
		this.folder = folder;
	}
}
