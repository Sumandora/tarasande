#version 120

uniform sampler2D shapes;
uniform sampler2D tex;
uniform vec2 resolution;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution.xy;
    if (texture2D(shapes, uv).a > 0.0) {
        gl_FragColor = texture2D(tex, uv);
    } else {
        discard;
    }
}
