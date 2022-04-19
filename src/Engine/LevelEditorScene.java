package Engine;

import renderer.*;
import util.Time;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;

import components.FontRenderer;
import components.SpriteRenderer;


public class LevelEditorScene extends Scene {



    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
        //position              //color                     // UV Coordinates    
        100.5f,  0.5f, 0.0f,    1.0f, 0.0f, 0.0f, 1.0f,     1,1,    //bottom right
        0.5f,    100.5f, 0.0f,  0.0f, 1.0f, 0.0f, 1.0f,     0,0,    //top left
        100.5f,  100.5f, 0.0f,  0.0f, 0.0f, 1.0f, 1.0f,     1,0,    //top right
        0.5f,    0.5f, 0.0f,    1.0f, 1.0f, 0.0f, 1.0f,     0,1,    //bottom left
    };

    //IMPORTANT: must be in counter clockwise order
    private int[] elementArray = {
        2,1,0,  //top right triangle
        0,1,3   //bottom lefr triangle
    };
    private int vaoID, vboID, eboID;
    private Shader defaultShader;

    private Texture testTex;

    GameObject testObj;
    private boolean firstTime = false;

    public LevelEditorScene(){
        
    }


    @Override
    public void init() {
        System.out.println("Creating 'test object'");
        this.testObj = new GameObject("testObject");
        this.testObj.addComponenet(new SpriteRenderer());
        this.testObj.addComponenet(new FontRenderer());
        this.addGameObjectToScene(this.testObj);


        this.camera = new Camera(new Vector2f());
        defaultShader = new Shader("assets/shaders/defaultShader.glsl");
        defaultShader.compile();

        this.testTex = new Texture("assets/images/testImage.png");



        // ============================================================
        //  Generator VAO, VBO and EBO buffer objects, and send to GPU
        // ============================================================
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);
        
        //create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        //create VBO upload vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        //create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        //add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int uvSize = 2;
        int vertexSizeBytes = (positionsSize+colorSize+uvSize) * Float.BYTES;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);     
        
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize*Float.BYTES);
        glEnableVertexAttribArray(1);  
        
        glVertexAttribPointer(2, uvSize, GL_FLOAT, false, vertexSizeBytes, (positionsSize+colorSize)*Float.BYTES);
        glEnableVertexAttribArray(2);
    }

    @Override
    public void update(float dt) {

        camera.position.x = -500.0f;
        camera.position.y = -200.0f;

        defaultShader.use();

        //upload texture to shader
        defaultShader.uploadTexture("TEX_SAMPLER", 0);
        glActiveTexture(GL_TEXTURE0);
        testTex.bind();

        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());
        //bind the vao
        glBindVertexArray(vaoID);
        
        //enable the vertex attribute pointers
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        //draw
        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        //unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);

        defaultShader.detach();

        if (!firstTime) {
            System.out.println("Creating gameObject");
            GameObject go =  new GameObject("Game test 2");
            go.addComponenet(new SpriteRenderer());
            this.addGameObjectToScene(go);
            firstTime = true;
        }
        

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }
        
    }

    

}
