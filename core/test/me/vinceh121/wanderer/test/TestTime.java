package me.vinceh121.wanderer.test;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import me.vinceh121.wanderer.glx.SkyboxRenderer;

class TestTime {
	@Test
	void testToDayProgress() {
		assertEquals(0.75f, SkyboxRenderer.toDayProgress(0));
		assertEquals(0.25f, SkyboxRenderer.toDayProgress(720));
		assertEquals(0.0f, SkyboxRenderer.toDayProgress(360));
		assertEquals(0.75f, SkyboxRenderer.toDayProgress(1440));
	}

	@Test
	void testToDayProgressHour() {
		assertEquals(0.75f, SkyboxRenderer.toDayProgress(0, 0));
		assertEquals(0.25f, SkyboxRenderer.toDayProgress(12, 0));
		assertEquals(0.0f, SkyboxRenderer.toDayProgress(6, 0));
		assertEquals(0.75f, SkyboxRenderer.toDayProgress(24, 0));
	}
}
