package renderer;

import components.SpriteRenderer;
import jade.GameObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZEZ = 1000;
    private List<RenderBatch> batches;
    private static Shader currentShader;

    public Renderer() {
        this.batches = new ArrayList<>();
    }

    public void add(GameObject gameObject) {
        SpriteRenderer spriteRenderer = gameObject.getComponent(SpriteRenderer.class);
        if (spriteRenderer != null) add(spriteRenderer);
    }

    private void add(SpriteRenderer spriteRenderer) {
        boolean added = false;

        for (RenderBatch renderBatch : batches) {
            if (renderBatch.hasRoom() && renderBatch.zIndex() == spriteRenderer.gameObject.transform.zindex) {
                Texture texture = spriteRenderer.getTexture();

                if (texture == null || (renderBatch.hasTexture(texture) || renderBatch.hasTextureRoom())) {
                    renderBatch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZEZ, spriteRenderer.gameObject.transform.zindex);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(spriteRenderer);
            Collections.sort(batches);
        }
    }

    public static void bindShader(Shader shader) {
        currentShader = shader;
    }

    public static Shader getBoundShader() {
        return currentShader;
    }

    public void render() {
        currentShader.use();
        for (RenderBatch renderBatch : batches) {
            renderBatch.render();
        }
    }
}
