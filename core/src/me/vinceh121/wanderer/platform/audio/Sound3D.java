package me.vinceh121.wanderer.platform.audio;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector3;

public interface Sound3D extends Sound {

	boolean isDisposed();

	SoundEmitter3D playSource(final float volume, final Vector3 position);

	SoundEmitter3D playSource(final float volume);

	SoundEmitter3D playSource();

}
