package components;

import org.joml.Vector2f;
import renderer.Texture;

public class Sprite {

    private float width, height;

    private Texture texture = null;
    private Vector2f[] textureCoordinates = {
        new Vector2f(1, 1),
        new Vector2f(1, 0),
        new Vector2f(0, 0),
        new Vector2f(0, 1)
    };

    public Texture getTexture() {
        return this.texture;
    }

    public Vector2f[] getTextureCoordinates() {
        return this.textureCoordinates;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }

    public void setTextureCoordinates(Vector2f[] textureCoordinates) {
        this.textureCoordinates = textureCoordinates;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public int getTextureId() {
        return texture == null ? -1 : texture.getTextureID();
    }
}
