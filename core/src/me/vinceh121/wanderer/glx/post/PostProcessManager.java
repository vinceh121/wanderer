package me.vinceh121.wanderer.glx.post;

import java.util.LinkedList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;

import me.vinceh121.wanderer.GraphicsManager;
import me.vinceh121.wanderer.platform.glx.IComputeShaderProvider;

public class PostProcessManager {
	private final GraphicsManager glx;
	private final LinkedList<IPostProcessEffect> effects = new LinkedList<>();
	private FrameBuffer fbo;

	public PostProcessManager(GraphicsManager glx) {
		this.glx = glx;

		this.effects.add(new FxaaPostProcessEffect(
				IComputeShaderProvider.get().buildFxaa(Gdx.files.internal("shaders/fxaa.glsl"))));
	}

	public void addEffectFirst(IPostProcessEffect effect) {
		this.effects.addFirst(effect);
	}

	public void addEffectLast(IPostProcessEffect effect) {
		this.effects.addLast(effect);
	}

	public void begin() {
		assert this.fbo == null;

		this.fbo = new FrameBuffer(Format.RGB888, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		this.fbo.begin();
	}

	public Texture end() {
		this.fbo.end();
		Texture tex = this.fbo.getColorBufferTexture();

		for (final IPostProcessEffect fx : this.effects) {
			final Texture newTex = fx.process(this.glx, tex);

			if (!fx.isInPlace()) {
				tex.dispose();
			}

			tex = newTex;
		}

		Gdx.gl30.glBindFramebuffer(GL30.GL_READ_FRAMEBUFFER, this.fbo.getFramebufferHandle());
		Gdx.gl30.glBindFramebuffer(GL30.GL_DRAW_FRAMEBUFFER, 0);
		Gdx.gl30.glBlitFramebuffer(0,
				0,
				tex.getWidth(),
				tex.getHeight(),
				0,
				0,
				tex.getWidth(),
				tex.getHeight(),
				GL30.GL_COLOR_BUFFER_BIT,
				GL30.GL_NEAREST);

		this.fbo.dispose();
		this.fbo = null;

		return null;
	}

	public LinkedList<IPostProcessEffect> getEffects() {
		return effects;
	}
}
