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

const float offsety = 1.0 / 1080.0;  
const float offsetx = 1.0/1920.0;

const float difR = 0.25;
const float difB = 1.0;
const float feed = 0.025;
const float kill = 0.055;
const float deltaT = 1.0;

void main(){

    

    if (firstTime==1){
        //first render aka rendering the initial data
        color = fColor * texture(uTextures[1], fTexCoords);
    
    } else {

        //float kill = fTexCoords.x+0.5;
        //float feed = fTexCoords.y+0.5;

        vec2 offsets[9] = vec2[](
        vec2(-offsetx,  offsety), // top-left
        vec2( 0.0f,    offsety), // top-center
        vec2( offsetx,  offsety), // top-right
        vec2(-offsetx,  0.0f),   // center-left
        vec2( 0.0f,    0.0f),   // center-center
        vec2( offsetx,  0.0f),   // center-right
        vec2(-offsetx, -offsety), // bottom-left
        vec2( 0.0f,   -offsety), // bottom-center
        vec2( offsetx, -offsety)  // bottom-right    
        );

        float kernel[9] = float[](
        0.05, 0.2, 0.05, 
        0.2, -1.0, 0.2, 
        0.05, 0.2, 0.05 
        );

        vec3 sampleTex[9];
        for(int i = 0; i < 9; i++)
        {
            sampleTex[i] = vec3(texture(uTextures[2], fTexCoords + offsets[i]));
        }
        vec3 col = vec3(0.0);
        for(int i = 0; i < 9; i++){
            col += sampleTex[i] * kernel[i];
        }
        float thisR = texture(uTextures[2],fTexCoords).r;
        float thisB = texture(uTextures[2],fTexCoords).b;

        float red = thisR+(((difR * col.r)+(thisB*thisR*thisR)-(kill+feed)*thisR)*deltaT);

        float blue = thisB+(((difB *col.b)-(thisB*thisR*thisR)+(feed*(1-thisB)))*deltaT);

        color = vec4(red,0,blue,0);


        //iterate over texture
        //color = fColor * texture(uTextures[2], fTexCoords);
        
    }
}

