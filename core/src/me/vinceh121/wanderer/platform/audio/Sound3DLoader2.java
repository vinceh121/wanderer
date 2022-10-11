package me.vinceh121.wanderer.platform.audio;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.WandererConstants;

public class Sound3DLoader2 extends AsynchronousAssetLoader<Sound3D, Sound3DLoader2.Sound3DParameter> {
	private Sound3D sound;

	public Sound3DLoader2(FileHandleResolver resolver) {
		super(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file, Sound3DParameter parameter) {
		System.out.println(fileName);
		this.sound = WandererConstants.AUDIO.newSound3D(file);
	}

	@Override
	public Sound3D loadSync(AssetManager manager, String fileName, FileHandle file, Sound3DParameter parameter) {
		// XXX: what?
		Sound3D sound = this.sound;
		this.sound = null;
		return sound;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file, Sound3DParameter parameter) {
		return null;
	}

	public Sound3D getSound() {
		return sound;
	}

	public static class Sound3DParameter extends AssetLoaderParameters<Sound3D> {
	}
}
