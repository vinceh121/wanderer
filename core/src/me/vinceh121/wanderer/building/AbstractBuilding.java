package me.vinceh121.wanderer.building;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseProxy.CollisionFilterGroups;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractLivingEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.phys.ContactListenerAdapter;
import me.vinceh121.wanderer.phys.IContactListener;

public abstract class AbstractBuilding extends AbstractLivingEntity implements IClanMember {
	private final btGhostObject interactZone;
	private final IContactListener interactListener;
	private String name;
	private Clan clan;
	private Island island;
	private Slot slot;

	public AbstractBuilding(final Wanderer game, final AbstractBuildingMeta meta) {
		super(game);

		this.setCollideModel(meta.getCollisionModel());
		for (final DisplayModel m : meta.getDisplayModels()) {
			this.getModels().add(new DisplayModel(m)); // need to clone display models
		}

		this.interactZone = new btGhostObject();
		this.interactZone
			.setCollisionShape(new btCapsuleShape(meta.getInteractZoneRadius(), meta.getInteractZoneHeight()));
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

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
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

	public btGhostObject getInteractZone() {
		return this.interactZone;
	}

	public IContactListener getInteractListener() {
		return this.interactListener;
	}

	@Override
	public void onDeath() {
		// TODO explode
	}

	@Override
	public Clan getClan() {
		return this.clan;
	}

	@Override
	public void onJoinClan(final Clan clan) {
		this.clan = clan;
		// TODO change light decal's color
	}

	@Override
	public void loadCollideModel() {
		super.loadCollideModel();
		this.getCollideObject().setCollisionFlags(CollisionFlags.CF_KINEMATIC_OBJECT);
		this.getCollideObject().forceActivationState(4); // DISABLE_DEACTIVATION
	}

	@Override
	public void enterBtWorld(final btDiscreteDynamicsWorld world, final int idx) {
		super.enterBtWorld(world, idx);
		world.addCollisionObject(this.interactZone,
				CollisionFilterGroups.SensorTrigger,
				CollisionFilterGroups.CharacterFilter);
	}

	@Override
	public void leaveBtWorld(final btDiscreteDynamicsWorld world) {
		super.leaveBtWorld(world);
		world.removeCollisionObject(this.interactZone);
	}

	@Override
	protected void updateTransform() {
		super.updateTransform();
		this.interactZone.setWorldTransform(this.getTransform());
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
