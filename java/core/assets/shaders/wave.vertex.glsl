attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec2 a_texCoord0;

uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec4 u_cameraPosition;
uniform mat3 u_normalMatrix;

uniform vec3 u_ambientLight;
struct DirectionalLight
{
    vec3 color;
    vec3 direction;
};
uniform DirectionalLight u_dirLights[numDirectionalLights];
uniform vec3 u_ambientCubemap[6];

varying vec2 v_texCoord0;
varying vec3 v_position;

varying vec3 v_lightDir;
varying vec3 v_lightCol;
varying vec3 v_viewDir;

varying vec3 v_ambientLight;

vec3 getAmbientCubeLight(const in vec3 normal) {
    vec3 squaredNormal = normal * normal;
    vec3 isPositive  = step(0.0, normal);
    return squaredNormal.x * mix(u_ambientCubemap[0], u_ambientCubemap[1], isPositive.x) +
    squaredNormal.y * mix(u_ambientCubemap[2], u_ambientCubemap[3], isPositive.y) +
    squaredNormal.z * mix(u_ambientCubemap[4], u_ambientCubemap[5], isPositive.z);
}

vec3 getAmbient(const in vec3 normal){
    return u_ambientLight + getAmbientCubeLight(normal);
}

void main() {
    vec4 g_position;
    g_position = vec4(a_position, 1.0);
    g_position = u_worldTrans * g_position;
    gl_Position = u_projViewTrans * g_position;

    v_ambientLight = getAmbient(a_normal);

    vec3 g_binormal = vec3(0.0, 1.0, 0.0);
    vec3 g_tangent = vec3(1.0, 0.0, 0.0);

    mat3 worldToTangent;
    worldToTangent[0] = g_tangent;
    worldToTangent[1] = g_binormal;
    worldToTangent[2] = a_normal;

    v_lightDir = normalize(-u_dirLights[0].direction) * worldToTangent;
    v_lightCol = u_dirLights[0].color;

    vec3 viewDir = normalize(u_cameraPosition.xyz - g_position.xyz);
    v_viewDir = viewDir * worldToTangent;

    v_texCoord0 = a_texCoord0;
    v_position = g_position.xyz;
}
