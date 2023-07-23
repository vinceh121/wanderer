package me.vinceh121.wanderer.script;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;
import org.mozilla.javascript.commonjs.module.ModuleScope;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import me.vinceh121.wanderer.IMeta;
import me.vinceh121.wanderer.MetaRegistry;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.building.AbstractBuildingMeta;
import me.vinceh121.wanderer.building.BuildingArtifactEntity;
import me.vinceh121.wanderer.clan.IClanMember;
import me.vinceh121.wanderer.entity.AbstractEntity;

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
		JsUtils.install(scope, "findEntitiesByClass", this.game::findEntitiesByClass);

		JsUtils.install(scope, "loadMapFragment", this::loadMapFragment);
		JsUtils.install(scope, "setDayTime", this::setDayTime);
		JsUtils.install(scope, "playCinematic", this::playCinematic);
		JsUtils.install(scope, "spawn", this::spawn);
		JsUtils.install(scope, "newv", this::newv);

		JsUtils.install(scope, "debugAudio", this::debugAudio);
	}

	private void debugAudio() {
		this.game.setAudioEmittersDebug(!this.game.isAudioEmittersDebug());
		JsGame.LOG.info("Audio debug now {}", this.game.isAudioEmittersDebug());
	}

	private AbstractEntity newv(final String metaName) {
		if (this.game.getControlledEntity() == null) {
			JsGame.LOG.error("Cannot use newv when no entity is being controlled");
			return null;
		}
		final IMeta meta = MetaRegistry.getInstance().get(metaName);
		if (meta == null) {
			JsGame.LOG.error("Unknown meta {}", metaName);
			return null;
		}

		final AbstractEntity entity;
		if (meta instanceof AbstractBuildingMeta) {
			entity = new BuildingArtifactEntity(this.game, (AbstractBuildingMeta) meta);
		} else {
			entity = meta.create(this.game);
		}
		entity.setTranslation(((AbstractEntity) this.game.getControlledEntity()).getTranslation().cpy().add(4, 0, 0));
		this.game.addEntity(entity);

		if (entity instanceof IClanMember) {
			this.game.getPlayerClan().addMember((IClanMember) entity);
		}

		return entity;
	}

	private Object loadMapFragment(final Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
		final FileHandle path = JsGame.resolveMaybeRalativePath((String) args[0], thisObj);
		try {
			return this.game.loadMapFragment(path);
		} catch (final ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	private void setDayTime(final Number hours, final Number mins) {
		this.game.setElapsedTimeOfDay(hours.floatValue() * 3600 + mins.floatValue() * 60);
	}

	private Object playCinematic(final Context cx, final Scriptable scope, final Scriptable thisObj, final Object[] args) {
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

		final String prototype = (String) args[0];
		final IMeta meta = MetaRegistry.getInstance().get(prototype);
		if (meta == null) {
			return Undefined.instance;
		}

		final AbstractEntity ent = meta.create(this.game);
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
