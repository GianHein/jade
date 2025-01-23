package editor;

import Scenes.Scene;
import components.NonPickable;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import renderer.PickingTexture;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    private float debounce = 0.4f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float deltaTime, Scene currentScene) {
        debounce -= deltaTime;

        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT) && debounce < 0) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();

            if (x >= 0 && x <= pickingTexture.getWidth() && y >= 0 && y <= pickingTexture.getHeight()) {
                int gameObjectId = pickingTexture.readPixel(x, y);
                GameObject pickedGameObject = currentScene.getGameObject(gameObjectId);

                if (pickedGameObject != null && pickedGameObject.getComponent(NonPickable.class) == null) {
                    activeGameObject = pickedGameObject;
                } else if (pickedGameObject == null && !MouseListener.isDragging()) {
                    activeGameObject = null;
                }
            }

            this.debounce = 0.4f;
        }
    }

    public void imgui() {
        if (activeGameObject != null) {
            ImGui.begin("Properties");
            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return this.activeGameObject;
    }
}
