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
import me.vinceh121.wanderer.event.Event;
import me.vinceh121.wanderer.event.EventDispatcher;
import me.vinceh121.wanderer.event.IEventListener;
import me.vinceh121.wanderer.platform.audio.SoundEmitter3D;
import me.vinceh121.wanderer.util.MathUtilsW;

public class CinematicController {
	private static final Logger LOG = LogManager.getLogger(CinematicController.class);
	public static final String CAMERA_SYMBOLIC_NAME = "_camera";
	private final EventDispatcher eventDispatcher = new EventDispatcher();
	private final Wanderer game;
	private final List<CinematicData> cinematicDatas = new ArrayList<>();
	private final List<SoundEmitter3D> sounds = new ArrayList<>();
	private float time, startTime, endTime, rate = 1f;
	private boolean hasCamera, hasControlTaken, overTriggered;

	public CinematicController(final Wanderer game) {
		this.game = game;
	}

	public void skip() {
		this.update(this.endTime - this.time);
		for (final SoundEmitter3D s : this.sounds) {
			s.stop();
			s.dispose();
		}
	}

	public void update(final float delta) {
		final float newTime = this.time + delta * this.rate;
		for (final CinematicData d : this.cinematicDatas) {
			this.updateTrack(delta, newTime, d);
		}
		this.time = newTime;

		if (this.isOver()) {
			this.onOver();
		}
	}

	private void updateTrack(final float delta, final float newTime, final CinematicData data) {
		final boolean isCamera = CinematicController.CAMERA_SYMBOLIC_NAME.equals(data.getSymbolicName());

		if (this.time > data.getEndTime()) {
			return;
		}

		if (data.getCacheEntity() == null && data.getSymbolicName() != null && !isCamera) {
			final AbstractEntity ent = this.game.getEntity(data.getSymbolicName());
			if (ent == null) {
				CinematicController.LOG.error("No entity goes by symbolicName {}", data.getSymbolicName());
				return;
			}
			data.setCacheEntity(ent);
		}

		// process positional tracks only if we have an entity linked
		final Vector3 pos = data.getPosition().interpolate(newTime);
		if (pos != null) {
			MathUtilsW.fixNaN(pos, 0);
		}

		final Quaternion rot = data.getRotation().interpolate(newTime);
		if (rot != null) {
			MathUtilsW.fixNaNIdt(rot);
			rot.conjugate();
		}

		final Vector3 scl = data.getScale().interpolate(newTime);
		if (scl != null) {
			MathUtilsW.fixNaN(scl, 0);
		}

		if (isCamera) {
			if (pos != null) {
				this.game.getCamera().position.set(pos);
			}
			if (rot != null) {
				// is the up vector of nebula really this?
				this.game.getCamera().direction.set(rot.transform(new Vector3(0, 0, -1)));
			}

			this.game.getCamera().up.set(Vector3.Y);
			this.game.getCamera().update();
		} else if (data.getCacheEntity() != null) {
			final Matrix4 trans = new Matrix4(pos == null ? new Vector3() : pos,
					rot == null ? new Quaternion() : rot,
					scl == null ? new Vector3(1, 1, 1) : scl);

			data.getCacheEntity().setTransform(trans);
		}

		// process action track regardless of whether we have an entity or not
		final NavigableMap<Float, ActionKeyFrame> actions = data.getActions().inBetween(this.time, newTime);

		for (final ActionKeyFrame action : actions.values()) {
			action.action(this.game, this, data.getCacheEntity(), newTime);
		}
	}

	public void pause() {
		for (final SoundEmitter3D e : this.sounds) {
			if (!e.isDisposed()) {
				e.pause();
			}
		}
	}

	public void resume() {
		for (final SoundEmitter3D e : this.sounds) {
			if (!e.isDisposed()) {
				e.play();
			}
		}
	}

	private void onOver() {
		if (this.overTriggered) {
			return;
		}
		this.eventDispatcher.dispatchEvent(new Event("over"));
		this.overTriggered = true;
	}

	private void updateStartEnd() {
		this.startTime = this.cinematicDatas.stream()
			.map(CinematicData::getStartTime)
			.filter(t -> !Float.isNaN(t))
			.min(Float::compareTo)
			.get();
		this.endTime = this.cinematicDatas.stream()
			.map(CinematicData::getEndTime)
			.filter(t -> !Float.isNaN(t))
			.max(Float::compareTo)
			.get();

		this.hasCamera =
				this.cinematicDatas.stream().anyMatch(data -> CinematicController.CAMERA_SYMBOLIC_NAME.equals(data.getSymbolicName()));
	}

	public void reset() {
		this.cinematicDatas.clear();
		this.eventDispatcher.getListeners().clear();
		this.overTriggered = false;
	}

	public float getStartTime() {
		return this.startTime;
	}

	public float getEndTime() {
		return this.endTime;
	}

	public boolean hasCamera() {
		return this.hasCamera;
	}

	public boolean isHasControlTaken() {
		return this.hasControlTaken;
	}

	public void setHasControlTaken(final boolean hasControlTaken) {
		this.hasControlTaken = hasControlTaken;
	}

	public boolean isOver() {
		return this.getTime() > this.getEndTime();
	}

	public void addSound(final SoundEmitter3D emitter) {
		this.sounds.add(emitter);
	}

	public List<CinematicData> getCinematicDatas() {
		return this.cinematicDatas;
	}

	public void setCinematicDatas(final List<CinematicData> datas) {
		this.reset();
		this.cinematicDatas.addAll(datas);
		this.updateStartEnd();
	}

	public float getTime() {
		return this.time;
	}

	public void setTime(final float time) {
		this.time = time;
	}

	public float getRate() {
		return this.rate;
	}

	public void setRate(final float rate) {
		this.rate = rate;
		for (final SoundEmitter3D e : this.sounds) {
			if (!e.isDisposed()) {
				e.setPitch(rate);
			}
		}
	}

	public void addEventListener(final String type, final IEventListener l) {
		this.eventDispatcher.addEventListener(type, l);
	}

	public void removeEventListener(final String type, final IEventListener l) {
		this.eventDispatcher.removeEventListener(type, l);
	}
}
