package me.vinceh121.wanderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.MathUtils;
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
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.CharacterW;
import me.vinceh121.wanderer.entity.IControllableEntity;
import me.vinceh121.wanderer.entity.Prop;

public class Wanderer extends ApplicationAdapter {
	private PerspectiveCamera cam;
	private Viewport viewport;
	private ModelBatch batch;
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
	private boolean debugBullet = true;

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

		batch = new ModelBatch();

		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		cam = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(3f, 50f, 0f);
		cam.lookAt(5f, 4f, 0);
		cam.far = 1000f;
		cam.near = 0.1f;
		cam.update();

		viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cam);

		camcon = new CameraInputController(cam);
		this.inputMultiplexer.addProcessor(camcon);
		this.inputMultiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(int keycode) {
				if (keycode == Keys.F7) {
					Wanderer.this.debugBullet = !Wanderer.this.debugBullet;
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
						removeEntityControl(controlledEntity);
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
//		john.setMass(1);
		String ch = CHARACTERS[MathUtils.random(CHARACTERS.length - 1)];
		john.setDisplayModel("orig/char_" + ch + ".n/skin.obj");
		john.setDisplayTexture("orig/char_" + ch + ".n/texturenone.png");
		john.setTranslation(0, 50, 0);
		this.entities.add(john);
	}

	private static final String[] CHARACTERS = { "john", "goliath", "susie", "nomade", "dusty", "preston", "seraphim" };

	@Override
	public void render() {
		camcon.update();

		ScreenUtils.clear(0.1f, 0.1f, 0.1f, 0.1f, true);

		batch.begin(cam);
		for (int i = 0; i < this.entities.size; i++) {
			AbstractEntity e = this.entities.get(i);
			e.updatePhysics(btWorld);
			e.render(batch, this.env);
		}
		batch.end();
		WandererConstants.ASSET_MANAGER.update(62);

		this.btWorld.stepSimulation(1f / 60f, 10);
		this.btWorld.performDiscreteCollisionDetection();

		if (this.debugBullet) {
			this.debugDrawer.begin(viewport);
			this.btWorld.debugDrawWorld();
			this.debugDrawer.end();
		}

		if (Gdx.graphics.getFrameId() % 20 == 0) {

		}
	}

	public void addEntity(AbstractEntity e) {
		this.entities.add(e);
		if (e.getCollideObject() != null) {
			this.btWorld.addRigidBody(e.getCollideObject());
		}
	}

	public btDiscreteDynamicsWorld getBtWorld() {
		return btWorld;
	}

	public PerspectiveCamera getCamera() {
		return cam;
	}

	public void controlEntity(IControllableEntity e) {
		if (this.controlledEntity != null)
			this.controlledEntity.onRemoveControl();
		this.controlledEntity = e;
		this.inputMultiplexer.getProcessors().set(0, e.getInputProcessor());
		e.onTakeControl();
	}

	public void removeEntityControl(IControllableEntity e) {
		this.inputMultiplexer.getProcessors().set(0, camcon);
		e.onRemoveControl();
		this.controlledEntity = null;
	}

	@Override
	public void resize(int width, int height) {
		this.viewport.update(width, height);
	}

	@Override
	public void dispose() {
		batch.dispose();
		WandererConstants.ASSET_MANAGER.dispose();
	}

	static {
		Bullet.init();
	}
}
