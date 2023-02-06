package me.vinceh121.wanderer.glx;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

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
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.shaders.DefaultShader.Config;
import com.badlogic.gdx.graphics.g3d.utils.BaseShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.fasterxml.jackson.core.type.TypeReference;

import me.vinceh121.wanderer.WandererConstants;

public class SkyboxRenderer {
	private final Vector3 sunDir = new Vector3(), moonDir = new Vector3();
	private final Map<String, SkyProperties> skies = new Hashtable<>();
	private ModelInstance sky, stars, sun, mars, galaxy, skycap, skyring;
	private float previous;
	private SkyShader shader;
	private DirectionalLight sunLight, moonLight;
	private SkyProperties skyProperties;

	public void create() {
		try {
			this.skies.putAll(WandererConstants.MAPPER.readValue(Gdx.files.internal("skies.json").read(),
					new TypeReference<Map<String, SkyProperties>>() {
					}));
		} catch (IOException e) {
			throw new RuntimeException("Failed to load skies.json", e);
		}

		this.skyProperties = this.skies.get("normal");
		
		this.sky = this.makeSphereSky();

		this.sun = this.makePlaneOneOne("orig/lib/sun1/texturenone.ktx");
		this.mars = this.makePlaneAlpha("orig/lib/mars/texturealpha.ktx");
		this.galaxy = this.makePlaneOneOne("orig/lib/galaxy/texturenone.ktx");

		this.skycap = this.makeCapAlpha("orig/skybox02.n/texturealpha.ktx");
		this.skycap.transform.translate(0, -1, 0);

		this.skyring = this.makeRingAlpha("orig/skybox02.n/texture2wow3.ktx");

		this.stars = this.makeStarsOneOne("orig/lib/stars/texturenone.ktx");

		this.sunLight = new DirectionalLight().set(0.8f, 0.8f, 0.8f, 1f, 0f, 0f);
	}

	/**
	 * @param time Time of day, between 0 and 1
	 */
	public void update(float time) {
		assert time >= 0 && time <= 1;
		final float delta = time - this.previous;
		this.previous = time;

		if (this.shader != null) {
			this.shader.setTimeOfDay(time);
		}

		this.stars.transform.rotateRad(Vector3.Y, 0.02f * delta / 0.016666668f);

		this.move(this.sun, MathUtils.PI * 0.65f, time * MathUtils.PI2, 0.6f, 0);
		this.sunDir.setFromSpherical(MathUtils.PI * 0.65f, time * MathUtils.PI2);
		if (this.shader != null) {
			// shader needs sunDir to NOT be inverted
			this.shader.setSunDir(this.sunDir);
		}
		this.sunDir.scl(-1);

		this.move(this.mars, MathUtils.PI2 * time, 0.12f * MathUtils.PI2, 1f, 0);
		this.move(this.galaxy, MathUtils.sin(time * MathUtils.PI2) / 5 + MathUtils.PI * 0.1f, MathUtils.HALF_PI, 1f, 0);

		// skycap rotates counter clock-wise
		this.skycap.transform.rotateRad(Vector3.Y, 0.2f * delta / 0.016666668f);
		// skycap bobs up and down
		this.skycap.transform.setTranslation(0, MathUtils.sin(time * 32f * MathUtils.PI2) / 2 - 2, 0);

		// skyring rotates clockwise
		this.skyring.transform.rotateRad(Vector3.Y, -0.1f * delta / 0.016666668f);

		this.sunLight.setDirection(this.sunDir);
		this.sunLight.setColor(interpolatedColor(time, this.skyProperties.getSunLightColor()));
	}

	private Color interpolatedColor(float time, Map<TimeRange, Color> colors) {
		final TimeRange range = getTimeRange(time);
		final TimeRange nextRange = TimeRange.values()[(range.ordinal() + 1) % TimeRange.values().length];

		if (!colors.containsKey(range) || !colors.containsKey(nextRange)) {
			return new Color();
		}

		float alpha = (time - range.getRangeStart()) / (range.getRangeEnd() - range.getRangeStart());

		final Color c = colors.get(range).cpy();
		c.lerp(colors.get(nextRange), alpha);

		return c;
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

	private ModelInstance makeSphereSky() {
		return this.makeSphere(new Material(new DepthTestAttribute(false),
				IntAttribute.createCullFace(GL20.GL_FRONT),
				new ShaderAttribute(new BaseShaderProvider() {
					@Override
					protected Shader createShader(Renderable renderable) {
						SkyShader s = new SkyShader(renderable,
								new Config(Gdx.files.internal("shaders/sky.vert").readString(),
										Gdx.files.internal("shaders/sky.frag").readString()));
						s.setTimeOfDay(previous);
						s.setSunDir(sunDir);
						SkyboxRenderer.this.shader = s;
						return s;
					}
				})));
	}

	private ModelInstance makeSphere(Material mat) {
		ModelBuilder builder = new ModelBuilder();
		Model model = builder.createSphere(10, 10, 10, 4, 4, mat, VertexAttributes.Usage.Position);
		return new ModelInstance(model);
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
			.set(new DepthTestAttribute(false),
					IntAttribute.createCullFace(0),
					TextureAttribute.createDiffuse(tex),
					new NoLightningAttribute());
		return ins;
	}

	private ModelInstance makeCap(Texture tex) {
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

	private ModelInstance makeRing(Texture tex) {
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

	private ModelInstance makeStars(Texture tex) {
		// skybox01.n/model.obj has a big ring, others have the same with 10× smaller
		// scale
		final Model model = WandererConstants.ASSET_MANAGER.get("orig/lib/stars/stars.obj", Model.class);
		final ModelInstance ins = new ModelInstance(model);
		ins.materials.get(0)
			.set(new DepthTestAttribute(false),
					IntAttribute.createCullFace(0),
					TextureAttribute.createDiffuse(tex),
					new NoLightningAttribute());
		return ins;
	}

	public SkyProperties getSkyProperties() {
		return skyProperties;
	}

	public void setSkyProperties(SkyProperties skyProperties) {
		this.skyProperties = skyProperties;
	}

	public Vector3 getSunDir() {
		return sunDir;
	}

	public Vector3 getMoonDir() {
		return moonDir;
	}

	public DirectionalLight getSunLight() {
		return sunLight;
	}

	public DirectionalLight getMoonLight() {
		return moonLight;
	}

	public static TimeRange getTimeRange(float time) {
		for (TimeRange r : TimeRange.values()) {
			if (time <= r.getRangeEnd()) {
				return r;
			}
		}
		return null;
	}

	public static enum TimeRange {
		MORNING(0, 0.25f),
		NOON(0.25f, 0.375f),
		EVENING_START(0.375f, 0.4375f),
		EVENING_MID(0.4375f, 0.5f),
		EVENING_END(0.5f, 0.75f),
		MIDNIGHT(0.75f, 0.875f),
		/// ....
		NIGHT_END(0.875f, 1f);

		private final float rangeStart, rangeEnd;

		private TimeRange(float rangeStart, float rangeEnd) {
			this.rangeStart = rangeStart;
			this.rangeEnd = rangeEnd;
		}

		public float getRangeStart() {
			return rangeStart;
		}

		public float getRangeEnd() {
			return rangeEnd;
		}
	}
}
