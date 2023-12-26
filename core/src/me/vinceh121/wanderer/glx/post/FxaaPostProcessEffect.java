package me.vinceh121.wanderer.glx.post;

import com.badlogic.gdx.graphics.Texture;

import me.vinceh121.wanderer.GraphicsManager;
import me.vinceh121.wanderer.platform.glx.IFXAAShader;

public class FxaaPostProcessEffect implements IPostProcessEffect {
	private final IFXAAShader shader;

	public FxaaPostProcessEffect(IFXAAShader shader) {
		this.shader = shader;
	}

	@Override
	public Texture process(GraphicsManager glx, Texture tex) {
		return this.shader.run(glx.getModelBatch().getRenderContext(), tex);
	}
}
