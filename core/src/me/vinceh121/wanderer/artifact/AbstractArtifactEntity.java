package me.vinceh121.wanderer.artifact;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.MetaRegistry;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.event.Event;
import me.vinceh121.wanderer.glx.NoLightningAttribute;
import me.vinceh121.wanderer.phys.ContactListenerAdapter;
import me.vinceh121.wanderer.phys.IContactListener;

public abstract class AbstractArtifactEntity extends AbstractEntity {
	private final ArtifactMeta meta;
	private final btGhostObject pickupZone;
	private final IContactListener pickupListener;
	private boolean rotate;

	public AbstractArtifactEntity(final Wanderer game, final ArtifactMeta meta) {
		super(game);
		this.meta = meta;
		this.rotate = meta.isRotate();
		this.pickupZone = new btGhostObject();
		this.pickupZone.setCollisionFlags(CollisionFlags.CF_NO_CONTACT_RESPONSE);
		this.pickupZone.setCollisionShape(new btSphereShape(meta.getPickupZoneRadius()));
		this.pickupListener = new ContactListenerAdapter() {
			@Override
			public void onContactStarted(final btCollisionObject colObj0, final btCollisionObject colObj1) {
				// do not interact if we aren't controlling a character
				// TODO if vehicle, fetch controlling character
				if (!(game.getControlledEntity() instanceof CharacterW)) {
					return;
				}
				final CharacterW chara = (CharacterW) game.getControlledEntity();

				// if collided objects are interaction zone and player character
				if (colObj0.getCPointer() == AbstractArtifactEntity.this.pickupZone.getCPointer()
						&& colObj1.getCPointer() == chara.getGhostObject().getCPointer()
						|| colObj1.getCPointer() == AbstractArtifactEntity.this.pickupZone.getCPointer()
								&& colObj0.getCPointer() == chara.getGhostObject().getCPointer()) {
					if (AbstractArtifactEntity.this.onPickUp(game, chara)) {
						AbstractArtifactEntity.this.eventDispatcher.dispatchEvent(new Event("pickedUp"));
						game.removeEntity(AbstractArtifactEntity.this);
						AbstractArtifactEntity.this.dispose();
					}
				}
			}
		};
		this.game.getPhysicsManager().addContactListener(this.pickupListener);

		if (meta.getArtifactModel() != null && meta.getArtifactTexture() != null) {
			final DisplayModel model = new DisplayModel(meta.getArtifactModel(), meta.getArtifactTexture());
			if (meta.getArtifactColor() != null) {
				model.addTextureAttribute(ColorAttribute.createEmissive(meta.getArtifactColor()));
			}
			model.addTextureAttribute(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_DST_COLOR, 0.75f));
			model.addTextureAttribute(new NoLightningAttribute());
			this.addModel(model);
		}
	}

	/**
	 * Tries to pickup the artifact for the player.
	 *
	 * @return true if the artifact is to be picked up, false otherwise
	 */
	public abstract boolean onPickUp(Wanderer game, CharacterW chara);

	@Override
	public void enterBtWorld(final btDiscreteDynamicsWorld world) {
		world.addCollisionObject(this.pickupZone,
				CollisionFilterGroups.SensorTrigger,
				CollisionFilterGroups.CharacterFilter);
	}

	@Override
	public void leaveBtWorld(final btDiscreteDynamicsWorld world) {
		super.leaveBtWorld(world);
		world.removeCollisionObject(this.pickupZone);
	}

	@Override
	public void render(final ModelBatch batch, final Environment env) {
		super.render(batch, env);
		if (this.rotate) {
			this.rotate(Vector3.Y, 180f * Gdx.graphics.getDeltaTime());
		}
	}

	@Override
	protected void updateTransform() {
		super.updateTransform();
		final Matrix4 noScaleRotation = new Matrix4(this.getTransform().getTranslation(new Vector3()).add(0, 1, 0),
				new Quaternion(),
				new Vector3(1, 1, 1));
		this.pickupZone.setWorldTransform(noScaleRotation);
	}

	public ArtifactMeta getMeta() {
		return this.meta;
	}

	@JsonIgnore
	public btGhostObject getPickupZone() {
		return this.pickupZone;
	}

	@JsonIgnore
	public IContactListener getPickupListener() {
		return this.pickupListener;
	}

	public boolean isRotate() {
		return this.rotate;
	}

	public void setRotate(final boolean rotate) {
		this.rotate = rotate;
	}

	@Override
	public void writeState(final ObjectNode node) {
		super.writeState(node);
		node.put("meta", MetaRegistry.getInstance().getReverse(this.meta));
	}

	@Override
	public void dispose() {
		this.game.getPhysicsManager().removeContactListener(this.pickupListener);
		this.game.getBtWorld().removeCollisionObject(this.pickupZone);
		// hack to dispose of the zone next tick as it is still referenced in the
		// current one
		Gdx.app.postRunnable(() -> this.pickupZone.dispose());
		super.dispose();
	}
}
