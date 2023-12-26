package me.vinceh121.wanderer.desktop.glx;

import org.lwjgl.opengl.GL43;

import com.badlogic.gdx.files.FileHandle;

import me.vinceh121.wanderer.platform.glx.IComputeShader;
import me.vinceh121.wanderer.platform.glx.IComputeShaderProvider;
import me.vinceh121.wanderer.platform.glx.IFXAAShader;

public class LWJGLComputeShaderProvider implements IComputeShaderProvider {
	@Override
	public IComputeShader build(FileHandle source) {
		return this.build(source.readString());
	}

	@Override
	public IComputeShader build(String source) {
		final int program = GL43.glCreateProgram();
		final int shader = GL43.glCreateShader(GL43.GL_COMPUTE_SHADER);

		GL43.glShaderSource(shader, source);
		GL43.glCompileShader(shader);

		// XXX compile error handling here

		GL43.glAttachShader(program, shader);
		GL43.glLinkProgram(program);

		// XXX link error handling here

		return new LWJGLComputeShader(shader, program);
	}

	@Override
	public IFXAAShader buildFxaa(FileHandle source) {
		final IComputeShader shader = this.build(source);
		return new LWJGLFXAAShader(shader.getShaderHandle(), shader.getProgramHandle());
	}
}
