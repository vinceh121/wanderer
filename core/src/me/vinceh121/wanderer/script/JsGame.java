package me.vinceh121.wanderer.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.commonjs.module.ModuleScope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.IPrototype;
import me.vinceh121.wanderer.PrototypeRegistry;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.WandererConstants;
import me.vinceh121.wanderer.building.AbstractBuildingPrototype;
import me.vinceh121.wanderer.building.BuildingArtifactEntity;
import me.vinceh121.wanderer.clan.Amicability;
import me.vinceh121.wanderer.clan.Clan;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.glx.SkyProperties;
import me.vinceh121.wanderer.glx.SkyboxRenderer;

public class JsGame {
	private static final Logger LOG = LogManager.getLogger(JsGame.class);
	private final Wanderer game;

	public JsGame(final Wanderer game) {
		this.game = game;
	}

	public void install(final Scriptable scope) {
		scope.put("game", scope, this.game);

		JsUtils.install(scope, "getElapsedTimeOfDay", this.game::getElapsedTimeOfDay);
		JsUtils.install(scope, "setElapsedTimeOfDay", this.game::setElapsedTimeOfDay);
		JsUtils.install(scope, "getDayDuration", this.game::getDayDuration);
		JsUtils.install(scope, "setDayDuration", this.game::setDayDuration);
		JsUtils.install(scope, "getTimeOfDay", this.game::getTimeOfDay);
		JsUtils.install(scope, "setTimeOfDay", this.game::setTimeOfDay);
		JsUtils.install(scope, "getPlayerClan", this.game::getPlayerClan);
		JsUtils.install(scope, "controlEntity", this.game::controlEntity);
		JsUtils.install(scope, "getControlledEntity", this.game::getControlledEntity);
		JsUtils.install(scope, "showMessage", this.game::showMessage);
		JsUtils.install(scope, "getClanForMemeber", this.game::getClanForMember);
		JsUtils.install(scope, "removeEntity", this.game::removeEntity);
		JsUtils.install(scope, "findFirstEntityByClass", this.game::findFirstEntityByClass);

		JsUtils.install(scope, "findEntitiesByClass", this::findEntitiesByClass);

		JsUtils.install(scope, "loadMapFragment", this::loadMapFragment);
		JsUtils.install(scope, "setDayTime", this::setDayTime);
		JsUtils.install(scope, "playCinematic", this::playCinematic);
		JsUtils.install(scope, "spawn", this::spawn);
		JsUtils.install(scope, "newv", this::newv);
		JsUtils.install(scope, "newvEnemy", this::newvEnemy);
		JsUtils.install(scope, "setSky", this::setSky);

		JsUtils.install(scope, "debugAudio", this::debugAudio);
	}

	@SuppressWarnings("unchecked")
	private Stream<AbstractEntity> findEntitiesByClass(final Class<? extends AbstractEntity> cls) {
		return (Stream<AbstractEntity>) this.game.findEntitiesByClass(cls);
	}

	private void debugAudio() {
		this.game.setAudioEmittersDebug(!this.game.isAudioEmittersDebug());
		JsGame.LOG.info("Audio debug now {}", this.game.isAudioEmittersDebug());
	}

	private void setSky(final NativeObject skyRaw) {
		final SkyProperties sky = WandererConstants.MAPPER.convertValue(skyRaw, SkyProperties.class);
		this.game.getGraphicsManager().getSkybox().setSkyProperties(sky);
	}

