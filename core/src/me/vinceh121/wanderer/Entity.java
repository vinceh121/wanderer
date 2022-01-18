package me.vinceh121.wanderer;

import java.util.Objects;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;

public class Entity {
	private Matrix4 transform = new Matrix4();
	private String displayModel, collideModel;
	private ModelInstance cacheDisplayModel;
	private btRigidBody collideObject;
	private float mass;

	public void updatePhysics(btDiscreteDynamicsWorld world) {
		if (this.collideModel == null)
			return;

		if (collideObject != null) {
			this.transform.set(collideObject.getWorldTransform());
			System.out.println(transform);
		} else {
			if (WandererConstants.ASSET_MANAGER.isLoaded(collideModel)) {
				this.loadCollideModel();
				world.addRigidBody(collideObject);
			} else {
				WandererConstants.ASSET_MANAGER.load(collideModel, Model.class);
			}
		}
	}

	public void loadCollideModel() {
		Model model = WandererConstants.ASSET_MANAGER.get(collideModel, Model.class);
		btCollisionShape shape = Bullet.obtainStaticNodeShape(model.nodes);
		this.collideObject = new btRigidBody(mass, new btDefaultMotionState(), shape);
		this.collideObject.setWorldTransform(transform);
	}

	public void render(ModelBatch batch, Environment env) {
		if (this.displayModel == null)
			return;

		if (getCacheDisplayModel() != null) {
			batch.render(getCacheDisplayModel(), env);
		} else {
			if (WandererConstants.ASSET_MANAGER.isLoaded(getDisplayModel())) {
				this.loadDisplayModel();
				batch.render(getCacheDisplayModel(), env);
			} else {
				WandererConstants.ASSET_MANAGER.load(getDisplayModel(), Model.class);
			}
		}
	}

	public void loadDisplayModel() {
		setCacheDisplayModel(new ModelInstance(WandererConstants.ASSET_MANAGER.get(getDisplayModel(), Model.class)));
		getCacheDisplayModel().transform = transform;
	}

	public String getDisplayModel() {
		return displayModel;
	}

	public void setDisplayModel(String displayModel) {
		this.displayModel = displayModel;
	}

	public String getCollideModel() {
		return collideModel;
	}

	public void setCollideModel(String collideModel) {
		this.collideModel = collideModel;
	}

	public ModelInstance getCacheDisplayModel() {
		return cacheDisplayModel;
	}

	public void setCacheDisplayModel(ModelInstance cacheDisplayModel) {
		this.cacheDisplayModel = cacheDisplayModel;
	}

	public btRigidBody getCollideObject() {
		return collideObject;
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
	}

	private void updateTransform() {
		if (this.collideObject != null)
			this.collideObject.getWorldTransform(transform);
		if (this.cacheDisplayModel != null)
			this.cacheDisplayModel.transform = this.transform;
	}

	public Matrix4 getTransform() {
		return transform;
	}

	public void setTransform(Matrix4 transform) {
		Objects.nonNull(transform);
		this.transform = transform;
		this.updateTransform();
	}

	/**
	 * @param vector
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#setTranslation(com.badlogic.gdx.math.Vector3)
	 */
	public void setTranslation(Vector3 vector) {
		transform.setTranslation(vector);
		this.updateTransform();
	}

	/**
	 * @param x
	 * @param y
	 * @param z
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#setTranslation(float, float, float)
	 */
	public void setTranslation(float x, float y, float z) {
		transform.setTranslation(x, y, z);
		this.updateTransform();
	}

	/**
	 * @param scale
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#scl(com.badlogic.gdx.math.Vector3)
	 */
	public void scl(Vector3 scale) {
		transform.scl(scale);
		this.updateTransform();
	}

	/**
	 * @param axis
	 * @param degrees
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#rotate(com.badlogic.gdx.math.Vector3,
	 *      float)
	 */
	public void rotate(Vector3 axis, float degrees) {
		transform.rotate(axis, degrees);
		this.updateTransform();
	}

	/**
	 * @param rotation
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#rotate(com.badlogic.gdx.math.Quaternion)
	 */
	public void rotate(Quaternion rotation) {
		transform.rotate(rotation);
		this.updateTransform();
	}

	/**
	 * @param v1
	 * @param v2
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#rotate(com.badlogic.gdx.math.Vector3,
	 *      com.badlogic.gdx.math.Vector3)
	 */
	public void rotate(Vector3 v1, Vector3 v2) {
		transform.rotate(v1, v2);
		this.updateTransform();
	}

	/**
	 * @param scaleX
	 * @param scaleY
	 * @param scaleZ
	 * @return
	 * @see com.badlogic.gdx.math.Matrix4#scale(float, float, float)
	 */
	public void scale(float scaleX, float scaleY, float scaleZ) {
		transform.scale(scaleX, scaleY, scaleZ);
		this.updateTransform();
	}

	/**
	 * @param acceleration
	 * @see com.badlogic.gdx.physics.bullet.dynamics.btRigidBody#setGravity(com.badlogic.gdx.math.Vector3)
	 */
	public void setGravity(Vector3 acceleration) {
		if (collideObject == null)
			return;
		collideObject.setGravity(acceleration);
	}

	/**
	 * @param force
	 * @param rel_pos
	 * @see com.badlogic.gdx.physics.bullet.dynamics.btRigidBody#applyForce(com.badlogic.gdx.math.Vector3,
	 *      com.badlogic.gdx.math.Vector3)
	 */
	public void applyForce(Vector3 force, Vector3 rel_pos) {
		if (collideObject == null)
			return;
		collideObject.applyForce(force, rel_pos);
	}
}
