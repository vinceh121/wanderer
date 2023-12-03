package me.vinceh121.wanderer.glx;

import java.util.NavigableMap;
import java.util.Objects;
import java.util.TreeMap;

import com.badlogic.gdx.graphics.Color;

public class SkyProperties {
	private final NavigableMap<Float, Color> sunColor = new TreeMap<>();
	private final NavigableMap<Float, Color> sunLightColor = new TreeMap<>();
	private final NavigableMap<Float, Color> ambLightColor = new TreeMap<>();
	private final NavigableMap<Float, Color> skyTopColor = new TreeMap<>();
	private final NavigableMap<Float, Color> skyMiddleColor = new TreeMap<>();
	private final NavigableMap<Float, Color> skyBottomColor = new TreeMap<>();
	private final NavigableMap<Float, Color> sunShineColor = new TreeMap<>();

	public NavigableMap<Float, Color> getSunColor() {
		return sunColor;
	}

	public NavigableMap<Float, Color> getSunLightColor() {
		return sunLightColor;
	}

	public NavigableMap<Float, Color> getAmbLightColor() {
		return ambLightColor;
	}

	public NavigableMap<Float, Color> getSkyTopColor() {
		return skyTopColor;
	}

	public NavigableMap<Float, Color> getSkyMiddleColor() {
		return skyMiddleColor;
	}

	public NavigableMap<Float, Color> getSkyBottomColor() {
		return skyBottomColor;
	}

	public NavigableMap<Float, Color> getSunShineColor() {
		return sunShineColor;
	}

	@Override
	public int hashCode() {
		return Objects
			.hash(ambLightColor, skyBottomColor, skyMiddleColor, skyTopColor, sunColor, sunLightColor, sunShineColor);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SkyProperties other = (SkyProperties) obj;
		return Objects.equals(ambLightColor, other.ambLightColor)
				&& Objects.equals(skyBottomColor, other.skyBottomColor)
				&& Objects.equals(skyMiddleColor, other.skyMiddleColor)
				&& Objects.equals(skyTopColor, other.skyTopColor) && Objects.equals(sunColor, other.sunColor)
				&& Objects.equals(sunLightColor, other.sunLightColor)
				&& Objects.equals(sunShineColor, other.sunShineColor);
	}

	@Override
	public String toString() {
		return "SkyProperties [sunColor=" + sunColor + ", sunLightColor=" + sunLightColor + ", ambLightColor="
				+ ambLightColor + ", skyTopColor=" + skyTopColor + ", skyMiddleColor=" + skyMiddleColor
				+ ", skyBottomColor=" + skyBottomColor + ", sunShineColor=" + sunShineColor + "]";
	}
}