	private Object newv(final String prototypeName, Integer count) {
		if (this.game.getControlledEntity() == null) {
			JsGame.LOG.error("Cannot use newv when no entity is being controlled");
			return null;
		}

		if (count == null) {
			count = 1;
		}

		final IPrototype prototype = PrototypeRegistry.getInstance().get(prototypeName);

		if (prototype == null) {
			JsGame.LOG.error("Unknown prototype {}", prototypeName);
			return null;
		}

		final AbstractEntity controlled = (AbstractEntity) this.game.getControlledEntity();

		final List<AbstractEntity> entities = new ArrayList<>(count);

		for (int i = 0; i < count; i++) {
			final AbstractEntity entity;

			if (prototype instanceof AbstractBuildingPrototype) {
				entity = new BuildingArtifactEntity(this.game, (AbstractBuildingPrototype) prototype);
			} else {
				entity = prototype.create(this.game);
			}

			entity.setTranslation(new Vector3(0, 4, 4).mul(controlled.getRotation()).add(controlled.getTranslation()));
			this.game.addEntity(entity);

			if (entity instanceof IClanMember) {
				this.game.getPlayerClan().addMember((IClanMember) entity);
			}
		}

		if (entities.size() == 1) {
			return entities.get(0);
		} else {
			return entities;
		}
	}

	private Object newvEnemy(final String prototypeName) {
		if (this.game.getControlledEntity() == null) {
			JsGame.LOG.error("Cannot use newv when no entity is being controlled");
			return null;
		}

		final IPrototype prototype = PrototypeRegistry.getInstance().get(prototypeName);

		if (prototype == null) {
			JsGame.LOG.error("Unknown prototype {}", prototypeName);
			return null;
		}

		final AbstractEntity controlled = (AbstractEntity) this.game.getControlledEntity();

		final AbstractEntity entity;

		if (prototype instanceof AbstractBuildingPrototype) {
			entity = new BuildingArtifactEntity(this.game, (AbstractBuildingPrototype) prototype);
		} else {
			entity = prototype.create(this.game);
		}

		entity.setTranslation(new Vector3(0, 10, 4).mul(controlled.getRotation()).add(controlled.getTranslation()));

		if (entity instanceof IClanMember) {
			Clan enemyClan = this.game.getClansByName("newvEnemy").findFirst().orElse(null);

			if (enemyClan == null) {
				enemyClan = new Clan();
				enemyClan.setName("newvEnemy");
				enemyClan.setColor(new Color(1, 0, 0, 0));
				enemyClan.setDefaultAmicability(Amicability.HOSTILE);
				this.game.addClan(enemyClan);
			}

			enemyClan.addMember((IClanMember) entity);

			this.game.addEntity(entity);

			return entity;
		} else {
			LOG.error("Requested entity {} isn't a clan member. Won't spawn as enemy.", prototypeName);
			entity.dispose();
			return null;
		}
	}

	private Object loadMapFragment(final Context cx, final Scriptable scope, final Scriptable thisObj,
			final Object[] args) {
		final FileHandle path = JsGame.resolveMaybeRalativePath((String) args[0], thisObj);
		try {
			return this.game.loadMapFragment(path);
		} catch (final ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private void setDayTime(final Number hours, final Number mins) {
		this.game.setTimeOfDay(SkyboxRenderer.toDayProgress(hours.intValue(), mins.intValue()));
	}

	private Object playCinematic(final Context cx, final Scriptable scope, final Scriptable thisObj,
			final Object[] args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("playCinematic needs 1 argument");
		}

		try {
			this.game.startCinematic(JsGame.resolveMaybeRalativePath((String) args[0], thisObj));
			return this.game.getCinematicController();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private Object spawn(final Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
		if (args.length < 1) {
			throw new IllegalArgumentException("spawn needs arguments");
		}

		final String prototypeName = (String) args[0];
		final IPrototype prototype = PrototypeRegistry.getInstance().get(prototypeName);
		if (prototype == null) {
			return Undefined.instance;
		}

		final AbstractEntity ent = prototype.create(this.game);
		this.game.addEntity(ent);

		return ent;
	}

	private static FileHandle resolveMaybeRalativePath(String path, final Scriptable thisObj) {
		if (path.startsWith("./")) {
			final ModuleScope module = JsUtils.getModuleScope(thisObj);
			final FileHandle parent = FileHandleModuleSourceProvider.fromURI(module.getUri()).parent();
			path = parent + path.substring(1);
		}
		return Gdx.files.internal(path);
	}
}
