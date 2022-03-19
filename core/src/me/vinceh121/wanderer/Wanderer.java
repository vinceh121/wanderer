package me.vinceh121.wanderer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

import me.vinceh121.wanderer.character.ui.DebugOverlay;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.CharacterW;
import me.vinceh121.wanderer.entity.IControllableEntity;
import me.vinceh121.wanderer.entity.Prop;

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

		final Prop e = new Prop(this);
		e.setCollideModel("orig/first_island.n/collide.obj");
		e.setDisplayModel("orig/first_island.n/terrain.obj");
		e.setDisplayTexture("orig/first_island.n/texturenone.png");
		e.setExactCollideModel(true);
		this.entities.add(e);

		final Clan playerClan = new Clan();
		playerClan.setColor(Color.BLUE);
		playerClan.setName("player clan");

		final CharacterW john = new CharacterW(this);
		john.setDisplayModel("orig/char_john.n/skin.obj");
		john.setDisplayTexture("orig/char_john.n/texturenone.png");
		john.setTranslation(0.1f, 50f, 0.1f);
		playerClan.addMember(john);
		this.entities.add(john);

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
