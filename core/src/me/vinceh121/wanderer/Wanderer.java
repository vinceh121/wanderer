package me.vinceh121.wanderer;

import java.io.IOException;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.Texture.TextureWrap;
import com.badlogic.gdx.graphics.g3d.attributes.BlendingAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.FloatAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;

import me.vinceh121.wanderer.artifact.AbstractArtifactEntity;
import me.vinceh121.wanderer.artifact.BackpackArtifact;
import me.vinceh121.wanderer.artifact.EnergyArtefact;
import me.vinceh121.wanderer.building.AbstractBuilding;
import me.vinceh121.wanderer.building.BuildingArtifactEntity;
import me.vinceh121.wanderer.building.InConstructionBuilding;
import me.vinceh121.wanderer.building.Island;
import me.vinceh121.wanderer.building.IslandMeta;
import me.vinceh121.wanderer.building.LighthouseMeta;
import me.vinceh121.wanderer.character.CharacterMeta;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.DisplayModel;
import me.vinceh121.wanderer.entity.IControllableEntity;
import me.vinceh121.wanderer.glx.TiledMaterialAttribute;
import me.vinceh121.wanderer.ui.BlinkLabel;
import me.vinceh121.wanderer.ui.DebugOverlay;
import me.vinceh121.wanderer.ui.EnergyBar;
import me.vinceh121.wanderer.ui.ItemBar;

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
	private AbstractBuilding interactingBuilding;

	private BlinkLabel messageLabel;
	private ItemBar itemBar;
	private EnergyBar energyBar;

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

		this.messageLabel = new BlinkLabel("", WandererConstants.getDevSkin());
		this.messageLabel.setAlignment(Align.center);
		this.graphicsManager.getStage().addActor(this.messageLabel);

		this.itemBar = new ItemBar(this);
		this.itemBar.setCount(10);
		this.itemBar.setWidth(1);
		this.itemBar.setHeight(32);
		this.itemBar.setX(256 - 70);
		this.itemBar.setY(32);
		this.graphicsManager.getStage().addActor(this.itemBar);

		this.energyBar = new EnergyBar();
		this.energyBar.setWidth(256);
		this.energyBar.setHeight(128);
		this.energyBar.setX(-70);
		this.energyBar.setY(10);
		this.graphicsManager.getStage().addActor(this.energyBar);

		try {
			MetaRegistry.getInstance().loadDefaults();
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}

		///// GAMEPLAY

		final BackpackArtifact backpack = new BackpackArtifact(this);
		backpack.setTranslation(-5, 34, 10);
		this.addEntity(backpack);

		final LighthouseMeta lighthouseArtifactMeta = MetaRegistry.getInstance().get("j_lighthouse01");

		final AbstractArtifactEntity artifactEntity = new BuildingArtifactEntity(this, lighthouseArtifactMeta);
		artifactEntity.setTranslation(5, 34, 10);
		this.addEntity(artifactEntity);

		final EnergyArtefact energyEntity = new EnergyArtefact(this);
		energyEntity.setTranslation(2, 34, 10);
		this.addEntity(energyEntity);

		final Clan playerClan = new Clan();
		playerClan.setColor(Color.BLUE);
		playerClan.setName("player clan");
		playerClan.setMaxEnergy(100);
		this.clans.add(playerClan);
		this.energyBar.setClan(playerClan);

		final IslandMeta firstIsland = MetaRegistry.getInstance().get("first_island");

		final String sandName = "orig/lib/textures/detailmap_sandnone.ktx";
		WandererConstants.ASSET_MANAGER.load(sandName, Texture.class, WandererConstants.MIPMAPS);
		WandererConstants.ASSET_MANAGER.finishLoadingAsset(sandName);
		final Texture sand = WandererConstants.ASSET_MANAGER.get(sandName, Texture.class);
		sand.setFilter(TextureFilter.MipMapNearestLinear, TextureFilter.Linear);
		sand.setWrap(TextureWrap.Repeat, TextureWrap.Repeat);
		firstIsland.getDisplayModels()
			.get(0)
			.addTextureAttribute(TiledMaterialAttribute.create(sand, 1.333f, new Vector2(50f, 50f)));

		final Island island = new Island(this, firstIsland);

		this.addEntity(island);
		playerClan.addMember(island);

		final DisplayModel grass = new DisplayModel();
		grass.setDisplayModel("orig/first_island.n/grass.obj");
		grass.setDisplayTexture("orig/lib/detailobjects01/pflanzen_rasteralpha.ktx");
		grass.addTextureAttribute(IntAttribute.createCullFace(0));
		grass.addTextureAttribute(FloatAttribute.createAlphaTest(0.5f));
		grass.addTextureAttribute(new BlendingAttribute(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA, 1f));
		island.addModel(grass);

		final InConstructionBuilding lighthouse = new InConstructionBuilding(this, lighthouseArtifactMeta);
		this.addEntity(lighthouse);
		island.addBuilding(lighthouse, island.getSlot(0));
		playerClan.addMember(lighthouse);

		final CharacterMeta johnMeta = MetaRegistry.getInstance().get("john");
		johnMeta.ensureLoading();

		final CharacterW john = new CharacterW(this, johnMeta);
		john.setTranslation(0.1f, 50f, 0.1f);

		this.itemBar.setBelt(john.getBelt());

		playerClan.addMember(john);
		this.addEntity(john);

		this.controlEntity(john);
	}

	@Override
	public void render() {
		final float delta = Gdx.graphics.getDeltaTime();
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
		this.graphicsManager.renderParticles(delta);
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

	public AbstractEntity getEntityFromHash(int hashcode) {
		for (AbstractEntity e : this.entities) {
			if (e.hashCode() == hashcode) {
				return e;
			}
		}
		return null;
	}

	public Clan getClanForMember(final IClanMember member) {
		for (final Clan c : this.clans) {
			if (c.getMembers().contains(member, true)) {
				return c;
			}
		}
		return null;
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
		return this.controlledEntity;
	}

	public void enterInteractBuilding(final AbstractBuilding building) {
		if (this.interactingBuilding == building) {
			return;
		}
		this.interactingBuilding = building;
		this.showMessage("Control " + building.getName());
		WandererConstants.ASSET_MANAGER.get("orig/feedback/use_ok.wav", Sound.class).play();
	}

	public void removeInteractBuilding() {
		this.interactingBuilding = null;
	}

	public AbstractBuilding getInteractingBuilding() {
		return this.interactingBuilding;
	}

	public void showMessage(final String message) {
		this.messageLabel.setColor(1f, 1f, 1f, 0f);
		this.messageLabel.setText(message);
		this.messageLabel.blink();
	}

	@Override
	public void resize(final int width, final int height) {
		this.graphicsManager.resize(width, height);
		this.messageLabel.setX(width / 2);
		this.messageLabel.setY(height - 30);
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

	public GraphicsManager getGraphicsManager() {
		return this.graphicsManager;
	}
}
