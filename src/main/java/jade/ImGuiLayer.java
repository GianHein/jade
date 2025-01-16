package jade;

import Scenes.Scene;
import editor.GameViewWindow;
import editor.PropertiesWindow;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import imgui.ImGuiIO;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImBoolean;
import renderer.PickingTexture;

import static org.lwjgl.glfw.GLFW.*;

public class ImGuiLayer {

    private long glfwWindow;

    // Mouse cursors provided by GLFW
    private final long[] mouseCursors = new long[ImGuiMouseCursor.COUNT];

    // LWJGL3 renderer (SHOULD be initialized)
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();

    private GameViewWindow gameViewWindow;
    private PropertiesWindow propertiesWindow;

    public ImGuiLayer(long glfwWindow, PickingTexture pickingTexture) {
        this.glfwWindow = glfwWindow;
        this.gameViewWindow = new GameViewWindow();
        this.propertiesWindow = new PropertiesWindow(pickingTexture);
    }

    public void initImGui() {
        ImGui.createContext();
        ImGuiIO io = ImGui.getIO();

        io.setIniFilename("imgui.ini");
        io.setConfigFlags(ImGuiConfigFlags.NavEnableKeyboard);
        io.setConfigFlags(ImGuiConfigFlags.DockingEnable);
        io.setBackendFlags(ImGuiBackendFlags.HasMouseCursors);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.setBackendPlatformName("imgui_java_impl_glfw");

        // ------------------------------------------------------------
        // Fonts configuration
        // Read: https://raw.githubusercontent.com/ocornut/imgui/master/docs/FONTS.txt

        final ImFontAtlas fontAtlas = io.getFonts();
        final ImFontConfig fontConfig = new ImFontConfig(); // Natively allocated object, should be explicitly destroyed

        // Glyphs could be added per-font as well as per config used globally like here
        fontConfig.setGlyphRanges(fontAtlas.getGlyphRangesDefault());

        // Fonts merge example
        fontConfig.setPixelSnapH(true);
        fontAtlas.addFontFromFileTTF("assets/fonts/JetBrainsMono-Regular.ttf", 16, fontConfig);

        fontConfig.destroy(); // After all fonts were added we don't need this config more

        // ------------------------------------------------------------
        // Use freetype instead of stb_truetype to build a fonts texture
        fontAtlas.setFlags(ImGuiFreeTypeBuilderFlags.LightHinting);
        fontAtlas.build();

        imGuiGlfw.init(glfwWindow, true);
        imGuiGl3.init("#version 330 core");
    }

    public void updateImGui(float deltaTime, Scene currentScene) {
        startFrame(deltaTime);

        imGuiGlfw.newFrame();
        ImGui.newFrame();
        setupDockspace();
        currentScene.imgui();
        ImGui.showDemoWindow();
        gameViewWindow.imgui();
        propertiesWindow.update(deltaTime, currentScene);
        propertiesWindow.imgui();
        // ImGui.end();
        ImGui.render();

        endImGuiFrame();

        ImGuiIO io = ImGui.getIO();

        if (io.hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindow = glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            glfwMakeContextCurrent(backupWindow);
        }
    }

    private void startFrame(float deltaTime) {
        float[] windowWidth = {Window.getWidth()};
        float[] windowHeight = {Window.getHeight()};
        double[] mousePositionX = {0};
        double[] mousePositionY = {0};
        glfwGetCursorPos(glfwWindow, mousePositionX, mousePositionY);

        final ImGuiIO io = ImGui.getIO();
        io.setDisplaySize(windowWidth[0], windowHeight[0]);
        io.setDisplayFramebufferScale(1.0f, 1.0f);
        io.setMousePos((float) mousePositionX[0], (float) mousePositionY[0]);
        io.setDeltaTime(deltaTime);

        final int imguiCursor = ImGui.getMouseCursor();
        glfwSetCursor(glfwWindow, mouseCursors[imguiCursor]);
        glfwSetInputMode(glfwWindow, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
    }

    public void endImGuiFrame() {
        imGuiGl3.renderDrawData(ImGui.getDrawData());
        ImGui.render();
        ImGui.endFrame();
    }

    public void destroyImGui() {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
    }

    private void setupDockspace() {
//        int windowFlags = ImGuiWindowFlags.MenuBar | ImGuiWindowFlags.NoDocking;
//        ImGui.setNextWindowPos(0.0f, 0.0f, ImGuiCond.Always);
//        ImGui.setNextWindowSize(Window.getWidth(), Window.getHeight());
//        ImGui.pushStyleVar(ImGuiStyleVar.WindowRounding, 0.0f);
//        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
//        windowFlags |= ImGuiWindowFlags.NoTitleBar | ImGuiWindowFlags.NoCollapse |
//                ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove |
//                ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.NoNavFocus;
//        ImGui.begin("Dockspace Demo", new ImBoolean(true), windowFlags);
//        ImGui.popStyleVar(2);
//        // Dockspace
//        ImGui.dockSpace(ImGui.getID("Dockspace"));

        ImGui.dockSpaceOverViewport(ImGui.getMainViewport());
    }
}
