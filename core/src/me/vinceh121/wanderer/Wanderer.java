package me.vinceh121.wanderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
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
import me.vinceh121.wanderer.building.IslandPrototype;
import me.vinceh121.wanderer.building.LighthousePrototype;
import me.vinceh121.wanderer.character.CharacterPrototype;
import me.vinceh121.wanderer.character.CharacterW;
import me.vinceh121.wanderer.cinematic.CinematicController;
import me.vinceh121.wanderer.cinematic.CinematicData;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.entity.IControllableEntity;
import me.vinceh121.wanderer.entity.ILivingEntity;
import me.vinceh121.wanderer.entity.Prop;
import me.vinceh121.wanderer.i18n.I18N;
import me.vinceh121.wanderer.input.Input;
import me.vinceh121.wanderer.input.InputListenerAdapter;
import me.vinceh121.wanderer.math.EllipsePath;
import me.vinceh121.wanderer.modding.ModManager;
import me.vinceh121.wanderer.platform.audio.Sound3D;
import me.vinceh121.wanderer.script.JsGame;
import me.vinceh121.wanderer.ui.BlinkLabel;
import me.vinceh121.wanderer.ui.DebugOverlay;
import me.vinceh121.wanderer.ui.EnergyBar;
import me.vinceh121.wanderer.ui.ItemBar;
import me.vinceh121.wanderer.ui.LetterboxOverlay;
import me.vinceh121.wanderer.ui.Subtitle;
import me.vinceh121.wanderer.util.MathUtilsW;

public class Wanderer extends ApplicationDelegate {
	private static final Logger LOG = LogManager.getLogger(Wanderer.class);
	private final InputManager inputManager = new InputManager();
	private final PhysicsManager physicsManager = new PhysicsManager();
	private final GraphicsManager graphicsManager = new GraphicsManager();
	private final ScriptManager scriptManager = new ScriptManager();
	private final ModManager modManager = new ModManager();

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
	private boolean debugBullet = false, glxDebug = false, paused, audioEmittersDebug = false;

	private IControllableEntity controlledEntity;
	/**
	 * To store controlled entity before pause to remove control
	 */
	private IControllableEntity pauseControlledEntity;
	/**
	 * To store controlled entity before cinematic to remove control
	 */
	private IControllableEntity cinematicControlledEntity;
	private AbstractBuilding interactingBuilding;

	private CinematicController cinematicController;

	private BlinkLabel messageLabel;
	private ItemBar itemBar;
	private EnergyBar energyBar;
	private LetterboxOverlay letterboxOverlay;
	private Subtitle subtitle;

	private float timeOfDay, elapsedTimeOfDay, dayDuration = 15800f;
	private float cameraShakeIntensity, cameraShakeTime, cameraShakeRevolutionTime;

	public Wanderer(final ApplicationMultiplexer applicationMultiplexer) {
		super(applicationMultiplexer);
	}

