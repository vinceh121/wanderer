package me.vinceh121.wanderer.ui;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.ui.WSkinLoader.WSkinParameters;

public class WSkinLoader extends AsynchronousAssetLoader<WSkin, WSkinParameters> {

	public WSkinLoader(final FileHandleResolver resolver) {
		super(resolver);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Array<AssetDescriptor> getDependencies(final String fileName, final FileHandle file, final WSkinParameters parameter) {
		final Array<AssetDescriptor> deps = new Array();

		deps.add(new AssetDescriptor(file.pathWithoutExtension() + ".atlas", TextureAtlas.class));

		return deps;
	}

	@Override
	public void loadAsync(final AssetManager manager, final String fileName, final FileHandle file, final WSkinParameters parameter) {
	}

	@Override
	public WSkin loadSync(final AssetManager manager, final String fileName, final FileHandle file, final WSkinParameters parameter) {
		final String textureAtlasPath = file.pathWithoutExtension() + ".atlas";

		final TextureAtlas atlas = manager.get(textureAtlasPath, TextureAtlas.class);
		final WSkin skin = this.newSkin(atlas);

		skin.load(file);
		return skin;
	}

	/**
	 * Override to allow subclasses of Skin to be loaded or the skin instance to be
	 * configured.
	 *
	 * @param atlas The TextureAtlas that the skin will use.
	 * @return A new Skin (or subclass of Skin) instance based on the provided
	 *         TextureAtlas.
	 */
	protected WSkin newSkin(final TextureAtlas atlas) {
		return new WSkin(atlas);
	}

	public static class WSkinParameters extends AssetLoaderParameters<WSkin> {
	}
}
