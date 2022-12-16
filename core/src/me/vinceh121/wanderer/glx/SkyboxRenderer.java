package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.WandererConstants;

public class SkyboxRenderer {
	private ModelInstance sky, stars, sun, mars, galaxy, skycap, skyring;
	private float previous;

	public void create() {
		this.sky = this.makeStars("orig/lib/stars/texturenone.ktx");

		this.sun = this.makePlaneOneOne("orig/lib/sun1/texturenone.ktx");
		this.mars = this.makePlaneAlpha("orig/lib/mars/texturealpha.ktx");
		this.galaxy = this.makePlaneOneOne("orig/lib/galaxy/texturenone.ktx");

		this.skycap = this.makeCapAlpha("orig/skybox02.n/texturealpha.ktx");
		this.skycap.transform.translate(0, -1, 0);

		this.skyring = this.makeRingAlpha("orig/skybox02.n/texture2wow3.ktx");

		this.stars = this.makeStarsOneOne("orig/lib/stars/texturenone.ktx");
	}

	/**
	 * @param time Time of day, between 0 and 1
	 */
	public void update(float time) {
		if (time < 0 || time > 1) {
			throw new IllegalArgumentException("time must be between 0 and 1");
		}
		final float delta = Math.max(time - this.previous, 0);
		this.previous = time;

		this.stars.transform.rotateRad(Vector3.Y, 0.02f * delta / 0.016666668f);

		this.move(this.sun, MathUtils.PI * 0.65f, time * MathUtils.PI2, 0.6f, 0);
		this.move(this.mars, MathUtils.PI2 * time, 0.12f * MathUtils.PI2, 1f, 0);
		this.move(this.galaxy, MathUtils.sin(time * MathUtils.PI2) / 5 + MathUtils.PI * 0.1f, MathUtils.HALF_PI, 1f, 0);

		// skycap rotates counter clock-wise
		this.skycap.transform.rotateRad(Vector3.Y, 0.2f * delta / 0.016666668f);
		// skycap bobs up and down
		this.skycap.transform.translate(0, MathUtils.sin(time * MathUtils.PI2) / 1500, 0);

		// skyring rotates clockwise
		this.skyring.transform.rotateRad(Vector3.Y, -0.1f * delta / 0.016666668f);
	}

	public void setSkycapTexture(String tex) {
		Texture texture = WandererConstants.ASSET_MANAGER.get(tex);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		Material m = this.skycap.materials.get(0);
		((TextureAttribute) m.get(TextureAttribute.Diffuse)).textureDescription.texture = texture;
	}

	private void move(ModelInstance ins, float azi, float ang, float scl, int layer) {
		Vector3 pos = new Vector3();
		pos.setFromSpherical(azi, ang);

		Quaternion rot = new Quaternion();
		rot.setFromCross(pos, Vector3.Y);
		rot.conjugate();

		pos.scl(10 + layer);

		ins.transform.set(pos, rot, new Vector3(scl, scl, scl));
	}

	public void render(ModelBatch batch, Environment env) {
		batch.render(this.stars, env);
		batch.render(this.sun, env);
		batch.render(this.mars, env);
		batch.render(this.galaxy, env);
		batch.render(this.skycap, env);
		batch.render(this.skyring, env);
		batch.render(this.sky, env);
	}

	public boolean isDay(float time) {
		return time >= 0 && time <= 0.5f;
	}

	public boolean isNight(float time) {
		return time > 0.5f && time <= 1;
	}

	private ModelInstance makePlaneAlpha(String tex) {
		ModelInstance ins = this.makePlane(tex);
		ins.materials.get(0).set(new BlendingAttribute(0.5f));
		return ins;
	}

	private ModelInstance makePlaneOneOne(String tex) {
		ModelInstance ins = this.makePlane(tex);
		ins.materials.get(0).set(new BlendingAttribute(GL20.GL_ONE, GL20.GL_ONE, 0.5f));
		return ins;
	}

	private ModelInstance makeCapAlpha(String tex) {
		ModelInstance ins = this.makeCap(tex);
		ins.materials.get(0).set(new BlendingAttribute(0.5f));
		return ins;
	}

	private ModelInstance makeRingAlpha(String tex) {
		ModelInstance ins = this.makeRing(tex);
		ins.materials.get(0).set(new BlendingAttribute(0.5f));
		return ins;
	}

	private ModelInstance makeStarsOneOne(String tex) {
		ModelInstance ins = this.makeStars(tex);
		ins.materials.get(0).set(new BlendingAttribute(GL20.GL_ONE, GL20.GL_ONE, 0.5f));
		return ins;
	}

	private ModelInstance makePlane(String tex) {
		Texture texture = WandererConstants.ASSET_MANAGER.get(tex, Texture.class);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		return this.makePlane(texture);
	}

	private ModelInstance makeCap(String tex) {
		Texture texture = WandererConstants.ASSET_MANAGER.get(tex, Texture.class);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		return this.makeCap(texture);
	}

	private ModelInstance makeRing(String tex) {
		Texture texture = WandererConstants.ASSET_MANAGER.get(tex, Texture.class);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		return this.makeRing(texture);
	}

	private ModelInstance makeStars(String tex) {
		Texture texture = WandererConstants.ASSET_MANAGER.get(tex, Texture.class);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		return this.makeStars(texture);
	}

	private ModelInstance makePlane(Texture tex) {
		final Model model = WandererConstants.ASSET_MANAGER.get("orig/lib/mars/plane.obj", Model.class);
		final ModelInstance ins = new ModelInstance(model);
		ins.materials.get(0)
			.set(new DepthTestAttribute(false), IntAttribute.createCullFace(0), TextureAttribute.createDiffuse(tex));
		return ins;
	}

	private ModelInstance makeCap(Texture tex) {
		// skybox01.n/model.obj has a small cap, others have the same with 10× scale
		final Model model = WandererConstants.ASSET_MANAGER.get("orig/skybox01.n/model.obj", Model.class);
		final ModelInstance ins = new ModelInstance(model);
		ins.materials.get(0)
			.set(new DepthTestAttribute(false), IntAttribute.createCullFace(0), TextureAttribute.createDiffuse(tex));
		return ins;
	}

	private ModelInstance makeRing(Texture tex) {
		// skybox01.n/model.obj has a big ring, others have the same with 10× smaller
		// scale
		final Model model = WandererConstants.ASSET_MANAGER.get("orig/skybox01.n/bgplane.obj", Model.class);
		final ModelInstance ins = new ModelInstance(model);
		ins.materials.get(0)
			.set(new DepthTestAttribute(false), IntAttribute.createCullFace(0), TextureAttribute.createDiffuse(tex));
		return ins;
	}

	private ModelInstance makeStars(Texture tex) {
		// skybox01.n/model.obj has a big ring, others have the same with 10× smaller
		// scale
		final Model model = WandererConstants.ASSET_MANAGER.get("orig/lib/stars/stars.obj", Model.class);
		final ModelInstance ins = new ModelInstance(model);
		ins.materials.get(0)
			.set(new DepthTestAttribute(false), IntAttribute.createCullFace(0), TextureAttribute.createDiffuse(tex));
		return ins;
	}
}