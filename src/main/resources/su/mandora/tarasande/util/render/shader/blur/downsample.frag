// https://github.com/tryone144/dual-kawase-demo/blob/master/src/shaders/dual_kawase_down.frag

#version 400

uniform float offset;
uniform sampler2D tex;
uniform vec2 resolution;

void main() {
    vec4 color = texture(tex, gl_FragCoord.xy / resolution) * 4.0;
    color += texture(tex, (gl_FragCoord.xy - 0.5 * offset) / resolution);
    color += texture(tex, (gl_FragCoord.xy + 0.5 * offset) / resolution);
    color += texture(tex, (gl_FragCoord.xy + vec2(0.5, -0.5) * offset) / resolution);
    color += texture(tex, (gl_FragCoord.xy - vec2(0.5, -0.5) * offset) / resolution);

    gl_FragColor = vec4(color.rgb / 8.0, 1.0);
}