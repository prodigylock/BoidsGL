#type vertex
#version 330 core
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec4 aColor;
layout(location = 2) in vec2 aTexCoords;
layout(location = 3) in float aTexId;

uniform mat4 uProjection;
uniform mat4 uView;

out vec4 fColor;
out vec2 fTexCoords;
out float fTexId;

void main(){
    fColor = aColor;
    fTexCoords = aTexCoords;
    fTexId = aTexId;
    gl_Position = uProjection * uView * vec4(aPos,1.0);
}




#type fragment
#version 330 core


in vec4 fColor;
in vec2 fTexCoords;
in float fTexId;

uniform sampler2D uTextures[8];
uniform int firstTime;

out vec4 color;

void main(){

    if (firstTime==1){
        //first render aka rendering the initial data
        color = fColor * texture(uTextures[1], fTexCoords);
    
    } else {
        //iterate over texture
        color = fColor * texture(uTextures[2], fTexCoords);
        
    }
}

