package me.vinceh121.wanderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btGhostPairCallback;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.vinceh121.wanderer.character.ui.DebugOverlay;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.CharacterW;
import me.vinceh121.wanderer.entity.IControllableEntity;
import me.vinceh121.wanderer.entity.Prop;

public class Wanderer extends ApplicationAdapter {
	private PerspectiveCamera cam;
	private Viewport viewport3d;
	/**
	 * do NOT call update as to not stretch UI
	 */
	private ScreenViewport viewportUi;
	private ModelBatch modelBatch;
	private Stage stage;
	private DebugOverlay debugOverlay;
	private CameraInputController camcon;
	private Array<AbstractEntity> entities;
	private Environment env;

	private btDefaultCollisionConfiguration btConfig = new btDefaultCollisionConfiguration();
	private btCollisionDispatcher btDispatch = new btCollisionDispatcher(btConfig);
	private btBroadphaseInterface btInterface = new btDbvtBroadphase();
	private btSequentialImpulseConstraintSolver btSolver = new btSequentialImpulseConstraintSolver();
	private btGhostPairCallback ghostPairCallback = new btGhostPairCallback();
	private btDiscreteDynamicsWorld btWorld = new btDiscreteDynamicsWorld(btDispatch, btInterface, btSolver, btConfig);

	private DebugDrawer debugDrawer;
	private boolean debugBullet = false, glxDebug = false;

	private InputMultiplexer inputMultiplexer = new InputMultiplexer();

	private IControllableEntity controlledEntity;

	@Override
	public void create() {
		WandererConstants.ASSET_MANAGER.getLogger().setLevel(Logger.DEBUG);
		WandererConstants.ASSET_MANAGER.setErrorListener((asset, t) -> {
			System.err.println("Failed to load asset: " + asset);
			t.printStackTrace();
		});

		this.debugDrawer = new DebugDrawer();
		this.debugDrawer
				.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe | btIDebugDraw.DebugDrawModes.DBG_DrawText);
		btWorld.setDebugDrawer(debugDrawer);

		this.btInterface.getOverlappingPairCache().setInternalGhostPairCallback(ghostPairCallback);

		btWorld.setGravity(new Vector3(0, -9, 0));

		modelBatch = new ModelBatch();

		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		cam = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(3f, 50f, 0f);
		cam.lookAt(5f, 4f, 0);
		cam.far = 1000f;
		cam.near = 0.1f;
		cam.update();

		this.viewport3d = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cam);
		this.viewportUi = new ScreenViewport();

		this.stage = new Stage(this.viewportUi);
		this.debugOverlay = new DebugOverlay(this);

		camcon = new CameraInputController(cam);
		this.inputMultiplexer.addProcessor(camcon);
		this.inputMultiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.F7) {
					Wanderer.this.debugBullet = !Wanderer.this.debugBullet;
					return true;
				} else if (keycode == Keys.F3) {
					Wanderer.this.glxDebug = !Wanderer.this.glxDebug;
					if (Wanderer.this.glxDebug) {
						Wanderer.this.stage.addActor(debugOverlay);
					} else {
						Wanderer.this.stage.getRoot().removeActor(debugOverlay);
					}
					return true;
				} else if (keycode == Keys.TAB) {
					if (controlledEntity == null) {
						for (AbstractEntity e : entities) {
							if (e instanceof IControllableEntity) {
								System.out.println("Controlling " + e);
								controlEntity((IControllableEntity) e);
								return true;
							}
						}
					} else {
						System.out.println("Remove control");
						removeEntityControl();
					}
					return true;
				}
				return false;
			}
		});
		Gdx.input.setInputProcessor(this.inputMultiplexer);

		entities = new Array<>();

		Prop e = new Prop(this);
		e.setCollideModel("orig/first_island.n/collide.obj");
		e.setDisplayModel("orig/first_island.n/terrain.obj");
		e.setDisplayTexture("orig/first_island.n/texturenone.png");
		e.setExactCollideModel(true);
		this.entities.add(e);

		CharacterW john = new CharacterW(this);
		john.setDisplayModel("orig/char_john.n/skin.obj");
		john.setDisplayTexture("orig/char_john.n/texturenone.png");
		john.setTranslation(0.1f, 100f, 0.1f);
		this.entities.add(john);

		this.controlEntity(john);
	}

	@Override
	public void render() {
		this.viewport3d.apply();
		camcon.update();

		WandererConstants.ASSET_MANAGER.update(62);

		this.btWorld.stepSimulation(1f / 60f, 10);
		this.btWorld.performDiscreteCollisionDetection();

		Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 0.1f);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT
				| (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));

		modelBatch.begin(cam);
		for (int i = 0; i < this.entities.size; i++) {
			AbstractEntity e = this.entities.get(i);
			e.updatePhysics(btWorld);
			e.render(modelBatch, this.env);
		}
		modelBatch.end();

		if (this.debugBullet) {
			this.debugDrawer.begin(viewport3d);
			this.btWorld.debugDrawWorld();
			this.debugDrawer.end();
		}

		stage.act(Gdx.graphics.getDeltaTime());
		stage.draw();
	}

	public void addEntity(AbstractEntity e) {
		this.entities.add(e);
		e.enterBtWorld(btWorld);
	}

	public btDiscreteDynamicsWorld getBtWorld() {
		return btWorld;
	}

	public PerspectiveCamera getCamera() {
		return cam;
	}

	public Array<AbstractEntity> getEntities() {
		return entities;
	}

	public void controlEntity(IControllableEntity e) {
		if (this.controlledEntity != null)
			this.controlledEntity.onRemoveControl();
		this.controlledEntity = e;
		this.inputMultiplexer.getProcessors().set(0, e.getInputProcessor());
		e.onTakeControl();
	}

	public void removeEntityControl() {
		this.inputMultiplexer.getProcessors().set(0, camcon);
		this.controlledEntity.onRemoveControl();
		this.controlledEntity = null;
	}

	@Override
	public void resize(int width, int height) {
		this.viewport3d.update(width, height);
		this.viewportUi.update(width, height, true);
	}

	@Override
	public void dispose() {
		modelBatch.dispose();
		stage.dispose();
		WandererConstants.ASSET_MANAGER.dispose();
	}

	static {
		Bullet.init();
	}
}
