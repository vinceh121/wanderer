package me.vinceh121.wanderer.desktop.glx;

import org.lwjgl.opengl.GL43;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.utils.RenderContext;

import me.vinceh121.wanderer.platform.glx.IFXAAShader;

public class LWJGLFXAAShader extends LWJGLComputeShader implements IFXAAShader {
	private final int inputImageUniform, outputImageUniform, invResolutionUniform;

	public LWJGLFXAAShader(final int shaderHandle, final int programHandle) {
		super(shaderHandle, programHandle);

		this.inputImageUniform = this.getUniformLocation("inputImage");
		this.outputImageUniform = this.getUniformLocation("imgOutput");
		this.invResolutionUniform = this.getUniformLocation("invResolution");
	}

	@Override
	public Texture run(final RenderContext ctx, final Texture tex) {
		this.bind();

		final int width = Gdx.graphics.getWidth();
		final int height = Gdx.graphics.getHeight();

		final int texUnit = ctx.textureBinder.bind(tex);
		final Texture texOut = new Texture(width, height, Format.RGB888);
		final int texUnitOut = ctx.textureBinder.bind(texOut);

//		final int texUnit = 0;
//		tex.bind(0);

		this.setInputImage(ctx, texUnit);
		this.setOutputImage(ctx, texUnitOut);
		this.setInvResolution(width, height);

		this.dispatch(width / 64, height / 64, 1); // XXX divisions here should round up

		GL43.glMemoryBarrier(GL43.GL_SHADER_IMAGE_ACCESS_BARRIER_BIT);

		return texOut;
	}

	@Override
	public void setInputImage(final RenderContext ctx, final int texUnit) {
		this.setUniform1i(this.inputImageUniform, texUnit);
	}

	@Override
	public void setOutputImage(final RenderContext ctx, final int texUnit) {
		this.setUniform1i(this.outputImageUniform, texUnit);
	}

	@Override
	public void setInvResolution(final float width, final float height) {
		this.setUniform2f(this.invResolutionUniform, width, height);
	}
}
