package renderer;

import components.SpriteRenderer;
import jade.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer {
    private final int MAX_BATCH_SIZEZ = 1000;
    private List<RenderBatch> batches;

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
            if (renderBatch.hasRoom()) {
                Texture texture = spriteRenderer.getTexture();

                if (texture == null || (renderBatch.hasTexture(texture) || renderBatch.hasTextureRoom())) {
                    renderBatch.addSprite(spriteRenderer);
                    added = true;
                    break;
                }
            }
        }

        if (!added) {
            RenderBatch newBatch = new RenderBatch(MAX_BATCH_SIZEZ);
            newBatch.start();
            batches.add(newBatch);
            newBatch.addSprite(spriteRenderer);
        }
    }

    public void render() {
        for (RenderBatch renderBatch : batches) {
            renderBatch.render();
        }
    }
}
