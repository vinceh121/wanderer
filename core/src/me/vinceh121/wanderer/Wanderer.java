package me.vinceh121.wanderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
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
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Wanderer extends ApplicationAdapter {
	private PerspectiveCamera cam;
	private Viewport viewport;
	private ModelBatch batch;
	private CameraInputController camcon;
	private Array<Entity> entities;
	private Environment env;

	private btDefaultCollisionConfiguration btConfig = new btDefaultCollisionConfiguration();
	private btCollisionDispatcher btDispatch = new btCollisionDispatcher(btConfig);
	private btBroadphaseInterface btInterface = new btDbvtBroadphase();
	private btSequentialImpulseConstraintSolver btSolver = new btSequentialImpulseConstraintSolver();
	private btDiscreteDynamicsWorld btWorld = new btDiscreteDynamicsWorld(btDispatch, btInterface, btSolver, btConfig);

	private DebugDrawer debugDrawer;
	private boolean debugBullet;

	@Override
	public void create() {
		WandererConstants.ASSET_MANAGER.getLogger().setLevel(Logger.DEBUG);
		WandererConstants.ASSET_MANAGER.setErrorListener((asset, t) -> {
			System.err.println("Failed to load asset: " + asset);
			t.printStackTrace();
		});

		this.debugDrawer = new DebugDrawer();
		this.debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe);
		btWorld.setDebugDrawer(debugDrawer);

		btWorld.setGravity(new Vector3(0, -9, 0));

		batch = new ModelBatch();

		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		env.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		cam = new PerspectiveCamera(90, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(3f, 7f, 10f);
		cam.lookAt(0, 4f, 0);
		cam.far = 1000f;
		cam.update();

		viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), cam);

		camcon = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camcon);

		entities = new Array<>();

		WandererConstants.ASSET_MANAGER.load("orig/first_island.n/texturenone.png", Texture.class);
		WandererConstants.ASSET_MANAGER.finishLoading();

		Entity e = new Entity();
		e.setCollideModel("orig/first_island.n/collide.obj");
		e.setDisplayModel("orig/first_island.n/terrain.obj");
		e.setDisplayTexture("orig/first_island.n/texturenone.png");
		e.setExactCollideModel(true);
		this.entities.add(e);
	}

	private static final String[] CHARACTERS = { "john", "goliath", "susie" };

	@Override
	public void render() {
		camcon.update();

		ScreenUtils.clear(0.1f, 0.1f, 0.1f, 0.1f, true);

		batch.begin(cam);
		for (int i = 0; i < this.entities.size; i++) {
			Entity e = this.entities.get(i);
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

		if (Gdx.graphics.getFrameId() % 60 == 0) {
			Entity john = new Entity();
			john.setMass(1);
			String ch = CHARACTERS[MathUtils.random(2)];
			john.setDisplayModel("orig/char_" + ch + ".n/skin.obj");
			john.setCollideModel("orig/char_" + ch + ".n/collide.obj");
			john.setDisplayTexture("orig/char_" + ch + ".n/texturenone.png");
			john.setTranslation(0, 50, 0);
			this.entities.add(john);
		}
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
