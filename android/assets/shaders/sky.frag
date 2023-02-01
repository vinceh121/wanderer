precision highp float;

#define PI 3.1415927

#define MORNING 0.0
#define NOON 0.25
#define EVENING_START 0.375
#define EVENING_MID 0.4375
#define EVENING_END 0.5
#define MIDNIGHT 0.75

const vec3 skyTopNoon = vec3(0.09, 0.106, 0.129);
const vec3 skyMiddleNoon = vec3(0.741, 0.871, 0.882);
const vec3 skyBottomNoon = vec3(0.62, 0.663, 0.627);

const vec3 skyTopEvening = skyTopNoon;
const vec3 skyMiddleEvening = vec3(0.949, 0.851, 0.263);

uniform float time;
uniform vec3 sunDir;

in vec4 vertPos;

float logFrac(float f) {
	return log2(f * 255 + 1) / 8;
}

float progress(float start, float end) {
	return (time - start) / (end - start);
}

void main() {
	vec3 normVert = normalize(vertPos.xyz);
	// [-1; 1]
	float angle = dot(normVert, vec3(0, 1, 0));
	float sunSkyAngle = dot(normVert, sunDir);

	if (angle > 0) { // top half
		angle = 1 - angle;
		sunSkyAngle = 1 - sunSkyAngle;
		if (time > MORNING && time < NOON) {
			gl_FragColor = vec4(
					mix(skyTopNoon, skyMiddleNoon,
							logFrac(angle) * progress(MORNING, NOON)), 1);
		} else if (time > NOON && time < EVENING_START) {
			gl_FragColor = vec4(
					mix(skyTopNoon, skyMiddleNoon,
							logFrac(angle) * logFrac(1 - progress(
							NOON,
							EVENING_START))), 1);
		} else if (time > EVENING_START && time < EVENING_MID) {
			gl_FragColor = vec4(
					mix(
							mix(skyTopNoon, skyMiddleNoon,
									min(logFrac(angle), 0.3)),
							mix(skyTopEvening, skyMiddleEvening,
									angle * max(progress(EVENING_START,
									EVENING_MID), 0.9)),
							progress(EVENING_START, EVENING_MID)), 1);
		} else if (time > EVENING_MID && time < EVENING_END) {
			gl_FragColor = vec4(
					mix(mix(skyTopEvening, skyMiddleEvening, angle * 0.9),
							mix(skyTopEvening, skyMiddleEvening,
									angle * progress(EVENING_START,
									EVENING_MID) * (1 - logFrac(sunSkyAngle))),
							progress(EVENING_START, EVENING_MID)), 1);
		}
	} else { // bottom half
		angle += 1;
		if (time > MORNING && time < NOON) {
			gl_FragColor = vec4(
					mix(skyMiddleNoon, skyBottomNoon,
							angle * progress(MORNING, NOON)), 1);
		}
	}
}
