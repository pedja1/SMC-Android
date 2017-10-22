#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;
uniform float u_brightness;
uniform float u_contrast;

void main()
{
    vec4 color = texture2D(u_texture, v_texCoords);

    color.rgb /= color.a;

    // Apply contrast.
    color.rgb = ((color.rgb - 0.5) * max(u_contrast, 0.0)) + 0.5;

    // Apply brightness.
    color.rgb += u_brightness;

    // Return final pixel color.
    color.rgb *= color.a;

    color.rgb = mix(color.rgb, vec3(1.0), v_color.a);

    gl_FragColor = color;
}