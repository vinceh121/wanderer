package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;

public class SkyShader extends WandererShader {
	public final int u_time, u_sunDir;
	private float timeOfDay;
	private final Vector3 sunDir = new Vector3(0, 1, 0);

	public SkyShader(Renderable renderable, Config config) {
		super(renderable, config);

		this.u_time = register("time");
		this.u_sunDir = register("sunDir");
	}

	@Override
	public void render(Renderable renderable, Attributes combinedAttributes) {
		this.set(u_time, this.timeOfDay);
		this.set(u_sunDir, this.sunDir);
		super.render(renderable, combinedAttributes);
	}

	@Override
	public int compareTo(Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		return true;
	}

	public float getTimeOfDay() {
		return timeOfDay;
	}

	public void setTimeOfDay(float timeOfDay) {
		this.timeOfDay = timeOfDay;
	}

	public Vector3 getSunDir() {
		return sunDir;
	}

	public void setSunDir(Vector3 sunDir) {
		this.sunDir.set(sunDir);
	}
}
