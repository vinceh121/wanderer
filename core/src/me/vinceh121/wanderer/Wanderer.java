package me.vinceh121.wanderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;

import me.vinceh121.wanderer.artifact.AbstractArtifactEntity;
import me.vinceh121.wanderer.artifact.BackpackArtifact;
import me.vinceh121.wanderer.artifact.EnergyArtefact;
import me.vinceh121.wanderer.building.AbstractBuilding;
import me.vinceh121.wanderer.building.BuildingArtifactEntity;
import me.vinceh121.wanderer.building.Island;
import me.vinceh121.wanderer.building.IslandMeta;
import me.vinceh121.wanderer.building.LighthouseMeta;
import me.vinceh121.wanderer.character.CharacterMeta;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.IControllableEntity;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListenerAdapter;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.ui.BlinkLabel;
import me.vinceh121.wanderer.ui.DebugOverlay;
import me.vinceh121.wanderer.ui.EnergyBar;
import me.vinceh121.wanderer.ui.ItemBar;

public class Wanderer extends ApplicationAdapter {
	private final InputManager inputManager = new InputManager();
	private final PhysicsManager physicsManager = new PhysicsManager();
	private final GraphicsManager graphicsManager = new GraphicsManager();
	private final ScriptManager scriptManager = new ScriptManager();

	private ConsoleHandler consoleHandler;

	private Array<AbstractEntity> entities;
	/**
	 * List of entities that have been added this current tick. To be moved to
	 * normal entity list after they all ticked.
	 */
	private Array<AbstractEntity> toAdd, toRemove;
	private Array<Clan> clans;

	private DebugOverlay debugOverlay;
	private boolean debugBullet = false, glxDebug = false;

	private IControllableEntity controlledEntity;
	private AbstractBuilding interactingBuilding;

	private BlinkLabel messageLabel;
	private ItemBar itemBar;
	private EnergyBar energyBar;

	private float timeOfDay, elapsedTimeOfDay, dayDuration = 15800f;

	@Override
	public void create() {
		try {
			this.consoleHandler = new ConsoleHandler(this);
			this.consoleHandler.start();
		} catch (IOException e2) {
			System.err.println("Failed to init console");
			e2.printStackTrace();
		}

		WandererConstants.ASSET_MANAGER.getLogger().setLevel(Logger.DEBUG);
		WandererConstants.ASSET_MANAGER.setErrorListener((asset, t) -> {
			System.err.println("Failed to load asset: " + asset);
			t.printStackTrace();
		});

		this.inputManager.create();
		this.physicsManager.create();
		this.graphicsManager.create();

		try {
			this.inputManager.loadOrDefaults();
		} catch (final JsonProcessingException e) {
			System.err.println("Failed to load key bindings");
			e.printStackTrace();
		}
		this.inputManager.addListener(new InputListenerAdapter(0) {
			@Override
			public boolean inputDown(final Input in) {
				if (in == Input.DEBUG_BULLET) {
					Wanderer.this.debugBullet = !Wanderer.this.debugBullet;
					return true;
				} else if (in == Input.DEBUG_GLX) {
					Wanderer.this.glxDebug = !Wanderer.this.glxDebug;
					if (Wanderer.this.glxDebug) {
						Wanderer.this.graphicsManager.getStage().addActor(Wanderer.this.debugOverlay);
					} else {
						Wanderer.this.graphicsManager.getStage().getRoot().removeActor(Wanderer.this.debugOverlay);
					}
					return true;
				} else if (in == Input.SWITCH_CONTROLLED_VEHICLE) {
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
				} else if (in == Input.QUICK_SAVE) {
					try {
						Wanderer.this.saveStateless(Gdx.files.local("quick.json"));
						showMessage("Quick saved!");
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				} else if (in == Input.QUICK_LOAD) {
					try {
						Wanderer.this.loadStateless(Gdx.files.local("quick.json"));
						showMessage("Quick loaded!");
					} catch (IOException e) {
						e.printStackTrace();
					}
					return true;
				} else if (in == Input.DEBUG_TIMESCALE) {
					if (getDayDuration() == 15800f) {
						setDayDuration(30f);
					} else {
						setDayDuration(15800f);
					}
					return true;
				} else if (in == Input.CURSOR_CAPTURE) {
					Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
					return true;
				}
				return false;
			}
		});

		this.debugOverlay = new DebugOverlay(this);

		this.entities = new Array<>();
		this.toAdd = new Array<>();
		this.toRemove = new Array<>();
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
		} catch (final IOException e1) {
			throw new RuntimeException(e1);
		}

		///// GAMEPLAY

		final BackpackArtifact backpack = new BackpackArtifact(this);
		backpack.setTranslation(-5, 34, 10);
		this.addEntity(backpack);

		final LighthouseMeta lighthouseArtifactMeta = MetaRegistry.getInstance().get("j_lighthouse01");

		for (int i = 0; i < 5; i++) {
			final AbstractArtifactEntity artifactEntity = new BuildingArtifactEntity(this, lighthouseArtifactMeta);
			artifactEntity.setTranslation(5, 34, 10);
			this.addEntity(artifactEntity);
		}

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
		final Island island = new Island(this, firstIsland);

		this.addEntity(island);
		playerClan.addMember(island);

		final CharacterMeta johnMeta = MetaRegistry.getInstance().get("john");
		johnMeta.ensureLoading();

		final CharacterW john = new CharacterW(this, johnMeta);
		john.setBeltSize(5);
		john.setTranslation(0.1f, 50f, 0.1f);

//		SceneAsset scn = new GLTFLoader().load(Gdx.files.absolute("/tmp/owo.gltf"));
//		john.getModels().get(0).setCacheDisplayModel(new ModelInstance(scn.scene.model));

		this.itemBar.setCharacter(john);

		playerClan.addMember(john);
		this.addEntity(john);

		this.controlEntity(john);

		// GAMEPLAY SCRIPTS

		this.scriptManager.loadChapter(Gdx.files.internal("story/singleplayer.js"), Gdx.files.internal("story/"));
	}

