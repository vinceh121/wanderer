package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;

public class SkyShader extends WandererShader {
	public final int u_time;
	private float timeOfDay;

	public SkyShader(Renderable renderable, Config config) {
		super(renderable, config);

		this.u_time = register("time");
	}

	@Override
	public void render(Renderable renderable, Attributes combinedAttributes) {
		this.set(u_time, this.timeOfDay);
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
}
