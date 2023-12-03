package me.vinceh121.wanderer.glx;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.core.type.TypeReference;

import me.vinceh121.wanderer.Preferences;
import me.vinceh121.wanderer.WandererConstants;

public class SkyboxRenderer {
	private final Vector3 sunDir = new Vector3(), moonDir = new Vector3();
	private final Map<String, SkyProperties> skies = new Hashtable<>();
	private ModelInstance sky, stars, sun, mars, galaxy, skycap, skyring;
	private float previous;
	private SkyShader shader;
	private ColorAttribute ambiantLight;
	private DirectionalShadowLight sunLight;
	private DirectionalLight moonLight;
	private SkyProperties skyProperties;

	public void create() {
		try {
			this.skies.putAll(WandererConstants.MAPPER.readValue(Gdx.files.internal("skies.json").read(),
					new TypeReference<Map<String, SkyProperties>>() {
					}));
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load skies.json", e);
		}

		this.skyProperties = this.skies.get("normal");

		this.sky = this.makeSphereSky();

		this.sun = this.makePlaneOneOne("orig/lib/sun1/texturenone.ktx");
		this.mars = this.makePlaneAlpha("orig/lib/mars/texturealpha.ktx");
		this.galaxy = this.makePlaneOneMinusSrcAlphaOne("orig/lib/galaxy/texturenone.ktx");

		this.skycap = this.makeCapAlpha("orig/skybox02.n/texturealpha.ktx");
		this.skycap.transform.translate(0, -1, 0);

		this.skyring = this.makeRingAlpha("orig/skybox02.n/texture2wow3.ktx");

		this.stars = this.makeStarsOneMinusSrcAlphaOne("orig/lib/stars/texturenone.ktx");

		final int shadowQuality = Preferences.getPreferences().getIntOrElse("graphics.shadowQuality", 8);
		final int shadowDistance = Preferences.getPreferences().getIntOrElse("graphics.shadowDistance", 4);

		this.ambiantLight = new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f);
		this.sunLight = new DirectionalShadowLight(1024
				* shadowQuality, 1024 * shadowQuality, 128 * shadowDistance, 128 * shadowDistance, 0.1f, 2000);
		this.sunLight.set(0.8f, 0.8f, 0.8f, 1f, 0f, 0f);
	}

	/**
	 * @param time Time of day, between 0 and 1
	 */
	public void update(final float time) {
		assert time >= 0 && time <= 1;
		final float delta = time - this.previous;
		this.previous = time;

		this.stars.transform.rotateRad(Vector3.Y, 0.02f * delta / 0.016666668f);
		((BlendingAttribute) this.stars.materials.get(0).get(BlendingAttribute.Type)).opacity =
				1 - this.interpolatedFloat(time, this.skyProperties.getStarsOpacity());

		this.move(this.sun, MathUtils.PI * 0.65f, time * MathUtils.PI2, 0.6f, 0);
		this.sunDir.setFromSpherical(MathUtils.PI * 0.65f, time * MathUtils.PI2);

		if (this.shader != null) {
			// shader needs sunDir to NOT be inverted
			this.shader.setSunDir(this.sunDir);
			this.shader.setSkyTop(this.interpolatedColor(time, this.skyProperties.getSkyTopColor()));
			this.shader.setSkyMiddle(this.interpolatedColor(time, this.skyProperties.getSkyMiddleColor()));
			this.shader.setSkyBottom(this.interpolatedColor(time, this.skyProperties.getSkyBottomColor()));
			this.shader.setSunShine(this.interpolatedColor(time, this.skyProperties.getSunShineColor()));
		}

		this.sunDir.scl(-1);

		this.move(this.mars, MathUtils.PI2 * time, 0.12f * MathUtils.PI2, 1f, 0);

		this.move(this.galaxy, MathUtils.sin(time * MathUtils.PI2) / 5 + MathUtils.PI * 0.1f, MathUtils.HALF_PI, 1f, 0);
		((BlendingAttribute) this.galaxy.materials.get(0).get(BlendingAttribute.Type)).opacity =
				1 - this.interpolatedFloat(time, this.skyProperties.getGalaxyOpacity());

		// skycap rotates counter clock-wise
		this.skycap.transform.rotateRad(Vector3.Y, 0.2f * delta / 0.016666668f);
		// skycap bobs up and down
		this.skycap.transform.setTranslation(0, MathUtils.sin(time * 32f * MathUtils.PI2) / 2 - 2, 0);

		// skyring rotates clockwise
		this.skyring.transform.rotateRad(Vector3.Y, -0.1f * delta / 0.016666668f);

		this.sunLight.setDirection(this.sunDir);
		this.sunLight.setColor(this.interpolatedColor(time, this.skyProperties.getSunLightColor()));
		this.ambiantLight.color.set(this.interpolatedColor(time, this.skyProperties.getAmbLightColor()));
	}

	private float interpolatedFloat(final float time, final NavigableMap<Float, Float> floats) {
		Entry<Float, Float> left = floats.floorEntry(time);

		if (left == null) { // wrap around
			left = floats.lastEntry();
		}

		Entry<Float, Float> right = floats.ceilingEntry(time);

		if (right == null) { // wrap around
			right = floats.firstEntry();
		}

		if (right == null || left == null) {
			return 0;
		}

		final float alpha = (time - left.getKey()) / (right.getKey() - left.getKey());

		return MathUtils.lerp(left.getValue(), right.getValue(), alpha);
	}

	private Color interpolatedColor(final float time, final NavigableMap<Float, Color> colors) {
		Entry<Float, Color> left = colors.floorEntry(time);

		if (left == null) { // wrap around
			left = colors.lastEntry();
		}

		Entry<Float, Color> right = colors.ceilingEntry(time);

		if (right == null) { // wrap around
			right = colors.firstEntry();
		}

		if (right == null || left == null) {
			return Color.BLACK;
		}

		final float alpha = (time - left.getKey()) / (right.getKey() - left.getKey());

		final Color c = left.getValue().cpy();
		c.lerp(right.getValue(), alpha);

		return c;
	}

	public void setSkycapTexture(final String tex) {
		final Texture texture = WandererConstants.ASSET_MANAGER.get(tex);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		final Material m = this.skycap.materials.get(0);
		((TextureAttribute) m.get(TextureAttribute.Diffuse)).textureDescription.texture = texture;
	}

	private void move(final ModelInstance ins, final float azi, final float ang, final float scl, final int layer) {
		final Vector3 pos = new Vector3();
		pos.setFromSpherical(azi, ang);

		final Quaternion rot = new Quaternion();
		rot.setFromCross(pos, Vector3.Y);
		rot.conjugate();

		pos.scl(10 + layer);

		ins.transform.set(pos, rot, new Vector3(scl, scl, scl));
	}

	public void render(final ModelBatch batch, final Environment env) {
		batch.render(this.stars, env);
		batch.render(this.sun, env);
		batch.render(this.mars, env);
		batch.render(this.galaxy, env);
		batch.render(this.skycap, env);
		batch.render(this.skyring, env);
		batch.render(this.sky, env);
	}

	public boolean isDay(final float time) {
		return time >= 0 && time <= 0.5f;
	}

	public boolean isNight(final float time) {
		return time > 0.5f && time <= 1;
	}

	private ModelInstance makeSphereSky() {
		return this.makeSphere(new Material(new DepthTestAttribute(false),
				IntAttribute.createCullFace(GL20.GL_FRONT),
				new ShaderAttribute(new BaseShaderProvider() {
					@Override
					protected Shader createShader(final Renderable renderable) {
						final SkyShader s = new SkyShader(renderable,
								new Config(Gdx.files.internal("shaders/sky.vert").readString(),
										Gdx.files.internal("shaders/sky.frag").readString()));
						s.setSunDir(SkyboxRenderer.this.sunDir);
						SkyboxRenderer.this.shader = s;
						return s;
					}
				})));
	}

	private ModelInstance makeSphere(final Material mat) {
		final ModelBuilder builder = new ModelBuilder();
		final Model model = builder.createSphere(10, 10, 10, 4, 4, mat, VertexAttributes.Usage.Position);
		return new ModelInstance(model);
	}

	private ModelInstance makePlaneAlpha(final String tex) {
		final ModelInstance ins = this.makePlane(tex);
		ins.materials.get(0).set(new BlendingAttribute(0.5f));
		return ins;
	}

	private ModelInstance makePlaneOneOne(final String tex) {
		final ModelInstance ins = this.makePlane(tex);
		ins.materials.get(0).set(new BlendingAttribute(GL20.GL_ONE, GL20.GL_ONE, 0.5f));
		return ins;
	}

	private ModelInstance makePlaneOneMinusSrcAlphaOne(final String tex) {
		final ModelInstance ins = this.makePlane(tex);
		ins.materials.get(0).set(new BlendingAttribute(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, 0.5f));
		return ins;
	}

	private ModelInstance makeCapAlpha(final String tex) {
		final ModelInstance ins = this.makeCap(tex);
		ins.materials.get(0).set(new BlendingAttribute(0.5f));
		return ins;
	}

	private ModelInstance makeRingAlpha(final String tex) {
		final ModelInstance ins = this.makeRing(tex);
		ins.materials.get(0).set(new BlendingAttribute(0.5f));
		return ins;
	}

	private ModelInstance makeStarsOneMinusSrcAlphaOne(final String tex) {
		final ModelInstance ins = this.makeStars(tex);
		ins.materials.get(0).set(new BlendingAttribute(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE, 0.5f));
		return ins;
	}

	private ModelInstance makePlane(final String tex) {
		final Texture texture = WandererConstants.ASSET_MANAGER.get(tex, Texture.class);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		return this.makePlane(texture);
	}

	private ModelInstance makeCap(final String tex) {
		final Texture texture = WandererConstants.ASSET_MANAGER.get(tex, Texture.class);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		return this.makeCap(texture);
	}

	private ModelInstance makeRing(final String tex) {
		final Texture texture = WandererConstants.ASSET_MANAGER.get(tex, Texture.class);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		return this.makeRing(texture);
	}

	private ModelInstance makeStars(final String tex) {
		final Texture texture = WandererConstants.ASSET_MANAGER.get(tex, Texture.class);
		texture.setFilter(TextureFilter.MipMapLinearLinear, TextureFilter.Linear);
		return this.makeStars(texture);
	}

	private ModelInstance makePlane(final Texture tex) {
		final Model model = WandererConstants.ASSET_MANAGER.get("orig/lib/mars/plane.obj", Model.class);
		final ModelInstance ins = new ModelInstance(model);
		ins.materials.get(0)
			.set(new DepthTestAttribute(false),
					IntAttribute.createCullFace(0),
					TextureAttribute.createDiffuse(tex),
					new NoLightningAttribute());
		return ins;
	}

	private ModelInstance makeCap(final Texture tex) {
		// skybox01.n/model.obj has a small cap, others have the same with 10× scale
		final Model model = WandererConstants.ASSET_MANAGER.get("orig/skybox01.n/model.obj", Model.class);
		final ModelInstance ins = new ModelInstance(model);
		ins.materials.get(0)
			.set(new DepthTestAttribute(false),
					IntAttribute.createCullFace(0),
					TextureAttribute.createDiffuse(tex),
					new NoLightningAttribute());
		return ins;
	}

	private ModelInstance makeRing(final Texture tex) {
		// skybox01.n/model.obj has a big ring, others have the same with 10× smaller
		// scale
		final Model model = WandererConstants.ASSET_MANAGER.get("orig/skybox01.n/bgplane.obj", Model.class);
		final ModelInstance ins = new ModelInstance(model);
		ins.materials.get(0)
			.set(new DepthTestAttribute(false),
					IntAttribute.createCullFace(0),
					TextureAttribute.createDiffuse(tex),
					new NoLightningAttribute());
		return ins;
	}

	private ModelInstance makeStars(final Texture tex) {
		// skybox01.n/model.obj has a big ring, others have the same with 10× smaller
		// scale
		final Model model = WandererConstants.ASSET_MANAGER.get("orig/lib/stars/stars.obj", Model.class);
		final ModelInstance ins = new ModelInstance(model);
		ins.materials.get(0)
			.set(new DepthTestAttribute(false), IntAttribute.createCullFace(0), TextureAttribute.createDiffuse(tex)
//							,new NoLightningAttribute()
			);
		return ins;
	}

	public SkyProperties getSkyProperties() {
		return this.skyProperties;
	}

	public void setSkyProperties(final SkyProperties skyProperties) {
		this.skyProperties = skyProperties;
	}

	public Vector3 getSunDir() {
		return this.sunDir;
	}

	public Vector3 getMoonDir() {
		return this.moonDir;
	}

	public ColorAttribute getAmbiantLight() {
		return this.ambiantLight;
	}

	public DirectionalShadowLight getSunLight() {
		return this.sunLight;
	}

	public DirectionalLight getMoonLight() {
		return this.moonLight;
	}
}