	@Override
	public void create() {
		try {
			this.consoleHandler = new ConsoleHandler(this);
			this.consoleHandler.start();
		} catch (final IOException e2) {
			Wanderer.LOG.error("Failed to init console", e2);
		}

		this.inputManager.create();
		this.physicsManager.create();
		this.graphicsManager.create();
		new JsGame(this).install(this.scriptManager.getBaseScope());

		try {
			this.modManager.loadMods();
			this.modManager.executeModsEntryPoints(this.scriptManager);
		} catch (final IOException e) {
			Wanderer.LOG.error("Failed to load mods", e);
			System.exit(-3);
		}

		try {
			this.inputManager.loadOrDefaults();
		} catch (final JsonProcessingException e) {
			Wanderer.LOG.error("Failed to load key bindings", e);
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
					Wanderer.this.cycleControl();
					return true;
				} else if (in == Input.QUICK_SAVE) {
					try {
						Wanderer.this.saveStateless(Gdx.files.local("quick.json"));
						Wanderer.this.showMessage(I18N.gettext("Quick saved!"));
					} catch (final IOException e) {
						e.printStackTrace();
					}
					return true;
				} else if (in == Input.QUICK_LOAD) {
					try {
						Wanderer.this.loadStateless(Gdx.files.local("quick.json"));
						Wanderer.this.showMessage(I18N.gettext("Quick loaded!"));
					} catch (final IOException e) {
						e.printStackTrace();
					}
					return true;
				} else if (in == Input.DEBUG_TIMESCALE) {
					if (Wanderer.this.getDayDuration() == 15800f) {
						Wanderer.this.setDayDuration(30f);
						if (Wanderer.this.cinematicController != null) {
							Wanderer.this.cinematicController.setRate(500);
						}
					} else {
						Wanderer.this.setDayDuration(15800f);
						if (Wanderer.this.cinematicController != null) {
							Wanderer.this.cinematicController.setRate(1);
						}
					}
					return true;
				} else if (in == Input.DEBUG_AUDIO) {
					Wanderer.this.audioEmittersDebug = !Wanderer.this.audioEmittersDebug;
					return true;
				} else if (in == Input.CURSOR_CAPTURE) {
					Gdx.input.setCursorCatched(!Gdx.input.isCursorCatched());
					return true;
				} else if (in == Input.PAUSE_MENU) {
					if (Wanderer.this.cinematicController != null) {
						Wanderer.this.cinematicController.skip();
					} else if (Wanderer.this.paused) {
						Wanderer.this.resume();
					} else {
						Wanderer.this.pause();
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

		this.letterboxOverlay = new LetterboxOverlay();
		this.graphicsManager.getStage().addActor(this.letterboxOverlay);

		this.subtitle = new Subtitle();
		this.graphicsManager.getStage().addActor(this.subtitle);

		///// GAMEPLAY

		final Random islandRng = new Random(69420L);
		for (int i = 0; i < 300; i++) {
			final float spread = 9000;
			final Vector3 pos = new Vector3(islandRng.nextFloat() * spread, -160, islandRng.nextFloat() * spread)
				.add(-7740 - spread / 2, 0, -5890 - spread / 2);
			final Quaternion rot = new Quaternion(Vector3.Y, islandRng.nextFloat() * 360f);

			final Prop isl = new Prop(this, PrototypeRegistry.getInstance().get("rock" + (islandRng.nextInt(7) + 1)));
			isl.setTranslation(pos);
			isl.rotate(rot);
			this.addEntity(isl);
		}

		final BackpackArtifact backpack = new BackpackArtifact(this);
		backpack.setTranslation(-5, 34, 10);
		this.addEntity(backpack);

		final LighthousePrototype lighthouseArtifactPrototype = PrototypeRegistry.getInstance().get("j_lighthouse01");

		for (int i = 0; i < 3; i++) {
			final AbstractArtifactEntity artifactEntity = new BuildingArtifactEntity(this, lighthouseArtifactPrototype);
			artifactEntity.setTranslation(5, 34, 4 * i + 10);
			this.addEntity(artifactEntity);
		}

		final EnergyArtefact energyEntity = new EnergyArtefact(this);
		energyEntity.setTranslation(2, 34, 10);
		this.addEntity(energyEntity);

		this.playerClan = new Clan();
		this.playerClan.setColor(Color.BLUE);
		this.playerClan.setName("player clan");
		this.playerClan.setMaxEnergy(100);
		this.playerClan.setEnergy(50);
		this.clans.add(this.playerClan);

		final IslandPrototype firstIsland = PrototypeRegistry.getInstance().get("first_island");
		final Island island = new Island(this, firstIsland);
		island.setTranslation(-7740, -160, -5890);
		this.addEntity(island);
		this.playerClan.addMember(island);

		final CharacterPrototype johnPrototype = PrototypeRegistry.getInstance().get("goliath");

		final CharacterW john = new CharacterW(this, johnPrototype);
		john.setSymbolicName("player");
		john.setBeltSize(5);
		john.setTranslation(0.1f, 50f, 0.1f);
		john.setTranslation(-7740, -100, -5890);

		this.itemBar.setCharacter(john);

		this.playerClan.addMember(john);
		this.addEntity(john);

		this.setPlayerClan(this.playerClan);

		this.controlEntity(john);
	}

	@Override
	public void render() {
		final float delta = Gdx.graphics.getDeltaTime();

		final Vector3 cameraVelocity = WandererConstants.AUDIO.getListenerPosition();
		cameraVelocity.sub(this.getCamera().position);
		cameraVelocity.scl(1f / delta);
		MathUtilsW.fixInfinity(cameraVelocity, 0);
		MathUtilsW.fixNaN(cameraVelocity, 0);

		WandererConstants.AUDIO.setListenerVelocity(cameraVelocity);
		WandererConstants.AUDIO.setListenerPosition(this.getCamera().position);
		WandererConstants.AUDIO.setListenerOrientation(this.getCamera().direction, this.getCamera().up);

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

		this.controlledDeathTest();

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

		this.processCameraShake(delta);

		this.graphicsManager.clear();

		final Vector3 sunCenter;

		if (this.controlledEntity != null) {
			sunCenter = ((AbstractEntity) this.controlledEntity).getTransform().getTranslation(new Vector3());
		} else {
			sunCenter = this.graphicsManager.getCamera().position;
		}

		final Camera sunCam = this.graphicsManager.getSkybox().getSunLight().getCamera();
		this.graphicsManager.getSkybox().getSunLight().update(sunCenter, null);

		this.graphicsManager.getSkybox().getSunLight().begin();
		this.graphicsManager.getShadowBatch().begin(sunCam);
		for (int i = 0; i < this.entities.size; i++) {
			final AbstractEntity entity = this.entities.get(i);
			if (entity.isCastShadow()) {
				entity.render(this.graphicsManager.getShadowBatch(), this.graphicsManager.getEnv());
			}
		}
		this.graphicsManager.getShadowBatch().end();
		this.graphicsManager.getSkybox().getSunLight().end();

		this.graphicsManager.beginPostProcess();
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

		this.graphicsManager.endPostProcess();

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

	protected void controlledDeathTest() {
		if (this.controlledEntity instanceof ILivingEntity && ((ILivingEntity) this.controlledEntity).isDead()) {
			final Optional<CharacterW> optChar = this.findEntitiesByClass(CharacterW.class)
				.filter(c -> c.getClan() == this.getPlayerClan())
				.findFirst();

			if (optChar.isPresent()) {
				this.controlEntity(optChar.get());
			} else {
				Wanderer.LOG.error("No player character to control back after death of controlled vehicle");
			}
		}
	}

	public void shakeCamera(final float intensity, final float time) {
		this.shakeCamera(intensity, time, 0.25f);
	}

	public void shakeCamera(final float intensity, final float time, final float revolutionTime) {
		this.cameraShakeIntensity = intensity;
		this.cameraShakeTime = time;
		this.cameraShakeRevolutionTime = revolutionTime;
	}

	private void processCameraShake(final float delta) {
		// should be a percentage
		final float cameraShakeModifier = Preferences.getPreferences().getOrElse("a11y.cameraShake", 1.0).floatValue();

		if (cameraShakeModifier == 0 || this.cameraShakeTime == 0) {
			return;
		}

		final PerspectiveCamera cam = this.graphicsManager.getCamera();

		final EllipsePath path =
				new EllipsePath(0f, 0f, 0.5f * this.cameraShakeIntensity, 0.2f * this.cameraShakeIntensity);
		path.y = path.height / 2;
		final Vector2 shakeVec2 = path.valueAt(new Vector2(), this.cameraShakeTime / this.cameraShakeRevolutionTime);
		final Vector3 shakeVec3 = new Vector3(shakeVec2, 0);
		shakeVec3.rot(cam.combined);
		cam.position.add(shakeVec3);

		cam.update();

		this.cameraShakeTime = Math.max(0, this.cameraShakeTime - delta);
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
		return this.playerClan;
	}

	public void setPlayerClan(final Clan playerClan) {
		this.playerClan = playerClan;
		this.bindPlayerClan();
	}

	protected void bindPlayerClan() {
		this.energyBar.setClan(this.playerClan);
	}

	public void cycleControl() {
		for (int i = this.playerClan.getMembers()
			.indexOf(((AbstractEntity) this.controlledEntity).getId(), false); i < this.playerClan.getMembers().size * 2
					- 1; i++) {
			final AbstractEntity e =
					this.getEntity(this.playerClan.getMembers().get(i % this.playerClan.getMembers().size));
			if (e instanceof IControllableEntity && e != this.controlledEntity) {
				Wanderer.LOG.info("Controlling {}", e);
				WandererConstants.ASSET_MANAGER.get("orig/feedback/taken_control.wav", Sound3D.class).playGeneral();
				this.showMessage(I18N.gettext("Taking control..."));
				Wanderer.this.controlEntity((IControllableEntity) e);
				return;
			}
		}

		this.showMessage(I18N.gettext("Nothing to control"));
	}

	public void saveStateless(final FileHandle dest) throws IOException {
		final Save save = this.saveStateless();

		try (OutputStream out = dest.write(false)) {
			WandererConstants.SAVE_MAPPER.writeValue(out, save);
		}
	}

	public Save saveStateless() {
		final MapW mapW = this.saveMap();

		final Save save = new Save();
		if (this.controlledEntity != null) {
			save.setControlled(((AbstractEntity) this.controlledEntity).getId());
		}
		if (this.playerClan != null) {
			save.setPlayerClan(this.playerClan.getId());
		}
		save.setTime(this.timeOfDay);
		save.setMap(mapW);
		return save;
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

	public void loadStateless(final FileHandle src) throws IOException {
		try (InputStream in = src.read()) {
			final Save sav = WandererConstants.SAVE_MAPPER.readValue(in, Save.class);
			final MapW map = sav.getMap();
			this.setTimeOfDay(sav.getTime());
			this.loadMap(map);
			this.flushEntityQueue();

			final AbstractEntity contEnt = this.getEntity(sav.getControlled());
			if (contEnt != null) {
				if (contEnt instanceof IControllableEntity) {
					this.controlEntity((IControllableEntity) contEnt);
				} else {
					throw new IllegalStateException("Cannot control entity from save " + contEnt);
				}
			} else {
				Wanderer.LOG.info("No controlled entity in save");
			}

			final Clan pClan = this.getClan(sav.getPlayerClan());
			if (pClan != null) {
				this.setPlayerClan(pClan);
			}
		} catch (final Exception e) {
			Wanderer.LOG.error("Failed to load stateless save", e);
			System.exit(-1);
		}
	}

	public void loadMap(final MapW map) throws ReflectiveOperationException {
		this.removeEntityControl();
		for (final AbstractEntity e : this.entities) {
			e.leaveBtWorld(this.getBtWorld());
			e.dispose();
		}
		this.entities.clear();
		this.clans.clear();
		this.loadMapFragment(map);
	}

	public MapW loadMapFragment(final FileHandle fh) throws ReflectiveOperationException {
		try (InputStream in = fh.read()) {
			final MapW map = WandererConstants.MAPPER.readValue(in, MapW.class);
			this.loadMapFragment(map);
			return map;
		} catch (final IOException e) {
			Wanderer.LOG.error("Failed to load map fragment", e);
			System.exit(-1);
			throw new RuntimeException(e);
		}
	}

	public void loadMapFragment(final MapW map) throws ReflectiveOperationException {
		for (final ObjectNode n : map.getEntities()) {
			AbstractEntity ent = null;
			final Class<?> cls = Class.forName(n.get("@class").asText());
			for (final Constructor<?> c : cls.getConstructors()) {
				if (c.getParameterTypes().length == 2 && c.getParameterTypes()[0] == Wanderer.class
						&& IPrototype.class.isAssignableFrom(c.getParameterTypes()[1])) {
					if (n.get("prototype") == null) {
						Wanderer.LOG.error(
								"Entity {} has constructor w/ prototype but save is missing prototype property",
								cls);
						continue;
					}
					ent = (AbstractEntity) c.newInstance(this,
							PrototypeRegistry.getInstance().get(n.get("prototype").asText()));
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
		if (map.getClans() != null) {
			this.clans.addAll(map.getClans());
		}
	}

	public void startCinematic(final FileHandle fh) throws IOException {
		final List<CinematicData> datas =
				WandererConstants.MAPPER.readValue(fh.read(), new TypeReference<List<CinematicData>>() {
				});
		this.startCinematic(datas);
	}

	public void startCinematic(final List<CinematicData> data) {
		this.cinematicController = new CinematicController(this);
		this.cinematicController.setCinematicDatas(data);
		this.letterboxOverlay.start();
		this.setHUDVisible(false);
		this.cinematicControlledEntity = this.controlledEntity;
		this.removeEntityControl();
	}

	public void stopCinematic() {
		this.cinematicController = null;
		if (this.letterboxOverlay.isStarted()) {
			this.letterboxOverlay.stop();
		}
		this.setHUDVisible(true);
		if (this.cinematicControlledEntity != null) {
			this.controlEntity(this.cinematicControlledEntity);
			this.cinematicControlledEntity = null;
		}
	}

	public void setHUDVisible(final boolean vis) {
		this.energyBar.setVisible(vis);
		this.itemBar.setVisible(vis);
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
		this.showMessage(building.getControlMessage());
		WandererConstants.ASSET_MANAGER.get("orig/feedback/use_ok.wav", Sound3D.class).playGeneral();
	}

	public void removeInteractBuilding() {
		this.interactingBuilding = null;
	}

	public AbstractBuilding getInteractingBuilding() {
		return this.interactingBuilding;
	}

	public float getElapsedTimeOfDay() {
		return this.elapsedTimeOfDay;
	}

	public void setElapsedTimeOfDay(final float elapsedTimeOfDay) {
		this.elapsedTimeOfDay = elapsedTimeOfDay;
	}

	/**
	 * @return progress through the day/night between 0 and 1
	 */
	public float getTimeOfDay() {
		return this.timeOfDay;
	}

	/**
	 * @param timeOfDay progress through the day/night between 0 and 1
	 */
	public void setTimeOfDay(final float timeOfDay) {
		this.timeOfDay = timeOfDay;

		this.elapsedTimeOfDay = this.timeOfDay * this.dayDuration;
	}

	/**
	 * @return number of seconds for a full day-night cycle
	 */
	public float getDayDuration() {
		return this.dayDuration;
	}

	/**
	 * @param dayDuration number of seconds for a full day-night cycle
	 */
	public void setDayDuration(final float dayDuration) {
		this.dayDuration = dayDuration;

		this.elapsedTimeOfDay = this.dayDuration * this.timeOfDay;
	}

	public CinematicController getCinematicController() {
		return this.cinematicController;
	}

	public AbstractEntity findFirstEntityByClass(final Class<? extends AbstractEntity> cls) {
		return this.findEntitiesByClass(cls).findFirst().orElse(null);
	}

	@SuppressWarnings("unchecked")
	public <T extends AbstractEntity> Stream<T> findEntitiesByClass(final Class<T> cls) {
		// This game of casts looks redundant but it's not!
		// Using Stream.of(this.entities.items) causes a ClassCastException
		// This is due to an implicit (AbtractEntity[]) this.entities.items added by the
		// compiler that will always fail!
		// GDX's Array<T>#items has a T[] type, which the compiler will always compile
		// as Object[]
		return Stream.of((Object[]) this.entities.items).filter(e -> cls.isInstance(e)).map(e -> (T) e);
	}

	public void showMessage(final String message) {
		this.messageLabel.setColor(1f, 1f, 1f, 0f);
		this.messageLabel.setText(message);
		this.messageLabel.blink();
	}

	public LetterboxOverlay getLetterboxOverlay() {
		return this.letterboxOverlay;
	}

	public Subtitle getSubtitle() {
		return this.subtitle;
	}

	public boolean isPaused() {
		return this.paused;
	}

	@Override
	public void pause() {
		this.paused = true;
		this.pauseControlledEntity = this.controlledEntity;
		if (this.controlledEntity != null) {
			this.removeEntityControl();
		}

		if (this.cinematicController != null) {
			this.cinematicController.pause();
		}
	}

	@Override
	public void resume() {
		this.paused = false;
		if (this.pauseControlledEntity != null) {
			this.controlEntity(this.pauseControlledEntity);
			this.pauseControlledEntity = null;
		}

		if (this.cinematicController != null) {
			this.cinematicController.resume();
		}
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
		if (this.consoleHandler != null) {
			try {
				this.consoleHandler.close();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	public ConsoleHandler getConsoleHandler() {
		return this.consoleHandler;
	}

	public btDiscreteDynamicsWorld getBtWorld() {
		return this.physicsManager.getBtWorld();
	}

	public PerspectiveCamera getCamera() {
		return this.graphicsManager.getCamera();
	}

	public boolean isAudioEmittersDebug() {
		return this.audioEmittersDebug;
	}

	public void setAudioEmittersDebug(final boolean audioEmittersDebug) {
		this.audioEmittersDebug = audioEmittersDebug;
	}
}
