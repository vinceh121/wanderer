package me.vinceh121.wanderer.building;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.ContactResultCallback;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject.CollisionFlags;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObjectWrapper;
import com.badlogic.gdx.physics.bullet.collision.btManifoldPoint;
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
	private final Array<Slot> slots;
	private final Array<AbstractBuilding> buildings = new Array<>();
	private final Vector3 velocity = new Vector3();
	private Clan clan;

	public Island(final Wanderer game, final IslandMeta meta) {
		super(game);
		this.slots = meta.getSlots();
		this.setCollideModel(meta.getCollisionModel());
		this.getModels().addAll(meta.getDisplayModels());
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

	public void addBuilding(final AbstractBuilding value, final Slot slot) {
		if (this.isSlotTaken(slot)) {
			throw new IllegalStateException("Slot is taken");
		}
		this.buildings.add(value);
		value.setIsland(this);
		value.setSlot(slot);
		this.updateBuildings();
	}

	public void removeBuilding(final AbstractBuilding building) {
		this.buildings.removeValue(building, true);
	}

	private void updateBuildings() {
		final Matrix4 islandTrans = this.getTransform();
		for (final AbstractBuilding building : this.buildings) {
			building.setTransform(islandTrans.cpy().translate(building.getSlot().getLocation()));
			building.rotate(building.getSlot().getRotation());
		}
	}

	public boolean isSlotTaken(final Slot slot) {
		for (final AbstractBuilding b : this.buildings) {
			if (b.getSlot() == slot) {
				return true;
			}
		}
		return false;
	}

	public AbstractBuilding getBuildingForSlot(final Slot slot) {
		for (final AbstractBuilding b : this.buildings) {
			if (b.getSlot() == slot) {
				return b;
			}
		}
		return null;
	}

	@Override
	public void updatePhysics(final btDiscreteDynamicsWorld world) {
		super.updatePhysics(world);
		if (!this.velocity.equals(Vector3.Zero)) {
			final Vector3 transOrig = this.getTransform().getTranslation(new Vector3());
			final Matrix4 toTrans = new Matrix4();
			btTransformUtil.integrateTransform(this.getTransform(), this.velocity, Vector3.Zero,
					Gdx.graphics.getDeltaTime(), toTrans);
			this.setTransform(toTrans);
			this.updateBuildings();
			final Vector3 transDelta = this.getTransform().getTranslation(new Vector3()).sub(transOrig);
			if (this.getCollideObject() != null && !this.velocity.equals(Vector3.Zero)) {
				final Array<CharacterW> done = new Array<>();
				world.contactTest(this.getCollideObject(), new ContactResultCallback() {
					@Override
					public float addSingleResult(final btManifoldPoint cp, final btCollisionObjectWrapper colObj0Wrap,
							final int partId0, final int index0, final btCollisionObjectWrapper colObj1Wrap,
							final int partId1, final int index1) {
						for (final AbstractEntity e : Island.this.game.getEntities()) {
							if (e instanceof CharacterW) {
								final CharacterW chara = (CharacterW) e;
								if (!done.contains(chara, true)) {
									done.add(chara);
									if (chara.getGhostObject().getCPointer() == colObj0Wrap.getCollisionObject()
											.getCPointer()) {
										Island.this.moveCharacterAlong(chara, transDelta);
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

	private void moveCharacterAlong(final CharacterW character, final Vector3 transDelta) {
		final Matrix4 trans = character.getGhostObject().getWorldTransform();
		final Vector3 origTrans = trans.getTranslation(new Vector3());
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

	public Vector3 getVelocity() {
		return this.velocity;
	}
}