	@Override
	public void render() {
		WandererConstants.AUDIO.setListenerPosition(this.getCamera().position);
		WandererConstants.AUDIO.setListenerOrientation(this.getCamera().direction, this.getCamera().up);

		final float delta = Gdx.graphics.getDeltaTime();
		this.graphicsManager.apply();

		WandererConstants.ASSET_MANAGER.update(8);

		this.inputManager.render();
		this.physicsManager.render();

		this.elapsedTimeOfDay += Gdx.graphics.getDeltaTime();
		this.elapsedTimeOfDay %= this.dayDuration;
		this.timeOfDay = this.elapsedTimeOfDay / this.dayDuration;
		this.timeOfDay %= 1;

		this.entities.removeAll(this.toRemove, true);
		for (final AbstractEntity e : this.toRemove) {
			this.toAdd.removeValue(e, true);
			e.leaveBtWorld(this.physicsManager.getBtWorld());
			if (e instanceof IClanMember) {
				for (final Clan c : this.clans) {
					c.removeMember((IClanMember) e);
				}
			}
		}
		this.toRemove.clear();

		this.entities.addAll(this.toAdd);
		for (final AbstractEntity e : this.toAdd) {
			e.enterBtWorld(this.physicsManager.getBtWorld());
		}
		this.toAdd.clear();

		this.graphicsManager.clear();
		this.graphicsManager.renderSkybox(this.timeOfDay);

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

		this.scriptManager.update();

		this.graphicsManager.renderUI();
	}

	public void addEntity(final AbstractEntity e) {
		this.toAdd.add(e);
	}

	public void removeEntity(final AbstractEntity e) {
		this.toRemove.add(e);
	}

	public AbstractEntity getEntity(final ID id) {
		return this.getEntity(id.getValue());
	}

	public AbstractEntity getEntity(final int id) {
		for (final AbstractEntity e : this.entities) {
			if (e.getId().getValue() == id) {
				return e;
			}
		}
		return null;
	}

	public Clan getClanForMember(final IClanMember member) {
		for (final Clan c : this.clans) {
			if (c.getMembers().contains(member.getId(), false)) {
				return c;
			}
		}
		return null;
	}

	public Clan getClan(final ID id) {
		return this.getClan(id.getValue());
	}

	public Clan getClan(final int id) {
		for (final Clan c : this.clans) {
			if (c.getId().getValue() == id) {
				return c;
			}
		}
		return null;
	}

	public void saveStateless(FileHandle dest) throws IOException {
		MapW mapW = this.saveMap();

		final Save save = new Save();
		save.setMap(mapW);
		try (OutputStream out = dest.write(false)) {
			WandererConstants.SAVE_MAPPER.writeValue(out, save);
		}
	}

