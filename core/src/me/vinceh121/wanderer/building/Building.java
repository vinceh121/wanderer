package me.vinceh121.wanderer.building;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractLivingEntity;
import me.vinceh121.wanderer.entity.CharacterW;
import me.vinceh121.wanderer.phys.ContactListenerAdapter;
import me.vinceh121.wanderer.phys.IContactListener;

public class Building extends AbstractLivingEntity implements IClanMember {
	private final btGhostObject interactZone;
	private final IContactListener interactListener;
	private Clan clan;
	private Island island;
	private Slot slot;

	public Building(final Wanderer game) {
		super(game);

		this.interactZone = new btGhostObject();
		this.interactZone.setCollisionShape(new btCapsuleShape(10f, 20f));
		this.interactListener = new ContactListenerAdapter() {
			@Override
			public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
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
					game.enterInteractBuilding(Building.this);
				}
			}
		};
		this.game.getPhysicsManager().addContactListener(interactListener);
	}

	/**
	 * @return the island
	 */
	public Island getIsland() {
		return this.island;
	}

	/**
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
	 * @param slot the slot to set
	 */
	public void setSlot(final Slot slot) {
		this.slot = slot;
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
	public void render(ModelBatch batch, Environment env) {
		// TODO move into Island code, run only on island move
		this.setTranslation(this.island.getTransform().getTranslation(new Vector3()).add(this.slot.getLocation()));
		super.render(batch, env);
	}

	@Override
	public void enterBtWorld(btDiscreteDynamicsWorld world) {
		super.enterBtWorld(world);
		world.addCollisionObject(this.interactZone);
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
		this.game.getPhysicsManager().removeContactListener(interactListener);
		this.interactZone.dispose();
		super.dispose();
	}
}
