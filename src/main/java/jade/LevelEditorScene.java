package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Rigidbody;
import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import imgui.ImGui;
import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.system.CallbackI;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    private GameObject object1;

    private SpriteRenderer object1SpriteRenderer;

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f(0, 0));

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
        AssetPool.addSpriteSheet("assets/images/spritesheet.png", new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"), 16, 16, 26, 0));
        AssetPool.getTexture("assets/images/blendImage2.png");
    }

    @Override
    public void update(float deltaTime) {
//        System.out.println("FPS: " + 1.0f / deltaTime);

        for (GameObject gameObject : this.gameObjects) {
            gameObject.update(deltaTime);
        }

        this.renderer.render();
    }

    @Override
    public void imgui() {
        ImGui.begin("Test window");
        ImGui.text("Some random text");
        ImGui.end();
    }
}
