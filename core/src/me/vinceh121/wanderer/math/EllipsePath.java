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

	public EllipsePath(Circle circle) {
		super(circle);
	}

	public EllipsePath(Ellipse ellipse) {
		super(ellipse);
	}

	public EllipsePath(float x, float y, float width, float height) {
		super(x, y, width, height);
	}

	public EllipsePath(Vector2 position, float width, float height) {
		super(position, width, height);
	}

	public EllipsePath(Vector2 position, Vector2 size) {
		super(position, size);
	}

	@Override
	public Vector2 derivativeAt(Vector2 out, float t) {
		throw new UnsupportedOperationException("EllipsePath#derivativeAt(out, t) is not implemented");
	}

	@Override
	public Vector2 valueAt(Vector2 out, float t) {
		final float rad = t * MathUtils.PI2;

		return this.valueAtRad(out, rad);
	}

	public Vector2 valueAtRad(Vector2 out, float rad) {
		out.set(this.width * MathUtils.cos(rad), this.height * MathUtils.sin(rad));
		out.add(this.x, this.y);

		return out;
	}

	@Override
	public float approximate(Vector2 v) {
		throw new UnsupportedOperationException("EllipsePath#approximate(v) is not implemented");
	}

	@Override
	public float locate(Vector2 v) {
		throw new UnsupportedOperationException("EllipsePath#locate(v) is not implemented");
	}

	@Override
	public float approxLength(int samples) {
		throw new UnsupportedOperationException("EllipsePath#approxLength(samples) is not implemented");
	}

}
