#version 120

uniform float sigma;
uniform sampler2D tex;
uniform vec2 direction;
uniform vec2 resolution;

#define M_PI 3.14

layout (location = 0) out vec4 fragColor;

float gaussian(float x) {
    return exp(-(x * x) / (2.0 * sigma * sigma)) / (sqrt(2.0 * M_PI) * sigma);
}

void main() {
    vec3 color = vec3(0.0);

    float halfSize = sigma * 3.0;

    for (float x = -halfSize; x <= halfSize; x++) {
        color += texture2D(tex, (gl_FragCoord.xy + direction * x) / resolution).rgb * gaussian(x);
    }

    fragColor = vec4(color, 1.0);
}
