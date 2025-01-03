package me.vinceh121.wanderer.entity;

import java.util.LinkedList;
import java.util.Objects;
import java.util.function.Consumer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btDefaultMotionState;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.ID;
import me.vinceh121.wanderer.ISaveable;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.event.Event;
import me.vinceh121.wanderer.event.EventDispatcher;
import me.vinceh121.wanderer.event.IEventListener;
import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;
import me.vinceh121.wanderer.util.MathUtilsW;

public abstract class AbstractEntity implements Disposable, ISaveable {
	protected final Wanderer game;
	protected final EventDispatcher eventDispatcher = new EventDispatcher();
	private final Vector3 collideObjectOffset = new Vector3();
	private final Array<DisplayModel> models = new Array<>();
	private final Array<ParticleEmitter> particles = new Array<>();
	private final Array<SoundEmitter3D> soundEmitters = new Array<>();
	private final Matrix4 transform = new Matrix4();
	// note: ID can't be final to be able to set explicit values when loading a save
	private ID id = new ID();
	private boolean disposed;
	private String collideModel, symbolicName;
	private btRigidBody collideObject;
	private float mass;
	private boolean exactCollideModel = true, invisible = false, castShadow = true;
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
			this.setTransform(colTransform);
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
		this.getCollideObject().setUserIndex(this.getId().getValue());

		final Vector3 inertia = new Vector3();
		this.getCollideObject().getCollisionShape().calculateLocalInertia(this.getMass(), inertia);
		this.getCollideObject().setMassProps(this.getMass(), inertia);

