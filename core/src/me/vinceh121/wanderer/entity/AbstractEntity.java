package me.vinceh121.wanderer.entity;

import java.util.Objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;

public abstract class AbstractEntity implements Disposable {
	protected final Wanderer game;
	private Matrix4 transform = new Matrix4();
	private final Vector3 collideObjectOffset = new Vector3();
	private final Array<DisplayModel> models = new Array<>();
	private String collideModel;
	private btRigidBody collideObject;
	private float mass;
	private boolean exactCollideModel = true;
	private int collisionGroup = CollisionFilterGroups.DefaultFilter, collisionMask = CollisionFilterGroups.AllFilter;

	public AbstractEntity(final Wanderer game) {
		this.game = game;
	}

	public void updatePhysics(final btDiscreteDynamicsWorld world) {
		// if we have a (manually-)defined body, copy transform subtract collision
		// offset from bullet's transform
		if (this.collideObject != null && !this.collideObject.isStaticOrKinematicObject()) {
			final Matrix4 colTransform = this.collideObject.getWorldTransform();
			final Vector3 colTranslation = new Vector3();
			colTransform.getTranslation(colTranslation);
			colTranslation.sub(this.collideObjectOffset);
			colTransform.setTranslation(colTranslation);
			this.transform.set(colTransform);
		} else if (this.collideModel != null && this.collideObject == null) {
			// else, try to load it the collision model if present and we don't already have
			// a collision defined
			if (WandererConstants.ASSET_MANAGER.isLoaded(this.collideModel)) {
				this.loadCollideModel();
			} else {
				WandererConstants.ASSET_MANAGER.load(this.collideModel, Model.class);
			}
		}
	}

	public void loadCollideModel() {
		if (this.exactCollideModel) {
			this.loadCollideModelMesh();
		} else {
			this.loadCollideModelConvex();
		}
		this.updateTransform();
	}

	private void loadCollideModelMesh() {
		final Model model = WandererConstants.ASSET_MANAGER.get(this.collideModel, Model.class);

		this.setCollideObject(
				new btRigidBody(this.mass, this.createMotionState(), new btBvhTriangleMeshShape(model.meshParts)));
	}

