#ifdef GL_ES
#define LOWP lowp
#define MED mediump
#define HIGH highp
precision mediump float;
#else
#define MED
#define LOWP
#define HIGH
#endif

#ifdef billboard
//Billboard particles
varying vec4 v_color;
varying MED vec2 v_texCoords0;
uniform sampler2D u_diffuseTexture;

#ifdef blendedFlag
varying float v_opacity;
#ifdef alphaTestFlag
uniform float v_alphaTest;
#endif //alphaTestFlag
#endif //blendedFlag

void main() {
	gl_FragColor = texture2D(u_diffuseTexture, v_texCoords0) * v_color;
#ifdef blendedFlag
	gl_FragColor.a = 0.5;
	#ifdef alphaTestFlag
		if (gl_FragColor.a <= v_alphaTest)
			discard;
	#endif
#else
	gl_FragColor.a = 1.0;
#endif
}
#else

//Point particles
varying vec4 v_color;
varying vec4 v_rotation;
varying MED vec4 v_region;
varying vec2 v_uvRegionCenter;

uniform sampler2D u_diffuseTexture;
uniform vec2 u_regionSize;

void main() {
	vec2 uv = v_region.xy + gl_PointCoord * v_region.zw - v_uvRegionCenter;
	vec2 texCoord = mat2(v_rotation.x, v_rotation.y, v_rotation.z, v_rotation.w)
			* uv + v_uvRegionCenter;
	gl_FragColor = texture2D(u_diffuseTexture, texCoord) * v_color;
}

#endif
