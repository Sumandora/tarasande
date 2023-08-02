#version 150 core

uniform sampler2D shapes;
uniform sampler2D tex;
uniform vec2 resolution;

out vec4 fragColor;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    if (texture(shapes, uv).a > 0.0) {
        fragColor = texture(tex, uv);
    } else {
        discard;
    }
}
