package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;

public interface IShaderBuilder {
	Shader build(Renderable r);
}
