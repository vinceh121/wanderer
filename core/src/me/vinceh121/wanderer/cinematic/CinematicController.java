package me.vinceh121.wanderer.cinematic;

import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.Wanderer;
import me.vinceh121.wanderer.entity.AbstractEntity;
import me.vinceh121.wanderer.util.MathUtilsW;

public class CinematicController {
	private static final Logger LOG = LogManager.getLogger(CinematicController.class);
	private final Wanderer game;
	private final List<CinematicData> cinematicDatas = new ArrayList<>();
	private float time, start, end;

	public CinematicController(Wanderer game) {
		this.game = game;
	}

	public void update(float delta) {
		final float newTime = this.time + delta;
		for (CinematicData d : this.cinematicDatas) {
			this.updateTrack(delta, newTime, d);
		}
		this.time = newTime;
	}

	private void updateTrack(float delta, float newTime, CinematicData data) {
		if (data.getCacheEntity() == null && data.getSymbolicName() != null) {
			AbstractEntity ent = this.game.getEntity(data.getSymbolicName());
			if (ent == null) {
				LOG.error("No entity goes by symbolicName {}", data.getSymbolicName());
				return;
			}
			data.setCacheEntity(ent);
		}

		// process positional tracks only if we have an entity linked
		if (data.getCacheEntity() != null) {
			Vector3 pos = data.getPosition().interpolate(newTime);
			MathUtilsW.fixNaN(pos, 0);

			Quaternion rot = data.getRotation().interpolate(newTime);
			Vector3 scl = data.getScale().interpolate(newTime);

			Matrix4 trans = new Matrix4(pos == null ? new Vector3() : pos,
					rot == null ? new Quaternion() : rot,
					scl == null ? new Vector3(1, 1, 1) : scl);

			data.getCacheEntity().setTransform(trans);
		}

		// process action track regardless of whether we have an entity or not
		NavigableMap<Float, ActionKeyFrame> actions = data.getActions().inBetween(this.time, newTime);

		for (ActionKeyFrame action : actions.values()) {
			action.action(game, data.getCacheEntity(), newTime);
		}
	}

	private void updateStartEnd() {
		this.start = this.cinematicDatas.stream()
			.map(CinematicData::getStartTime)
			.filter(t -> !Float.isNaN(t))
			.min(Float::compareTo)
			.get();
		this.end = this.cinematicDatas.stream()
			.map(CinematicData::getEndTime)
			.filter(t -> !Float.isNaN(t))
			.max(Float::compareTo)
			.get();
	}

	public float getStartTime() {
		return start;
	}

	public float getEndTime() {
		return end;
	}

	public boolean isOver() {
		return this.getTime() > this.getEndTime();
	}

	public List<CinematicData> getCinematicDatas() {
		return cinematicDatas;
	}

	public void setCinematicDatas(List<CinematicData> datas) {
		this.cinematicDatas.clear();
		this.cinematicDatas.addAll(datas);
		this.updateStartEnd();
	}

	public float getTime() {
		return time;
	}

	public void setTime(float time) {
		this.time = time;
	}
}
