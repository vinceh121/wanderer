precision highp float;

#define MORNING 0.0
#define NOON 0.25
#define EVENING_START 0.375
#define EVENING_MID 0.4375
#define EVENING_END 0.5
#define MIDNIGHT_START 0.625
#define MIDNIGHT 0.75

const vec3 skyTopNoon = vec3(0.09, 0.106, 0.129);
const vec3 skyMiddleNoon = vec3(0.741, 0.871, 0.882);
const vec3 skyBottomNoon = vec3(0.62, 0.663, 0.627);

const vec3 skyTopEvening = skyTopNoon;
const vec3 skyMiddleEvening = vec3(0.949, 0.851, 0.263);
const vec3 skyBottomEvening = skyBottomNoon;

const vec3 skyTopMidnight = vec3(0.054902, 0.086275, 0.121569);
const vec3 skyMiddleMidnight = vec3(0.266667, 0.321569, 0.337255);
const vec3 skyBottomMidnight = vec3(0.141176, 0.145098, 0.149020);

uniform float time;
uniform vec3 sunDir;

in vec4 vertPos;

float logFrac(float f) {
	return log2(f * 255 + 1) / 8;
}

float progress(float start, float end) {
	return (time - start) / (end - start);
}

float angle;
float sunSkyAngle;

vec3 topMorning() {
	return mix(skyTopNoon, skyMiddleNoon,
			logFrac(angle) * progress(MORNING, NOON));
}

vec3 bottomMorning() {
	return mix(mix(skyTopNoon, skyBottomNoon, progress(MORNING, NOON)),
			topMorning(), logFrac(angle));
}

vec3 topNoon() {
	return mix(skyTopNoon, skyMiddleNoon, logFrac(angle));
}

vec3 bottomNoon() {
	return mix(skyBottomNoon, topNoon(), angle);
}

vec3 topEveningStart() {
	return mix(skyTopEvening, skyMiddleEvening, angle);
}

vec3 bottomEveningStart() {
	return mix(skyBottomNoon, topEveningStart(), angle);
}

vec3 topEveningMid() {
	return mix(skyTopEvening, skyMiddleEvening, angle * progress(EVENING_MID,
	EVENING_END) * (1 - logFrac(sunSkyAngle)));
}

vec3 bottomEveningMid() {
	return mix(skyBottomEvening, topEveningMid(), angle);
}

vec3 topMidnightStart() {
	return mix(skyTopMidnight, skyMiddleMidnight, 1 - sunSkyAngle);
}

vec3 bottomMidnightStart() {
	return mix(skyBottomMidnight, topMidnightStart(), angle);
}

vec3 topMidnight() {
	return mix(skyTopMidnight, skyMiddleMidnight, angle);
}

vec3 bottomMidnight() {
	return mix(skyBottomMidnight, skyMiddleMidnight, angle);
}

void main() {
	vec3 normVert = normalize(vertPos.xyz);
	// [-1; 1]
	angle = dot(normVert, vec3(0, 1, 0));

	if (angle > 0) { // top half
		angle = 1 - angle;
		sunSkyAngle = dot(normVert, sunDir);
		sunSkyAngle = 1 - sunSkyAngle;
		if (time > MORNING && time < NOON) {
			gl_FragColor = vec4(topMorning(), 1);
		} else if (time > NOON && time < EVENING_START) {
			gl_FragColor = vec4(
					mix(topMorning(), topNoon(), progress(NOON, EVENING_START)),
					1);
		} else if (time > EVENING_START && time < EVENING_MID) {
			gl_FragColor = vec4(
					mix(topNoon(), topEveningStart(),
							progress(EVENING_START, EVENING_MID)), 1);
		} else if (time > EVENING_MID && time < EVENING_END) {
			gl_FragColor = vec4(
					mix(topEveningStart(), topEveningMid(),
							progress(EVENING_MID, EVENING_END)), 1);
		} else if (time > EVENING_END && time < MIDNIGHT_START) {
			gl_FragColor = vec4(
					mix(topEveningMid(), topMidnightStart(),
							progress(EVENING_END, MIDNIGHT_START)), 1);
		} else if (time > MIDNIGHT_START && time < MIDNIGHT) {
			gl_FragColor = vec4(
					mix(topMidnightStart(), topMidnight(),
							progress(MIDNIGHT_START, MIDNIGHT)), 1);
		}
	} else { // bottom half
		angle += 1;
		// need to invert one of those dirs
		sunSkyAngle = dot(-1 * normVert, sunDir);
		sunSkyAngle += 1;
		if (time > MORNING && time < NOON) {
			gl_FragColor = vec4(bottomMorning(), 1);
		} else if (time > NOON && time < EVENING_START) {
			gl_FragColor = vec4(
					mix(bottomMorning(), bottomNoon(),
							progress(NOON, EVENING_START)), 1);
		} else if (time > EVENING_START && time < EVENING_MID) {
			gl_FragColor = vec4(
					mix(bottomNoon(), bottomEveningStart(),
							progress(EVENING_START, EVENING_MID)), 1);
		} else if (time > EVENING_MID && time < EVENING_END) {
			gl_FragColor = vec4(
					mix(bottomEveningStart(), bottomEveningMid(),
							progress(EVENING_MID, EVENING_END)), 1);
		} else if (time > EVENING_END && time < MIDNIGHT_START) {
			gl_FragColor = vec4(
					mix(bottomEveningMid(), bottomMidnightStart(),
							progress(EVENING_END, MIDNIGHT_START)), 1);
		} else if (time > MIDNIGHT_START && time < MIDNIGHT) {
			gl_FragColor = vec4(
					mix(bottomMidnightStart(), bottomMidnight(),
							progress(MIDNIGHT_START, MIDNIGHT)), 1);
		}
	}
}
