package me.vinceh121.wanderer.character;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btPairCachingGhostObject;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.MetaRegistry;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.animation.MultiplexedAnimationController;
import me.vinceh121.wanderer.animation.MultiplexedAnimationController.PlaybackType;
import me.vinceh121.wanderer.artifact.ArtifactMeta;
import me.vinceh121.wanderer.building.AbstractBuildingMeta;
import me.vinceh121.wanderer.building.InConstructionBuilding;
import me.vinceh121.wanderer.building.Island;
import me.vinceh121.wanderer.building.PreviewBuilding;
import me.vinceh121.wanderer.building.Slot;
import me.vinceh121.wanderer.character.CharacterWController.FallListener;
import me.vinceh121.wanderer.entity.AbstractLivingControllableEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.event.Event;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListener;
import me.vinceh121.wanderer.input.InputListenerAdapter;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.ui.BeltSelection;

/**
 * An entity of a character, a person.
 *
 * Named like that to differentiate with java.lang.Character
 */
public class CharacterW extends AbstractLivingControllableEntity {
	public static final String EVENT_START_FALL = "START_FALL", EVENT_END_FALL = "END_FALL",
			EVENT_JUMP_END = "JUMP_END";
	private final CharacterMeta meta;
	private final CharacterWController controller;
	private final Vector3 characterDirection = new Vector3();
	private final Vector3 walkDirection = new Vector3();
	private final Array<ArtifactMeta> belt = new Array<>();
	private Island attachedIsland;
	private int beltSize = 3, placingSlotIndex;
	private boolean beltOpen, justRan, justBackedUp, justTurnedLeft, justTurnedRight;
	private BeltSelection beltWidget;
	private AbstractBuildingMeta placing;
	private PreviewBuilding previewBuilding;
	private float cameraHeight = 0.5f;
	private MultiplexedAnimationController animController;

