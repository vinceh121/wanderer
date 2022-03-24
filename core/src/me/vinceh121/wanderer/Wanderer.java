package me.vinceh121.wanderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

import me.vinceh121.wanderer.building.Building;
import me.vinceh121.wanderer.building.Island;
import me.vinceh121.wanderer.building.Lighthouse;
import me.vinceh121.wanderer.building.Slot;
import me.vinceh121.wanderer.building.SlotType;
import me.vinceh121.wanderer.character.CharacterMeta;
import me.vinceh121.wanderer.character.ui.DebugOverlay;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.CharacterW;
import me.vinceh121.wanderer.entity.IControllableEntity;

public class Wanderer extends ApplicationAdapter {
	private final PhysicsManager physicsManager = new PhysicsManager();
	private final GraphicsManager graphicsManager = new GraphicsManager();

	private Array<AbstractEntity> entities;
	private Array<Clan> clans;

	private CameraInputController camcon;
	private final InputMultiplexer inputMultiplexer = new InputMultiplexer();

	private DebugOverlay debugOverlay;
	private boolean debugBullet = false, glxDebug = false;

	private IControllableEntity controlledEntity;
	private Building interactingBuilding;

	@Override
	public void create() {
		WandererConstants.ASSET_MANAGER.getLogger().setLevel(Logger.DEBUG);
		WandererConstants.ASSET_MANAGER.setErrorListener((asset, t) -> {
			System.err.println("Failed to load asset: " + asset);
			t.printStackTrace();
		});

		this.physicsManager.create();
		this.graphicsManager.create();

		this.camcon = new CameraInputController(this.getCamera());
		this.inputMultiplexer.addProcessor(this.camcon);
		this.inputMultiplexer.addProcessor(new InputAdapter() {
			@Override
			public boolean keyDown(final int keycode) {
				if (keycode == Keys.F7) {
					Wanderer.this.debugBullet = !Wanderer.this.debugBullet;
					return true;
				} else if (keycode == Keys.F3) {
					Wanderer.this.glxDebug = !Wanderer.this.glxDebug;
					if (Wanderer.this.glxDebug) {
						Wanderer.this.graphicsManager.getStage().addActor(Wanderer.this.debugOverlay);
					} else {
						Wanderer.this.graphicsManager.getStage().getRoot().removeActor(Wanderer.this.debugOverlay);
					}
					return true;
				} else if (keycode == Keys.TAB) {
					if (Wanderer.this.controlledEntity == null) {
						for (final AbstractEntity e : Wanderer.this.entities) {
							if (e instanceof IControllableEntity) {
								System.out.println("Controlling " + e);
								Wanderer.this.controlEntity((IControllableEntity) e);
								return true;
							}
						}
					} else {
						System.out.println("Remove control");
						Wanderer.this.removeEntityControl();
					}
					return true;
				}
				return false;
			}
		});
		Gdx.input.setInputProcessor(this.inputMultiplexer);

		this.debugOverlay = new DebugOverlay(this);

		this.entities = new Array<>();
		this.clans = new Array<>();

		///// GAMEPLAY

		final Clan playerClan = new Clan();
		playerClan.setColor(Color.BLUE);
		playerClan.setName("player clan");

		final Island island = new Island(this);
		island.setCollideModel("orig/first_island.n/collide.obj");
		island.setDisplayModel("orig/first_island.n/terrain.obj");
		island.setDisplayTexture("orig/first_island.n/texturenone.png");
		island.addSlot(new Slot(SlotType.LIGHTHOUSE, new Vector3(-26, 36, 8)));
		this.addEntity(island);
		playerClan.addMember(island);

		final Lighthouse lighthouse = new Lighthouse(this);
		lighthouse.setDisplayModel("orig/j_lighthouse01.n/j_lighthouse01.obj");
		lighthouse.setCollideModel("orig/j_lighthouse01.n/collide.obj");
		lighthouse.setDisplayTexture("orig/j_lighthouse01.n/texturenone.png");
		lighthouse.setSlot(island.getSlot(0));
		lighthouse.setIsland(island);
		this.addEntity(lighthouse);
		playerClan.addMember(lighthouse);

		final CharacterMeta johnMeta = WandererConstants.CHARACTER_METAS.get(1);
		johnMeta.ensureLoading();

		final CharacterW john = new CharacterW(this);
		john.setMeta(johnMeta);
		john.setTranslation(0.1f, 50f, 0.1f);

		playerClan.addMember(john);
		this.addEntity(john);

		this.controlEntity(john);
	}

	@Override
	public void render() {
		this.graphicsManager.apply();
		this.camcon.update();

		WandererConstants.ASSET_MANAGER.update(62);

		this.physicsManager.render();

		this.graphicsManager.begin();
		for (int i = 0; i < this.entities.size; i++) {
			final AbstractEntity entity = this.entities.get(i);
			entity.updatePhysics(this.physicsManager.getBtWorld());
			entity.render(this.graphicsManager.getModelBatch(), this.graphicsManager.getEnv());
		}
		this.graphicsManager.end();

		if (this.debugBullet) {
			this.physicsManager.getDebugDrawer().begin(this.graphicsManager.getViewport3d());
			this.physicsManager.getBtWorld().debugDrawWorld();
			this.physicsManager.getDebugDrawer().end();
		}

		this.graphicsManager.renderUI();
	}

	public void addEntity(final AbstractEntity e) {
		this.entities.add(e);
		e.enterBtWorld(this.physicsManager.getBtWorld());
	}

	public void removeEntity(final AbstractEntity e) {
		this.entities.removeValue(e, true);
		e.leaveBtWorld(this.physicsManager.getBtWorld());
		if (e instanceof IClanMember) {
			for (final Clan c : this.clans) {
				c.removeMember((IClanMember) e);
			}
		}
	}

	public Array<AbstractEntity> getEntities() {
		return this.entities;
	}

	public PhysicsManager getPhysicsManager() {
		return this.physicsManager;
	}

	public void controlEntity(final IControllableEntity e) {
		if (this.controlledEntity != null) {
			this.controlledEntity.onRemoveControl();
		}
		this.controlledEntity = e;
		this.inputMultiplexer.getProcessors().set(0, e.getInputProcessor());
		e.onTakeControl();
	}

	public void removeEntityControl() {
		this.inputMultiplexer.getProcessors().set(0, this.camcon);
		this.controlledEntity.onRemoveControl();
		this.controlledEntity = null;
	}

	public IControllableEntity getControlledEntity() {
		return controlledEntity;
	}

	public void enterInteractBuilding(final Building building) {
		if (this.interactingBuilding == building) {
			return;
		}
		this.interactingBuilding = building;
		WandererConstants.ASSET_MANAGER.get("orig/feedback/use_ok.wav", Sound.class).play();
		System.out.println("interacting");
	}

	public void removeInteractBuilding() {
		this.interactingBuilding = null;
		System.out.println("remove interact");
	}

	public Building getInteractingBuilding() {
		return interactingBuilding;
	}

	@Override
	public void resize(final int width, final int height) {
		this.graphicsManager.resize(width, height);
	}

	@Override
	public void dispose() {
		WandererConstants.ASSET_MANAGER.dispose();
	}

	public btDiscreteDynamicsWorld getBtWorld() {
		return this.physicsManager.getBtWorld();
	}

	public PerspectiveCamera getCamera() {
		return this.graphicsManager.getCamera();
	}
}
