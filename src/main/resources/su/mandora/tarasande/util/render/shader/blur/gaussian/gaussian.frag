#version 150 core

uniform float sigma;
uniform sampler2D tex;
uniform ivec2 direction;
uniform vec2 resolution;

#define M_PI 3.14

out vec4 fragColor;

float gaussian(float x) {
    return exp(-(x * x) / (2.0 * sigma * sigma)) / (sqrt(2.0 * M_PI) * sigma);
}

void main() {
    vec3 color = vec3(0.0);

    float halfSize = sigma * 3.0;

    for (float x = -halfSize; x <= halfSize; x++) {
        color += texture(tex, (gl_FragCoord.xy + direction * x) / resolution).rgb * gaussian(x);
    }

    fragColor = vec4(color, 1.0);
}
