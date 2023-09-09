// https://github.com/tryone144/dual-kawase-demo/blob/master/src/shaders/dual_kawase_up.frag

#version 150 core

uniform float offset;
uniform sampler2D tex;
uniform vec2 resolution;

out vec4 fragColor;

void main() {
    fragColor = texture(tex, (gl_FragCoord.xy + vec2(-1.0, 0.0) * offset) / resolution);
    fragColor += texture(tex, (gl_FragCoord.xy + vec2(-0.5, 0.5) * offset) / resolution) * 2.0;
    fragColor += texture(tex, (gl_FragCoord.xy + vec2(0.0, 1.0) * offset) / resolution);
    fragColor += texture(tex, (gl_FragCoord.xy + vec2(0.5, 0.5) * offset) / resolution) * 2.0;
    fragColor += texture(tex, (gl_FragCoord.xy + vec2(1.0, 0.0) * offset) / resolution);
    fragColor += texture(tex, (gl_FragCoord.xy + vec2(0.5, -0.5) * offset) / resolution) * 2.0;
    fragColor += texture(tex, (gl_FragCoord.xy + vec2(0.0, -1.0) * offset) / resolution);
    fragColor += texture(tex, (gl_FragCoord.xy + vec2(-0.5, -0.5) * offset) / resolution) * 2.0;

    fragColor /= 12.0;
    fragColor.a = 1.0;
}