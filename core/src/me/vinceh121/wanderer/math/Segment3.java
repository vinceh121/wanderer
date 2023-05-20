package me.vinceh121.wanderer.math;

import java.util.Objects;

import com.badlogic.gdx.math.Vector3;

public class Segment3 {
	private final Vector3 start = new Vector3();
	private final Vector3 end = new Vector3();

	public Segment3() {
	}

	public Segment3(Vector3 start, Vector3 end) {
		this.start.set(start);
		this.end.set(end);
	}

	public Vector3 getStart() {
		return start;
	}

	public Vector3 getEnd() {
		return end;
	}

	public float length() {
		return this.start.dst(this.end);
	}

	public Vector3 set(float x, float y, float z) {
		return start.set(x, y, z);
	}

	public Vector3 set(Vector3 vector) {
		return start.set(vector);
	}

	public Vector3 setEnd(float x, float y, float z) {
		return end.set(x, y, z);
	}

	public Vector3 setStart(Vector3 vector) {
		return end.set(vector);
	}

	@Override
	public int hashCode() {
		return Objects.hash(end, start);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Segment3 other = (Segment3) obj;
		return Objects.equals(end, other.end) && Objects.equals(start, other.start);
	}

	@Override
	public String toString() {
		return "Segment3 [start=" + start + ", end=" + end + "]";
	}
}
