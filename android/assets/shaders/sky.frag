precision highp float;

uniform vec4 skyTop;
uniform vec4 skyMiddle;
uniform vec4 skyBottom;
uniform vec4 sunShine;

uniform vec3 sunDir;

in vec4 vertPos;

float logFrac(float f) {
	return log2(f * 255 + 1) / 8;
}

void main() {
	vec3 normVert = normalize(vertPos.xyz);
	// [-1; 1]
	float angle = dot(normVert, vec3(0, 1, 0));
	float sunSkyAngle;

	if (angle > 0) { // top half
		angle = 1 - angle;
		sunSkyAngle = dot(normVert, sunDir);
		sunSkyAngle = 1 - sunSkyAngle;

		gl_FragColor = mix(skyTop, skyMiddle, logFrac(angle));
	} else { // bottom half
		angle += 1;
		// need to invert one of those dirs
		sunSkyAngle = dot(-1 * normVert, sunDir);
		sunSkyAngle += 1;

		gl_FragColor = mix(skyMiddle, skyBottom, 1 - logFrac(angle));
	}

	gl_FragColor = mix(gl_FragColor, sunShine,
			sunShine.a * (1 - logFrac(sunSkyAngle)));
}
