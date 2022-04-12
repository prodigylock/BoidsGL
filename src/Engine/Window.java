package Engine;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.nio.*;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;


public class Window {
    private int width, height;
    private String title;
    private long glfwWindow;

    private float r,g,b,a;

    private boolean fadeToBlack = false;


    //allows for only one instance of window
    private static Window window = null;


    //private so its cant be called frome the outside to make multiple
    private Window(){
        this.width = 1920;
        this.height = 1080;
        this.title = "Boids";
        r = 1;
        g = 1;
        b = 1;
        a = 1;

    }

    //creates only one
    public static Window get(){
        if (Window.window==null) {
            Window.window = new Window();
        }
        return Window.window;
    }

    public void run(){
        System.out.println("Hello LWJGL " + Version.getVersion()+"!");

        innit();
        loop();

        //free memory
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        //terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void innit(){
        //setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //configure glfw
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);

        //create the window, which is a number where the window exists in memory
        glfwWindow = glfwCreateWindow(this.width, this.height, this.title, NULL, NULL);
        if (glfwWindow==NULL) {
            throw new IllegalStateException("Failed to create the GLFW window.");
        }


        //set mouse listener callbacks
        glfwSetCursorPosCallback(glfwWindow, MouseListener::mousePosCallback);
        glfwSetMouseButtonCallback(glfwWindow, MouseListener::mouseButtonCallback);
        glfwSetScrollCallback(glfwWindow, MouseListener::mouseScrollCallback);
        glfwSetKeyCallback(glfwWindow, KeyListener::keyCallback);


        //make the OpenGL context current
        glfwMakeContextCurrent(glfwWindow);

        //enable v-sync
        glfwSwapInterval(1);

        //Make the window visible
        glfwShowWindow(glfwWindow);


        // This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
        GL.createCapabilities();
    }

    public void loop(){
        while (!glfwWindowShouldClose(glfwWindow)) {
            //Poll events
            glfwPollEvents();

            glClearColor(r,g,b,a);
            glClear(GL_COLOR_BUFFER_BIT);

            if (!fadeToBlack) {
                r = Math.min(r + 0.01f, 1);
                g = Math.min(g + 0.01f, 1);
                b = Math.min(b + 0.01f, 1);
                a = Math.min(a + 0.01f, 1);
            }
            
            if (fadeToBlack) {
                r = Math.max(r - 0.01f, 0);
                g = Math.max(g - 0.01f, 0);
                b = Math.max(b - 0.01f, 0);
                a = Math.max(a - 0.01f, 0);
                fadeToBlack = false;
            }


            if (KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
                fadeToBlack = true;
                
            }




            glfwSwapBuffers(glfwWindow);
        }
    }
}
