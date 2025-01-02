package jade;

import components.Sprite;
import components.SpriteRenderer;
import components.Spritesheet;
import org.joml.Vector2f;
import util.AssetPool;

public class LevelEditorScene extends Scene {

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        loadResources();

        this.camera = new Camera(new Vector2f(0, 0));

        Spritesheet sprites = AssetPool.getSpritesheet("assets/images/spritesheet.png");

        GameObject object1 = new GameObject("Object 1", new Transform(new Vector2f(100, 100), new Vector2f(256, 256)));
        object1.addComponent(new SpriteRenderer(sprites.getSprite(0)));
        this.addGameObjectToScene(object1);

        GameObject object2 = new GameObject("Object 2", new Transform(new Vector2f(400, 100), new Vector2f(256, 256)));
        object2.addComponent(new SpriteRenderer(sprites.getSprite(10)));
        this.addGameObjectToScene(object2);
    }

    private void loadResources() {
        AssetPool.getShader("assets/shaders/default.glsl");

        AssetPool.addSpriteSheet("assets/images/spritesheet.png", new Spritesheet(AssetPool.getTexture("assets/images/spritesheet.png"), 16, 16, 26, 0));
    }

    @Override
    public void update(float deltaTime) {
        System.out.println("FPS: " + 1.0f / deltaTime);

        for (GameObject gameObject : this.gameObjects) {
            gameObject.update(deltaTime);
        }

        this.renderer.render();
    }
}