	public MapW saveMap() {
		final MapW mapW = new MapW();
		mapW.setClans(this.clans);

		final Array<ObjectNode> ents = new Array<>(this.entities.size);
		for (final AbstractEntity e : this.entities) {
			final ObjectNode n = WandererConstants.SAVE_MAPPER.createObjectNode();
			e.writeState(n);
			ents.add(n);
		}
		mapW.setEntities(ents);
		return mapW;
	}

	public void loadStateless(FileHandle src) throws IOException {
		try (InputStream in = src.read()) {
			Save sav = WandererConstants.SAVE_MAPPER.readValue(in, Save.class);
			MapW map = sav.getMap();
			this.loadMap(map);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	public void loadMap(MapW map) throws ReflectiveOperationException {
		this.removeEntityControl();
		for (AbstractEntity e : this.entities) {
			e.leaveBtWorld(this.getBtWorld());
			e.dispose();
		}
		this.entities.clear();
		this.clans.clear();
		for (ObjectNode n : map.getEntities()) {
			AbstractEntity ent = null;
			final Class<?> cls = Class.forName(n.get("@class").asText());
			for (Constructor<?> c : cls.getConstructors()) {
				if (c.getParameterTypes().length == 2 && c.getParameterTypes()[0] == Wanderer.class
						&& IMeta.class.isAssignableFrom(c.getParameterTypes()[1])) {
					if (n.get("meta") == null) {
						System.err
							.println("Entity " + cls + " has constructor w/ meta but save is missing meta property");
						continue;
					}
					ent = (AbstractEntity) c.newInstance(this, MetaRegistry.getInstance().get(n.get("meta").asText()));
					break;
				} else if (Arrays.equals(c.getParameterTypes(), new Class<?>[] { Wanderer.class })) {
					ent = (AbstractEntity) c.newInstance(this);
					break;
				}
			}
			if (ent == null) {
				throw new IllegalStateException("Couldn't deserialize entity " + n.toPrettyString());
			}
			ent.readState(n);
			this.addEntity(ent);
		}
		this.clans.addAll(map.getClans());
	}

	public Array<AbstractEntity> getEntities() {
		return this.entities;
	}

	public InputManager getInputManager() {
		return this.inputManager;
	}

	public PhysicsManager getPhysicsManager() {
		return this.physicsManager;
	}

	public GraphicsManager getGraphicsManager() {
		return this.graphicsManager;
	}

	public void controlEntity(final IControllableEntity e) {
		if (this.controlledEntity != null) {
			this.getInputManager().removeListener(this.controlledEntity.getInputProcessor());
			this.controlledEntity.onRemoveControl();
		}
		this.controlledEntity = e;
		this.getInputManager().addListener(e.getInputProcessor());
		e.onTakeControl();
	}

	public void removeEntityControl() {
		this.getInputManager().removeListener(this.controlledEntity.getInputProcessor());
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
		WandererConstants.ASSET_MANAGER.get("orig/feedback/use_ok.wav", Sound3D.class).playGeneral();
	}

	public void removeInteractBuilding() {
		this.interactingBuilding = null;
	}

	public AbstractBuilding getInteractingBuilding() {
		return this.interactingBuilding;
	}

	public float getElapsedTimeOfDay() {
		return elapsedTimeOfDay;
	}

	public void setElapsedTimeOfDay(float elapsedTimeOfDay) {
		this.elapsedTimeOfDay = elapsedTimeOfDay;
	}

	/**
	 * @return progress through the day/night between 0 and 1
	 */
	public float getTimeOfDay() {
		return timeOfDay;
	}

	/**
	 * @param timeOfDay progress through the day/night between 0 and 1
	 */
	public void setTimeOfDay(float timeOfDay) {
		this.timeOfDay = timeOfDay;

		this.elapsedTimeOfDay = this.timeOfDay * this.dayDuration;
	}

	/**
	 * @return number of seconds for a full day-night cycle
	 */
	public float getDayDuration() {
		return dayDuration;
	}

	/**
	 * @param dayDuration number of seconds for a full day-night cycle
	 */
	public void setDayDuration(float dayDuration) {
		this.dayDuration = dayDuration;

		this.elapsedTimeOfDay = this.dayDuration * this.timeOfDay;
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
		this.scriptManager.dispose();
		this.graphicsManager.dispose();
		this.physicsManager.dispose();
		WandererConstants.ASSET_MANAGER.dispose();
		if (this.consoleHandler != null) {
			try {
				this.consoleHandler.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public btDiscreteDynamicsWorld getBtWorld() {
		return this.physicsManager.getBtWorld();
	}

	public PerspectiveCamera getCamera() {
		return this.graphicsManager.getCamera();
	}
}
