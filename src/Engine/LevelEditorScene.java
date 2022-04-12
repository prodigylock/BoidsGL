package Engine;

import renderer.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import static org.lwjgl.glfw.GLFW.*;

public class LevelEditorScene extends Scene {

    private String vertexShaderSrc = "#version 330 core\n" +
    "layout(location = 0) in vec3 aPos;\n" +
    "layout(location = 1) in vec4 aColor;\n" +
    "out vec4 fColor;\n" +
    "void main(){\n" +
    "   fColor = aColor;\n" +
    "   gl_Position = vec4(aPos,1.0);\n" +
    "}\n";


    private String fragmentShaderSrc = "#version 330 core\n" +
    "in vec4 fColor;\n" +
    "out vec4 color;\n" +
    "void main(){\n" +
    "    color = fColor;\n" +
    "}";


    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
        //position              //color
         0.5f,  -0.5f, 0.0f,    1.0f, 0.0f, 0.0f, 1.0f,     //bottom right
        -0.5f,   0.5f, 0.0f,    0.0f, 1.0f, 0.0f, 1.0f,     //top left
         0.5f,   0.5f, 0.0f,    0.0f, 0.0f, 1.0f, 1.0f,     //top right
        -0.5f,  -0.5f, 0.0f,    1.0f, 1.0f, 0.0f, 1.0f,     //bottom left
    };

    //IMPORTANT: must be in counter clockwise order

    private int[] elementArray = {
        2,1,0,  //top right triangle
        0,1,3   //bottom lefr triangle
    };

    private int vaoID, vboID, eboID;
    
    private Shader defaultShader;

    public LevelEditorScene(){
        defaultShader = new Shader("assets/shaders/defaultShader.glsl");
        defaultShader.compile();
    }


    @Override
    public void init() {
        


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
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize+colorSize) * floatSizeBytes;
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);       
        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize*floatSizeBytes);
        glEnableVertexAttribArray(1);       
      
    }

    @Override
    public void update(float dt) {
        defaultShader.use();

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
        
    }

    

}
