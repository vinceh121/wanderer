package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetLoaderParameters;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.utils.Array;

import net.mgsx.gltf.loaders.gltf.GLTFAssetLoader;
import net.mgsx.gltf.scene3d.scene.SceneAsset;

/**
 * Loads a GLTF and returns only the first node of the indicated scene.
 */
public class GLTFModelLoader extends AsynchronousAssetLoader<Model, AssetLoaderParameters<Model>> {
	private final GLTFAssetLoader delegate;
	
	public GLTFModelLoader() {
		this(new InternalFileHandleResolver());
	}

	public GLTFModelLoader(FileHandleResolver resolver) {
		super(resolver);
		this.delegate = new GLTFAssetLoader(resolver);
	}

	@Override
	public void loadAsync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<Model> parameter) {
		this.delegate.loadAsync(manager, fileName, file, null);
	}

	@Override
	public Model loadSync(AssetManager manager, String fileName, FileHandle file,
			AssetLoaderParameters<Model> parameter) {
		final SceneAsset scn = this.delegate.loadSync(manager, fileName, file, null);
		return scn.scene.model;
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Array<AssetDescriptor> getDependencies(String fileName, FileHandle file,
			AssetLoaderParameters<Model> parameter) {
		return this.delegate.getDependencies(fileName, file, null);
	}
}