	private void loadCollideModelConvex() {
		final Model model = WandererConstants.ASSET_MANAGER.get(this.collideModel, Model.class);
		final Mesh mesh = model.meshes.get(0);
		this.setCollideObject(new btRigidBody(this.mass, this.createMotionState(),
				new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize())));
	}

	public void enterBtWorld(final btDiscreteDynamicsWorld world) {
		if (this.getCollideObject() != null) {
			world.addRigidBody(this.getCollideObject(), this.collisionGroup, this.collisionMask);
		}
	}

	public void leaveBtWorld(final btDiscreteDynamicsWorld world) {
		if (this.getCollideObject() != null) {
			world.removeRigidBody(this.getCollideObject());
		}
	}

	protected btMotionState createMotionState() {
		return new btDefaultMotionState(this.transform);
	}

	public void render(final ModelBatch batch, final Environment env) {
		for (final DisplayModel m : this.models) {
			m.render(batch, env);
		}
	}

	public String getCollideModel() {
		return this.collideModel;
	}

	public void setCollideModel(final String collideModel) {
		this.collideModel = collideModel;
		this.collideObject = null; // trigger collide model reload
	}

	public btRigidBody getCollideObject() {
		return this.collideObject;
	}

	public void setCollideObject(final btRigidBody collideObject) {
		// https://pybullet.org/Bullet/BulletFull/btDiscreteDynamicsWorld_8cpp_source.html#l00579
		final boolean isDynamic = !collideObject.isStaticObject() && !collideObject.isKinematicObject();
		final int collisionFilterGroup = isDynamic ? btBroadphaseProxy.CollisionFilterGroups.DefaultFilter
				: btBroadphaseProxy.CollisionFilterGroups.StaticFilter;
		final int collisionFilterMask = isDynamic ? btBroadphaseProxy.CollisionFilterGroups.AllFilter
				: btBroadphaseProxy.CollisionFilterGroups.AllFilter
						^ btBroadphaseProxy.CollisionFilterGroups.StaticFilter;
		this.setCollideObject(collideObject, collisionFilterGroup, collisionFilterMask);
	}

	/**
	 * Sets this entity's collision model, removing the previous one from the Bullet
	 * world, and adding the new one.
	 *
	 * @param collideObject
	 */
	public void setCollideObject(final btRigidBody collideObject, final int collisionGroup, final int collisionMask) {
		this.collideObject = collideObject;
		if (this.collideObject != null) {
			this.game.getBtWorld().removeRigidBody(this.collideObject);
			this.game.getBtWorld().addRigidBody(collideObject, collisionGroup, collisionMask);
		}
	}

	public float getMass() {
		return this.mass;
	}

	public void setMass(final float mass) {
		this.mass = mass;
		if (this.collideObject != null) {
			this.collideObject.setMassProps(mass, new Vector3()); // inertia doesn't change if vector is (0,0,0)
		}
	}

	protected void updateTransform() {
		if (this.collideObject != null) {
			if (this.collideObject.isStaticObject()) {
				this.collideObject.setWorldTransform(this.transform);
			} else {
				this.collideObject.getMotionState().setWorldTransform(this.transform);
			}
		}

		for (final DisplayModel m : this.models) {
			m.updateTransform(this.transform);
		}
	}

	public void addModel(final DisplayModel value) {
		this.models.add(value);
	}

	public boolean removeModel(final DisplayModel value) {
		return this.models.removeValue(value, true);
	}

	public DisplayModel removeModel(final int index) {
		return this.models.removeIndex(index);
	}

	public void clearModels() {
		this.models.clear();
	}

	public Array<DisplayModel> getModels() {
		return this.models;
	}

	public Matrix4 getTransform() {
		return this.transform;
	}

	public void setTransform(final Matrix4 transform) {
		Objects.nonNull(transform);
		this.transform = transform;
		this.updateTransform();
	}

	/**
	 * @param vector
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#setTranslation(com.badlogic.gdx.math.Vector3)
	 */
	public void setTranslation(final Vector3 vector) {
		this.transform.setTranslation(vector);
		this.updateTransform();
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#setTranslation(float, float, float)
	 */
	public void setTranslation(final float x, final float y, final float z) {
		this.transform.setTranslation(x, y, z);
		this.updateTransform();
	}

	/**
	 * @param scale
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#scl(com.badlogic.gdx.math.Vector3)
	 */
	public void scl(final Vector3 scale) {
		this.transform.scl(scale);
		this.updateTransform();
	}

	/**
	 * @param axis
	 * @param degrees
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#rotate(com.badlogic.gdx.math.Vector3,
	 *      float)
	 */
	public void rotate(final Vector3 axis, final float degrees) {
		this.transform.rotate(axis, degrees);
		this.updateTransform();
	}

	/**
	 * @param rotation
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#rotate(com.badlogic.gdx.math.Quaternion)
	 */
	public void rotate(final Quaternion rotation) {
		this.transform.rotate(rotation);
		this.updateTransform();
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#rotate(com.badlogic.gdx.math.Vector3,
	 *      com.badlogic.gdx.math.Vector3)
	 */
	public void rotate(final Vector3 v1, final Vector3 v2) {
		this.transform.rotate(v1, v2);
		this.updateTransform();
	}

	/**
	 * @param scaleX
	 * @param scaleY
	 * @param scaleZ
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#scale(float, float, float)
	 */
	public void scale(final float scaleX, final float scaleY, final float scaleZ) {
		this.transform.scale(scaleX, scaleY, scaleZ);
		this.updateTransform();
	}

	/**
	 * @param acceleration
	 * @see com.badlogic.gdx.physics.bullet.dynamics.btRigidBody#setGravity(com.badlogic.gdx.math.Vector3)
	 */
	public void setGravity(final Vector3 acceleration) {
		if (this.collideObject == null) {
			return;
		}
		this.collideObject.setGravity(acceleration);
	}

	/**
	 * @param force
	 * @param rel_pos
	 * @see com.badlogic.gdx.physics.bullet.dynamics.btRigidBody#applyForce(com.badlogic.gdx.math.Vector3,
	 *      com.badlogic.gdx.math.Vector3)
	 */
	public void applyForce(final Vector3 force, final Vector3 rel_pos) {
		if (this.collideObject == null) {
			return;
		}
		this.collideObject.applyForce(force, rel_pos);
	}

	/**
	 * @return the exactCollideModel
	 */
	public boolean isExactCollideModel() {
		return this.exactCollideModel;
	}

	/**
	 * @param exactCollideModel the exactCollideModel to set
	 */
	public void setExactCollideModel(final boolean exactCollideModel) {
		this.exactCollideModel = exactCollideModel;
	}

	/**
	 * @return the collideObjectOffset
	 */
	public Vector3 getCollideObjectOffset() {
		return this.collideObjectOffset;
	}

	/**
	 * @param collideObjectOffset the collideObjectOffset to set
	 */
	public void setCollideObjectOffset(final Vector3 collideObjectOffset) {
		this.collideObjectOffset.set(collideObjectOffset);
	}

	public int getCollisionGroup() {
		return this.collisionGroup;
	}

	public void setCollisionGroup(final int collisionGroup) {
		this.collisionGroup = collisionGroup;
	}

	public int getCollisionMask() {
		return this.collisionMask;
	}

	public void setCollisionMask(final int collisionMask) {
		this.collisionMask = collisionMask;
	}

	@Override
	public void dispose() {
		if (this.collideObject != null) {
			Gdx.app.postRunnable(() -> this.collideObject.dispose());
		}
	}

	@Override
	public String toString() {
		return "E:" + super.toString();
	}
}
