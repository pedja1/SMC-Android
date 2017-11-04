#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

uniform float u_time;
uniform vec4 a_color;

void main()
{
    // Smooth interpolation between 0.1 and 0.9
    float y = smoothstep(.1, 1.8, sin((v_texCoords.x-v_texCoords.y+u_time)*3.));
    vec4 textureColor = texture2D(u_texture, v_texCoords);
    vec4 color;
    color = vec4(vec3(1. - .5 * y), 1.) * textureColor;
    //color = vec4(vec3(1.), 1.-1.2*y) * textureColor;
     gl_FragColor = v_color * color;
}
