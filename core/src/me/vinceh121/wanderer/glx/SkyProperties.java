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
	private final NavigableMap<Float, Color> starsColor = new TreeMap<>();
	private final NavigableMap<Float, Color> galaxyColor = new TreeMap<>();

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

	public NavigableMap<Float, Color> getStarsColor() {
		return starsColor;
	}

	public NavigableMap<Float, Color> getGalaxyColor() {
		return galaxyColor;
	}

	@Override
	public String toString() {
		return "SkyProperties [sunColor=" + sunColor + ", sunLightColor=" + sunLightColor + ", ambLightColor="
				+ ambLightColor + ", skyTopColor=" + skyTopColor + ", skyMiddleColor=" + skyMiddleColor
				+ ", skyBottomColor=" + skyBottomColor + ", sunShineColor=" + sunShineColor + ", starsColor="
				+ starsColor + ", galaxyColor=" + galaxyColor + "]";
	}
}
