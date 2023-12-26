package me.vinceh121.wanderer.platform.glx;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

public interface IFXAAShader extends IComputeShader {

	void setInputImage(RenderContext ctx, int texUnit);

	void setOutputImage(RenderContext ctx, int texUnit);

	void setInvResolution(float width, float height);

	Texture run(RenderContext ctx, Texture tex);

}
