package components;

import jade.KeyListener;
import jade.Window;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;

public class GizmoSystem extends Component {
    private Spritesheet gizmos;
    private int mode = 0;

    public GizmoSystem(Spritesheet gizmosprites) {
        this.gizmos = gizmosprites;
    }

    @Override
    public void start() {
         gameObject.addComponent(new TranslateGizmo(gizmos.getSprite(1), Window.getImGuiLayer().getPropertiesWindow()));
         gameObject.addComponent(new ScaleGizmo(gizmos.getSprite(2), Window.getImGuiLayer().getPropertiesWindow()));
    }

    @Override
    public void update(float deltaTime) {
        if (mode == 0) {
            gameObject.getComponent(TranslateGizmo.class).setUsing();
            gameObject.getComponent(ScaleGizmo.class).setNotUsing();
        } else if (mode == 1) {
            gameObject.getComponent(ScaleGizmo.class).setUsing();
            gameObject.getComponent(TranslateGizmo.class).setNotUsing();
        }

        if (KeyListener.isKeyPressed(GLFW_KEY_E)) {
            mode = 0;
        } else if (KeyListener.isKeyPressed(GLFW_KEY_R)) {
            mode = 1;
        }
    }
}
