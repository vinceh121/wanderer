package me.vinceh121.wanderer.platform.audio;

import com.badlogic.gdx.Audio;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector3;

public interface AudioSystem3D extends Audio {

	Sound3D newSound3D(final FileHandle fileHandle);

	void setListenerOrientation(final Vector3 at, final Vector3 up);

	void setListenerVelocity(final Vector3 vel);

	Vector3 getListenerPosition();

	void setListenerPosition(final Vector3 pos);

}
