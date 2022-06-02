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
		for (DisplayModel m : meta.getDisplayModels()) {
			this.getModels().add(new DisplayModel(m)); // need to clone display models
		}
		
		this.interactZone = new btGhostObject();
		this.interactZone
				.setCollisionShape(new btCapsuleShape(meta.getInteractZoneRadius(), meta.getInteractZoneHeight()));
		this.interactZone.setCollisionFlags(CollisionFlags.CF_NO_CONTACT_RESPONSE);
		this.interactListener = new ContactListenerAdapter() {
			@Override
			public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
				onInteractContact(colObj0, colObj1);
			}

			@Override
			public void onContactEnded(btCollisionObject colObj0, btCollisionObject colObj1) {
				onInteractStop(colObj0, colObj1);
			}
		};
		this.game.getPhysicsManager().addContactListener(interactListener);
	}

	protected void onInteractContact(btCollisionObject colObj0, btCollisionObject colObj1) {
		// do not interact if we aren't controlling a character
		if (!(game.getControlledEntity() instanceof CharacterW)) {
			return;
		}
		CharacterW chara = (CharacterW) game.getControlledEntity();

		// if collided objects are interaction zone and player character
		if ((colObj0.getCPointer() == interactZone.getCPointer()
				&& colObj1.getCPointer() == chara.getGhostObject().getCPointer())
				|| (colObj1.getCPointer() == interactZone.getCPointer()
						&& colObj0.getCPointer() == chara.getGhostObject().getCPointer())) {
			game.enterInteractBuilding(AbstractBuilding.this);
		}
	}

	protected void onInteractStop(btCollisionObject colObj0, btCollisionObject colObj1) {
		// do not interact if we aren't controlling a character
		if (!(game.getControlledEntity() instanceof CharacterW)) {
			return;
		}
		CharacterW chara = (CharacterW) game.getControlledEntity();

		// if collided objects are interaction zone and player character
		if ((colObj0.getCPointer() == interactZone.getCPointer()
				&& colObj1.getCPointer() == chara.getGhostObject().getCPointer())
				|| (colObj1.getCPointer() == interactZone.getCPointer()
						&& colObj0.getCPointer() == chara.getGhostObject().getCPointer())) {
			game.removeInteractBuilding();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
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
		return interactZone;
	}

	public IContactListener getInteractListener() {
		return interactListener;
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
	public void enterBtWorld(btDiscreteDynamicsWorld world) {
		super.enterBtWorld(world);
		world.addCollisionObject(this.interactZone, CollisionFilterGroups.SensorTrigger,
				CollisionFilterGroups.CharacterFilter);
	}

	@Override
	public void leaveBtWorld(btDiscreteDynamicsWorld world) {
		super.leaveBtWorld(world);
		world.removeCollisionObject(this.interactZone);
	}

	@Override
	protected void updateTransform() {
		super.updateTransform();
		this.interactZone.setWorldTransform(getTransform());
	}

	@Override
	public void dispose() {
		if (this.island != null) {
			this.island.removeBuilding(this);
		}
		this.game.getPhysicsManager().removeContactListener(interactListener);
		Gdx.app.postRunnable(() -> this.interactZone.dispose());
		super.dispose();
	}
}
