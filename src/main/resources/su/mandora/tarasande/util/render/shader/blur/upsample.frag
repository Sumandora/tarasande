// https://github.com/tryone144/dual-kawase-demo/blob/master/src/shaders/dual_kawase_up.frag

#version 400

uniform float offset;
uniform sampler2D tex;
uniform vec2 resolution;

layout (location = 0) out vec4 fragColor;

void main() {
    vec4 color = texture(tex, (gl_FragCoord.xy + vec2(-1.0, 0.0) * offset) / resolution);
    color += texture(tex, (gl_FragCoord.xy + vec2(-0.5, 0.5) * offset) / resolution) * 2.0;
    color += texture(tex, (gl_FragCoord.xy + vec2(0.0, 1.0) * offset) / resolution);
    color += texture(tex, (gl_FragCoord.xy + vec2(0.5, 0.5) * offset) / resolution) * 2.0;
    color += texture(tex, (gl_FragCoord.xy + vec2(1.0, 0.0) * offset) / resolution);
    color += texture(tex, (gl_FragCoord.xy + vec2(0.5, -0.5) * offset) / resolution) * 2.0;
    color += texture(tex, (gl_FragCoord.xy + vec2(0.0, -1.0) * offset) / resolution);
    color += texture(tex, (gl_FragCoord.xy + vec2(-0.5, -0.5) * offset) / resolution) * 2.0;

    fragColor = vec4(color.rgb / 12.0, 1.0);
}