package me.vinceh121.wanderer.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.utils.Array;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.artifact.ArtifactMeta;
import me.vinceh121.wanderer.building.AbstractBuildingMeta;
import me.vinceh121.wanderer.building.InConstructionBuilding;
import me.vinceh121.wanderer.building.Island;
import me.vinceh121.wanderer.building.PreviewBuilding;
import me.vinceh121.wanderer.building.Slot;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractLivingControllableEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.ui.BeltSelection;

/**
 * An entity of a character, a person.
 *
 * Named like that to differentiate with java.lang.Character
 */
public class CharacterW extends AbstractLivingControllableEntity implements IClanMember {
	private final CharacterMeta meta;
	private final CharacterWController controller;
	private final Vector3 characterDirection = new Vector3();
	private final Vector3 walkDirection = new Vector3();
	private final Array<ArtifactMeta> belt = new Array<>();
	private Island attachedIsland;
	private int beltSize = 3;
	private Clan clan;
	private boolean beltOpen;
	private BeltSelection beltWidget;
	private AbstractBuildingMeta placing;
	private int placingSlotIndex;
	private PreviewBuilding previewBuilding;

	public CharacterW(final Wanderer game, final CharacterMeta meta) {
		super(game);
		this.meta = meta;
		this.setCollideObjectOffset(meta.getCapsuleOffset());
		this.controller = new CharacterWController(this.game, this, meta.getCapsuleRadius(), meta.getCapsuleHeight());
		this.controller.setFallListener(this::onFall);
		this.getGhostObject().setUserIndex(this.getId().getValue());
		game.getBtWorld().addAction(this.controller);

		this.addModel(new DisplayModel(meta.getModel(), meta.getTexture()));
	}

	private void onFall(final boolean bigJump) {
		WandererConstants.ASSET_MANAGER.get(this.meta.getFallSound(), Sound.class).play();
	}

	@Override
	public void updatePhysics(final btDiscreteDynamicsWorld world) {
		super.updatePhysics(world);

		final Matrix4 colTransform = this.controller.getGhostObject().getWorldTransform();
		final Vector3 colTranslation = new Vector3();
		colTransform.getTranslation(colTranslation);
		colTranslation.sub(this.getCollideObjectOffset());
		colTransform.setTranslation(colTranslation);

		// do not call setTransform as not to cause update to ghostobject
		this.getTransform().set(colTransform);
		super.updateTransform();
	}

	@Override
	public void render(final ModelBatch batch, final Environment env) {
		super.render(batch, env);

		if (this.isControlled()) {
			this.processInput();
			this.moveCamera();
		}
	}

	private void moveCamera() {
		final PerspectiveCamera cam = this.game.getCamera();

		if (this.placing != null) {
			if (this.attachedIsland == null) {
				this.placing = null;
				return;
			}
			cam.position.set(this.attachedIsland.getPlaceCameraPosition())
				.add(this.attachedIsland.getTransform().getTranslation(new Vector3()));
			if (this.attachedIsland.getPlaceCameraDirection().equals(Vector3.Zero)) {
				cam.lookAt(this.attachedIsland.getTransform().getTranslation(new Vector3()));
			} else {
				cam.direction.set(this.attachedIsland.getPlaceCameraDirection());
			}
		} else {
			final Vector3 characterTransform = new Vector3();
			this.getTransform().getTranslation(characterTransform);

			final Quaternion characterRotation = new Quaternion();
			this.getTransform().getRotation(characterRotation);

			cam.position.set(characterRotation.transform(new Vector3(0, 3, -4)).add(characterTransform));
			cam.lookAt(characterTransform.cpy().add(0, 1, 0));
			cam.up.set(0, 1, 0); // should this be doable without this?
		}
		cam.update(true);
	}

	@Override
	public void onDeath() {
		// TODO
		System.out.println("Dead!");
	}

