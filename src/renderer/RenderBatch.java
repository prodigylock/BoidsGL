package renderer;

import components.SpriteRenderer;
import util.AssetPool;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

import Engine.Window;

public class RenderBatch {
    //Vertex
    //======
    //Pos               Color                       Tex coords      tex id
    //float, float,     float,float,float,float     float, flaot    float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;
    
    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE*Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE* Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0,1,2,3,4,5,6,7};

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    private int fboID_A;
    private int fboID_B;
    private int fboTex_A;
    private int fboTex_B;

    private ByteBuffer nill;

    private boolean usingFB = false;
    private boolean AorB = true;
    private boolean firstTime = true;

    public RenderBatch(int maxBatchSize){

        shader = AssetPool.getShader("assets/shaders/defaultShader.glsl");
        shader.compile();
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        vertices = new float[maxBatchSize*4*VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
    }

    public void start(){
        //Generate and bind vertex array object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        //allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length*Float.BYTES, GL_DYNAMIC_DRAW);

        //create and upload indicies buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndicies();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        //enable buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
        
        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
        

        //frame buffer experiment

        /*
        =============================================
                    Order of opertaions:
        =============================================
        1) bind frame buffer A
        2) render initial data
        
        3)enter game loop
        4)bind frame buffer B
        5)Run shader program that does the simulation update
            a. This will probably take the texture from Framebuffer A and then render a new result based on that texture
        6)Render the texture in Framebuffer B to the main Window
        7)7. Repeat from step 4, except swap A and B this time

        =(Data)=>A=(update)=>B=(update)=>...
        =============================================
        */


        // //create frame buffer A
        // fboID_A = glGenFramebuffers();
        // glBindFramebuffer(GL_FRAMEBUFFER, fboID_A);
        
        // //bind texture to frame buffer A
        // fboTex_A = glGenTextures();
        // glBindTexture(GL_TEXTURE_2D, fboTex_A);
        // glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 1920, 1080, 0, GL_RGB, GL_UNSIGNED_BYTE, nill);
	    // // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	    // // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); // Prevents edge bleeding
	    // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE); // Prevents edge bleeding
	    // glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTex_A, 0);
        // glBindTexture(GL_TEXTURE_2D, 0);

        // if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        //     assert false:"FrameBuffer A failed to initialise";
        // }


        // //create frame buffer B
        // fboID_B = glGenFramebuffers();
        // glBindFramebuffer(GL_FRAMEBUFFER, fboID_B);
        
        // //bind texture to frame buffer B
        // fboTex_B = glGenTextures();
        // glBindTexture(GL_TEXTURE_2D, fboTex_B);
        // glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 1920, 1080, 0, GL_RGB, GL_UNSIGNED_BYTE, nill);
	    // // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	    // // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    // // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); // Prevents edge bleeding
	    // // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE); // Prevents edge bleeding
	    // glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTex_B, 0);

        // if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
        //     assert false:"FrameBuffer B failed to initialise";
        // }

    }

    public void addSprite(SpriteRenderer spr){
        //get index and add rendererObject

        int index = this.numSprites;
        //[0,1,2,3,4,5]

        this.sprites[index] = spr;
        this.numSprites++;

        if (spr.getTexture()!=null) {
            if (!textures.contains(spr.getTexture())) {
                textures.add(spr.getTexture());
            }
        }

        //add properties to local vertices array
        loadVertexProperties(index);

        if (numSprites>=this.maxBatchSize){
            this.hasRoom = false;
        }

    }



    public void render(){
        //for noww rebuffer all data every frame

        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        for (int i = 0; i < textures.size(); i++) {
            glActiveTexture(GL_TEXTURE0+i+1);
            textures.get(i).bind();
        }

        shader.uploadintArray("uTextures", texSlots);
        shader.uploadBool("firstTime", firstTime);

        //use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().getCamera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glActiveTexture(GL_TEXTURE2);

        if (usingFB) {
            if (AorB) {
                bindFB_A();
                AorB = false;
            } else {
                bindFB_B();
                AorB = true;
            }
        }
        
        if (firstTime) {
            glDrawElements(GL_TRIANGLES, this.numSprites*6, GL_UNSIGNED_INT, 0);
        }
        firstTime = false;
        
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboID_A);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
        glBlitFramebuffer(0, 0, 1920, 1080, 0, 0, 1920, 1080, GL_COLOR_BUFFER_BIT, GL_NEAREST);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        glActiveTexture(GL_TEXTURE1);
        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }

        shader.detach();
    }

    private void loadVertexProperties(int index){
        SpriteRenderer sprite = this.sprites[index];

        //find offse within array
        int offset = index*4 *VERTEX_SIZE;
        //float float       float float float float

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoord();

        int texId = 0;
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i)==sprite.getTexture()) {
                    texId = i+1;
                    break;
                }
            }
        }
        

        //add vertices with the appropriate properties

        float xAdd = 1.0f;
        float yAdd = 1.0f;
        
        for (int i = 0; i < 4; i++) {
            if (i==1) {
                yAdd = 0.0f;
            } else if (i==2) {
                xAdd = 0.0f;
            } else if (i==3) {
                yAdd=1.0f;
            }

            vertices[offset] =  sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset+1] =  sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            //load color

            vertices[offset +2] = color.x;
            vertices[offset +3] = color.y;
            vertices[offset +4] = color.z;
            vertices[offset +5] = color.w;
            
            //load texure coordinats
            vertices[offset +6] = texCoords[i].x;
            vertices[offset +7] = texCoords[i].y;

            //load texture id
            vertices[offset+8] = texId;

            offset+= VERTEX_SIZE;
        }
    }


    private int[] generateIndicies(){
        //6 indicies per quad (3 per triang)
        int[] elements = new int[6*maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }
        return elements;
    }

    private void loadElementIndices(int[] elements, int index){
        int offsetArrayIndex = 6 *index;
        int offset = 4*index;

        //3,2,0,0,2,1       7,6,4,4,6,5
        //triangle 1

        elements[offsetArrayIndex] = offset+3;
        elements[offsetArrayIndex+1] = offset+2;
        elements[offsetArrayIndex+2] = offset+0;

        //triangle 2
        elements[offsetArrayIndex+3] = offset+0;
        elements[offsetArrayIndex+4] = offset+2;
        elements[offsetArrayIndex+5] = offset+1;


    }

    public boolean hasRoom(){
        return this.hasRoom;
    }

    public void bindFB_A(){
        //create frame buffer A
        fboID_A = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID_A);
        
        //bind texture to frame buffer A
        fboTex_A = glGenTextures();
        glActiveTexture(GL_TEXTURE1);
        glBindTexture(GL_TEXTURE_2D, fboTex_A);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 1920, 1080, 0, GL_RGB, GL_UNSIGNED_BYTE, nill);
	    // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	    // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); // Prevents edge bleeding
	    //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE); // Prevents edge bleeding
	    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTex_A, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false:"FrameBuffer A failed to initialise";
        }
    }

    public void bindFB_B(){
        //create frame buffer B
        fboID_B = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboID_B);
        
        //bind texture to frame buffer B
        fboTex_B = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, fboTex_B);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, 1920, 1080, 0, GL_RGB, GL_UNSIGNED_BYTE, nill);
	    // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
	    // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
	    // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE); // Prevents edge bleeding
	    // glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE); // Prevents edge bleeding
	    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTex_B, 0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false:"FrameBuffer B failed to initialise";
        }
    }

}
