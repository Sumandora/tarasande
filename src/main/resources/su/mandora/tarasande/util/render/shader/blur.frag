#version 400

uniform float offset;
uniform sampler2D tex;
uniform vec2 resolution;

vec4 sampleRegion(vec2 direction) {
    vec4 color = vec4(0.0);

    vec2 position = direction * offset;
    vec2 expandPosition = direction * (offset + 1.0);

    color += texture(tex, (gl_FragCoord.xy + position) / resolution);
    color += texture(tex, (gl_FragCoord.xy + expandPosition) / resolution);

    color += texture(tex, (gl_FragCoord.xy + vec2(position.x, expandPosition.y)) / resolution);
    color += texture(tex, (gl_FragCoord.xy + vec2(expandPosition.x, position.y)) / resolution);

    return vec4(color.rgb * 0.25, 1.0);
}

void main() {
    vec4 color = vec4(0.0);

    color += sampleRegion(vec2(-1, -1));
    color += sampleRegion(vec2(1, -1));
    color += sampleRegion(vec2(1, 1));
    color += sampleRegion(vec2(-1, 1));

    gl_FragColor = vec4(color.rgb * 0.25, 1.0);
}