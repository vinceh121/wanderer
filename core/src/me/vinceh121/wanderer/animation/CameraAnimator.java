package me.vinceh121.wanderer.animation;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Matrix4;

public class CameraAnimator implements ITransformAnimatable {
	private Camera cam;
	private boolean ignoreRotation;

	public CameraAnimator(Camera cam) {
		this.cam = cam;
	}

	@Override
	public void setWorldTransform(Matrix4 transform) {
		transform.getTranslation(this.cam.position);
		if (!this.ignoreRotation) {
			cam.direction.set(0, 0, -1);
			cam.up.set(0, 1, 0);
			cam.rotate(transform);
		}
	}

	public Camera getCam() {
		return cam;
	}

	public void setCam(Camera cam) {
		this.cam = cam;
	}

	public boolean isIgnoreRotation() {
		return ignoreRotation;
	}

	public void setIgnoreRotation(boolean ignoreRotation) {
		this.ignoreRotation = ignoreRotation;
	}
}
