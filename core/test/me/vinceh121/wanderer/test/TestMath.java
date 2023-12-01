package me.vinceh121.wanderer.test;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import me.vinceh121.wanderer.math.Circle3;

class TestMath {

	@Test
	void testCircle3() {
		final Circle3 circ = new Circle3();
		circ.setRadius(1);
		Assertions.assertEquals(new Vector3(1, 0, 0), circ.getPointOn(0, 1));
		Assertions.assertEquals(new Vector3(-1, 0, 0), circ.getPointOn(MathUtils.PI, 1));
		Assertions.assertEquals(new Vector3(0, 0, 0), circ.getPointOn(MathUtils.PI, 0));

		circ.setCenter(new Vector3(10, 10, 10));

		Assertions.assertEquals(new Vector3(9, 10, 10), circ.getPointOn(MathUtils.PI, 1));

		circ.setRotation(new Quaternion(Vector3.Y, 180));

		Assertions.assertEquals(new Vector3(11, 10, 10), circ.getPointOn(MathUtils.PI, 1));

		circ.setRotation(new Quaternion(Vector3.X, 180));

		Assertions.assertEquals(new Vector3(9, 10, 10), circ.getPointOn(MathUtils.PI, 1));
	}
}
