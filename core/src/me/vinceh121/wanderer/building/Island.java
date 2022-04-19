package me.vinceh121.wanderer.building;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.linearmath.btTransformUtil;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.AbstractLivingEntity;

public class Island extends AbstractLivingEntity implements IClanMember {
	private final Array<Slot> slots = new Array<>();
	private final Array<Building> buildings = new Array<>();
	private final Vector3 velocity = new Vector3(0, 0.5f, -2f);
	private Clan clan;

	public Island(final Wanderer game) {
		super(game);
	}

	@Override
	public void onDeath() {
		// TODO
	}

	/**
	 * @param value
	 * @see com.badlogic.gdx.utils.Array#add(java.lang.Object)
	 */
	public void addSlot(final Slot value) {
		this.slots.add(value);
	}

	/**
	 * @param index
	 * @return
	 * @see com.badlogic.gdx.utils.Array#get(int)
	 */
	public Slot getSlot(final int index) {
		return this.slots.get(index);
	}

	public Array<Slot> getSlots() {
		return this.slots;
	}

	public void addBuilding(final Building value, final Slot slot) {
		if (this.isSlotTaken(slot)) {
			throw new IllegalStateException("Slot is taken");
		}
		buildings.add(value);
		value.setIsland(this);
		value.setSlot(slot);
		this.updateBuildings();
	}

	private void updateBuildings() {
		final Matrix4 islandTrans = this.getTransform();
		for (Building building : this.buildings) {
			building.setTransform(islandTrans.cpy().translate(building.getSlot().getLocation()));
		}
	}

	public boolean isSlotTaken(Slot slot) {
		for (Building b : this.buildings) {
			if (b.getSlot() == slot) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void updatePhysics(btDiscreteDynamicsWorld world) {
		super.updatePhysics(world);
		if (!this.velocity.equals(Vector3.Zero)) {
			this.updateBuildings();
			Vector3 transOrig = getTransform().getTranslation(new Vector3());
			Matrix4 toTrans = new Matrix4();
			btTransformUtil.integrateTransform(getTransform(), velocity, Vector3.Zero, Gdx.graphics.getDeltaTime(),
					toTrans);
			setTransform(toTrans);
			Vector3 transDelta = getTransform().getTranslation(new Vector3()).sub(transOrig);
			if (getCollideObject() != null && !this.velocity.equals(Vector3.Zero)) {
				Array<CharacterW> done = new Array<>();
				world.contactTest(getCollideObject(), new ContactResultCallback() {
					@Override
					public float addSingleResult(btManifoldPoint cp, btCollisionObjectWrapper colObj0Wrap, int partId0,
							int index0, btCollisionObjectWrapper colObj1Wrap, int partId1, int index1) {
						for (AbstractEntity e : game.getEntities()) {
							if (e instanceof CharacterW) {
								CharacterW chara = (CharacterW) e;
								if (!done.contains(chara, true)) {
									done.add(chara);
									if (chara.getGhostObject().getCPointer() == colObj0Wrap.getCollisionObject()
											.getCPointer()) {
										moveCharacterAlong(chara, transDelta);
									}
								}
							}
						}
						return 0f;
					}
				});
			}
		}
	}

	private void moveCharacterAlong(CharacterW character, Vector3 transDelta) {
		Matrix4 trans = character.getGhostObject().getWorldTransform();
		Vector3 origTrans = trans.getTranslation(new Vector3());
		origTrans.add(transDelta);
		trans.setTranslation(origTrans);
		character.getGhostObject().setWorldTransform(trans);
	}

	@Override
	public void loadCollideModel() {
		super.loadCollideModel();
		this.getCollideObject().setCollisionFlags(CollisionFlags.CF_KINEMATIC_OBJECT);
		this.getCollideObject().forceActivationState(4); // DISABLE_DEACTIVATION
	}

	@Override
	public Clan getClan() {
		return this.clan;
	}

	@Override
	public void onJoinClan(final Clan clan) {
		this.clan = clan;
		// TODO probably nothing?
	}
}
