package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Attributes;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.math.Vector3;

public class HosekWilkieShader extends WandererShader {
	public final int u_camDir, u_sunAzimuth, u_sunPolar, u_radiance, u_configX, u_configY, u_configZ;

	private Vector3 camDir = new Vector3();
	private Color radiance = Color.RED;
	private float sunAzimuth, sunPolar;
	private float[] configX = new float[9], configY = new float[9], configZ = new float[9];

	public HosekWilkieShader(Renderable renderable, Config config) {
		super(renderable, config);

		this.u_camDir = register("camDir");
		this.u_sunAzimuth = register("sunAzimuth");
		this.u_sunPolar = register("sunPolar");
		this.u_radiance = register("radiance");
		this.u_configX = register("configX");
		this.u_configY = register("configY");
		this.u_configZ = register("configZ");
	}

	@Override
	public void render(Renderable renderable, Attributes combinedAttributes) {
		this.set(u_camDir, this.camDir);
		this.set(u_radiance, this.radiance.r, this.radiance.g, this.radiance.b); // do not pass alpha
		this.set(u_sunAzimuth, this.sunAzimuth);
		this.set(u_sunPolar, this.sunPolar);
		this.set(u_configX, configX);
		this.set(u_configY, configY);
		this.set(u_configZ, configZ);
		super.render(renderable, combinedAttributes);
	}

	private void set(int uniform, float[] arr) {
		Gdx.gl20.glUniform1fv(this.loc(uniform), arr.length, arr, 0);
	}

	@Override
	public int compareTo(Shader other) {
		return 0;
	}

	@Override
	public boolean canRender(Renderable instance) {
		return true;
	}

	public Vector3 getCamDir() {
		return camDir;
	}

	public void setCamDir(Vector3 camDir) {
		this.camDir = camDir;
	}

	public Color getRadiance() {
		return radiance;
	}

	public void setRadiance(Color radiance) {
		this.radiance = radiance;
	}

	public float getSunAzimuth() {
		return sunAzimuth;
	}

	public void setSunAzimuth(float sunAzimuth) {
		this.sunAzimuth = sunAzimuth;
	}

	public float getSunPolar() {
		return sunPolar;
	}

	public void setSunPolar(float sunPolar) {
		this.sunPolar = sunPolar;
	}

	public float[] getConfigX() {
		return configX;
	}

	public void setConfigX(float[] configX) {
		this.configX = configX;
	}

	public float[] getConfigY() {
		return configY;
	}

	public void setConfigY(float[] configY) {
		this.configY = configY;
	}

	public float[] getConfigZ() {
		return configZ;
	}

	public void setConfigZ(float[] configZ) {
		this.configZ = configZ;
	}
}
