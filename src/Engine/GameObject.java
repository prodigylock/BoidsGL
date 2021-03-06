package Engine;

import java.util.ArrayList;
import java.util.List;

public class GameObject {
    private String name;
    private List<Component> components;
    public Transform transform;

    public GameObject(String name){
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = new Transform();
    }

    public GameObject(String name, Transform transform){
        this.name = name;
        this.components = new ArrayList<>();
        this.transform = transform;
    }

    public <T extends Component> T getComponent(Class<T> componenetClass){
        for (Component c : components) {
            if (componenetClass.isAssignableFrom(c.getClass())) {
                try {
                    return componenetClass.cast(c);
                } catch (Exception e) {
                    e.printStackTrace();
                    assert false: "Error: Casting component.";
                }
                
            }
        }
        return null;
    }


    public <T extends Component> void removeComponent(Class<T> componenClass){
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componenClass.isAssignableFrom(c.getClass())) {
                components.remove(i);
                return;
            }
        }
    }

    public void addComponent(Component c){
        this.components.add(c);
        c.gameObject = this;
    }

    public void update(float dt){
        for (int i = 0; i < components.size(); i++) {
            components.get(i).update(dt);
        }
    }

    public void start(){
        for (int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }

    public String getName(){
        return this.name;
    }
}