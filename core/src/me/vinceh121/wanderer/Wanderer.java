package me.vinceh121.wanderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import me.vinceh121.wanderer.cinematic.CinematicController;
import me.vinceh121.wanderer.cinematic.CinematicData;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.IControllableEntity;
import me.vinceh121.wanderer.entity.Prop;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListenerAdapter;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.script.JsGame;
import me.vinceh121.wanderer.ui.BlinkLabel;
import me.vinceh121.wanderer.ui.DebugOverlay;
import me.vinceh121.wanderer.ui.EnergyBar;
import me.vinceh121.wanderer.ui.ItemBar;
import me.vinceh121.wanderer.ui.LetterboxOverlay;

public class Wanderer extends ApplicationAdapter {
	private static final Logger LOG = LogManager.getLogger(Wanderer.class);
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
	private Clan playerClan;

	private DebugOverlay debugOverlay;
	private boolean debugBullet = false, glxDebug = false, paused;

	private IControllableEntity controlledEntity;
	/**
	 * To store controlled entity before pause to remove control
	 */
	private IControllableEntity pauseControlledEntity;
	private AbstractBuilding interactingBuilding;

	private CinematicController cinematicController;

	private BlinkLabel messageLabel;
	private ItemBar itemBar;
	private EnergyBar energyBar;
	private LetterboxOverlay cutsceneOverlay;

	private float timeOfDay, elapsedTimeOfDay, dayDuration = 15800f;

