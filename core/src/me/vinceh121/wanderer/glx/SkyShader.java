package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;

public class SkyShader extends WandererShader {
	public final int u_time, u_sunDir;
	private float timeOfDay;
	private final Vector3 sunDir = new Vector3(0, 1, 0);

	public SkyShader(final Renderable renderable, final Config config) {
		super(renderable, config);

		this.u_time = this.register("time");
		this.u_sunDir = this.register("sunDir");
	}

	@Override
	public void render(final Renderable renderable, final Attributes combinedAttributes) {
		this.set(this.u_time, this.timeOfDay);
		this.set(this.u_sunDir, this.sunDir);
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

	public float getTimeOfDay() {
		return this.timeOfDay;
	}

	public void setTimeOfDay(final float timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public Vector3 getSunDir() {
		return this.sunDir;
	}

	public void setSunDir(final Vector3 sunDir) {
		this.sunDir.set(sunDir);
	}
}
