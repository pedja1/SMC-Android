#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

#define F vec3(.2126, .7152, .0722)

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;


void main()
{
    vec4 color = texture2D(u_texture, v_texCoords);
    gl_FragColor = vec4(vec3(dot(color.rgb, F)), color.a);
}