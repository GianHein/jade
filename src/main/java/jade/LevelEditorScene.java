package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Rigidbody;
import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import imgui.ImVec2;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject object1;

    private SpriteRenderer object1SpriteRenderer;
    private Spritesheet sprites;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");

        if (loadedLevel) {
            this.activeGameObject = gameObjects.get(0);
            return;
        }

        object1 = new GameObject("Object 1", new Transform(new Vector2f(200, 100), new Vector2f(256, 256)), 2);
        object1SpriteRenderer = new SpriteRenderer();
        object1SpriteRenderer.setColor(new Vector4f(1, 0, 0, 1));
        object1.addComponent(object1SpriteRenderer);
        object1.addComponent(new Rigidbody());
        this.addGameObjectToScene(object1);
        this.activeGameObject = object1;

        GameObject object2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)), 3);
        SpriteRenderer object2SpriteRenderer = new SpriteRenderer();
        Sprite object2Sprite = new Sprite();
        object2Sprite.setTexture(AssetPool.getTexture("assets/images/blendImage2.png"));
        object2SpriteRenderer.setSprite(object2Sprite);
        object2.addComponent(object2SpriteRenderer);
        this.addGameObjectToScene(object2);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png", new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");
    }

    @Override
    public void update(float deltaTime) {
//        System.out.println("FPS: " + 1.0f / deltaTime);
        MouseListener.getOrthoX();

        for (GameObject gameObject : this.gameObjects) {
            gameObject.update(deltaTime);
        }

        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test window");


        ImVec2 windowPosition = new ImVec2();
        ImGui.getWindowPos(windowPosition);
        ImVec2 windowSize = new ImVec2();
        ImGui.getWindowSize(windowSize);
        ImVec2 itemSpacing = new ImVec2();
        ImGui.getStyle().getItemSpacing(itemSpacing);

        float windowX2 = windowPosition.x + windowSize.x;

        for (int i = 0; i < sprites.size(); i++) {
            Sprite sprite = sprites.getSprite(i);
            float spriteWidth = sprite.getWidth() * 2;
            float spriteHeight = sprite.getHeight() * 2;
            int id = sprite.getTextureId();
            Vector2f[] textureCoordinates = sprite.getTextureCoordinates();

            ImGui.pushID(i);
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinates[0].x, textureCoordinates[0].y, textureCoordinates[2].x, textureCoordinates[2].y)) {
                System.out.println("Button " + i + " clicked");
            }
            ImGui.popID();

            ImVec2 lastButtonPosition = new ImVec2();
            ImGui.getItemRectMax(lastButtonPosition);
            float lastButtonX2 = lastButtonPosition.x;
            float nextButtonX2 = lastButtonX2 + itemSpacing.x + spriteWidth;

            if (i + 1 < sprites.size() && nextButtonX2 < windowX2) {
                ImGui.sameLine();
            }
        }



        ImGui.end();
    }
}
