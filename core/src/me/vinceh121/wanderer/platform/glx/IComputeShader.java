package me.vinceh121.wanderer.platform.glx;

import com.badlogic.gdx.utils.Disposable;

public interface IComputeShader extends Disposable {
	void bind();

	void dispatch(int x, int y, int z);

	int getUniformLocation(String name);

	void setUniform1i(int location, int value);

	void setUniform2f(int location, float v0, float v1);

	int getProgramHandle();

	int getShaderHandle();

}
