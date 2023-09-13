#version 150 core

uniform int size;
uniform sampler2D tex;
uniform ivec2 direction;
uniform vec2 resolution;

out vec4 fragColor;

void main() {
    fragColor = vec4(0.0);
    float halfSize = size / 2.0;

    for (float x = -halfSize; x < halfSize; x++) {
        fragColor += texture(tex, (gl_FragCoord.xy + direction * x) / resolution);
    }

    fragColor /= size;
    fragColor.a = 1.0;
}
