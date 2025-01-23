package jade;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;

import static org.lwjgl.glfw.GLFW.GLFW_PRESS;
import static org.lwjgl.glfw.GLFW.GLFW_RELEASE;

public class MouseListener {
    private static MouseListener instance;
    private double scrollX, scrollY;
    private double xPos, yPos, lastY, lastX, worldX, worldY, lastWorldX, lastWorldY;
    private boolean mouseButtonPressed[] = new boolean[3];
    private boolean isDragging;

    private int mouseButtonDown = 0;

    private static Vector2f gameViewportPos = new Vector2f();
    private static Vector2f gameViewportSize = new Vector2f();

    private MouseListener() {
        this.scrollX = 0.0;
        this.scrollY = 0.0;
        this.xPos = 0.0;
        this.yPos = 0.0;
        this.lastX = 0.0;
        this.lastY = 0.0;
    }

    public static MouseListener get() {
        if (MouseListener.instance == null) MouseListener.instance = new MouseListener();

        return MouseListener.instance;
    }

    public static void mousePosCallback(long window, double xPos, double yPos) {
        if (get().mouseButtonDown > 0) {
            get().isDragging = true;
        }
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
        get().xPos = xPos;
        get().yPos = yPos;
        calcOrthoX();
        calcOrthoY();
    }

    public static void mouseButtonCallback(long window, int button, int action, int mods) {
        if (action == GLFW_PRESS) get().mouseButtonDown++;
        if (action == GLFW_RELEASE) get().mouseButtonDown--;

        if (action == GLFW_PRESS && button < get().mouseButtonPressed.length) get().mouseButtonPressed[button] = true;

        if (action == GLFW_RELEASE && button < get().mouseButtonPressed.length) {
            get().mouseButtonPressed[button] = false;
            get().isDragging = false;
        }
    }

    public static void mouseScrollCallback(long window, double xOffset, double yOffset) {
        get().scrollX = xOffset;
        get().scrollY = yOffset;
    }

    public static void endFrame() {
        get().scrollX = 0;
        get().scrollY = 0;
        get().lastX = get().xPos;
        get().lastY = get().yPos;
        get().lastWorldX = get().worldX;
        get().lastWorldY = get().worldY;
    }

    public static float getX() {
        return (float) get().xPos;
    }

    public static float getY() {
        return (float) get().yPos;
    }

    public static float getDx() {
        return (float) (get().lastX - get().xPos);
    }

    public static float getDy() {
        return (float) (get().lastY - get().yPos);
    }

    public static float getWorldDx() {
        return (float) (get().lastWorldX - get().worldX);
    }

    public static float getWorldDy() {
        return (float) (get().lastWorldY - get().worldY);
    }

    public static float getScrollX() {
        return (float) get().scrollX;
    }

    public static float getScrollY() {
        return (float) get().scrollY;
    }

    public static boolean isDragging() {
        return get().isDragging;
    }

    public static boolean mouseButtonDown(int button) {
        if (button < get().mouseButtonPressed.length) return get().mouseButtonPressed[button];

        return false;
    }

    public static float getScreenX() {
        float currentX = getX() - gameViewportPos.x;
        currentX = (currentX / gameViewportSize.x) * 2560.0f;

        return currentX;
    }

    public static float getScreenY() {
        float currentY = getY() - gameViewportPos.y;
        currentY = 1440.0f-((currentY / gameViewportSize.y) * 1440.0f);

        return currentY;
    }

    public static float getOrthoX() {
        return (float)get().worldX;
    }

    public static float getOrthoY() {
        return (float)get().worldY;
    }

    private static void calcOrthoX() {
        float currentX = getX() - gameViewportPos.x;
        currentX = (currentX / gameViewportSize.x) * 2.0f - 1.0f;
        Vector4f temporary = new Vector4f(currentX, 0, 0, 1);

        Matrix4f viewProjection = new Matrix4f();
        Window.getScene().getCamera().getInverseView().mul(Window.getScene().getCamera().getInverseProjection(), viewProjection);

        temporary.mul(viewProjection);
        get().worldX = temporary.x;
    }

    private static void calcOrthoY() {
        float currentY = getY() - gameViewportPos.y;
        currentY = -((currentY / gameViewportSize.y) * 2.0f - 1.0f);
        Vector4f temporary = new Vector4f(0, currentY, 0, 1);

        Matrix4f viewProjection = new Matrix4f();
        Window.getScene().getCamera().getInverseView().mul(Window.getScene().getCamera().getInverseProjection(), viewProjection);

        temporary.mul(viewProjection);
        get().worldY = temporary.y;
    }

    public static void setGameViewportPos(Vector2f gameViewportPos) {
        MouseListener.gameViewportPos.set(gameViewportPos);
    }

    public static void setGameViewportSize(Vector2f gameViewportSize) {
        MouseListener.gameViewportSize.set(gameViewportSize);
    }
}