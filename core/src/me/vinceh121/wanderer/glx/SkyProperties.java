package me.vinceh121.wanderer.glx;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;

import me.vinceh121.wanderer.glx.SkyboxRenderer.TimeRange;

public class SkyProperties {
	private final Map<TimeRange, Color> sunColor = new EnumMap<>(TimeRange.class);
	private final Map<TimeRange, Color> sunLightColor = new EnumMap<>(TimeRange.class);
	private final Map<TimeRange, Color> ambLightColor = new EnumMap<>(TimeRange.class);
	private final Map<TimeRange, Color> skyTopColor = new EnumMap<>(TimeRange.class);
	private final Map<TimeRange, Color> skyMiddleColor = new EnumMap<>(TimeRange.class);
	private final Map<TimeRange, Color> skyBottomColor = new EnumMap<>(TimeRange.class);

	public Map<TimeRange, Color> getSunColor() {
		return sunColor;
	}

	public Map<TimeRange, Color> getSunLightColor() {
		return sunLightColor;
	}

	public Map<TimeRange, Color> getAmbLightColor() {
		return ambLightColor;
	}

	public Map<TimeRange, Color> getSkyTopColor() {
		return skyTopColor;
	}

	public Map<TimeRange, Color> getSkyMiddleColor() {
		return skyMiddleColor;
	}

	public Map<TimeRange, Color> getSkyBottomColor() {
		return skyBottomColor;
	}
}
