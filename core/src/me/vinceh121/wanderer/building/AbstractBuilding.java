package me.vinceh121.wanderer.building;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.collision.CollisionConstants;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.PrototypeRegistry;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.entity.AbstractClanLivingEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.phys.ContactListenerAdapter;
import me.vinceh121.wanderer.phys.IContactListener;

public abstract class AbstractBuilding extends AbstractClanLivingEntity {
	private final btGhostObject interactZone;
	private final IContactListener interactListener;
	private final AbstractBuildingPrototype prototype;
	private final Array<DisplayModel> explosionParts = new Array<>();
	private String controlMessage;
	private Island island;
	private Slot slot;

	public AbstractBuilding(final Wanderer game, final AbstractBuildingPrototype prototype) {
		super(game);
		this.prototype = prototype;

		this.setCollideModel(prototype.getCollisionModel());
		for (final DisplayModel m : prototype.getDisplayModels()) {
			this.getModels().add(new DisplayModel(m)); // need to clone display models
		}

		for (final DisplayModel m : prototype.getExplosionParts()) {
			this.explosionParts.add(new DisplayModel(m));
		}

		this.interactZone = new btGhostObject();
		this.interactZone.setCollisionShape(
				new btCapsuleShape(prototype.getInteractZoneRadius(), prototype.getInteractZoneHeight()));
		this.interactZone.setCollisionFlags(CollisionFlags.CF_NO_CONTACT_RESPONSE);
		this.interactListener = new ContactListenerAdapter() {
			@Override
			public void onContactStarted(final btCollisionObject colObj0, final btCollisionObject colObj1) {
				AbstractBuilding.this.onInteractContact(colObj0, colObj1);
			}

			@Override
			public void onContactEnded(final btCollisionObject colObj0, final btCollisionObject colObj1) {
				AbstractBuilding.this.onInteractStop(colObj0, colObj1);
			}
		};
		this.game.getPhysicsManager().addContactListener(this.interactListener);
	}

	protected void onInteractContact(final btCollisionObject colObj0, final btCollisionObject colObj1) {
		// do not interact if we aren't controlling a character
		if (!(this.game.getControlledEntity() instanceof CharacterW)) {
			return;
		}

		final CharacterW chara = (CharacterW) this.game.getControlledEntity();

		// if collided objects are interaction zone and player character
		if (colObj0.getCPointer() == this.interactZone.getCPointer()
				&& colObj1.getCPointer() == chara.getGhostObject().getCPointer()
				|| colObj1.getCPointer() == this.interactZone.getCPointer()
						&& colObj0.getCPointer() == chara.getGhostObject().getCPointer()) {
			this.game.enterInteractBuilding(AbstractBuilding.this);
		}
	}

	protected void onInteractStop(final btCollisionObject colObj0, final btCollisionObject colObj1) {
		// do not interact if we aren't controlling a character
		if (!(this.game.getControlledEntity() instanceof CharacterW)) {
			return;
		}

		final CharacterW chara = (CharacterW) this.game.getControlledEntity();

		// if collided objects are interaction zone and player character
		if (colObj0.getCPointer() == this.interactZone.getCPointer()
				&& colObj1.getCPointer() == chara.getGhostObject().getCPointer()
				|| colObj1.getCPointer() == this.interactZone.getCPointer()
						&& colObj0.getCPointer() == chara.getGhostObject().getCPointer()) {
			this.game.removeInteractBuilding();
		}
	}

	@Override
	public void onDeath() {
		this.game.removeEntity(this);
		this.dispose();

		for (final DisplayModel m : this.explosionParts) {
			final ExplosionPart part = new ExplosionPart(this.game, m);
			part.translate(this.getTranslation());
			part.addEventListener("collideModelLoaded", e -> part.thrust(10));
			this.game.addEntity(part);
		}
	}

	public String getControlMessage() {
		return this.controlMessage;
	}

	public void setControlMessage(final String controlMessage) {
		this.controlMessage = controlMessage;
	}

	/**
	 * @return the island
	 */
	public Island getIsland() {
		return this.island;
	}

	/**
	 * Do not call direct, use {@link Island#addBuilding(AbstractBuilding, Slot)}
	 *
	 * @param island the island to set
	 */
	public void setIsland(final Island island) {
		this.island = island;
	}

	/**
	 * @return the slot
	 */
	public Slot getSlot() {
		return this.slot;
	}

	/**
	 * Do not call direct, use {@link Island#addBuilding(AbstractBuilding, Slot)}
	 *
	 * @param slot the slot to set
	 */
	public void setSlot(final Slot slot) {
		this.slot = slot;
	}

	@JsonIgnore
	public btGhostObject getInteractZone() {
		return this.interactZone;
	}

	@JsonIgnore
	public IContactListener getInteractListener() {
		return this.interactListener;
	}

	@Override
	public void loadCollideModel() {
		super.loadCollideModel();
		this.getCollideObject().setCollisionFlags(CollisionFlags.CF_KINEMATIC_OBJECT);
		this.getCollideObject().forceActivationState(CollisionConstants.DISABLE_DEACTIVATION);
	}

	@Override
	public void enterBtWorld(final btDiscreteDynamicsWorld world) {
		world.addCollisionObject(this.interactZone,
				CollisionFilterGroups.SensorTrigger,
				CollisionFilterGroups.CharacterFilter);
	}

	@Override
	public void leaveBtWorld(final btDiscreteDynamicsWorld world) {
		super.leaveBtWorld(world);

		if (this.interactZone != null && !this.interactZone.isDisposed()) {
			world.removeCollisionObject(this.interactZone);
		}
	}

	@Override
	protected void updateTransform() {
		super.updateTransform();
		this.interactZone.setWorldTransform(this.getTransform());
	}

	public AbstractBuildingPrototype getPrototype() {
		return this.prototype;
	}

	@Override
	public void writeState(final ObjectNode node) {
		super.writeState(node);
		node.put("prototype", PrototypeRegistry.getInstance().getReverse(this.getPrototype()));
		node.put("island", this.getIsland().getId().getValue());
	}

	@Override
	public void readState(final ObjectNode node) {
		super.readState(node);
		this.setIsland((Island) this.game.getEntity(node.get("island").asInt()));
	}

	@Override
	public void dispose() {
		if (this.island != null) {
			this.island.removeBuilding(this);
		}

		this.game.getPhysicsManager().removeContactListener(this.interactListener);
		Gdx.app.postRunnable(() -> this.interactZone.dispose());
		super.dispose();
	}
}
