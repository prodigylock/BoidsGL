package Engine;

import java.nio.IntBuffer;

import org.joml.Vector2f;
import org.joml.Vector4f;

import components.SpriteRenderer;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private IntBuffer tempBuffer;
    private IntBuffer tempBuffer2;


    public LevelEditorScene(){
        
    }


    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(0,0));

        // GameObject bg = new GameObject("background", new Transform(new Vector2f(0,0), new Vector2f(1920,1080)));
        // bg.addComponent(new SpriteRenderer(new Vector4f(1,1,1,1)));
        // this.addGameObjectToScene(bg);

        GameObject obj1 = new GameObject("obj1", new Transform(new Vector2f(0,0), new Vector2f(1280,672) ));
        obj1.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/Untitled.png")));
        this.addGameObjectToScene(obj1);

        // GameObject obj2 = new GameObject("obj2", new Transform(new Vector2f(400,100), new Vector2f(256,256) ));
        // obj2.addComponent(new SpriteRenderer(AssetPool.getTexture("assets/images/testImage.jpg")));
        // this.addGameObjectToScene(obj2);

        loadResources();
    }

    private void loadResources(){
        AssetPool.getShader("assets/shaders/defaultShader.glsl");
    }

    @Override
    public void update(float dt) {
        System.out.println("FPS: " + (1.0f/dt));

        for (GameObject go : this.gameObjects) {
            go.update(dt);
        }

        this.renderer.render();
        
        

    }

    

}
