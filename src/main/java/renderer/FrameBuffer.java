package renderer;

import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {
    private int frameBufferObjectID = 0;
    private Texture texture;

    public FrameBuffer(int width, int height) {
        frameBufferObjectID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferObjectID);

        this.texture = new Texture(width, height);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getTextureID(), 0);

        int renderBufferObjectID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderBufferObjectID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderBufferObjectID);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) assert false : "Error: FrameBuffer is not complete!";

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferObjectID);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getFrameBufferObjectID() {
        return frameBufferObjectID;
    }

    public int getTextureId() {
        return texture.getTextureID();
    }
}
