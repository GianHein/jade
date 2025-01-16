package editor;

import Scenes.Scene;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import renderer.PickingTexture;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;

public class PropertiesWindow {
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.pickingTexture = pickingTexture;
    }

    public void update(float deltaTime, Scene currentScene) {
        if (MouseListener.mouseButtonDown(GLFW_MOUSE_BUTTON_LEFT)) {
            int x = (int)MouseListener.getScreenX();
            int y = (int)MouseListener.getScreenY();

            if (x >= 0 && x <= pickingTexture.getWidth() && y >= 0 && y <= pickingTexture.getHeight()) {
                int gameObjectId = pickingTexture.readPixel(x, y);
                activeGameObject = currentScene.getGameObject(gameObjectId);
            }
        }
    }

    public void imgui() {
        if (activeGameObject != null) {
            ImGui.begin("Properties");
            activeGameObject.imgui();
            ImGui.end();
        }
    }
}
