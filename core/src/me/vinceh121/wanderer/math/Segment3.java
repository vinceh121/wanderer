package me.vinceh121.wanderer.math;

import java.util.Objects;

import com.badlogic.gdx.math.Vector3;

public class Segment3 {
	private final Vector3 start = new Vector3();
	private final Vector3 end = new Vector3();

	public Segment3() {
	}

	public Segment3(final Vector3 start, final Vector3 end) {
		this.start.set(start);
		this.end.set(end);
	}

	public Vector3 getStart() {
		return this.start;
	}

	public Vector3 getEnd() {
		return this.end;
	}

	public float length() {
		return this.start.dst(this.end);
	}

	public Vector3 set(final float x, final float y, final float z) {
		return this.start.set(x, y, z);
	}

	public Vector3 set(final Vector3 vector) {
		return this.start.set(vector);
	}

	public Vector3 setEnd(final float x, final float y, final float z) {
		return this.end.set(x, y, z);
	}

	public Vector3 setStart(final Vector3 vector) {
		return this.end.set(vector);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.end, this.start);
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final Segment3 other = (Segment3) obj;
		return Objects.equals(this.end, other.end) && Objects.equals(this.start, other.start);
	}

	@Override
	public String toString() {
		return "Segment3 [start=" + this.start + ", end=" + this.end + "]";
	}
}
