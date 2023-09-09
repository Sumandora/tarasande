// https://github.com/tryone144/dual-kawase-demo/blob/master/src/shaders/dual_kawase_down.frag

#version 150 core

uniform float offset;
uniform sampler2D tex;
uniform vec2 resolution;

out vec4 fragColor;

void main() {
    fragColor = texture(tex, gl_FragCoord.xy / resolution) * 4.0;
    fragColor += texture(tex, (gl_FragCoord.xy - 0.5 * offset) / resolution);
    fragColor += texture(tex, (gl_FragCoord.xy + 0.5 * offset) / resolution);
    fragColor += texture(tex, (gl_FragCoord.xy + vec2(0.5, -0.5) * offset) / resolution);
    fragColor += texture(tex, (gl_FragCoord.xy - vec2(0.5, -0.5) * offset) / resolution);

    fragColor /= 8.0;
    fragColor.a = 1.0;
}