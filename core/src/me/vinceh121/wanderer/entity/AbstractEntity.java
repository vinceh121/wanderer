package me.vinceh121.wanderer.entity;

import java.util.Objects;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btTriangleMesh;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Disposable;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;

public abstract class AbstractEntity implements Disposable {
	protected final Wanderer game;
	private Matrix4 transform = new Matrix4();
	private Vector3 collideObjectOffset = new Vector3();
	private String displayModel, collideModel, displayTexture;
	private ModelInstance cacheDisplayModel;
	private btRigidBody collideObject;
	private float mass;
	private boolean exactCollideModel;

	public AbstractEntity(Wanderer game) {
		this.game = game;
	}

	public void updatePhysics(btDiscreteDynamicsWorld world) {
		if (collideObject != null) { // if we have a (manually-)defined body, copy transform
			// subtract collision offset from bullet's transform
			Matrix4 colTransform = collideObject.getWorldTransform();
			Vector3 colTranslation = new Vector3();
			colTransform.getTranslation(colTranslation);
			colTranslation.sub(collideObjectOffset);
			colTransform.setTranslation(colTranslation);
			this.transform.set(colTransform);
		} else if (this.collideModel != null) { // else, try to load it the collision model if present
			if (WandererConstants.ASSET_MANAGER.isLoaded(collideModel)) {
				this.loadCollideModel();
			} else {
				WandererConstants.ASSET_MANAGER.load(collideModel, Model.class);
			}
		}
	}

	public void loadCollideModel() {
		if (this.exactCollideModel) {
			this.loadCollideModelMesh();
		} else {
			this.loadCollideModelConvex();
		}
	}

	private void loadCollideModelMesh() {
		Model model = WandererConstants.ASSET_MANAGER.get(collideModel, Model.class);

		btTriangleMesh btmesh = new btTriangleMesh();
		btmesh.addMeshParts(model.meshParts);

		this.setCollideObject(new btRigidBody(mass, createMotionState(), new btBvhTriangleMeshShape(model.meshParts)));
	}

	private void loadCollideModelConvex() {
		Model model = WandererConstants.ASSET_MANAGER.get(collideModel, Model.class);
		Mesh mesh = model.meshes.get(0);
		this.setCollideObject(new btRigidBody(mass, createMotionState(),
				new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize())));
	}

	protected btMotionState createMotionState() {
		return new btDefaultMotionState(transform);
	}

	public void render(ModelBatch batch, Environment env) {
		if (this.displayModel == null)
			return;

		if (getCacheDisplayModel() != null) {
			batch.render(getCacheDisplayModel(), env);
		} else {
			if (WandererConstants.ASSET_MANAGER.isLoaded(getDisplayModel())
					&& (this.displayTexture == null || WandererConstants.ASSET_MANAGER.isLoaded(displayTexture))) {
				this.loadDisplayModel();
				batch.render(getCacheDisplayModel(), env);
			} else {
				WandererConstants.ASSET_MANAGER.load(getDisplayModel(), Model.class);
				if (displayTexture != null)
					WandererConstants.ASSET_MANAGER.load(displayTexture, Texture.class);
			}
		}
	}

	public void loadDisplayModel() {
		Model model = WandererConstants.ASSET_MANAGER.get(getDisplayModel(), Model.class);
		Texture texture = WandererConstants.ASSET_MANAGER.get(displayTexture, Texture.class);
		ModelInstance instance = new ModelInstance(model);
		if (this.displayTexture != null)
			instance.materials.get(0).set(TextureAttribute.createDiffuse(texture));
		setCacheDisplayModel(instance);
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
		this.collideObject = null; // trigger collide model reload
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

	/**
	 * Sets this entity's collision model, removing the previous one from the Bullet
	 * world, and adding the new one.
	 *
	 * @param collideObject
	 */
	public void setCollideObject(btRigidBody collideObject) {
		if (this.collideObject != null)
			this.game.getBtWorld().removeRigidBody(this.collideObject);
		this.collideObject = collideObject;
		this.game.getBtWorld().addRigidBody(collideObject);
	}

	public float getMass() {
		return mass;
	}

	public void setMass(float mass) {
		this.mass = mass;
		this.collideObject.setMassProps(mass, new Vector3()); // inertia doesn't change if vector is (0,0,0)
	}

	protected void updateTransform() {
		if (this.collideObject != null)
			this.collideObject.setWorldTransform(this.transform);
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

	/**
	 * @return the exactCollideModel
	 */
	public boolean isExactCollideModel() {
		return exactCollideModel;
	}

	/**
	 * @param exactCollideModel the exactCollideModel to set
	 */
	public void setExactCollideModel(boolean exactCollideModel) {
		this.exactCollideModel = exactCollideModel;
	}

	/**
	 * @return the displayTexture
	 */
	public String getDisplayTexture() {
		return displayTexture;
	}

	/**
	 * @param displayTexture the displayTexture to set
	 */
	public void setDisplayTexture(String displayTexture) {
		this.displayTexture = displayTexture;
	}

	/**
	 * @return the collideObjectOffset
	 */
	public Vector3 getCollideObjectOffset() {
		return collideObjectOffset;
	}

	/**
	 * @param collideObjectOffset the collideObjectOffset to set
	 */
	public void setCollideObjectOffset(Vector3 collideObjectOffset) {
		this.collideObjectOffset = collideObjectOffset;
	}

	@Override
	public void dispose() {
		if (this.collideObject != null)
			this.collideObject.dispose();
	}
}
