package me.vinceh121.wanderer.building;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
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
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.phys.ContactListenerAdapter;
import me.vinceh121.wanderer.phys.IContactListener;

public class Island extends AbstractLivingEntity implements IClanMember {
	private final Array<Slot> slots = new Array<>();
	private final Array<AbstractBuilding> buildings = new Array<>();
	private final Array<CharacterW> attachedCharacters = new Array<>();
	private final Vector3 velocity = new Vector3(), placeCameraPosition = new Vector3(),
			placeCameraDirection = new Vector3();
	private final IContactListener characterContactListener;
	private Clan clan;

	public Island(final Wanderer game, final IslandMeta meta) {
		super(game);

		for (Slot s : meta.getSlots()) {
			slots.add(new Slot(s)); // clone slots as to not accidentally edit the prototype
		}
		for (DisplayModel m : meta.getDisplayModels()) {
			this.getModels().add(new DisplayModel(m)); // clone models as to not accidentally edit the prototype
		}

		this.setCollideModel(meta.getCollisionModel());
		this.setPlaceCameraPosition(meta.getPlaceCameraPosition().cpy());
		this.setPlaceCameraDirection(meta.getPlaceCameraDirection().cpy());

		this.characterContactListener = new ContactListenerAdapter() {
			private CharacterW getChara(btCollisionObject colObj0, btCollisionObject colObj1) {
				final AbstractEntity ent0, ent1;
				try {
					ent0 = game.getEntity(colObj0.getUserIndex());
					ent1 = game.getEntity(colObj1.getUserIndex());
				} catch (IndexOutOfBoundsException e) {
					return null;
				}

				final CharacterW chara;
				if (ent0 instanceof CharacterW && ent1 == Island.this) {
					chara = (CharacterW) ent0;
				} else if (ent1 instanceof CharacterW && ent0 == Island.this) {
					chara = (CharacterW) ent1;
				} else {
					return null;
				}
				return chara;
			}

			@Override
			public void onContactStarted(btCollisionObject colObj0, btCollisionObject colObj1) {
				CharacterW chara = this.getChara(colObj0, colObj1);
				if (chara == null)
					return;

				if (!attachedCharacters.contains(chara, true))
					attachedCharacters.add(chara);
				chara.setAttachedIsland(Island.this);
			}

			@Override
			public void onContactEnded(btCollisionObject colObj0, btCollisionObject colObj1) {
				CharacterW chara = this.getChara(colObj0, colObj1);
				if (chara == null)
					return;

				attachedCharacters.removeValue(chara, true);
				chara.setAttachedIsland(null);
			}
		};
		game.getPhysicsManager().addContactListener(this.characterContactListener);
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

	public void moveCameraBuildingPlace(Camera cam) {
		this.getTransform().getTranslation(cam.position);
		cam.position.add(placeCameraPosition);

		cam.direction.set(this.placeCameraDirection);
		cam.rotate(this.getTransform().getRotation(new Quaternion()));

		cam.update();
	}

	@Override
	public void updatePhysics(final btDiscreteDynamicsWorld world) {
		super.updatePhysics(world);
		if (!this.velocity.equals(Vector3.Zero)) {
			final Vector3 transOrig = this.getTransform().getTranslation(new Vector3());
			final Matrix4 toTrans = new Matrix4();
			btTransformUtil.integrateTransform(this
				.getTransform(), this.velocity, Vector3.Zero, Gdx.graphics.getDeltaTime(), toTrans);
			this.setTransform(toTrans);
			this.updateBuildings();
			final Vector3 transDelta = this.getTransform().getTranslation(new Vector3()).sub(transOrig);
			if (this.getCollideObject() != null && !this.velocity.equals(Vector3.Zero)) {
				final Array<CharacterW> done = new Array<>();
				for (final CharacterW chara : this.attachedCharacters) {
					if (!done.contains(chara, true)) {
						done.add(chara);
						Island.this.moveCharacterAlong(chara, transDelta);
					}
				}
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

	public Vector3 getPlaceCameraPosition() {
		return placeCameraPosition;
	}

	public void setPlaceCameraPosition(Vector3 placeCameraPosition) {
		this.placeCameraPosition.set(placeCameraPosition);
	}

	public Vector3 getPlaceCameraDirection() {
		return placeCameraDirection;
	}

	public void setPlaceCameraDirection(Vector3 placeCameraDirection) {
		this.placeCameraDirection.set(placeCameraDirection);
	}

	@Override
	public void dispose() {
		this.game.getPhysicsManager().removeContactListener(this.characterContactListener);
		super.dispose();
	}
}
