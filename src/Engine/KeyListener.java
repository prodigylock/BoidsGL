package Engine;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener {
    private static KeyListener instace;
    private boolean keyPressed[] = new boolean[GLFW_KEY_LAST];

    private KeyListener(){

    }

    public static KeyListener get(){
        if (KeyListener.instace == null) {
            KeyListener.instace = new KeyListener();
        }

        return KeyListener.instace;
    }

    public static void keyCallback(long window, int key, int scancode, int action, int mods){
        if (action == GLFW_PRESS) {
            get().keyPressed[key] = true;
        } else if (action == GLFW_RELEASE){
            get().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode ){
        return get().keyPressed[keyCode];
    }
}
