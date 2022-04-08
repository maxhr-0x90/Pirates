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

varying vec4 v_position; // POSITION ATTRIBUTE - FRAGMENT
vec4 g_position = vec4(0.0, 0.0, 0.0, 1.0);

varying vec4 v_color; // COLOR ATTRIBUTE - FRAGMENT
vec4 g_color = vec4(1.0, 1.0, 1.0, 1.0);

varying vec3 v_normal; // NORMAL ATTRIBUTE - FRAGMENT
vec3 g_normal = vec3(0.0, 0.0, 1.0);

varying vec3 v_binormal; // BINORMAL ATTRIBUTE - FRAGMENT
vec3 g_binormal = vec3(0.0, 0.0, 1.0);

varying vec3 v_tangent; // TANGENT ATTRIBUTE - FRAGMENT
vec3 g_tangent = vec3(1.0, 0.0, 0.0);

varying vec2 v_texCoord0; // TEXCOORD0 ATTRIBUTE - FRAGMENT
vec2 g_texCoord0 = vec2(0.0, 0.0);


// Uniforms which are always available
uniform mat4 u_projViewTrans;
uniform mat4 u_worldTrans;
uniform vec4 u_cameraPosition;
uniform mat3 u_normalMatrix;

// Other uniforms
#ifdef blendedFlag
uniform float u_opacity;
#else
const float u_opacity = 1.0;
#endif

#ifdef alphaTestFlag
uniform float u_alphaTest;
#else
const float u_alphaTest = 0.0;
#endif

#ifdef shininessFlag
uniform float u_shininess;
#else
const float u_shininess = 20.0;
#endif


#ifdef diffuseColorFlag
uniform vec4 u_diffuseColor;
#endif

#ifdef diffuseTextureFlag
uniform sampler2D u_diffuseTexture;
#endif

#ifdef specularColorFlag
uniform vec4 u_specularColor;
#endif

#ifdef specularTextureFlag
uniform sampler2D u_specularTexture;
#endif

#ifdef normalTextureFlag
uniform sampler2D u_normalTexture;
#endif

#ifdef shadowMapFlag
uniform sampler2D u_shadowTexture;
uniform float u_shadowPCFOffset;
varying vec3 v_shadowMapUv;

float getShadowness(vec2 offset){
    const vec4 bitShifts = vec4(1.0, 1.0 / 255.0, 1.0 / 65025.0, 1.0 / 160581375.0);
    return step(v_shadowMapUv.z, dot(texture2D(u_shadowTexture, v_shadowMapUv.xy + offset), bitShifts));//+(1.0/255.0));
}

float getShadow(){
    return (//getShadowness(vec2(0,0)) +
    getShadowness(vec2(u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, u_shadowPCFOffset)) +
    getShadowness(vec2(u_shadowPCFOffset, -u_shadowPCFOffset)) +
    getShadowness(vec2(-u_shadowPCFOffset, -u_shadowPCFOffset))) * 0.25;
}
#endif //shadowMapFlag

#if defined(diffuseTextureFlag) && defined(diffuseColorFlag)
vec4 fetchColorDiffuseTD(vec2 texCoord, vec4 defaultValue) {
    return texture2D(u_diffuseTexture, texCoord) * u_diffuseColor;
}
#elif defined(diffuseTextureFlag)
vec4 fetchColorDiffuseTD(vec2 texCoord, vec4 defaultValue) { return texture2D(u_diffuseTexture, texCoord); }
#elif defined(diffuseColorFlag)
vec4 fetchColorDiffuseTD(vec2 texCoord, vec4 defaultValue) { return u_diffuseColor; }
#else
vec4 fetchColorDiffuseTD(vec2 texCoord, vec4 defaultValue) { return defaultValue; }
#endif

vec4 fetchColorDiffuse() { return fetchColorDiffuseTD(g_texCoord0, vec4(1.0)); }


#if defined(specularTextureFlag) && defined(specularColorFlag)
vec4 fetchColorSpecularTD(vec2 texCoord, vec3 defaultValue) {
    return texture2D(u_specularTexture, texCoord) * u_specularColor;
}
#elif defined(diffuseTextureFlag)
vec4 fetchColorDiffuseTD(vec2 texCoord, vec4 defaultValue) { return texture2D(u_specularTexture, texCoord); }
#elif defined(diffuseColorFlag)
vec4 fetchColorSpecularTD(vec2 texCoord, vec4 defaultValue) { return u_specularColor; }
#else
vec4 fetchColorSpecularTD(vec2 texCoord, vec4 defaultValue) { return defaultValue; }
#endif

vec4 fetchColorSpecular() { return fetchColorSpecularTD(g_texCoord0, vec4(0.0)); }

varying vec3 v_lightDir;
varying vec3 v_lightCol;
varying vec3 v_viewDir;

#ifdef environmentCubemapFlag
varying vec3 v_reflect;
#endif

#ifdef environmentCubemapFlag
uniform samplerCube u_environmentCubemap;
#endif

#ifdef reflectionColorFlag
uniform vec4 u_reflectionColor;
#endif

varying vec3 v_ambientLight;

void main() {
    g_color = v_color ;
    g_texCoord0 = v_texCoord0 ;

    #if defined(diffuseTextureFlag) || defined(diffuseColorFlag)
    vec4 diffuse = g_color * fetchColorDiffuse();
    #else
    vec4 diffuse = g_color;
    #endif

    #if defined(specularTextureFlag) || defined(specularColorFlag)
    vec4 specular = fetchColorSpecular();
    #endif

    #ifdef normalTextureFlag
    vec4 N = vec4(normalize(texture2D(u_normalTexture, g_texCoord0).xyz * 2.0 - 1.0), 1.0);

    #ifdef environmentCubemapFlag
    vec3 reflectDir = normalize(v_reflect + (vec3(0.0, 0.0, 1.0) - N.xyz));
    #endif

    #else
    vec4 N = vec4(0.0, 0.0, 1.0, 1.0);
    #ifdef environmentCubemapFlag
    vec3 reflectDir = normalize(v_reflect);
    #endif
    #endif

    vec3 L = normalize(v_lightDir);
    vec3 V = normalize(v_viewDir);
    vec3 H = normalize(L + V);
    float NL = dot(N.xyz, L);
    float NH = max(0.0, dot(N.xyz, H));

    float specOpacity = 1.0; //(1.0 - diffuse.w);
    float spec = min(1.0, pow(NH, 10.0) * specOpacity);
    float selfShadow = clamp(4.0 * NL, 0.0, 1.0);
    //
    #ifdef environmentCubemapFlag
    vec3 environment = textureCube(u_environmentCubemap, reflectDir).rgb;
    specular *= vec4(environment, 1.0);
    #ifdef reflectionColorFlag
    diffuse.rgb = clamp(vec3(1.0) - u_reflectionColor.rgb, 0.0, 1.0) * diffuse.rgb + environment * u_reflectionColor.rgb;
    #endif
    #endif

    #ifdef shadowMapFlag
    gl_FragColor = vec4(clamp((v_lightCol * diffuse.rgb) * NL * getShadow(), 0.0, 1.0), diffuse.w);
    #else
    gl_FragColor = vec4(clamp((v_lightCol * diffuse.rgb) * NL, 0.0, 1.0), diffuse.w);
    #endif

    gl_FragColor.rgb += v_ambientLight * diffuse.rgb;
    gl_FragColor.rgb += (selfShadow * spec) * specular.rgb;
}