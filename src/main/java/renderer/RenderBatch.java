package renderer;

import components.SpriteRenderer;
import jade.Window;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEXTURE_COORDINATES_SIZE = 2;
    private final int TEXTURE_ID_SIZE = 1;
    private final int ENTITY_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEXTURE_COORDINATES_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEXTURE_ID_OFFSET = TEXTURE_COORDINATES_OFFSET + TEXTURE_COORDINATES_SIZE * Float.BYTES;
    private final int ENTITY_ID_OFFSET = TEXTURE_ID_OFFSET + TEXTURE_ID_SIZE * Float.BYTES;

    private final int VERTEX_SIZE = 10;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] textureSlots = {0, 1, 2, 3, 4, 5, 6, 7};

    private List<Texture> textures;

    private int vaoID, vboID;
    private int maxBatchSize;
    private int zIndex;


    public RenderBatch(int maxBatchSize, int zIndex) {
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
        this.zIndex = zIndex;
    }

    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);

        glVertexAttribPointer(2, TEXTURE_COORDINATES_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_COORDINATES_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEXTURE_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEXTURE_ID_OFFSET);
        glEnableVertexAttribArray(3);

        glVertexAttribPointer(4, ENTITY_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, ENTITY_ID_OFFSET);
        glEnableVertexAttribArray(4);
    }

    private int[] generateIndices() {
        int[] elements = new int[6 * maxBatchSize];

        for (int i = 0; i < maxBatchSize; i++) {
            loadElementIndices(elements, i);
        }

        return elements;
    }

   public void addSprite(SpriteRenderer spriteRenderer) {
        // Get index and add renderObject
       int index = this.numSprites;
       this.sprites[index] = spriteRenderer;
       this.numSprites++;

       if (spriteRenderer.getTexture() != null && !textures.contains(spriteRenderer.getTexture())) textures.add(spriteRenderer.getTexture());

       // Add properties to local vertices array
       loadVertexProperties(index);

       if (numSprites >= this.maxBatchSize) this.hasRoom = false;
   }

    public void render() {
        boolean rebufferData = false;

        for (int i = 0; i < numSprites; i++) {
            SpriteRenderer spriteRenderer = sprites[i];

            if (spriteRenderer.isDirty()) {
                loadVertexProperties(i);
                spriteRenderer.setClean();
                rebufferData = true;
            }
        }

        if (rebufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);
        }

        // Use shader
        Shader shader = Renderer.getBoundShader();
        shader.uploadMat4f("uniformProjection", Window.getScene().getCamera().getProjectionMatrix());
        shader.uploadMat4f("uniformView", Window.getScene().getCamera().getViewMatrix());

        for (int i = 0; i < textures.size(); i ++) {
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }

        shader.uploadIntArray("uniformTextures", textureSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);

        for (int i = 0; i < textures.size(); i ++) {
            glActiveTexture(GL_TEXTURE0 + i);
            textures.get(i).unbind();
        }

        shader.detach();
    }

    private void loadVertexProperties(int index) {
        SpriteRenderer spriteRenderer = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = spriteRenderer.getColor();
        Vector2f[] textureCoordinates = spriteRenderer.getTextureCoordinates();

        int textureID = 0;
        if (spriteRenderer.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                if (textures.get(i).equals(spriteRenderer.getTexture())) {
                    textureID = i + 1;
                    break;
                }
            }
        }

        boolean isRotated = spriteRenderer.gameObject.transform.rotation != 0.0f;
        Matrix4f transformMatrix = new Matrix4f().identity();

        if (isRotated) {
            transformMatrix.translate(spriteRenderer.gameObject.transform.position.x, spriteRenderer.gameObject.transform.position.y, 0.0f);
            transformMatrix.rotate((float)Math.toRadians(spriteRenderer.gameObject.transform.rotation), 0, 0, 1);
            transformMatrix.scale(spriteRenderer.gameObject.transform.scale.x, spriteRenderer.gameObject.transform.scale.y, 1.0f);
        }

        // Add vertices with appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;

        for (int i = 0; i < 4; i++) {
            if (i == 1) yAdd = 0.0f;
            if (i == 2) xAdd = 0.0f;
            if (i == 3) yAdd = 1.0f;

            Vector4f currentPosition = new Vector4f(spriteRenderer.gameObject.transform.position.x + (xAdd * spriteRenderer.gameObject.transform.scale.x), spriteRenderer.gameObject.transform.position.y + (yAdd * spriteRenderer.gameObject.transform.scale.y), 0.0f, 1.0f);

            if (isRotated) {
                currentPosition = new Vector4f(xAdd, yAdd, 0,1 ).mul(transformMatrix);
            }

            // Load position
            vertices[offset] = currentPosition.x;
            vertices[offset + 1] = currentPosition.y;

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Load texture coordinates
            vertices[offset + 6] = textureCoordinates[i].x;
            vertices[offset + 7] = textureCoordinates[i].y;

            // Load texture id
            vertices[offset + 8] = textureID;

            // Load entity id
            vertices[offset + 9] = spriteRenderer.gameObject.getUid() + 1;

            offset += VERTEX_SIZE;
        }
    }

    private void loadElementIndices(int[] elements, int index) {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        elements[offsetArrayIndex] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;


        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }

    public boolean hasTextureRoom() {
        return textures.size() < 8;
    }

    public boolean hasTexture(Texture texture) {
        return this.textures.contains(texture);
    }

    public int zIndex() {
        return this.zIndex;
    }

    @Override
    public int compareTo(@NotNull RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex);
    }
}
