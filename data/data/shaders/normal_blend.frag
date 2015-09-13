#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

void main()
{
    vec4 color = texture2D(u_texture, v_texCoords);

    color.rgb = mix(color.rgb, vec3(1.0), v_color.a);

    gl_FragColor = color;
}