package me.vinceh121.wanderer;

/**
 * TODO: I'd want to fit some other info in there, like timestamp, or entity
 * type. But I can't seem to be able to do that in 32 bits.
 *
 * Oh well, an increment also works I guess.
 */
public class ID implements Comparable<ID> {
	public static final ID NULL_ID = new ID(-1);
	public static int increment;

	private final int value;

	public ID() {
		this(ID.newValue());
	}

	public ID(final int value) {
		this.value = value;
	}

	public int getValue() {
		return this.value;
	}

	@Override
	public int hashCode() {
		return this.value;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if ((obj == null) || (this.getClass() != obj.getClass())) {
			return false;
		}
		final ID other = (ID) obj;
		return this.value == other.value;
	}

	@Override
	public int compareTo(final ID o) {
		return Integer.compare(o.value, this.value);
	}

	@Override
	public String toString() {
		return Integer.toHexString(this.value);
	}

	public static int newValue() {
		return ID.increment++;
	}
}
