package me.vinceh121.wanderer.platform.glx;

import com.badlogic.gdx.Application.ApplicationType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public interface IComputeShaderProvider {
	public static IComputeShaderProvider get() {
		try {
			final Class<?> cls;

			if (Gdx.app.getType() == ApplicationType.Desktop) {
				cls = Class.forName("me.vinceh121.wanderer.desktop.glx.LWJGLComputeShaderProvider");
			} else {
				throw new UnsupportedOperationException(
						"Platform not supported for compute shader provider: " + Gdx.app.getType());
			}

			return (IComputeShaderProvider) cls.getConstructor().newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	IComputeShader build(FileHandle source);

	IComputeShader build(String source);

	IFXAAShader buildFxaa(FileHandle source);
}
