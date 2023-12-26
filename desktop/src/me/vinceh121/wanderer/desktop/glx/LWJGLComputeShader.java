package me.vinceh121.wanderer.desktop.glx;

import org.lwjgl.opengl.GL43;

import me.vinceh121.wanderer.platform.glx.IComputeShader;

public class LWJGLComputeShader implements IComputeShader {
	private final int shaderHandle, programHandle;

	public LWJGLComputeShader(int shaderHandle, int programHandle) {
		this.shaderHandle = shaderHandle;
		this.programHandle = programHandle;
	}

	@Override
	public void bind() {
		GL43.glUseProgram(this.programHandle);
	}

	@Override
	public void dispatch(int x, int y, int z) {
		GL43.glDispatchCompute(x, y, z);
	}

	@Override
	public int getUniformLocation(String name) {
		return GL43.glGetUniformLocation(this.programHandle, name);
	}

	@Override
	public void setUniform1i(int location, int value) {
		GL43.glUniform1i(location, value);
	}

	@Override
	public void setUniform2f(int location, float v0, float v1) {
		GL43.glUniform2f(location, v0, v1);
	}

	@Override
	public int getShaderHandle() {
		return shaderHandle;
	}

	@Override
	public int getProgramHandle() {
		return programHandle;
	}

	@Override
	public void dispose() {
		GL43.glDeleteProgram(this.programHandle);
		GL43.glDeleteShader(this.shaderHandle);
	}
}
