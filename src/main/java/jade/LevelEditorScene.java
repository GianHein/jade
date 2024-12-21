package jade;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import renderer.Shader;
import util.Time;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene {

    private int vaoID, vboID, eboID;
    private Shader defaultShader;

    private float[] vertexArray = {
            // position           // color
            100f, 0.0f, 0.0f,     1.0f, 0.0f, 0.0f, 1.0f, // Bottom right    0
            0.0f, 100f, 0.0f,     0.0f, 1.0f, 0.0f, 1.0f, // Top left        1
            100f, 100f, 0.0f,     1.0f, 0.0f, 1.0f, 1.0f, // Top right       2
            0.0f, 0.0f, 0.0f,     1.0f, 1.0f, 0.0f, 1.0f, // Bottom left     3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
            2, 1, 0, // top right triangle
            0, 1, 3  // bottom left triangle
    };

    public LevelEditorScene() {

    }

    @Override
    public void init() {
        this.camera = new Camera(new Vector2f(-200, -300));
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        // Create the VAO and bind it
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer from the vertex data
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        vertexBuffer.put(vertexArray).flip();

        // Create the VBO and upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        // Create the EBO and upload the element buffer
        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4; // Java uses 4 bytes per float
        int vertexSizeBytes =  (positionsSize + colorSize) * floatSizeBytes;

        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float deltaTime) {
        camera.position.x -= deltaTime * 50.0f;
        camera.position.y -= deltaTime * 20.0f;

        defaultShader.use();
        defaultShader.uploadMat4f("uniformProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uniformView", camera.getViewMatrix());
        defaultShader.uploadFloat("uniformTime", Time.getTime());

        // Bind the VAO
        glBindVertexArray(vaoID);

        // Enable the vertex attribute arrays
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // Unbind everything
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0);
        glBindVertexArray(0);

        defaultShader.detach();
    }
}
