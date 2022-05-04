package renderer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.joml.*;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;

public class Shader {

    private int shaderProgramID;
    private int vertexID, fragmentID;
    private String vertexSource;
    private String fragmentSource;
    private String filePath;

    private boolean beingUsed = false;
    
    public Shader(String filepath){
        this.filePath = filepath;
        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            //find first pattern
            int index = source.indexOf("#type")+6;
            int eol = source.indexOf("\r\n", index);
            String firstPattern = source.substring(index, eol).trim();

            //find second pattern
            index  = source.indexOf("#type", eol)+6;
            eol = source.indexOf("\r\n", index);
            String secondPattern = source.substring(index, eol).trim();

            if (firstPattern.equals("vertex")) {
                vertexSource = splitString[1];
            } else if (firstPattern.equals("fragment")){
                fragmentSource = splitString[1];
            } else {
                throw new IOException("Unexpected token '"+ firstPattern +"'");
            }
            
            if (secondPattern.equals("vertex")) {
                vertexSource = splitString[2];
            } else if (secondPattern.equals("fragment")){
                fragmentSource = splitString[2];
            } else {
                throw new IOException("Unexpected token '"+ secondPattern +"'");
            }  
        } catch (IOException e) {
            e.printStackTrace();
            assert false: "Error: Could not open file for shader '"+ filepath+"'";
        }

        // System.out.println(vertexSource);
        // System.out.println(fragmentSource);
    }

    public void compile(){
        //Compile and link shaders

        //first load and compile
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        //pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        //Check for errors in compilation
        int success  = glGetShaderi(vertexID, GL_COMPILE_STATUS);
        if (success==GL_FALSE) {
            int len = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERRORL '"+filePath+"'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID,len));
            assert false:"";
        }


        //fragment shader+
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        //pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        //Check for errors in compilation
        success  = glGetShaderi(fragmentID, GL_COMPILE_STATUS);
        if (success==GL_FALSE) {
            int len = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERRORL '"+filePath+"'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID,len));
            assert false:"";
        }


        //Link shaders and check for errors
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        //check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);
        if (success==GL_FALSE){
            int len = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERRORL '"+filePath+"'\n\tLinking shaders failed.");
            System.out.println(glGetProgramInfoLog(fragmentID,len));
            assert false:"";
        }
    }

    public void use(){
        if (!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }

        //bind shader program
        
    }

    public void detach(){
        glUseProgram(0);
        beingUsed = false;
    }

    public void uploadMat4f(String varName, Matrix4f mat4){
        int varLoaction = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(16);
        mat4.get(matBuffer);
        glUniformMatrix4fv(varLoaction, false, matBuffer);
    }

    public void uploadVec4f(String varName, Vector4f vec){
        int varLoaction=glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform4f(varLoaction, vec.x,vec.y,vec.z,vec.w);
    }

    public void uploadFloat(String varName, float val){
        int varLoaction = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1f(varLoaction, val);
    }

    public void uploadInt(String varName, int val){
        int varLoaction = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLoaction, val);
    }

    public void uploadVec3f(String varName, Vector3f vec){
        int varLoaction=glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform3f(varLoaction, vec.x,vec.y,vec.z);
    }

    public void uploadVec2f(String varName, Vector2f vec){
        int varLoaction=glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform2f(varLoaction, vec.x,vec.y);
    }

    public void uploadMat3f(String varName, Matrix3f mat3){
        int varLoaction = glGetUniformLocation(shaderProgramID, varName);
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer(9);
        mat3.get(matBuffer);
        glUniformMatrix3fv(varLoaction, false, matBuffer);
    }

    public void uploadTexture(String varName, int slot){
        int varLoaction = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLoaction, slot);
    }

    public void uploadintArray(String varName, int[] array){
        int varLoaction = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1iv(varLoaction, array);
    }

    public void uploadBool(String varName, Boolean val){
        int value = val ? 1 : 0;
        int varLoaction = glGetUniformLocation(shaderProgramID, varName);
        use();
        glUniform1i(varLoaction, value);
    }
}
