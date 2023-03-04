package me.vinceh121.wanderer.script;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

import me.vinceh121.wanderer.IMeta;
import me.vinceh121.wanderer.MetaRegistry;
import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;

public class JsGame {
	private final Wanderer game;

	public JsGame(Wanderer game) {
		this.game = game;
	}

	public void install(Scriptable scope) {
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

		JsUtils.install(scope, "spawn", this::spawn);
	}

	private Object spawn(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
		if (args.length < 1) {
			throw new IllegalArgumentException("spawn needs arguments");
		}

		String prototype = (String) args[0];
		IMeta meta = MetaRegistry.getInstance().get(prototype);
		if (meta == null) {
			return Undefined.instance;
		}

		AbstractEntity ent = meta.create(this.game);
		this.game.addEntity(ent);

		return ent;
	}
}
