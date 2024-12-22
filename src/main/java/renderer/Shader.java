package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;

public class Shader {

    private int shaderProgramID;
    private boolean beingUsed = false;

    private String vertexSource;
    private String fragmentSource;
    private String filepath;

    public Shader(String filepath) {
        this.filepath = filepath;

        try {
            String source = new String(Files.readAllBytes(Paths.get(filepath)));
            String[] splitString = source.split("(#type)( )+([a-zA-Z]+)");

            // Find the first pattern after #type 'pattern'
            int index = source.indexOf("#type") + 6;
            int endOfLine = source.indexOf("\n", index);
            String firstPattern = source.substring(index, endOfLine).trim();

            // Find the second pattern after #type 'pattern'
            index = source.indexOf("#type", endOfLine) + 6;
            endOfLine = source.indexOf("\n", index);
            String secondPattern = source.substring(index, endOfLine).trim();

            if (firstPattern.equals("vertex")) vertexSource = splitString[1];
            if (firstPattern.equals("fragment")) fragmentSource = splitString[1];
            if (!firstPattern.equals("vertex") && !firstPattern.equals("fragment")) throw new IOException("Unexpected token '" + firstPattern+ "'");

            if (secondPattern.equals("vertex")) vertexSource = splitString[2];
            if (secondPattern.equals("fragment")) fragmentSource = splitString[2];
            if (!secondPattern.equals("vertex") && !secondPattern.equals("fragment")) throw new IOException("Unexpected token '" + firstPattern+ "'");
        } catch(IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: '" + filepath + "'";
        }
    }

    public void compile() {
        int vertexID, fragmentID;

        // Load and compile the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Pass the shader source to the GPU
        glShaderSource(vertexID, vertexSource);
        glCompileShader(vertexID);

        // Check for errors in compilation
        int success = glGetShaderi(vertexID, GL_COMPILE_STATUS);

        if (success == GL_FALSE) {
            int stringLength = glGetShaderi(vertexID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tVertex shader compilation failed.");
            System.out.println(glGetShaderInfoLog(vertexID, stringLength));
            assert false : "";
        }

        // Load and compile the fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass the shader source to the GPU
        glShaderSource(fragmentID, fragmentSource);
        glCompileShader(fragmentID);

        // Check for errors in compilation
        success = glGetShaderi(fragmentID, GL_COMPILE_STATUS);

        if (success == GL_FALSE) {
            int stringLength = glGetShaderi(fragmentID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tFragment shader compilation failed.");
            System.out.println(glGetShaderInfoLog(fragmentID, stringLength));
            assert false : "";
        }

        // Link shaders
        shaderProgramID = glCreateProgram();
        glAttachShader(shaderProgramID, vertexID);
        glAttachShader(shaderProgramID, fragmentID);
        glLinkProgram(shaderProgramID);

        // Check for linking errors
        success = glGetProgrami(shaderProgramID, GL_LINK_STATUS);

        if (success == GL_FALSE) {
            int stringLength = glGetProgrami(shaderProgramID, GL_INFO_LOG_LENGTH);
            System.out.println("ERROR: '" + filepath + "'\n\tLinking of shaders failed.");
            System.out.println(glGetProgramInfoLog(shaderProgramID, stringLength));
            assert false : "";
        }
    }

    public void use() {
        if (!beingUsed) {
            glUseProgram(shaderProgramID);
            beingUsed = true;
        }
    }

    public void detach() {
        glUseProgram(0);
        beingUsed = false;
    }

    private boolean containsVariable(String variableName) {
        return vertexSource.contains(variableName) || fragmentSource.contains(variableName);
    }

    public void uploadMat3f(String variableName, Matrix3f matrix3f) {
        assert containsVariable(variableName) : "Error: Trying to upload non-existing matrix3f uniform '" + variableName + "' to shader '" + filepath + "'";
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        use();
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(9);
        matrix3f.get(matrixBuffer);
        glUniformMatrix3fv(variableLocation, false, matrixBuffer);
    }

    public void uploadMat4f(String variableName, Matrix4f matrix4f) {
        assert containsVariable(variableName) : "Error: Trying to upload non-existing matrix4f uniform '" + variableName + "' to shader '" + filepath + "'";
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        use();
        FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
        matrix4f.get(matrixBuffer);
        glUniformMatrix4fv(variableLocation, false, matrixBuffer);
    }

    public void uploadVec2f(String variableName, Vector2f vector2f) {
        assert containsVariable(variableName) : "Error: Trying to upload non-existing vector2f uniform '" + variableName + "' to shader '" + filepath + "'";
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        use();
        glUniform2f(variableLocation, vector2f.x, vector2f.y);
    }

    public void uploadVec3f(String variableName, Vector3f vector3f) {
        assert containsVariable(variableName) : "Error: Trying to upload non-existing vector3f uniform '" + variableName + "' to shader '" + filepath + "'";
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        use();
        glUniform3f(variableLocation, vector3f.x, vector3f.y, vector3f.z);
    }

    public void uploadVec4f(String variableName, Vector4f vector4f) {
        assert containsVariable(variableName) : "Error: Trying to upload non-existing vector4f uniform '" + variableName + "' to shader '" + filepath + "'";
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        use();
        glUniform4f(variableLocation, vector4f.x, vector4f.y, vector4f.z, vector4f.w);
    }

    public void uploadFloat(String variableName, float value) {
        assert containsVariable(variableName) : "Error: Trying to upload non-existing float uniform '" + variableName + "' to shader '" + filepath + "'";
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        use();
        glUniform1f(variableLocation, value);
    }

    public void uploadInt(String variableName, int value) {
        assert containsVariable(variableName) : "Error: Trying to upload non-existing int uniform '" + variableName + "' to shader '" + filepath + "'";
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        use();
        glUniform1i(variableLocation, value);
    }

    public void uploadTexture(String variableName, int slot) {
        assert containsVariable(variableName) : "Error: Trying to upload non-existing texture uniform '" + variableName + "' to shader '" + filepath + "'";
        int variableLocation = glGetUniformLocation(shaderProgramID, variableName);
        use();
        glUniform1i(variableLocation, slot);
    }
}