	@Override
	public void create() {
		try {
			this.consoleHandler = new ConsoleHandler(this);
			this.consoleHandler.start();
		} catch (IOException e2) {
			LOG.error("Failed to init console", e2);
		}

		this.inputManager.create();
		this.physicsManager.create();
		this.graphicsManager.create();
		new JsGame(this).install(this.scriptManager.getBaseScope());

		try {
			this.inputManager.loadOrDefaults();
		} catch (final JsonProcessingException e) {
			LOG.error("Failed to load key bindings", e);
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
								LOG.info("Controlling {}", e);
								Wanderer.this.controlEntity((IControllableEntity) e);
								return true;
							}
						}
					} else {
						LOG.info("Remove control");
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
				} else if (in == Input.PAUSE_MENU) {
					if (paused) {
						resume();
					} else {
						pause();
					}
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

		this.cutsceneOverlay = new LetterboxOverlay();
		this.graphicsManager.getStage().addActor(this.cutsceneOverlay);

		try {
			MetaRegistry.getInstance().loadDefaults();
		} catch (final IOException e1) {
			throw new RuntimeException(e1);
		}

		///// GAMEPLAY

		Random islandRng = new Random(69420L);
		for (int i = 0; i < 90; i++) {
			Vector3 pos = new Vector3()
				.setFromSpherical(0, islandRng.nextFloat() * MathUtils.PI2 * 0.7f + 2f * (MathUtils.PI2 * 0.3f))
				.scl(1000);
			Quaternion rot = new Quaternion(Vector3.Y, islandRng.nextFloat() * 360f);

			Prop isl = new Prop(this, MetaRegistry.getInstance().get("rock" + (islandRng.nextInt(7) + 1)));
			isl.setTranslation(pos);
			isl.rotate(rot);
			this.addEntity(isl);
		}

		final BackpackArtifact backpack = new BackpackArtifact(this);
		backpack.setTranslation(-5, 34, 10);
		this.addEntity(backpack);

		final LighthouseMeta lighthouseArtifactMeta = MetaRegistry.getInstance().get("j_lighthouse01");

		for (int i = 0; i < 3; i++) {
			final AbstractArtifactEntity artifactEntity = new BuildingArtifactEntity(this, lighthouseArtifactMeta);
			artifactEntity.setTranslation(5, 34, 4 * i + 10);
			this.addEntity(artifactEntity);
		}

		final EnergyArtefact energyEntity = new EnergyArtefact(this);
		energyEntity.setTranslation(2, 34, 10);
		this.addEntity(energyEntity);

		playerClan = new Clan();
		playerClan.setColor(Color.BLUE);
		playerClan.setName("player clan");
		playerClan.setMaxEnergy(100);
		this.clans.add(playerClan);

		final IslandMeta firstIsland = MetaRegistry.getInstance().get("first_island");
		final Island island = new Island(this, firstIsland);

		this.addEntity(island);
		playerClan.addMember(island);

		final CharacterMeta johnMeta = MetaRegistry.getInstance().get("goliath");
		johnMeta.ensureLoading();

		final CharacterW john = new CharacterW(this, johnMeta);
		john.setSymbolicName("player");
		john.setBeltSize(5);
		john.setTranslation(0.1f, 50f, 0.1f);

		this.itemBar.setCharacter(john);

		playerClan.addMember(john);
		this.addEntity(john);

		this.setPlayerClan(playerClan);

		this.controlEntity(john);
	}

	@Override
	public void render() {
		WandererConstants.AUDIO.setListenerPosition(this.getCamera().position);
		WandererConstants.AUDIO.setListenerOrientation(this.getCamera().direction, this.getCamera().up);

		final float delta = Gdx.graphics.getDeltaTime();
		this.graphicsManager.apply();

		WandererConstants.ASSET_MANAGER.update(8);

		if (!this.paused) {
			this.physicsManager.render();

			this.elapsedTimeOfDay += Gdx.graphics.getDeltaTime();
			this.elapsedTimeOfDay %= this.dayDuration;
			this.timeOfDay = this.elapsedTimeOfDay / this.dayDuration;
			this.timeOfDay %= 1;
		}

		this.flushEntityQueue();

		for (int i = 0; i < this.entities.size; i++) {
			final AbstractEntity entity = this.entities.get(i);
			entity.updatePhysics(this.physicsManager.getBtWorld());
			if (!this.paused) {
				entity.tick(delta);
			}
		}

		if (this.cinematicController != null && !this.paused) {
			this.cinematicController.update(delta);
			if (this.cinematicController.isOver()) {
				this.stopCinematic();
			}
		}

		this.graphicsManager.clear();
		this.graphicsManager.renderSkybox(this.timeOfDay);

		this.graphicsManager.begin();
		for (int i = 0; i < this.entities.size; i++) {
			final AbstractEntity entity = this.entities.get(i);
			entity.render(this.graphicsManager.getModelBatch(), this.graphicsManager.getEnv());
		}
		this.graphicsManager.end();

		this.graphicsManager.begin();
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

	protected void flushEntityQueue() {
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

	public AbstractEntity getEntity(final String symbolicName) {
		assert symbolicName != null : "symbolicName cannot be null";
		for (final AbstractEntity e : this.entities) {
			if (symbolicName.equals(e.getSymbolicName())) {
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

	public Clan getPlayerClan() {
		return playerClan;
	}

	public void setPlayerClan(Clan playerClan) {
		this.playerClan = playerClan;
		this.bindPlayerClan();
	}

	protected void bindPlayerClan() {
		this.energyBar.setClan(this.playerClan);
	}

	public void saveStateless(FileHandle dest) throws IOException {
		MapW mapW = this.saveMap();

		final Save save = new Save();
		if (this.controlledEntity != null) {
			save.setControlled(((AbstractEntity) this.controlledEntity).getId());
		}
		if (this.playerClan != null) {
			save.setPlayerClan(this.playerClan.getId());
		}
		save.setTime(this.timeOfDay);
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
			this.setTimeOfDay(sav.getTime());
			this.loadMap(map);
			this.flushEntityQueue();

			AbstractEntity contEnt = this.getEntity(sav.getControlled());
			if (contEnt != null) {
				if (contEnt instanceof IControllableEntity) {
					this.controlEntity((IControllableEntity) contEnt);
				} else {
					throw new IllegalStateException("Cannot control entity from save " + contEnt);
				}
			} else {
				LOG.info("No controlled entity in save");
			}

			Clan pClan = this.getClan(sav.getPlayerClan());
			if (pClan != null) {
				this.setPlayerClan(pClan);
			}
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
						LOG.error("Entity {} has constructor w/ meta but save is missing meta property", cls);
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

	public void startCinematic(FileHandle fh) throws IOException {
		List<CinematicData> datas =
				WandererConstants.MAPPER.readValue(fh.read(), new TypeReference<List<CinematicData>>() {
				});
		this.startCinematic(datas);
	}

	public void startCinematic(List<CinematicData> data) {
		this.cinematicController = new CinematicController(this);
		this.cinematicController.setCinematicDatas(data);
		this.cutsceneOverlay.start();
	}

	public void stopCinematic() {
		this.cinematicController = null;
		this.cutsceneOverlay.stop();
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

	public ScriptManager getScriptManager() {
		return this.scriptManager;
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

	public CinematicController getCinematicController() {
		return cinematicController;
	}

	public AbstractEntity findFirstEntityByClass(Class<? extends AbstractEntity> cls) {
		return this.findEntitiesByClass(cls).findFirst().orElse(null);
	}

	public Stream<AbstractEntity> findEntitiesByClass(Class<? extends AbstractEntity> cls) {
		// This game of casts looks redundant but it's not!
		// Using Stream.of(this.entities.items) causes a ClassCastException
		// This is due to an implicit (AbtractEntity[]) this.entities.items added by the
		// compiler that will always fail!
		// GDX's Array<T>#items has a T[] type, which the compiler will always compile
		// as Object[]
		return Stream.of((Object[]) this.entities.items).filter(e -> cls.isInstance(e)).map(e -> (AbstractEntity) e);
	}

	public void showMessage(final String message) {
		this.messageLabel.setColor(1f, 1f, 1f, 0f);
		this.messageLabel.setText(message);
		this.messageLabel.blink();
	}

	public boolean isPaused() {
		return paused;
	}

	@Override
	public void pause() {
		this.paused = true;
		this.pauseControlledEntity = this.controlledEntity;
		this.removeEntityControl();
	}

	@Override
	public void resume() {
		this.paused = false;
		this.controlEntity(this.pauseControlledEntity);
		this.pauseControlledEntity = null;
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