	public void processInput() {
		if (this.controller.isJumping() || this.controller.isFalling() || this.beltOpen || this.placing != null) {
			return;
		}

		if (Gdx.input.isKeyPressed(Keys.LEFT)) {
			this.controller.setWorldTransform(this.controller.getWorldTransform().rotate(0, 1, 0, 3f));
		}
		if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
			this.controller.setWorldTransform(this.controller.getWorldTransform().rotate(0, 1, 0, -3f));
		}
		this.characterDirection.set(0, 0, 1).rot(this.getTransform()).nor();
		this.walkDirection.set(0, 0, 0);

		if (Gdx.input.isKeyPressed(Keys.UP)) {
			this.walkDirection.add(this.characterDirection);
		}
		if (Gdx.input.isKeyPressed(Keys.DOWN)) {
			this.walkDirection.add(-this.characterDirection.x, -this.characterDirection.y, -this.characterDirection.z);
		}
		this.walkDirection.scl(8f * Gdx.graphics.getDeltaTime());
		this.controller.setWalkDirection(this.walkDirection);
	}

	@Override
	public InputProcessor getInputProcessor() {
		return new InputAdapter() {
			@Override
			public boolean keyDown(final int keycode) {
				if (CharacterW.this.beltOpen) {
					if (keycode == Keys.RIGHT) {
						CharacterW.this.beltWidget.increment();
						return true;
					} else if (keycode == Keys.LEFT) {
						CharacterW.this.beltWidget.decrement();
						return true;
					} else if (keycode == Keys.ESCAPE) {
						CharacterW.this.closeBelt();
						return true;
					} else if (keycode == Keys.ENTER) {
						CharacterW.this.selectBuilding();
						return true;
					}
					return false;
				}

				if (CharacterW.this.placing != null) {
					if (keycode == Keys.RIGHT) {
						CharacterW.this.incrementPreviewSlot();
						return true;
					} else if (keycode == Keys.LEFT) {
						CharacterW.this.decrementPreviewSlot();
						return true;
					} else if (keycode == Keys.ESCAPE) {
						CharacterW.this.placing = null;
						CharacterW.this.attachedIsland.removeBuilding(CharacterW.this.previewBuilding);
						CharacterW.this.game.removeEntity(CharacterW.this.previewBuilding);
						CharacterW.this.previewBuilding.dispose();
						return true;
					} else if (keycode == Keys.ENTER) {
						CharacterW.this.placeBuilding();
						return true;
					}
					return false;
				}

				if (keycode == Keys.SPACE && Gdx.input.isKeyPressed(Keys.UP)) {
					CharacterW.this.controller.bigJump();
					return true;
				} else if (keycode == Keys.SPACE) {
					CharacterW.this.controller.jump();
					return true;
				} else if (keycode == Keys.ENTER) {
					CharacterW.this.openBelt();
					return true;
				}
				return false;
			}
		};
	}

	private void placeBuilding() {
		if (this.getClan().getEnergy() < this.placing.getEnergyRequired()) {
			this.game.showMessage("Not enough energy!");
			return;
		}

		this.getClan().setEnergy(this.getClan().getEnergy() - this.placing.getEnergyRequired());

		final Slot s = this.previewBuilding.getSlot();

		this.attachedIsland.removeBuilding(this.previewBuilding);
		this.game.removeEntity(this.previewBuilding);
		this.previewBuilding.dispose();

		final InConstructionBuilding build = new InConstructionBuilding(this.game, this.placing);
		this.game.addEntity(build);
		this.attachedIsland.addBuilding(build, s);

		this.belt.removeValue(this.placing, true);
		this.placing = null;
	}

	private void selectBuilding() {
		final int idx = CharacterW.this.beltWidget.getIndex();
		final ArtifactMeta arti = CharacterW.this.belt.get(idx);
		if (!(arti instanceof AbstractBuildingMeta)) {
			return;
		}
		final AbstractBuildingMeta build = (AbstractBuildingMeta) arti;
		if (this.getClan().getEnergy() < build.getEnergyRequired()) {
			this.game.showMessage("Not enough energy!");
			WandererConstants.ASSET_MANAGER.get("orig/feedback/noenergy.wav", Sound.class).play();
			return;
		}
		final Array<Slot> free = this.attachedIsland.getFreeSlots(build.getSlotType());
		if (free.size == 0) {
			this.game.showMessage("No free slot!");
			WandererConstants.ASSET_MANAGER.get("orig/feedback/nobuildarea.wav", Sound.class).play();
			return;
		}
		CharacterW.this.placing = build;
		if (this.previewBuilding != null) {
			this.game.removeEntity(this.previewBuilding);
			this.previewBuilding.dispose();
		}
		this.previewBuilding = new PreviewBuilding(this.game, this.placing);
		this.game.addEntity(this.previewBuilding);
		this.closeBelt();
		this.updatePlacePreview(free);
	}

	private void incrementPreviewSlot() {
		final Array<Slot> freeSlots = this.attachedIsland.getFreeSlots(this.placing.getSlotType());
		if (freeSlots.size != 0) {
			this.placingSlotIndex = (this.placingSlotIndex + 1) % freeSlots.size;
		}
		this.updatePlacePreview(freeSlots);
	}

	private void decrementPreviewSlot() {
		this.placingSlotIndex = Math.max(this.placingSlotIndex - 1, 0);
		this.updatePlacePreview(this.attachedIsland.getFreeSlots(this.placing.getSlotType()));
	}

	private void updatePlacePreview(final Array<Slot> freeSlots) {
		if (freeSlots.size == 0) {
			return;
		}
		final Slot s = freeSlots.get(this.placingSlotIndex);
		this.attachedIsland.removeBuilding(this.previewBuilding);
		this.attachedIsland.addBuilding(this.previewBuilding, s);
	}

	public void openBelt() {
		this.beltOpen = true;
		this.beltWidget = new BeltSelection(this.game, this.belt);
		this.beltWidget.setWidth(Gdx.graphics.getWidth());
		this.beltWidget.setHeight(Gdx.graphics.getHeight());
		this.game.getGraphicsManager().getStage().addActor(this.beltWidget);
	}

	public void closeBelt() {
		this.beltOpen = false;
		this.game.getGraphicsManager().getStage().getRoot().removeActor(this.beltWidget);
		this.beltWidget = null;
	}

	@Override
	protected void updateTransform() {
		super.updateTransform();
		this.controller.getGhostObject().setWorldTransform(this.getTransform());
	}

	@Override
	public void setMass(final float mass) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void dispose() {
		this.game.getBtWorld().removeAction(this.controller);
		this.controller.dispose();
		super.dispose();
	}

	@Override
	public Clan getClan() {
		return this.clan;
	}

	@Override
	public void onJoinClan(final Clan clan) {
		this.clan = clan;
		// TODO change light decals colors
	}

	/**
	 * @return the meta
	 */
	public CharacterMeta getMeta() {
		return this.meta;
	}

	/**
	 * @return
	 * @see me.vinceh121.wanderer.character.CharacterWController#getGhostObject()
	 */
	public btPairCachingGhostObject getGhostObject() {
		return this.controller.getGhostObject();
	}

	public int getBeltSize() {
		return this.beltSize;
	}

	public void setBeltSize(final int beltSize) {
		this.beltSize = beltSize;
	}

	public Array<ArtifactMeta> getBelt() {
		return this.belt;
	}

	public boolean canPickUpArtifact() {
		return this.beltSize > this.belt.size;
	}

	public void pickUpArtifact(final ArtifactMeta artifact) {
		if (!this.canPickUpArtifact()) {
			throw new IllegalStateException("Can't pickup items");
		}
		this.belt.add(artifact);
	}

	public Island getAttachedIsland() {
		return this.attachedIsland;
	}

	public void setAttachedIsland(final Island attachedIsland) {
		this.attachedIsland = attachedIsland;
	}
}
