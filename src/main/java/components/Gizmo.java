package components;

import editor.PropertiesWindow;
import jade.GameObject;
import jade.MouseListener;
import jade.Prefabs;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class Gizmo extends Component {
    private Vector4f xAxisColor = new Vector4f(1, 0.3f, 0.3f, 1);
    private Vector4f xAxisColorHover = new Vector4f(1, 0, 0, 1);
    private Vector4f yAxisColor = new Vector4f(0.3f, 1, 0.3f, 1);
    private Vector4f yAxisColorHover = new Vector4f(0, 1, 0, 1);

    private GameObject xAxisObject;
    private GameObject yAxisObject;
    private SpriteRenderer xAxisSpriteRenderer;
    private SpriteRenderer yAxisSpriteRenderer;
    protected GameObject activeGameObject = null;

    private Vector2f xAxisOffset = new Vector2f(55.0f, -5.0f);
    private Vector2f yAxisOffset = new Vector2f(10.0f, 55.0f);

    private int gizmoWidth = 16;
    private int gizmoHeight = 48;

    protected boolean xAxisActive = false;
    protected boolean yAxisActive = false;

    private boolean using = false;

    private PropertiesWindow propertiesWindow;

    public Gizmo(Sprite arrowSprite, PropertiesWindow propertiesWindow) {
        this.xAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.yAxisObject = Prefabs.generateSpriteObject(arrowSprite, 16, 48);
        this.xAxisSpriteRenderer = xAxisObject.getComponent(SpriteRenderer.class);
        this.yAxisSpriteRenderer = yAxisObject.getComponent(SpriteRenderer.class);
        this.propertiesWindow = propertiesWindow;

        this.xAxisObject.addComponent(new NonPickable());
        this.yAxisObject.addComponent(new NonPickable());

        Window.getScene().addGameObjectToScene(xAxisObject);
        Window.getScene().addGameObjectToScene(yAxisObject);
    }

    @Override
    public void start() {
        this.xAxisObject.transform.rotation = 90;
        this.xAxisObject.transform.zindex = 100;
        this.yAxisObject.transform.rotation = 180;
        this.yAxisObject.transform.zindex = 100;
        this.xAxisObject.setNoSerialize();
        this.yAxisObject.setNoSerialize();
    }

    @Override
    public void update(float deltaTime) {
        if (!using) return;

        this.activeGameObject = this.propertiesWindow.getActiveGameObject();

        if (this.activeGameObject != null) {
            this.setActive();
        } else {
            this.setInactive();
            return;
        }

        boolean xAxisHot = checkXHoverState();
        boolean yAxisHot = checkYHoverState();

        if ((xAxisHot || xAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            xAxisActive = true;
            yAxisActive = false;
        } else if ((yAxisHot || yAxisActive) && MouseListener.isDragging() && MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            yAxisActive = true;
            xAxisActive = false;
        } else {
            xAxisActive = false;
            yAxisActive = false;
        }

        if (this.activeGameObject != null) {
            this.xAxisObject.transform.position.set(this.activeGameObject.transform.position);
            this.yAxisObject.transform.position.set(this.activeGameObject.transform.position);

            this.xAxisObject.transform.position.add(this.xAxisOffset);
            this.yAxisObject.transform.position.add(this.yAxisOffset);
        }
    }

    private void setActive() {
        this.xAxisSpriteRenderer.setColor(xAxisColor);
        this.yAxisSpriteRenderer.setColor(yAxisColor);
    }

    private void setInactive() {
        this.activeGameObject = null;
        this.xAxisSpriteRenderer.setColor(new Vector4f(0, 0, 0, 0));
        this.yAxisSpriteRenderer.setColor(new Vector4f(0, 0, 0, 0));
    }

    private boolean checkXHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= xAxisObject.transform.position.x &&
                mousePos.x >= xAxisObject.transform.position.x - gizmoHeight &&
                mousePos.y >= xAxisObject.transform.position.y &&
                mousePos.y <= xAxisObject.transform.position.y + gizmoWidth) {
            xAxisSpriteRenderer.setColor(xAxisColorHover);
            return true;
        }

        xAxisSpriteRenderer.setColor(xAxisColor);
        return false;
    }

    private boolean checkYHoverState() {
        Vector2f mousePos = new Vector2f(MouseListener.getOrthoX(), MouseListener.getOrthoY());
        if (mousePos.x <= yAxisObject.transform.position.x &&
                mousePos.x >= yAxisObject.transform.position.x - gizmoWidth &&
                mousePos.y <= yAxisObject.transform.position.y &&
                mousePos.y >= yAxisObject.transform.position.y - gizmoHeight) {
            yAxisSpriteRenderer.setColor(yAxisColorHover);
            return true;
        }

        yAxisSpriteRenderer.setColor(yAxisColor);
        return false;
    }

    public void setUsing() {
        this.using = true;
    }

    public void setNotUsing() {
        this.using = false;
        this.setInactive();
    }
}
