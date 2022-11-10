package me.vinceh121.wanderer.platform.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class Sound3DLoader extends AsynchronousAssetLoader<Sound3D, Sound3DLoader.Sound3DParameter> {
	private Sound3D sound;

	public Sound3DLoader(final FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final Sound3DParameter parameter) {
		// Do not use WandererConstants as it is locked
		this.sound = ((AudioSystem3D) Gdx.audio).newSound3D(file);
	}

	@Override
	public Sound3D loadSync(final AssetManager manager, final String fileName, final FileHandle file, final Sound3DParameter parameter) {
		final Sound3D sound = this.sound;
		this.sound = null;
		return sound;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final Sound3DParameter parameter) {
		return null;
	}

	public Sound3D getSound() {
		return this.sound;
	}

	public static class Sound3DParameter extends AssetLoaderParameters<Sound3D> {
	}
}
