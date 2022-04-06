package me.vinceh121.wanderer.entity;

import java.util.Objects;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy;
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
import me.vinceh121.wanderer.glx.TiledMaterialAttribute;

public abstract class AbstractEntity implements Disposable {
	protected final Wanderer game;
	private Matrix4 transform = new Matrix4();
	private Vector3 collideObjectOffset = new Vector3();
	private String displayModel, collideModel, displayTexture;
	private ModelInstance cacheDisplayModel;
	private btRigidBody collideObject;
	private float mass;
	private boolean exactCollideModel = true;

	public AbstractEntity(final Wanderer game) {
		this.game = game;
	}

	public void updatePhysics(final btDiscreteDynamicsWorld world) {
		if (this.collideObject != null) { // if we have a (manually-)defined body, copy transform
			// subtract collision offset from bullet's transform
			final Matrix4 colTransform = this.collideObject.getWorldTransform();
			final Vector3 colTranslation = new Vector3();
			colTransform.getTranslation(colTranslation);
			colTranslation.sub(this.collideObjectOffset);
			colTransform.setTranslation(colTranslation);
			this.transform.set(colTransform);
		} else if (this.collideModel != null) { // else, try to load it the collision model if present
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
	}

	private void loadCollideModelMesh() {
		final Model model = WandererConstants.ASSET_MANAGER.get(this.collideModel, Model.class);

		final btTriangleMesh btmesh = new btTriangleMesh();
		btmesh.addMeshParts(model.meshParts);

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
			world.addRigidBody(this.getCollideObject());
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
		if (this.displayModel == null) {
			return;
		}

		if (this.getCacheDisplayModel() != null) {
			batch.render(this.getCacheDisplayModel(), env);
		} else {
			if (WandererConstants.ASSET_MANAGER.isLoaded(this.getDisplayModel())
					&& (this.displayTexture == null || WandererConstants.ASSET_MANAGER.isLoaded(this.displayTexture))) {
				this.loadDisplayModel();
				batch.render(this.getCacheDisplayModel(), env);
			} else {
				WandererConstants.ASSET_MANAGER.load(this.getDisplayModel(), Model.class);
				if (this.displayTexture != null) {
					WandererConstants.ASSET_MANAGER.load(this.displayTexture, Texture.class);
				}
			}
		}
	}

	public void loadDisplayModel() {
		final Model model = WandererConstants.ASSET_MANAGER.get(this.getDisplayModel(), Model.class);
		final Texture texture = WandererConstants.ASSET_MANAGER.get(this.displayTexture, Texture.class);
		final ModelInstance instance = new ModelInstance(model);
		if (this.displayTexture != null) {
			texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);

			final String sandName = "orig/lib/textures/detailmap_sandnone.png";
			WandererConstants.ASSET_MANAGER.load(sandName, Texture.class);
			WandererConstants.ASSET_MANAGER.finishLoadingAsset(sandName);
			Texture sand = WandererConstants.ASSET_MANAGER.get(sandName, Texture.class);
			sand.setFilter(TextureFilter.Linear, TextureFilter.Linear);
			sand.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);

			instance.materials.get(0).set(TextureAttribute.createDiffuse(texture), TiledMaterialAttribute.create(sand, 0.7f, new Vector2(100f, 100f)));
		}
		this.setCacheDisplayModel(instance);
		this.getCacheDisplayModel().transform = this.transform;
	}

	public String getDisplayModel() {
		return this.displayModel;
	}

	public void setDisplayModel(final String displayModel) {
		this.displayModel = displayModel;
	}

	public String getCollideModel() {
		return this.collideModel;
	}

	public void setCollideModel(final String collideModel) {
		this.collideModel = collideModel;
		this.collideObject = null; // trigger collide model reload
	}

	public ModelInstance getCacheDisplayModel() {
		return this.cacheDisplayModel;
	}

	public void setCacheDisplayModel(final ModelInstance cacheDisplayModel) {
		this.cacheDisplayModel = cacheDisplayModel;
	}

	public btRigidBody getCollideObject() {
		return this.collideObject;
	}

	public void setCollideObject(final btRigidBody collideObject) {
		// https://pybullet.org/Bullet/BulletFull/btDiscreteDynamicsWorld_8cpp_source.html#l00579
		final boolean isDynamic = (!collideObject.isStaticObject() && !collideObject.isKinematicObject());
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
		if (this.collideObject != null) {
			this.game.getBtWorld().removeRigidBody(this.collideObject);
		}
		this.collideObject = collideObject;
		this.game.getBtWorld().addRigidBody(collideObject, collisionGroup, collisionMask);
	}

	public float getMass() {
		return this.mass;
	}

	public void setMass(final float mass) {
		this.mass = mass;
		this.collideObject.setMassProps(mass, new Vector3()); // inertia doesn't change if vector is (0,0,0)
	}

	protected void updateTransform() {
		if (this.collideObject != null) {
			this.collideObject.setWorldTransform(this.transform);
		}
		if (this.cacheDisplayModel != null) {
			this.cacheDisplayModel.transform = this.transform;
		}
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
	 * @return the displayTexture
	 */
	public String getDisplayTexture() {
		return this.displayTexture;
	}

	/**
	 * @param displayTexture the displayTexture to set
	 */
	public void setDisplayTexture(final String displayTexture) {
		this.displayTexture = displayTexture;
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
		this.collideObjectOffset = collideObjectOffset;
	}

	@Override
	public void dispose() {
		if (this.collideObject != null) {
			this.collideObject.dispose();
		}
	}

	@Override
	public String toString() {
		return "E:" + super.toString();
	}
}
