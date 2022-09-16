package me.vinceh121.wanderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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

import me.vinceh121.wanderer.phys.ContactDispatcher;
import me.vinceh121.wanderer.phys.IContactListener;

public class PhysicsManager extends ApplicationAdapter {
	private final btDefaultCollisionConfiguration btConfig = new btDefaultCollisionConfiguration();
	private final btCollisionDispatcher btDispatch = new btCollisionDispatcher(this.btConfig);
	private final btBroadphaseInterface btInterface = new btDbvtBroadphase();
	private final btSequentialImpulseConstraintSolver btSolver = new btSequentialImpulseConstraintSolver();
	private final btGhostPairCallback ghostPairCallback = new btGhostPairCallback();
	private final btDiscreteDynamicsWorld btWorld = new btDiscreteDynamicsWorld(this.btDispatch,
			this.btInterface,
			this.btSolver,
			this.btConfig);
	private ContactDispatcher contactDispatcher;
	private DebugDrawer debugDrawer;

	@Override
	public void create() {
		this.debugDrawer = new DebugDrawer(); // do not init in place
		this.debugDrawer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_DrawWireframe
				| btIDebugDraw.DebugDrawModes.DBG_DrawText | btIDebugDraw.DebugDrawModes.DBG_DrawContactPoints);
		this.debugDrawer.setFont(WandererConstants.getDevSkin().getFont("default-font"));
		this.btWorld.setDebugDrawer(this.debugDrawer);

		this.btInterface.getOverlappingPairCache().setInternalGhostPairCallback(this.ghostPairCallback);

		this.btWorld.setGravity(new Vector3(0, -9, 0));

		this.contactDispatcher = new ContactDispatcher();
		this.contactDispatcher.enableOnStarted();
	}

	@Override
	public void render() {
		this.btWorld.stepSimulation(Gdx.graphics.getDeltaTime(), 10);
		this.btWorld.performDiscreteCollisionDetection();
	}

	/**
	 * @return the btConfig
	 */
	public btDefaultCollisionConfiguration getBtConfig() {
		return this.btConfig;
	}

	/**
	 * @return the btDispatch
	 */
	public btCollisionDispatcher getBtDispatch() {
		return this.btDispatch;
	}

	/**
	 * @return the btInterface
	 */
	public btBroadphaseInterface getBtInterface() {
		return this.btInterface;
	}

	/**
	 * @return the btSolver
	 */
	public btSequentialImpulseConstraintSolver getBtSolver() {
		return this.btSolver;
	}

	/**
	 * @return the ghostPairCallback
	 */
	public btGhostPairCallback getGhostPairCallback() {
		return this.ghostPairCallback;
	}

	/**
	 * @return the btWorld
	 */
	public btDiscreteDynamicsWorld getBtWorld() {
		return this.btWorld;
	}

	/**
	 * @return the debugDrawer
	 */
	public DebugDrawer getDebugDrawer() {
		return this.debugDrawer;
	}

	public ContactDispatcher getContactDispatcher() {
		return this.contactDispatcher;
	}

	public void addContactListener(final IContactListener value) {
		this.contactDispatcher.addContactListener(value);
	}

	public boolean removeContactListener(final IContactListener value) {
		return this.contactDispatcher.removeContactListener(value);
	}

	static {
		Bullet.init();
	}
}
