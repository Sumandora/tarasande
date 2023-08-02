#version 150 core

uniform int size;
uniform sampler2D tex;
uniform ivec2 direction;
uniform vec2 resolution;

out vec4 fragColor;

void main() {
    vec3 color = vec3(0.0);

    float halfSize = size / 2.0;

    int sum = 0;

    for (float x = -halfSize; x <= halfSize; x++) {
        color += texture(tex, (gl_FragCoord.xy + direction * x) / resolution).rgb;
        sum++;
    }

    fragColor = vec4(color / sum, 1.0);
}
