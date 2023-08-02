// https://github.com/tryone144/dual-kawase-demo/blob/master/src/shaders/dual_kawase_down.frag

#version 150 core

uniform float offset;
uniform sampler2D tex;
uniform vec2 resolution;

out vec4 fragColor;

void main() {
    vec4 color = texture(tex, gl_FragCoord.xy / resolution) * 4.0;
    color += texture(tex, (gl_FragCoord.xy - 0.5 * offset) / resolution);
    color += texture(tex, (gl_FragCoord.xy + 0.5 * offset) / resolution);
    color += texture(tex, (gl_FragCoord.xy + vec2(0.5, -0.5) * offset) / resolution);
    color += texture(tex, (gl_FragCoord.xy - vec2(0.5, -0.5) * offset) / resolution);

    fragColor = vec4(color.rgb / 8.0, 1.0);
}