		this.updateTransform();
		this.eventDispatcher.dispatchEvent(new Event("collideModelLoaded"));
	}

	private void loadCollideModelMesh() {
		final Model model = WandererConstants.ASSET_MANAGER.get(this.collideModel, Model.class);

		this.setCollideObject(
				new btRigidBody(this.mass, this.createMotionState(), new btBvhTriangleMeshShape(model.meshParts)));
	}

	private void loadCollideModelConvex() {
		final Model model = WandererConstants.ASSET_MANAGER.get(this.collideModel, Model.class);
		final Mesh mesh = model.meshes.get(0);
		this.setCollideObject(new btRigidBody(this.mass,
				this.createMotionState(),
				new btConvexHullShape(mesh.getVerticesBuffer(false), mesh.getNumVertices(), mesh.getVertexSize())));
	}

	public void enterBtWorld(final btDiscreteDynamicsWorld world) {
		if (this.getCollideObject() != null) {
			world.addRigidBody(this.getCollideObject(), this.collisionGroup, this.collisionMask);
		}
	}

	public void leaveBtWorld(final btDiscreteDynamicsWorld world) {
		if (this.getCollideObject() != null && !this.collideObject.isDisposed()) {
			world.removeRigidBody(this.getCollideObject());
		}
	}

	protected btMotionState createMotionState() {
		return new btDefaultMotionState(this.transform);
	}

	public void tick(final float delta) {
	}

	public void render(final ModelBatch batch, final Environment env) {
		if (this.invisible) {
			return;
		}
		for (final DisplayModel m : this.models) {
			if (m.getCacheDisplayModel() != null) {
				m.getCacheDisplayModel().userData = m.getDisplayModel();
			}
			m.render(batch, env);
		}
		for (final ParticleEmitter e : this.particles) {
			e.updateLoading();
		}
		for (final SoundEmitter3D e : this.soundEmitters) {
			final Vector3 relPos = e.getRelativePosition().cpy();
			relPos.mul(this.getTransform());
			e.setPosition(relPos);

			if (this.game.isAudioEmittersDebug()) {
				final ModelInstance ins = new ModelInstance(WandererConstants.getAudioDebug(), relPos);
				ins.transform.scl(5);
				batch.render(ins);
			}
		}
	}

	public void animateParts(final String animationChannel, final Consumer<Matrix4> transformation) {
		for (final DisplayModel roots : this.getModels()) {
			if (animationChannel.equals(roots.getAnimationChannel())) {
				transformation.accept(roots.getRelativeTransform());
				roots.updateTransform(this.getTransform());
			}

			this.animatePartsRecurse(animationChannel, transformation, roots);
		}
	}

	private void animatePartsRecurse(final String animationChannel, final Consumer<Matrix4> transformation,
			final DisplayModel parent) {
		for (final DisplayModel m : parent.getChildren()) {
			if (animationChannel.equals(m.getAnimationChannel())) {
				transformation.accept(m.getRelativeTransform());
				m.updateTransform(parent.getAbsoluteTransform());
			}

			this.animatePartsRecurse(animationChannel, transformation, m);
		}
	}

	public boolean isInvisible() {
		return this.invisible;
	}

	public void setInvisible(final boolean invisible) {
		this.invisible = invisible;
	}

	public boolean isCastShadow() {
		return this.castShadow && !this.invisible;
	}

	public void setCastShadow(final boolean castShadow) {
		this.castShadow = castShadow;
	}

	public String getCollideModel() {
		return this.collideModel;
	}

	public void setCollideModel(final String collideModel) {
		this.collideModel = collideModel;
		this.collideObject = null; // trigger collide model reload
	}

	public String getSymbolicName() {
		return this.symbolicName;
	}

	public void setSymbolicName(final String symbolicName) {
		this.symbolicName = symbolicName;
	}

	public btRigidBody getCollideObject() {
		return this.collideObject;
	}

	public void setCollideObject(final btRigidBody collideObject) {
		collideObject.setUserIndex(this.getId().getValue());
		this.setCollideObject(collideObject, this.collisionGroup, this.collisionMask);
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
		this.game.getBtWorld().addRigidBody(collideObject, collisionGroup, collisionMask);
		this.collideObject = collideObject;
	}

	public void removeCollideObject() {
		this.game.getBtWorld().removeRigidBody(this.collideObject);
		this.collideObject = null;
	}

	public void disposeCollideObject() {
		this.game.getBtWorld().removeRigidBody(this.collideObject);
		this.collideObject.dispose();
		this.collideObject = null;
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
		for (final ParticleEmitter p : this.particles) {
			p.updateTransform(this.transform);
		}
	}

	public Vector3 getMidPoint() {
		if (this.collideObject != null) {
			return this.collideObject.getCenterOfMassPosition();
		}

		return this.getTranslation();
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

	public LinkedList<DisplayModel> getFlatModels() {
		return DisplayModel.flattenModels(this.models);
	}

	public Array<SoundEmitter3D> getSoundEmitters() {
		return this.soundEmitters;
	}

	public void addSoundEmitter(final SoundEmitter3D emitter) {
		this.soundEmitters.add(emitter);
	}

	public Matrix4 getTransform() {
		return this.transform;
	}

	public Vector3 getTranslation() {
		return this.getTransform().getTranslation(new Vector3());
	}

	public Quaternion getRotation() {
		return this.getTransform().getRotation(new Quaternion());
	}

	public Vector3 getScale() {
		return this.getTransform().getScale(new Vector3());
	}

	public void setTransform(final Matrix4 transform) {
		Objects.nonNull(transform);
		this.transform.set(transform);
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
	public void scale(final Vector3 scale) {
		this.transform.scl(scale);
		this.updateTransform();
	}

	public void scale(final float scale) {
		this.transform.scl(scale);
		this.updateTransform();
	}

	public void translate(final Vector3 translation) {
		this.transform.translate(translation);
		this.updateTransform();
	}

	public void translate(final float x, final float y, final float z) {
		this.transform.translate(x, y, z);
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

	public void rotateRad(final Vector3 axis, final float radians) {
		this.transform.rotateRad(axis, radians);
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
	 * @param yaw The Euler yaw in radians
	 */
	public void setYaw(float yaw) {
		Quaternion rotation = this.getRotation();
		float pitch = rotation.getPitchRad();
		float roll = rotation.getRollRad();

		Quaternion newRot = new Quaternion().setEulerAnglesRad(yaw, pitch, roll);
		MathUtilsW.setRotation(transform, newRot);
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

	public void addParticle(final ParticleEmitter value) {
		this.particles.add(value);
	}

	public Array<ParticleEmitter> getParticles() {
		return this.particles;
	}

	public void addEventListener(final String type, final IEventListener l) {
		this.eventDispatcher.addEventListener(type, l);
	}

	public void removeEventListener(final String type, final IEventListener l) {
		this.eventDispatcher.removeEventListener(type, l);
	}

	@Override
	public void writeState(final ObjectNode node) {
		node.put("@class", this.getClass().getCanonicalName());
		node.put("id", this.id.getValue());
		node.putPOJO("transform", this.getTransform());
		node.put("mass", this.getMass());
	}

	@Override
	public void readState(final ObjectNode node) {
		assert this.getClass().getCanonicalName().equals(node.get("@class").asText());
		if (node.has("id")) {
			this.id = new ID(node.required("id").asInt());
		}
		if (node.has("transform")) {
			this.setTransform(WandererConstants.MAPPER.convertValue(node.get("transform"), Matrix4.class));
		}
		if (node.has("mass")) {
			this.setMass(node.get("mass").floatValue());
		}
		if (node.has("symbolicName")) {
			this.setSymbolicName(node.required("symbolicName").asText());
		}
	}

	public boolean isDisposed() {
		return this.disposed;
	}

	public ID getId() {
		return this.id;
	}

	@Override
	public void dispose() {
		this.disposed = true;

		for (final ParticleEmitter e : this.particles) {
			this.game.getGraphicsManager().removeParticle(e);
			e.dispose();
		}
		this.particles.clear();

		for (final SoundEmitter3D e : this.soundEmitters) {
			e.dispose();
		}
		this.soundEmitters.clear();

		this.leaveBtWorld(this.game.getBtWorld());
		if (this.collideObject != null) {
			Gdx.app.postRunnable(() -> this.collideObject.dispose());
		}
	}

	@Override
	public String toString() {
		return "E:" + super.toString();
	}
}
