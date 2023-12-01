package me.vinceh121.wanderer.math;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Path;
import com.badlogic.gdx.math.Vector2;

public class EllipsePath extends Ellipse implements Path<Vector2> {
	private static final long serialVersionUID = 9035881773211510268L;

	public EllipsePath() {
	}

	public EllipsePath(final Circle circle) {
		super(circle);
	}

	public EllipsePath(final Ellipse ellipse) {
		super(ellipse);
	}

	public EllipsePath(final float x, final float y, final float width, final float height) {
		super(x, y, width, height);
	}

	public EllipsePath(final Vector2 position, final float width, final float height) {
		super(position, width, height);
	}

	public EllipsePath(final Vector2 position, final Vector2 size) {
		super(position, size);
	}

	@Override
	public Vector2 derivativeAt(final Vector2 out, final float t) {
		throw new UnsupportedOperationException("EllipsePath#derivativeAt(out, t) is not implemented");
	}

	@Override
	public Vector2 valueAt(final Vector2 out, final float t) {
		final float rad = t * MathUtils.PI2;

		return this.valueAtRad(out, rad);
	}

	public Vector2 valueAtRad(final Vector2 out, final float rad) {
		out.set(this.width * MathUtils.cos(rad) + this.x, this.height * MathUtils.sin(rad) + this.y);
		return out;
	}

	@Override
	public float approximate(final Vector2 v) {
		throw new UnsupportedOperationException("EllipsePath#approximate(v) is not implemented");
	}

	@Override
	public float locate(final Vector2 v) {
		throw new UnsupportedOperationException("EllipsePath#locate(v) is not implemented");
	}

	@Override
	public float approxLength(final int samples) {
		throw new UnsupportedOperationException("EllipsePath#approxLength(samples) is not implemented");
	}

}
