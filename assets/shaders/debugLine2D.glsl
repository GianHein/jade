#type vertex
#version 330 core

layout (location = 0) in vec3 attributePosition;
layout (location = 1) in vec3 attributeColor;

out vec3 fragmentColor;

uniform mat4 uniformView;
uniform mat4 uniformProjection;

void main() {
    fragmentColor = attributeColor;
    gl_Position = uniformProjection * uniformView * vec4(attributePosition, 1.0);
}

#type fragment
#version 330 core

in vec3 fragmentColor;
out vec4 color;

void main() {
    color = vec4(fragmentColor, 1.0);
}