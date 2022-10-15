#version 120

uniform int size;
uniform sampler2D tex;
uniform vec2 direction;
uniform vec2 resolution;

void main() {
    vec3 color = vec3(0.0);

    float halfSize = size / 2.0;

    int sum = 0;

    for (float x = -halfSize; x <= halfSize; x++) {
        color += texture2D(tex, (gl_FragCoord.xy + direction * x) / resolution).rgb;
        sum++;
    }

    gl_FragColor = vec4(color / sum, 1.0);
}
