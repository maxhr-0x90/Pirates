#ifdef positionFlag
attribute vec3 a_position; // POSITION ATTRIBUTE - VERTEX
#endif
varying vec4 v_position;
vec4 g_position = vec4(0.0, 0.0, 0.0, 1.0);

#ifdef colorFlag
attribute vec4 a_color; // COLOR ATTRIBUTE - VERTEX
#endif
varying vec4 v_color;
vec4 g_color = vec4(1.0, 1.0, 1.0, 1.0);

#ifdef normalFlag
attribute vec3 a_normal; // NORMAL ATTRIBUTE - VERTEX
#endif
varying vec3 v_normal;
vec3 g_normal = vec3(0.0, 0.0, 1.0);

#ifdef binormalFlag
attribute vec3 a_binormal; // BINORMAL ATTRIBUTE - VERTEX
#endif
varying vec3 v_binormal;
vec3 g_binormal = vec3(0.0, 1.0, 0.0);

#ifdef tangentFlag
attribute vec3 a_tangent; // TANGENT ATTRIBUTE - VERTEX
#endif
varying vec3 v_tangent;
vec3 g_tangent = vec3(1.0, 0.0, 0.0);

#ifdef texCoord0Flag
attribute vec2 a_texCoord0; // TEXCOORD0 ATTRIBUTE - VERTEX
#endif
varying vec2 v_texCoord0;
vec2 g_texCoord0 = vec2(0.0, 0.0);

// Uniforms which are always available
uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec4 u_cameraPosition;
uniform mat3 u_normalMatrix;


//////////////////////////////////////////////////////
////// AMBIENT LIGHT
//////////////////////////////////////////////////////
#ifdef ambientLightFlag
#ifndef ambientFlag
#define ambientFlag
#endif
uniform vec3 u_ambientLight;
#else
const vec3 u_ambientLight = vec3(0.0);
#endif


//////////////////////////////////////////////////////
////// AMBIENT CUBEMAP
//////////////////////////////////////////////////////
#ifdef ambientCubemapFlag
#ifndef ambientFlag
#define ambientFlag
#endif
uniform vec3 u_ambientCubemap[6];
vec3 getAmbientCubeLight(const in vec3 normal) {
	vec3 squaredNormal = normal * normal;
	vec3 isPositive  = step(0.0, normal);
	return squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +
	squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +
	squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);
}
#else
vec3 getAmbientCubeLight(const in vec3 normal) {return vec3(0.0);}
#endif

#if defined(ambientLightFlag) && defined(ambientCubemapFlag)
vec3 getAmbient(const in vec3 normal){
	return u_ambientLight + getAmbientCubeLight(normal);
}
#elif defined(ambientLightFlag)
vec3 getAmbient(const in vec3 normal){
	return u_ambientLight;
}
#elif defined(ambientCubemapFlag)
vec3 getAmbient(const in vec3 normal){
	return getAmbientCubeLight(normal);
}
#else
vec3 getAmbient(const in vec3 normal){
	return vec3(0.0);
}
#endif


//////////////////////////////////////////////////////
////// POINTS LIGHTS
//////////////////////////////////////////////////////
#ifdef lightingFlag
#if defined(numPointLights) && (numPointLights > 0)
#define pointLightsFlag
#endif // numPointLights
#endif //lightingFlag

#ifdef pointLightsFlag
struct PointLight
{
	vec3 color;
	vec3 position;
	float intensity;
};
uniform PointLight u_pointLights[numPointLights];
#endif

//////////////////////////////////////////////////////
////// DIRECTIONAL LIGHTS
//////////////////////////////////////////////////////
#ifdef lightingFlag
#if defined(numDirectionalLights) && (numDirectionalLights > 0)
#define directionalLightsFlag
#endif // numDirectionalLights
#endif //lightingFlag

#ifdef directionalLightsFlag
struct DirectionalLight
{
	vec3 color;
	vec3 direction;
};
uniform DirectionalLight u_dirLights[numDirectionalLights];
#endif

varying vec3 v_lightDir;
varying vec3 v_lightCol;
varying vec3 v_viewDir;

#ifdef environmentCubemapFlag
varying vec3 v_reflect;
#endif

varying vec3 v_ambientLight;

void main() {
	// Non-constant global initializers do not work on Android - setting globals from attributes outside main()
	#if defined(positionFlag)
	g_position = vec4(a_position, 1.0);
	#endif
	#if defined(colorFlag)
	g_color = a_color;
	#endif
	#if defined(normalFlag)
	g_normal = a_normal;
	#endif
	#if defined(binormalFlag)
	g_binormal = a_binormal;
	#endif
	#if defined(tangentFlag)
	g_tangent = a_tangent;
	#endif
	#if defined(texCoord0Flag)
	g_texCoord0 = a_texCoord0;
	#endif

	g_position = u_worldTrans * g_position;
	gl_Position = u_projViewTrans * g_position;

	#ifdef shadowMapFlag
	vec4 spos = u_shadowMapProjViewTrans * g_position;
	v_shadowMapUv.xy = (spos.xy / spos.w) * 0.5 + 0.5;
	v_shadowMapUv.z = min(spos.z * 0.5 + 0.5, 0.998);
	#endif //shadowMapFlag

	mat3 worldToTangent;
	worldToTangent[0] = g_tangent;
	worldToTangent[1] = g_binormal;
	worldToTangent[2] = g_normal;

	v_ambientLight = getAmbient(g_normal);

	v_lightDir = normalize(-u_dirLights[0].direction) * worldToTangent;
	v_lightCol = u_dirLights[0].color;

	vec3 viewDir = normalize(u_cameraPosition.xyz - g_position.xyz);
	v_viewDir = viewDir * worldToTangent;
	#ifdef environmentCubemapFlag
	v_reflect = reflect(-viewDir, g_normal);
	#endif

	v_color = g_color;
	v_texCoord0 = g_texCoord0;
}