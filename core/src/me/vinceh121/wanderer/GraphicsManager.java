package me.vinceh121.wanderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.Shader;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.DepthTestAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.particles.ParticleShader.AlignMode;
import com.badlogic.gdx.graphics.g3d.particles.ParticleSystem;
import com.badlogic.gdx.graphics.g3d.particles.batches.BillboardParticleBatch;
import com.badlogic.gdx.graphics.g3d.utils.DefaultShaderProvider;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.vinceh121.wanderer.entity.ParticleEmitter;
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

	@Override
	public void create() {
		this.modelBatch = new ModelBatch(new DefaultShaderProvider(Gdx.files.internal("shaders/default.vert"),
				Gdx.files.internal("shaders/default.frag")) {
			@Override
			protected Shader createShader(final Renderable renderable) {
				return new WandererShader(renderable, this.config);
			}
		});

		this.env = new Environment();
		this.env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		this.env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

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
				new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1f),
				new DepthTestAttribute(GL20.GL_LESS, true)) {
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
	}

	public void apply() {
		this.viewport3d.apply();
	}

	public void begin() {
		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 0.1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

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
}
