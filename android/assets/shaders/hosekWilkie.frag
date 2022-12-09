/**
 * HOSEK, Lukas et WILKIE, Alexander. An analytic model for full spectral
 * sky-dome radiance. ACM Transactions on Graphics (TOG), 2012, vol. 31, no 4,
 * p. 1-9.
 *
 * Implementation mostly adapted from Blender's shader
 * <intern/cycles/kernel/osl/shaders/node_sky_texture.osl>, under Apache License 2.0
 * <https://www.apache.org/licenses/LICENSE-2.0.html>
 */
#define PI 3.1415927
#define PI2 6.2831855
#define HALF_PI 1.5707964

uniform vec3 camDir;
uniform float sunAzimuth;
uniform float sunPolar;
uniform vec3 radiance;
uniform float configX[9];
uniform float configY[9];
uniform float configZ[9];

float atan2(float x, float y) {
	if (x > 0) {
		return atan(y, x);
	} else if (x < 0 && y >= 0) {
		return atan(y, x) + PI;
	} else if (x < 0 && y < 0) {
		return atan(y, x) - PI;
	} else if (x == 0 && y > 0) {
		return HALF_PI;
	} else if (x == 0 && y < 0) {
		return -HALF_PI;
	} else if (x == 0 && y == 0) {
		return 0; // undefined
	}
}

vec2 spherical(vec3 direction) {
	return vec2(acos(direction.y), atan2(direction.x, direction.z));
}

float polarAngle(float polarA, float azimuthA, float polarB, float azimuthB) {
	float cospsi = sin(polarA) * sin(polarB) * cos(azimuthB - azimuthA)
			+ cos(polarA) * cos(polarB);

	if (cospsi > 1.0)
		return 0.0;
	if (cospsi < -1.0)
		return PI;

	return acos(cospsi);
}

float skyRadiance(float config[9], float polar, float angle) {
	float cpolar = cos(polar);
	float cangle = cos(angle);

	float expM = exp(config[4] * angle);
	float rayM = cangle * cangle;
	float mieM = (1.0 + rayM)
			/ pow((1.0 + config[8] * config[8] - 2.0 * config[8] * cangle),
					1.5);
	float zenith = sqrt(cpolar);

	return (1.0 + config[0] * exp(config[1] / (cpolar + 0.01)))
			* (config[2] + config[3] * expM + config[5] * rayM
					+ config[6] * mieM + config[7] * zenith);
}

vec3 xyz_to_rgb(float x, float y, float z) { // wtf?
	return vec3(3.240479 * x + -1.537150 * y + -0.498535 * z,
			-0.969256 * x + 1.875991 * y + 0.041556 * z,
			0.055648 * x + -0.204043 * y + 1.057311 * z);
}

void main() {
	vec2 sph = spherical(camDir);
	float camPolar = sph.x;
	float camAzimuth = sph.y;

	float camSunAngle = polarAngle(camAzimuth, camPolar, sunAzimuth, sunPolar);

	camAzimuth = min(camPolar, PI2);

	float x = skyRadiance(configX, camPolar, camSunAngle) * radiance.x;
	float y = skyRadiance(configY, camPolar, camSunAngle) * radiance.y;
	float z = skyRadiance(configZ, camPolar, camSunAngle) * radiance.z;

	gl_FragColor = vec4(xyz_to_rgb(x, y, z) * (PI2 / 683), 1); // the divider is an adjuster of strength, should this be variable?
}
