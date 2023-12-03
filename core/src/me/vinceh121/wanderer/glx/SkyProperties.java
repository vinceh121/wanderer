package me.vinceh121.wanderer.glx;

import java.util.NavigableMap;
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
	private final NavigableMap<Float, Float> starsOpacity = new TreeMap<>();
	private final NavigableMap<Float, Float> galaxyOpacity = new TreeMap<>();

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

	public NavigableMap<Float, Float> getStarsOpacity() {
		return starsOpacity;
	}

	public NavigableMap<Float, Float> getGalaxyOpacity() {
		return galaxyOpacity;
	}

	@Override
	public String toString() {
		return "SkyProperties [sunColor=" + sunColor + ", sunLightColor=" + sunLightColor + ", ambLightColor="
				+ ambLightColor + ", skyTopColor=" + skyTopColor + ", skyMiddleColor=" + skyMiddleColor
				+ ", skyBottomColor=" + skyBottomColor + ", sunShineColor=" + sunShineColor + ", starsOpacity="
				+ starsOpacity + ", galaxyOpacity=" + galaxyOpacity + "]";
	}
}