	public CharacterW(final Wanderer game, final CharacterMeta meta) {
		super(game);
		this.meta = meta;
		this.setCollideObjectOffset(meta.getCapsuleOffset());
		this.controller = new CharacterWController(this.game, this, meta.getCapsuleRadius(), meta.getCapsuleHeight());
		this.controller.setFallListener(new FallListener() {

			@Override
			public void onStartFall() {
			}

			@Override
			public void onJumpEnd(final boolean bigJump) {
				WandererConstants.ASSET_MANAGER.get(meta.getFallSound(), Sound3D.class)
					.playSource3D()
					.setPosition(CharacterW.this.getTransform().getTranslation(new Vector3()));
				CharacterW.this.eventDispatcher.dispatchEvent(new Event(EVENT_JUMP_END));
				justRan = justBackedUp = justTurnedLeft = justTurnedRight = false;
			}

			@Override
			public void onEndFall() {
			}
		});
		this.getGhostObject().setUserIndex(this.getId().getValue());
		game.getBtWorld().addAction(this.controller);

		this.addModel(new DisplayModel(meta.getModel(), meta.getTexture()));
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
	public void tick(float delta) {
		super.tick(delta);

		if (this.animController == null && this.getModels().size > 0
				&& this.getModels().get(0).getCacheDisplayModel() != null) {
			this.animController = new MultiplexedAnimationController(this.getModels().get(0).getCacheDisplayModel());
		}

		if (this.animController != null) {
			this.animController.update(Gdx.graphics.getDeltaTime());

			if (this.controller.isFalling()) {
				this.animController.playAnimationOptional("slitter_slitter", PlaybackType.LOOP, 1);
			} else if (this.controller.isBigJump()) {
				this.animController.playAnimationOptional("sprung_sprung", PlaybackType.NORMAL, 1);
			} else if (this.controller.isJumping()) {
				this.animController.playAnimationOptional("hop_hop", PlaybackType.NORMAL, 1);
			} else if (this.justRan) {
				this.animController.playAnimationOptional("boden_run", PlaybackType.LOOP_SMOOTH, 1);
			} else if (this.justTurnedLeft) {
				this.animController.playAnimationOptional("boden_drehenlinks", PlaybackType.LOOP, 1);
			} else if (this.justTurnedRight) {
				this.animController.playAnimationOptional("boden_drehenrechts", PlaybackType.LOOP, 1);
			} else if (this.justBackedUp) {
				this.animController.playAnimationOptional("boden_laufzur", PlaybackType.LOOP_SMOOTH, 1);
			} else {
				this.animController.playAnimationOptional("boden_stehen", PlaybackType.LOOP_SMOOTH, 1);
			}
		}

		if (this.isControlled()) {
			this.processInput();
			this.moveCamera();
		}
	}

	@Override
	public void render(final ModelBatch batch, final Environment env) {
		super.render(batch, env);
	}

	private void moveCamera() {
		final PerspectiveCamera cam = this.game.getCamera();

		final Vector3 characterTransform = new Vector3();
		this.getTransform().getTranslation(characterTransform);

		final Quaternion characterRotation = new Quaternion();
		this.getTransform().getRotation(characterRotation);

		final float invertCameraHeight = 1f - this.cameraHeight;

		final Vector3 aheadPoint =
				new Vector3(0, 5 * invertCameraHeight, 5 * this.cameraHeight).rot(this.getTransform())
					.add(characterTransform);

		if (this.controller.isFalling()) {
			cam.lookAt(aheadPoint);
			cam.up.set(0, 1, 0); // should this be doable without this?
		} else if (this.placing != null) {
			if (this.attachedIsland == null) {
				this.placing = null;
				return;
			}

			final Vector3 pos = new Vector3(this.attachedIsland.getPlaceCameraPosition())
				.add(this.attachedIsland.getTransform().getTranslation(new Vector3()));
			pos.lerp(cam.position, 0.9f);
			cam.position.set(pos);

			if (this.attachedIsland.getPlaceCameraDirection().equals(Vector3.Zero)) {
				cam.lookAt(this.attachedIsland.getTransform().getTranslation(new Vector3()));
			} else {
				cam.direction.set(this.attachedIsland.getPlaceCameraDirection());
			}
			cam.up.set(0, 1, 0); // should this be doable without this?
		} else {
			final Vector3 pos = new Vector3(characterRotation
				.transform(new Vector3(0, 5f * this.cameraHeight, -0.1f + -6f * invertCameraHeight))
				.add(characterTransform));
			pos.lerp(cam.position, 0.8f);
			cam.position.set(pos);

			cam.lookAt(aheadPoint);
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

		if (this.game.getInputManager().isPressed(Input.WALK_LEFT)) {
			this.controller.setWorldTransform(this.controller.getWorldTransform().rotate(0, 1, 0, 3f));
			this.justTurnedLeft = true;
		} else {
			this.justTurnedLeft = false;
		}
		if (this.game.getInputManager().isPressed(Input.WALK_RIGHT)) {
			this.controller.setWorldTransform(this.controller.getWorldTransform().rotate(0, 1, 0, -3f));
			this.justTurnedRight = true;
		} else {
			this.justTurnedRight = false;
		}
		this.characterDirection.set(0, 0, 1).rot(this.getTransform()).nor();
		this.walkDirection.set(0, 0, 0);

		if (this.game.getInputManager().isPressed(Input.WALK_FORWARDS)) {
			this.walkDirection.add(this.characterDirection);
			this.justRan = true;
		} else {
			this.justRan = false;
		}
		if (this.game.getInputManager().isPressed(Input.WALK_BACKWARDS)) {
			this.walkDirection.add(-this.characterDirection.x, -this.characterDirection.y, -this.characterDirection.z);
			this.walkDirection.scl(0.15f);
			this.justBackedUp = true;
		} else {
			this.justBackedUp = false;
		}
		this.walkDirection.scl(8f * Gdx.graphics.getDeltaTime());
		this.controller.setWalkDirection(this.walkDirection);
	}

	@Override
	public InputListener createInputProcessor() {
		return new InputListenerAdapter(50) {
			@Override
			public boolean inputDown(final Input in) {
				if (in == Input.SCROLL_BELT_RIGHT) {
					game.setTimeOfDay(MathUtils.clamp(game.getTimeOfDay() + 0.005f, 0, 1));
					return true;
				} else if (in == Input.SCROLL_BELT_LEFT) {
					game.setTimeOfDay(MathUtils.clamp(game.getTimeOfDay() - 0.005f, 0, 1));
					return true;
				}

				if (controller.isJumping() || controller.isFalling()) {
					return false;
				}

				if (CharacterW.this.placing != null) {
					if (in == Input.SCROLL_BELT_RIGHT) {
						CharacterW.this.incrementPreviewSlot();
						return true;
					} else if (in == Input.SCROLL_BELT_LEFT) {
						CharacterW.this.decrementPreviewSlot();
						return true;
					} else if (in == Input.PAUSE_MENU) {
						CharacterW.this.placing = null;
						CharacterW.this.attachedIsland.removeBuilding(CharacterW.this.previewBuilding);
						CharacterW.this.game.removeEntity(CharacterW.this.previewBuilding);
						CharacterW.this.previewBuilding.dispose();
						return true;
					} else if (in == Input.UI_VALIDATE) {
						CharacterW.this.placeBuilding();
						return true;
					}
					return false;
				}

				if (CharacterW.this.beltOpen) {
					if (in == Input.SCROLL_BELT_RIGHT) {
						CharacterW.this.beltWidget.increment();
						return true;
					} else if (in == Input.SCROLL_BELT_LEFT) {
						CharacterW.this.beltWidget.decrement();
						return true;
					} else if (in == Input.PAUSE_MENU) {
						CharacterW.this.closeBelt();
						return true;
					} else if (in == Input.UI_VALIDATE) {
						CharacterW.this.selectBuilding();
						return true;
					}
					return false;
				}

				if (in == Input.JUMP && CharacterW.this.game.getInputManager().isPressed(Input.WALK_FORWARDS)) {
					CharacterW.this.controller.bigJump();
					return true;
				} else if (in == Input.JUMP) {
					CharacterW.this.controller.jump();
					return true;
				} else if (in == Input.OPEN_BELT) {
					CharacterW.this.openBelt();
					return true;
				}
				return false;
			}

			@Override
			public boolean mouseMoved(final int x, final int y) {
				if (controller.isJumping() || controller.isFalling()) {
					return false;
				}

				final float lookSensY =
						Gdx.app.getPreferences("me.vinceh121.wanderer.gameplay").getFloat("lookSensitivityY", 0.005f);
				CharacterW.this.cameraHeight = MathUtils.clamp(CharacterW.this.cameraHeight + lookSensY * y, 0, 1);

				if (!CharacterW.this.controller.canJump()) {
					return false;
				}

				final float lookSensX =
						Gdx.app.getPreferences("me.vinceh121.wanderer.gameplay").getFloat("lookSensitivityX", 0.2f);

				CharacterW.this.controller.setWorldTransform(
						CharacterW.this.controller.getWorldTransform().rotate(Vector3.Y, -lookSensX * x));
				return true;
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
			WandererConstants.ASSET_MANAGER.get("orig/feedback/noenergy.wav", Sound3D.class).playGeneral();
			return;
		}
		final Array<Slot> free = this.attachedIsland.getFreeSlots(build.getSlotType());
		if (free.size == 0) {
			this.game.showMessage("No free slot!");
			WandererConstants.ASSET_MANAGER.get("orig/feedback/nobuildarea.wav", Sound3D.class).playGeneral();
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
		if (this.belt.size == 0 || this.attachedIsland == null) {
			return;
		}
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
	}

	@Override
	public void dispose() {
		this.game.getBtWorld().removeAction(this.controller);
		this.controller.dispose();
		super.dispose();
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
	@JsonIgnore
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

	@JsonIgnore
	public Island getAttachedIsland() {
		return this.attachedIsland;
	}

	@JsonIgnore
	public void setAttachedIsland(final Island attachedIsland) {
		this.attachedIsland = attachedIsland;
	}

	@Override
	public void writeState(ObjectNode node) {
		super.writeState(node);
		node.put("meta", MetaRegistry.getInstance().getReverse(this.meta));
		node.put("beltSize", this.getBeltSize());
		final ArrayNode belt = node.putArray("belt");
		for (ArtifactMeta m : this.belt) {
			belt.add(MetaRegistry.getInstance().getReverse(m));
		}
	}

	@Override
	public void readState(ObjectNode node) {
		super.readState(node);
		this.setBeltSize(node.get("beltSize").asInt());
		this.belt.clear();
		final ArrayNode arrBelt = node.withArray("belt");
		for (JsonNode n : arrBelt) {
			this.belt.add(MetaRegistry.getInstance().get(n.asText()));
		}
	}
}
