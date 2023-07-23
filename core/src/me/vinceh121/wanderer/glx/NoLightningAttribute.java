package me.vinceh121.wanderer.glx;

import com.badlogic.gdx.graphics.g3d.Attribute;

public class NoLightningAttribute extends Attribute {
	public static final String NO_LIGHTNING_ALIAS = "no_lightning";
	public static final long NO_LIGHTNING = Attribute.register(NoLightningAttribute.NO_LIGHTNING_ALIAS);

	public NoLightningAttribute() {
		super(NoLightningAttribute.NO_LIGHTNING);
	}

	@Override
	public int compareTo(final Attribute o) {
		return 0;
	}

	@Override
	public Attribute copy() {
		return new NoLightningAttribute();
	}
}
