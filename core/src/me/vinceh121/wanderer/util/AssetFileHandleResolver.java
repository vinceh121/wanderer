package me.vinceh121.wanderer.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;

public class AssetFileHandleResolver implements FileHandleResolver {
	private static final String ASSETS_FOLDER = "assets/";

	@Override
	public FileHandle resolve(final String fileName) {
		FileHandle fh = Gdx.files.local(AssetFileHandleResolver.ASSETS_FOLDER + fileName);
		if (fh.exists()) {
			return fh;
		}
		fh = Gdx.files.classpath(fileName);
		return fh;
	}
}
