#type vertex
#version 330 core

layout (location = 0) in vec3 attributePosition;
layout (location = 1) in vec4 attributeColor;
layout (location = 2) in vec2 attributeTextureCoordinates;
layout (location = 3) in float attributeTextureID;
layout (location = 4) in float attributeEntityID;

uniform mat4 uniformProjection;
uniform mat4 uniformView;

out vec4 fragmentColor;
out vec2 fragmentTextureCoordinates;
out float fragmentTextureID;
out float fragmentEntityID;

void main() {
    fragmentColor = attributeColor;
    fragmentTextureCoordinates = attributeTextureCoordinates;
    fragmentTextureID = attributeTextureID;
    fragmentEntityID = attributeEntityID;
    gl_Position = uniformProjection * uniformView * vec4(attributePosition, 1.0);
}

#type fragment
#version 330 core

in vec4 fragmentColor;
in vec2 fragmentTextureCoordinates;
in float fragmentTextureID;
in float fragmentEntityID;

uniform sampler2D uniformTextures[8];

out vec3 color;

void main() {
    vec4 textureColor = vec4(1, 1, 1, 1);
    if (fragmentTextureID > 0) {
        int id = int(fragmentTextureID);
        textureColor = fragmentColor * texture(uniformTextures[id], fragmentTextureCoordinates);
    }

    if (textureColor.a < 0.5) {
        discard;
    }

    color = vec3(fragmentEntityID, fragmentEntityID, fragmentEntityID);
}