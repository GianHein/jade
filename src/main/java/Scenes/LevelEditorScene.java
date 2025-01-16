package Scenes;

import components.*;
import imgui.ImGui;
import imgui.ImVec2;
import jade.*;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import renderer.DebugDraw;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject object1;
    private Spritesheet sprites;
    private SpriteRenderer object1SpriteRenderer;

    GameObject levelEditorStuff = new GameObject("LevelEditor", new Transform(new Vector2f()), 0);

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        levelEditorStuff.addComponent(new MouseControls());
        levelEditorStuff.addComponent(new GridLines());

        loadResources();

        this.camera = new Camera(new Vector2f(-250, 0));
        sprites = AssetPool.getSpritesheet("assets/images/spritesheets/decorationsAndBlocks.png");
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");
        AssetPool.addSpriteSheet("assets/images/spritesheets/decorationsAndBlocks.png", new Spritesheet(AssetPool.getTexture("assets/images/spritesheets/decorationsAndBlocks.png"), 16, 16, 81, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");

        for (GameObject gameObject : this.gameObjects) {
            if (gameObject.getComponent(SpriteRenderer.class) != null)  {
                SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);

                if (spriteRenderer.getTexture() != null) {
                    spriteRenderer.setTexture(AssetPool.getTexture(spriteRenderer.getTexture().getFilepath()));
                }
            }
        }
    }

    float x = 0.0f;
    float y = 0.0f;
    @Override
    public void update(float deltaTime) {
        levelEditorStuff.update(deltaTime);

        DebugDraw.addCircle2D(new Vector2f(x, y), 64, new Vector3f(0, 1, 0), 1);
        x += 1.0f + deltaTime;
        y += 1.0f + deltaTime;

        for (GameObject gameObject : this.gameObjects) {
            gameObject.update(deltaTime);
        }
    }

    @Override
    public void render() {
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
            if (ImGui.imageButton(id, spriteWidth, spriteHeight, textureCoordinates[2].x, textureCoordinates[0].y, textureCoordinates[0].x, textureCoordinates[2].y)) {
                GameObject object = Prefabs.generateSpriteObject(sprite, 32, 32);
                levelEditorStuff.getComponent(MouseControls.class).pickupObject(object);
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
