#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 v_color;
varying vec2 v_texCoords;

uniform sampler2D u_texture;

const float blurSize = 1.0 / 512.0;
const float intensity = 0.35;

void main()
{
    vec4 sum = vec4(0);

     sum += texture2D(u_texture, vec2(v_texCoords.x - 4.0*blurSize, v_texCoords.y)) * 0.05;
     sum += texture2D(u_texture, vec2(v_texCoords.x - 3.0*blurSize, v_texCoords.y)) * 0.09;
     sum += texture2D(u_texture, vec2(v_texCoords.x - 2.0*blurSize, v_texCoords.y)) * 0.12;
     sum += texture2D(u_texture, vec2(v_texCoords.x - blurSize, v_texCoords.y)) * 0.15;
     sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y)) * 0.16;
     sum += texture2D(u_texture, vec2(v_texCoords.x + blurSize, v_texCoords.y)) * 0.15;
     sum += texture2D(u_texture, vec2(v_texCoords.x + 2.0*blurSize, v_texCoords.y)) * 0.12;
     sum += texture2D(u_texture, vec2(v_texCoords.x + 3.0*blurSize, v_texCoords.y)) * 0.09;
     sum += texture2D(u_texture, vec2(v_texCoords.x + 4.0*blurSize, v_texCoords.y)) * 0.05;

     sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - 4.0*blurSize)) * 0.05;
     sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - 3.0*blurSize)) * 0.09;
     sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - 2.0*blurSize)) * 0.12;
     sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y - blurSize)) * 0.15;
     sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y)) * 0.16;
     sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + blurSize)) * 0.15;
     sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + 2.0*blurSize)) * 0.12;
     sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + 3.0*blurSize)) * 0.09;
     sum += texture2D(u_texture, vec2(v_texCoords.x, v_texCoords.y + 4.0*blurSize)) * 0.05;

     gl_FragColor = sum * texture2D(u_texture, v_texCoords);
}