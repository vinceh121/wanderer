package me.vinceh121.wanderer.animation;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;

public class CameraAnimator implements ITransformAnimatable {
	private Camera cam;
	private boolean ignoreRotation;

	public CameraAnimator(final Camera cam) {
		this.cam = cam;
	}

	@Override
	public void setWorldTransform(final Matrix4 transform) {
		transform.getTranslation(this.cam.position);
		if (!this.ignoreRotation) {
			this.cam.direction.set(0, 0, -1);
			this.cam.up.set(0, 1, 0);
			this.cam.rotate(transform);
		}
	}

	public Camera getCam() {
		return this.cam;
	}

	public void setCam(final Camera cam) {
		this.cam = cam;
	}

	public boolean isIgnoreRotation() {
		return this.ignoreRotation;
	}

	public void setIgnoreRotation(final boolean ignoreRotation) {
		this.ignoreRotation = ignoreRotation;
	}
}
