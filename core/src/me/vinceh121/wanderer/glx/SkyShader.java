package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;

public class SkyShader extends WandererShader {
	public final int u_sunDir, u_skyTop, u_skyMiddle, u_skyBottom;
	private final Vector3 sunDir = new Vector3(0, 1, 0);
	private final Color skyTop = new Color();
	private final Color skyMiddle = new Color();
	private final Color skyBottom = new Color();

	public SkyShader(final Renderable renderable, final Config config) {
		super(renderable, config);

		this.u_sunDir = this.register("sunDir");
		this.u_skyTop = this.register("skyTop");
		this.u_skyMiddle = this.register("skyMiddle");
		this.u_skyBottom = this.register("skyBottom");
	}

	@Override
	public void render(final Renderable renderable, final Attributes combinedAttributes) {
		this.set(this.u_sunDir, this.sunDir);
		this.set(this.u_skyTop, this.skyTop);
		this.set(this.u_skyMiddle, this.skyMiddle);
		this.set(this.u_skyBottom, this.skyBottom);
		
		super.render(renderable, combinedAttributes);
	}

	@Override
	public int compareTo(final Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(final Renderable instance) {
		return true;
	}

	public Vector3 getSunDir() {
		return this.sunDir;
	}

	public void setSunDir(final Vector3 sunDir) {
		this.sunDir.set(sunDir);
	}

	public Color getSkyTop() {
		return skyTop;
	}

	public void setSkyTop(Color skyTop) {
		this.skyTop.set(skyTop);
	}

	public Color getSkyMiddle() {
		return skyMiddle;
	}

	public void setSkyMiddle(Color skyMiddle) {
		this.skyMiddle.set(skyMiddle);
	}

	public Color getSkyBottom() {
		return skyBottom;
	}

	public void setSkyBottom(Color skyBottm) {
		this.skyBottom.set(skyBottom);
	}
}
