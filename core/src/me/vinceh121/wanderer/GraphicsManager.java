package me.vinceh121.wanderer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader.AlignMode;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.vinceh121.wanderer.entity.ParticleEmitter;
import me.vinceh121.wanderer.glx.ShaderAttribute;
import me.vinceh121.wanderer.glx.SkyboxRenderer;
import me.vinceh121.wanderer.glx.WandererParticleShader;
import me.vinceh121.wanderer.glx.WandererShader;

public class GraphicsManager extends ApplicationAdapter {
	private ScreenViewport viewportUi;
	private Environment env;
	private ModelBatch modelBatch;
	private PerspectiveCamera cam;
	private Viewport viewport3d;
	private Stage stage;
	private ParticleSystem particleSystem;
	private BillboardParticleBatch particleBatch;

	private SkyboxRenderer skybox;

	@Override
	public void create() {
		this.modelBatch = new ModelBatch(new WandererShaderProvider());

		this.env = new Environment();

		this.cam = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.cam.position.set(3f, 50f, 0f);
		this.cam.lookAt(5f, 4f, 0);
		this.cam.far = 10000f;
		this.cam.near = 0.1f;
		this.cam.update();

		this.particleSystem = new ParticleSystem();
		this.particleBatch = new BillboardParticleBatch(AlignMode.Screen,
				true,
				1024,
				new BlendingAttribute(GL20.GL_ONE, GL20.GL_ONE, 1f),
				new DepthTestAttribute(GL20.GL_LEQUAL, true)) {
			@Override
			protected Renderable allocRenderable() {
				final Renderable r = super.allocRenderable();
				r.material.set(FloatAttribute.createAlphaTest(0.5f));
				return r;
			}

			@Override
			protected Shader getShader(final Renderable renderable) {
				final Shader shader = new WandererParticleShader(renderable);
				shader.init();
				return shader;
			}
		};
		this.particleBatch.setCamera(this.cam);
		this.particleSystem.add(this.particleBatch);

		this.viewportUi = new ScreenViewport();
		this.viewport3d = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), this.cam);

		this.stage = new Stage(this.viewportUi);

		this.skybox = new SkyboxRenderer();
		this.skybox.create();

		this.env.set(this.skybox.getAmbiantLight());
		this.env.add(this.skybox.getSunLight());
	}

	public void apply() {
		this.viewport3d.apply();
	}

	public void clear() {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 0.1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
	}

	public void begin() {
		this.modelBatch.begin(this.cam);
	}

	public void renderParticles(final float delta) {
		this.particleSystem.update(Gdx.graphics.getDeltaTime());
		this.particleSystem.begin();
		this.particleSystem.draw();
		this.particleSystem.end();
		this.modelBatch.render(this.particleSystem);
	}

	public void end() {
		this.modelBatch.end();
	}

	public void renderUI() {
		this.modelBatch.begin(this.viewportUi.getCamera()); // allows the rendering of 3D objects in the UI viewport
		this.stage.act(Gdx.graphics.getDeltaTime());
		this.stage.draw();
		this.modelBatch.end();
	}

	public void renderSkybox(float time) {
		final Vector3 oldPos = new Vector3(this.cam.position);
		this.cam.position.setZero();
		this.cam.update();
		this.modelBatch.begin(cam);

		this.skybox.update(time);
		this.skybox.render(this.modelBatch, this.env);

		this.modelBatch.end();
		this.cam.position.set(oldPos);
		this.cam.update();
	}

	@Override
	public void resize(final int width, final int height) {
		this.viewport3d.update(width, height);
		this.viewportUi.update(width, height, true);
	}

	@Override
	public void dispose() {
		this.modelBatch.dispose();
		this.stage.dispose();
	}

	public void addParticle(final ParticleEmitter effect) {
		this.particleSystem.add(effect.getDelegate());
	}

	public void removeParticle(final ParticleEmitter effect) {
		this.particleSystem.remove(effect.getDelegate());
	}

	/**
	 * @return the cam
	 */
	public PerspectiveCamera getCamera() {
		return this.cam;
	}

	/**
	 * @param cam the cam to set
	 */
	public void setCam(final PerspectiveCamera cam) {
		this.cam = cam;
	}

	/**
	 * @return the viewport3d
	 */
	public Viewport getViewport3d() {
		return this.viewport3d;
	}

	/**
	 * @param viewport3d the viewport3d to set
	 */
	public void setViewport3d(final Viewport viewport3d) {
		this.viewport3d = viewport3d;
	}

	/**
	 * @return the viewportUi
	 */
	public ScreenViewport getViewportUi() {
		return this.viewportUi;
	}

	/**
	 * @return the modelBatch
	 */
	public ModelBatch getModelBatch() {
		return this.modelBatch;
	}

	/**
	 * @return the stage
	 */
	public Stage getStage() {
		return this.stage;
	}

	/**
	 * @param stage the stage to set
	 */
	public void setStage(final Stage stage) {
		this.stage = stage;
	}

	/**
	 * @return the env
	 */
	public Environment getEnv() {
		return this.env;
	}

	public ParticleSystem getParticleSystem() {
		return this.particleSystem;
	}

	private static class WandererShaderProvider extends DefaultShaderProvider {

		public WandererShaderProvider() {
			super(Gdx.files.internal("shaders/default.vert"), Gdx.files.internal("shaders/default.frag"));
			this.config.numBones = 128;
		}

		@Override
		public Shader getShader(Renderable renderable) {
			if (renderable.material.get(ShaderAttribute.TYPE_SHADER) != null) {
				return this.createShader(renderable);
			}
			return super.getShader(renderable);
		}

		@Override
		protected Shader createShader(final Renderable renderable) {
			if (renderable.shader != null) {
				return renderable.shader;
			} else if (renderable.material.get(ShaderAttribute.TYPE_SHADER) != null) {
				ShaderAttribute att = (ShaderAttribute) renderable.material.get(ShaderAttribute.TYPE_SHADER);
				Shader shader = att.getShaderProvider().getShader(renderable);
				renderable.shader = shader;
				return shader;
			}
			return new WandererShader(renderable, this.config);
		}
	}

	private static final Pattern PAT_ANNOTATION = Pattern.compile("[0-9]+\\(([0-9]+)\\) : (.+)");

	public static String shaderDebug(String glLog, String fragCode, String vertCode) {
		String[] logLines = glLog.split("\n");
		MultiValuedMap<Integer, String> annotations = new ArrayListValuedHashMap<>();
		StringBuilder out = new StringBuilder();

		// read GL error log
		for (int i = 1; i < logLines.length; i++) { // FIXME ignore "Fragment shader:", "Vertex shader:" line
			Matcher m = PAT_ANNOTATION.matcher(logLines[i]);
			if (m.find()) {
				annotations.put(Integer.parseInt(m.group(1)), m.group(2));
			}
		}

		String[] fragLines = fragCode.split("\n");
		out.append("\n");
		for (int i = 0; i < fragLines.length; i++) {
			out.append(String.format("%4d", i + 1));
			out.append("  ");
			out.append(fragLines[i]);
			out.append("\n");
			if (annotations.containsKey(i + 1)) {
				for (String a : annotations.get(i + 1)) {
					out.append("        ^ ");
					out.append(a);
					out.append("\n\n");
				}
			}
		}

		return out.toString();
	}
}
