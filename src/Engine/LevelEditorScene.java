package Engine;


import javafx.scene.input.KeyEvent;
import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;
import util.Time;
import java.nio.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class LevelEditorScene extends Scene {

    private boolean changingScene = false;
    private float timeToChangeScene = 2.0f;
    
    public LevelEditorScene(){
        System.out.println("Inside level editor scene");
    }

    @Override
    public void update(float dt) {

        System.out.println("FPS: "+ (1.0f/dt));

        if (!changingScene && KeyListener.isKeyPressed(GLFW_KEY_SPACE)) {
            changingScene = true;
        }

        if (changingScene && timeToChangeScene>0) {
            timeToChangeScene -= dt;
            Window.get().r -= dt * 5.0f;
            Window.get().g -= dt * 5.0f;
            Window.get().b -= dt * 5.0f;
        }   else if (changingScene) {
            Window.changeScene(1);
        }
    }

}
